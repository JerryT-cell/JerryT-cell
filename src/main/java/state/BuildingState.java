package de.unisaarland.cs.se.selab.state;

import de.unisaarland.cs.se.selab.ConnectionUtils;
import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.comm.BidType;
import de.unisaarland.cs.se.selab.comm.TimeoutException;
import de.unisaarland.cs.se.selab.commands.ActionResult;
import de.unisaarland.cs.se.selab.commands.Command;
import de.unisaarland.cs.se.selab.commands.LeaveCommand;
import de.unisaarland.cs.se.selab.model.Adventurer;
import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.model.Monster;
import de.unisaarland.cs.se.selab.model.Player;
import de.unisaarland.cs.se.selab.model.dungeon.Room;
import de.unisaarland.cs.se.selab.model.dungeon.Tunnel;
import de.unisaarland.cs.se.selab.spells.Spell;
import de.unisaarland.cs.se.selab.state.bids.Bid;
import de.unisaarland.cs.se.selab.state.bids.SpellBid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This state handles one complete building phase including bid placement and evaluation.
 */
public final class BuildingState extends State {

    public static final int ROOMS_PER_ROUND = 2;
    public static final int MONSTERS_PER_ROUND = 3;
    public static final int NO_SUPERVISION_LIMIT = 3;
    public static final int SPELLS_PER_ROUND = 3;

    public BuildingState(final Model model, final ConnectionWrapper connection) {
        super(model, connection);
    }

    @Override
    public State run() {
        connection.sendNextYear(model.getYear());

        // Reset all players bids, monsters, adventurers.
        for (final Player player : model.getPlayers()) {
            player.getLockedTypes()
                    .forEach(type -> connection.sendBidRetrieved(player.getId(), type));
            player.unlockBidTypes();
            player.wakeUpMonsters();
            player.getDungeon().clearAdventurers();
        }

        while (model.hasNextRound()) {
            if (!handleSeason()) {
                return new EndGameState(this.model, this.connection);
            }
        }
        for (final Player player : model.getPlayers()) {
            player.buildingEnd();
        }
        return new CombatState(this.model, this.connection);
    }

    /**
     * Contains the logic for handling one season.
     *
     * @return {@code true} if the game should continue or {@code false} if all players left and the
     * game should exit
     */
    private boolean handleSeason() {
        connection.sendNextRound(model.getRound());
        // Draw new adventurers, monsters and rooms.
        drawCards();

        // Let players place their orders.
        connection.sendBiddingStarted();
        connection.sendActNow();
        if (!playersBid()) {
            return false;
        }

        // Place bids on bidding square and evaluate them.
        final Map<BidType, List<Bid>> biddingSquare = placeBids(model.getPlayersFromStarting());
        if (!evaluateBids(biddingSquare)) {
            return false;
        }

        lockAndUnlockBids(model.getPlayers());
        returnMiningImps(model.getPlayers());
        returnRoomImps(model.getPlayers());
        if (model.getRound() < Model.MAX_ROUNDS) {
            spreadAdventurersToPlayers();
        }
        model.seasonalCleanup();
        // Next round another player is first.
        model.nextPlayer();
        return true;
    }

    /**
     * Collect all player bids.
     *
     * @return {@code true} if the game should continue or {@code false} if all players left and the
     * game should exit
     */
    private boolean playersBid() {
        final List<Player> players = model.getPlayers();
        while (players.stream().anyMatch(Player::hasToBid)) {
            try {
                final Command command = connection.nextAction();
                final int sender = command.getId();
                final ActionResult result =
                        command.execute(model, connection, Phase.BUILDING);
                if (model.hasPlayer(sender)
                        && model.getPlayerById(sender).hasToBid()) {
                    connection.sendActNow(sender);
                }
                // return if last player has left
                if (result == ActionResult.END_GAME) {
                    return false;
                }
            } catch (final TimeoutException exception) {
                if (players.stream().anyMatch(Player::finishedBidding)) {
                    kickUnfinishedPlayers();
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    private void kickUnfinishedPlayers() {
        model.getPlayers().stream()
                .filter(player -> !player.finishedBidding())
                .forEach(player -> new LeaveCommand(player.getId())
                        .execute(model, connection, Phase.BUILDING));
    }

    /**
     * Place a single bid on the bidding square.
     *
     * @param bidType       the bidType of the bid to place
     * @param player        the player who placed the bid
     * @param biddingSquare the bidding square
     */
    private void addBid(final BidType bidType, final Player player, final Map<BidType,
            List<Bid>> biddingSquare) {
        final List<Bid> bidList = biddingSquare.getOrDefault(bidType, new ArrayList<>());
        if (bidList.size() < 3) {
            final Bid bid = Bid.createBid(bidType, player, bidList.size() + 1);
            bidList.add(bid);
            biddingSquare.put(bidType, bidList);
        }
    }

    /**
     * Place the player bids on the bidding square.
     *
     * @param players the players who made bids this round
     */
    private Map<BidType, List<Bid>> placeBids(final Collection<Player> players) {
        final Map<BidType, List<Bid>> biddingSquare = new EnumMap<>(BidType.class);
        for (int index = 0; index < Model.BID_LIMIT; index++) {
            for (final Player player : players) {
                //first bid of all players, index = 0
                addBid(player.getPlacedBidTypes().get(index), player, biddingSquare);
            }
        }
        return biddingSquare;
    }

    /**
     * Evaluate all bids.
     * and decorate bids with spells
     *
     * @return {@code true} if the game should continue or {@code false} if all players left and the
     * game should exit
     */
    public boolean evaluateBids(final Map<BidType, List<Bid>> biddingSquare) {

        for (final BidType type : BidType.values()) {
            final Optional<List<Bid>> bidsOfType = Optional.ofNullable(biddingSquare.get(type));
            if (bidsOfType.isPresent()) {
                for (final Bid bid : bidsOfType.get()) {
                    if (!evaluateBid(bid, type)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * evaluate a single bid and decorate with spells
     *
     * @param bid  bid to evaluate
     * @param type bidType of evaluation
     * @return false if the game must end( all players left)
     */
    private boolean evaluateBid(final Bid bid, final BidType type) {
        final List<Spell> spellToTrigger = model.getAvailableSpell(type, bid.getSlot());
        if (!(spellToTrigger.isEmpty())) {
            final Bid spellBid = new SpellBid(bid, spellToTrigger);
            if (spellBid.evaluate(model, connection) == ActionResult.END_GAME) {
                return false;
            }
            model.removeAvailableSpell(spellToTrigger);
        } else {
            return bid.evaluate(model, connection) != ActionResult.END_GAME;
        }
        return true;
    }

    private void lockAndUnlockBids(final Iterable<Player> players) {
        for (final Player player : players) {
            // Unlock previously locked BidTypes.
            for (final BidType type : player.getLockedTypes()) {
                connection.sendBidRetrieved(player.getId(), type);
            }
            // Lock or return currently used BitTypes.
            player.unlockBidTypes();
            connection.sendBidRetrieved(player.getId(), player.getPlacedBidTypes().get(0));
            player.lockBidTypes();
            player.clearBidTypes();

        }
    }

    private void returnMiningImps(final Iterable<Player> players) {
        for (final Player player : players) {
            final int minedGold = (int) player.getDungeon().getGraph().stream()
                    .filter(Tunnel::hasGoldMiningImp)
                    .count();
            final int returnedImps = player.getDungeon().returnMiners();
            if (returnedImps > 0) {
                player.changeImps(returnedImps);
                connection.sendImpsChanged(player.getId(), returnedImps);
                if (minedGold > 0) {
                    ConnectionUtils.changeGold(player, minedGold, connection);
                }
            }
        }
    }

    private void returnRoomImps(final Iterable<Player> players) {
        for (final Player player : players) {
            for (final Room room : player.getDungeon().getGraph().getRooms()) {
                if (room.isActivated()) {
                    final int returnedImps = room.returnImps();
                    player.changeImps(returnedImps);
                    connection.sendImpsChanged(player.getId(), returnedImps);
                    for (final BidType type : BidType.values()) {
                        final Optional<Integer> production = room.getProduction(type);
                        production.ifPresent(prod -> evaluateRoomProduction(player, prod, type));
                    }
                }
            }
        }
    }

    private void evaluateRoomProduction(final Player player, final int amount, final BidType type) {
        switch (type) {
            case FOOD -> ConnectionUtils.changeFood(player, amount, connection);
            case GOLD -> ConnectionUtils.changeGold(player, amount, connection);
            case NICENESS -> ConnectionUtils.changeEvilness(player, -amount, connection);
            case IMPS -> ConnectionUtils.changeImps(player, amount, connection);
            default -> {
            }
        }
    }

    private void drawCards() {
        // Draw adventurers from stack (but not in the last round)
        if (model.getRound() != Model.MAX_ROUNDS) {
            for (int i = 0; i < model.getPlayers().size(); i++) {
                final Adventurer adventurer = model.drawAdventurer();
                connection.sendAdventurerDrawn(adventurer.getId());
            }
        }
        // Draw monsters from stack
        for (int i = 0; i < MONSTERS_PER_ROUND; i++) {
            final Monster monster = model.drawMonster();
            connection.sendMonsterDrawn(monster.getId());
        }

        // Draw rooms from stack
        for (int i = 0; i < ROOMS_PER_ROUND; i++) {
            final Room room = model.drawRoom();
            connection.sendRoomDrawn(room.getId());
        }

        // Draw spells
        for (int i = 0; i < SPELLS_PER_ROUND; i++) {
            final Spell spell = model.drawSpell();
            connection.sendSpellDrawn(spell.getId());
        }
    }

    private void spreadAdventurersToPlayers() {
        model.getPlayers().stream()
                .sorted(Comparator.comparing(Player::getEvilness).thenComparing(Player::getId))
                .forEach(player -> {
                    final Adventurer adventurer = model.popAdventurer();
                    player.getDungeon().addAdventurer(adventurer);
                    connection.sendAdventurerArrived(player.getId(), adventurer.getId());
                });
    }
}
