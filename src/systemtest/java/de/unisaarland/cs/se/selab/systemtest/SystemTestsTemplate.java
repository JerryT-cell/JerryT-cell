package de.unisaarland.cs.se.selab.systemtest;

import de.unisaarland.cs.se.selab.comm.BidType;
import de.unisaarland.cs.se.selab.comm.TimeoutException;
import de.unisaarland.cs.se.selab.model.Adventurer;
import de.unisaarland.cs.se.selab.model.Monster;
import de.unisaarland.cs.se.selab.model.Player;
import de.unisaarland.cs.se.selab.model.dungeon.Coordinate;
import de.unisaarland.cs.se.selab.model.dungeon.Dungeon;
import de.unisaarland.cs.se.selab.model.dungeon.Room;
import de.unisaarland.cs.se.selab.model.dungeon.Tunnel;
import de.unisaarland.cs.se.selab.spells.Spell;
import de.unisaarland.cs.se.selab.state.BuildingState;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public abstract class SystemTestsTemplate extends SystemTestWrapper {

    protected List<Coordinate> player1Grounds;
    protected List<Coordinate> player2Grounds;


    protected SystemTestsTemplate(final Class<?> subclass, final boolean fail,
                                  final Set<Integer> socketSet,
                                  final String configPath, final int seed) {

        super(subclass, fail, socketSet, configPath, seed);
    }


    /**
     * Bidding Template !!
     *
     *
     * */

    /**
     * bidding is the great function that represents the template of bidding
     *
     * @param isFirstBidding   if is the first bidding phase
     * @param startByMax       if the game players has to attain the max to start
     * @param twoPlayers       correspond to the Boolean of two players
     * @param firstPlayerBids  the bids of the first player
     * @param secondPlayerBids the bids of the second player or null if absent
     * @param blockBids        the list of blockBids of the first player the next year
     * @throws TimeoutException exception
     */

    protected void bidding(final boolean isFirstBidding, final boolean startByMax,
                           final boolean twoPlayers,
                           final List<BidType> firstPlayerBids,
                           final List<BidType> secondPlayerBids,
                           //this is the map of round to block bids by spell of first player
                           final Map<Integer, BidType> blockBids)
            throws TimeoutException {
        if (isFirstBidding) {
            if (twoPlayers) {
                startGame(startByMax);
                socketToPlayer.put(1, model.getPlayerById(0));
                socketToPlayer.put(2, model.getPlayerById(1));
                socketToUsedImps.put(1, 0);
                socketToUsedImps.put(2, 0);
                socketToRoomImps.put(1, 0);
                socketToRoomImps.put(2, 0);
                goldToRetrieve.put(1, 0);
                goldToRetrieve.put(2, 0);

            } else {
                startGame(startByMax);
                socketToPlayer.put(1, model.getPlayerById(0));
                socketToUsedImps.put(1, 0);
                socketToRoomImps.put(1, 0);
                goldToRetrieve.put(1, 0);
            }
        }
        broadcastAssertNextYear(model.getYear());

        boolean isNewYear = model.getYear() > 1;
        while (model.hasNextRound()) {

            final Map<Integer, List<BidType>> playerMapBids =
                    collectBidForGame(twoPlayers, firstPlayerBids, secondPlayerBids);
            bidRetrieved(isNewYear, isFirstBidding);
            broadcastAssertNextRound(model.getRound());
            final Map<Integer, List<BidType>> playerMapToBids = playerMapBids;
            drawCards();
            broadcastAssertBiddingStarted();

            round(isFirstBidding, model.getRound(), playerMapToBids, blockBids);
            isNewYear = false;

        }


    }

    /**
     * This method collects the bids for the current bidding and place them in a hash map for
     * placing in game
     *
     * @param twoPlayers       if the game has 2 players
     * @param firstPlayerBids  the first bidding of the first player
     * @param secondPlayerBids the first bidding of the second player
     * @return return a hashmap of sockets to player bids for placing
     */

    protected Map<Integer, List<BidType>> collectBidForGame(final boolean twoPlayers,
                                                            final List<BidType> firstPlayerBids,
                                                            final List<BidType> secondPlayerBids) {
        final Map<Integer, List<BidType>> playerMapToBids = new LinkedHashMap<>();
        if (twoPlayers) {
            final List<BidType> player1Bids =
                    new ArrayList<>(
                            Arrays.asList(firstPlayerBids.remove(0), firstPlayerBids.remove(0),
                                    firstPlayerBids.remove(0)));
            playerMapToBids.put(1, player1Bids);
            final List<BidType> player2Bids =
                    new ArrayList<>(
                            Arrays.asList(secondPlayerBids.remove(0), secondPlayerBids.remove(0),
                                    secondPlayerBids.remove(0)));
            playerMapToBids.put(2, player2Bids);

        } else {
            final List<BidType> playerBids =
                    new ArrayList<>(
                            Arrays.asList(firstPlayerBids.remove(0), firstPlayerBids.remove(0),
                                    firstPlayerBids.remove(0)));
            playerMapToBids.put(1, playerBids);
        }

        return playerMapToBids;


    }

    /**
     * describes the collecting of bids
     *
     * @param socketBids the socketBids to use
     * @throws TimeoutException exception
     */
    protected void collectingBids(final Map<Integer, List<BidType>> socketBids)
            throws TimeoutException {
        broadcastAssertActNow();
        for (final Integer socket : this.socketList) {
            int i = 1;
            final List<BidType> soBidType = socketBids.get(socket);
            for (final BidType bid : soBidType) {
                this.sendPlaceBid(socket, bid, i);
                socketToPlayer.get(socket).placeBid(bid, i);
                broadCastBidPlaced(bid, socketToId.get(socket), i);
                if (i != 3) {
                    this.assertActNow(socket);
                }
                i++;
            }
        }
    }

    /**
     * this method retrieves the bid of players the next year
     *
     * @param isNewYear      a new year
     * @param isFirstBidding the first bidding
     * @throws TimeoutException exception
     */

    protected void bidRetrieved(final boolean isNewYear,
                                final boolean isFirstBidding)
            throws TimeoutException {

        if (!isFirstBidding && isNewYear) {
            for (final Player player : model.getPlayers()) {
                for (final BidType type : player.getLockedTypes()) {
                    broadCastBidRetrieved(type, player.getId());
                }
                player.unlockBidTypes();
                player.wakeUpMonsters();
                player.getDungeon().clearAdventurers();
            }

        }


    }


    /**
     * this round method does every evaluation of a bidding phase
     *
     * @param isFirstBidding  if it's the first bidding phase, the first evaluate methods should be
     *                        called, otherwise the others
     * @param round           the round of the bidding
     * @param playerMapToBids the playerToBidMap holds all the bids of the players
     * @param blockBids       the list of block bids of player 1
     * @throws TimeoutException the timeout exception
     */


    protected void round(final boolean isFirstBidding, final int round,
                         final Map<Integer, List<BidType>> playerMapToBids,
                         final Map<Integer, BidType> blockBids) throws TimeoutException {


        if (blockBids != null) {
            final BidType bidType = blockBids.get(round);
            if (bidType != null) {
                assertActNow(1);
                sendPlaceBid(1, bidType, 1);
                assertActionFailed(1);
            }
        }
        collectingBids(playerMapToBids);
        if (isFirstBidding) {
            switch (round) {
                case 1 -> evaluateRound1();
                case 2 -> evaluateRound2();
                case 3 -> evaluateRound3();
                case 4 -> evaluateRound4();
                default -> {
                }

            }
        } else {

            switch (round) {
                case 1 -> evaluateRound1Next();
                case 2 -> evaluateRound2Next();
                case 3 -> evaluateRound3Next();
                case 4 -> evaluateRound4Next();
                default -> {
                }

            }
        }

    }

    //first evaluation
    protected abstract void evaluateRound1() throws TimeoutException;

    protected abstract void evaluateRound2() throws TimeoutException;

    protected abstract void evaluateRound3() throws TimeoutException;

    protected abstract void evaluateRound4() throws TimeoutException;

    //second evaluation
    protected void evaluateRound1Next() throws TimeoutException {
        assertActionFailed(1);
    }


    protected void evaluateRound2Next() throws TimeoutException {

        assertActionFailed(1);
    }


    protected void evaluateRound3Next() throws TimeoutException {
        assertActionFailed(1);
    }


    protected void evaluateRound4Next() throws TimeoutException {
        assertActionFailed(1);
    }

    /**
     * template of combat
     */

    /**
     * This method controls a whole combat phase
     *
     * @param nextYear this describes the year, false if it's the first year, true if it's the
     *                 years two
     * @throws TimeoutException exception
     */

    protected void combat(final boolean nextYear) throws TimeoutException {

        boolean firstPlayer = true;
        for (final Player player : model.getPlayers()) {

            while (model.hasNextRound()) {
                broadcastAssertNextRound(model.getRound());

                final int socket = 1 + player.getId();
                if (!player.getDungeon().isConquered()) {
                    assertSetBattleGround(socket);
                    this.assertActNow(socket);
                }

                if (!nextYear) {
                    runCombat(model.getRound(), firstPlayer, player);
                } else {
                    runCombat2(model.getRound(), firstPlayer, player);
                }
                if (player.getDungeon().getAllAdventurers().isEmpty()) {
                    break;
                }
            }
            firstPlayer = false;

        }


    }

    protected void runCombat(final int round, final boolean firstPlayer, final Player player)
            throws TimeoutException {

        if (firstPlayer) {
            switch (round) {
                case 1 -> round1Player1(player);
                case 2 -> round2Player1(player);
                case 3 -> round3Player1(player);
                case 4 -> round4Player1(player);
                default -> {
                }
            }

        } else {
            switch (round) {
                case 1 -> round1Player2(player);
                case 2 -> round2Player2(player);
                case 3 -> round3Player2(player);
                case 4 -> round4Player2(player);
                default -> {
                }
            }
        }

    }

    protected void round1Player1(final Player player) throws TimeoutException {
        //TODO
        assertActionFailed(player.getId() + 1);
    }

    protected void round2Player1(final Player player) throws TimeoutException {
        //TODO
        assertActionFailed(player.getId() + 1);
    }

    protected void round3Player1(final Player player) throws TimeoutException {
        //TODO
        assertActionFailed(player.getId() + 1);
    }

    protected void round4Player1(final Player player) throws TimeoutException {
        //TODO
        assertActionFailed(player.getId() + 1);
    }

    protected void round1Player2(final Player player) throws TimeoutException {
        assertActionFailed(player.getId() + 1);
    }

    protected void round2Player2(final Player player) throws TimeoutException {
        assertActionFailed(player.getId() + 1);
    }

    protected void round3Player2(final Player player) throws TimeoutException {
        assertActionFailed(player.getId() + 1);
    }

    protected void round4Player2(final Player player) throws TimeoutException {
        assertActionFailed(player.getId() + 1);
    }

    void runCombat2(final int round, final boolean firstPlayer, final Player player)
            throws TimeoutException {

        if (firstPlayer) {
            switch (round) {
                case 1 -> round1Player1Next(player);
                case 2 -> round2Player1Next(player);
                case 3 -> round3Player1Next(player);
                case 4 -> round4Player1Next(player);
                default -> {
                }
            }
        } else {
            switch (round) {
                case 1 -> round1Player2Next(player);
                case 2 -> round2Player2Next(player);
                case 3 -> round3Player2Next(player);
                case 4 -> round4Player2Next(player);
                default -> {
                }

            }
        }

    }

    protected void round1Player1Next(final Player player) throws TimeoutException {
        //TODO
        assertActionFailed(player.getId() + 1);
    }

    protected void round2Player1Next(final Player player) throws TimeoutException {
        //TODO
        assertActionFailed(player.getId() + 1);
    }

    protected void round3Player1Next(final Player player) throws TimeoutException {
        //TODO
        assertActionFailed(player.getId() + 1);
    }

    protected void round4Player1Next(final Player player) throws TimeoutException {
        //TODO
        assertActionFailed(player.getId() + 1);
    }

    protected void round1Player2Next(final Player player) throws TimeoutException {
        assertActionFailed(player.getId() + 1);
    }

    protected void round2Player2Next(final Player player) throws TimeoutException {
        assertActionFailed(player.getId() + 1);
    }

    protected void round3Player2Next(final Player player) throws TimeoutException {
        assertActionFailed(player.getId() + 1);
    }

    protected void round4Player2Next(final Player player) throws TimeoutException {
        assertActionFailed(player.getId() + 1);
    }

    /**
     * set the battleGround and send events that correspond to it
     *
     * @param socket    the socket
     * @param isGround1 if the player uses the coordinates if the first ground list
     * @return the battleground
     * @throws TimeoutException exception
     */

    protected Tunnel battleGroundDefendYourself(final int socket,
                                                final int index,
                                                final boolean isGround1)
            throws TimeoutException {
        final Player player = socketToPlayer.get(socket);
        final Dungeon dungeon = player.getDungeon();
        final Optional<Tunnel> battleGround;
        final int x;
        final int y;
        if (isGround1) {
            x = player1Grounds.get(index).posX();
            y = player1Grounds.get(index).posY();
            battleGround = dungeon.getGraph().getTunnel(player1Grounds.get(index));
        } else {
            x = player2Grounds.get(index).posX();
            y = player2Grounds.get(index).posY();
            battleGround = dungeon.getGraph().getTunnel(player2Grounds.get(index));

        }

        dungeon.setBattleGround(battleGround.get());
        sendBattleGround(socket, x, y);
        broadcastAssertBattleGroundSet(player.getId(), x, y);
        assertDefendYourself(socket);
        assertActNow(socket);
        return battleGround.get();
    }


    /**
     * describes the beginning of the game i.e registration and starting
     */
    protected void startGame(final boolean startBeforeMax) throws TimeoutException {
        registerAll(startBeforeMax);
        broadcastAssertGameStarted();
        model.shuffleCards();
        for (final Integer player : socketList) {
            for (final Integer integer : socketList) {
                this.assertPlayer(integer, socketName.get(player),
                        socketToId.get(player));
            }
        }
    }

    protected void registerAll(final boolean startBeforeMax) throws TimeoutException {
        final String config = createConfig();
        int i = 0;
        for (final Integer player : socketList) {
            this.sendRegister(player, playerNames[i]);
            this.assertConfig(player, config);

            this.socketToId.put(player, i);
            model.addPlayer(playerNames[i]);
            socketName.put(player, playerNames[i]);
            i++;
        }
        if (startBeforeMax) {
            this.sendStartGame(socketList.get(0));
        }
    }

    /**
     * drawing cards
     */

    protected void drawCards()
            throws TimeoutException {
        if (model.getRound() < 4) {
            for (int i = 0; i < model.getPlayers().size(); i++) {
                final Adventurer adventurer = model.drawAdventurer();
                broadCastAdventurerDrawn(adventurer.getId());
            }
        }
        for (int i = 0; i < BuildingState.MONSTERS_PER_ROUND; i++) {
            final Monster monster = model.drawMonster();
            broadCastMonsterDrawn(monster.getId());
        }
        for (int i = 0; i < BuildingState.ROOMS_PER_ROUND; i++) {
            final Room room = model.drawRoom();
            broadCastRoomDrawn(room.getId());
        }

        for (int i = 0; i < BuildingState.SPELLS_PER_ROUND; i++) {
            final Spell spell = model.drawSpell();
            broadCastSpellDrawn(spell.getId());
        }


    }


}
