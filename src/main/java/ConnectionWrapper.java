package de.unisaarland.cs.se.selab;

import de.unisaarland.cs.se.selab.comm.BidType;
import de.unisaarland.cs.se.selab.comm.ServerConnection;
import de.unisaarland.cs.se.selab.comm.TimeoutException;
import de.unisaarland.cs.se.selab.commands.Command;
import de.unisaarland.cs.se.selab.commands.CommandFactory;
import de.unisaarland.cs.se.selab.model.dungeon.Coordinate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The connection wrapper provides easy access to the server connection and manages the
 * registered players.
 */
public class ConnectionWrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionWrapper.class);

    private final Map<Integer, Integer> commToPlayer;
    private final Map<Integer, Integer> playerToComm;
    private final ServerConnection<Command> connection;

    private final String config;

    public ConnectionWrapper(final int port, final int timeout, final String config) {
        this.commToPlayer = new HashMap<>();
        this.playerToComm = new HashMap<>();
        this.connection = new ServerConnection<>(port, timeout,
                new CommandFactory(this.commToPlayer));
        this.config = config;
    }

    /**
     * Register a player with the server.
     *
     * @param commId   the player's communication ID
     * @param playerId the player's player ID
     */
    public void addPlayer(final int commId, final int playerId) {
        this.commToPlayer.putIfAbsent(commId, playerId);
        this.playerToComm.putIfAbsent(playerId, commId);
    }

    /**
     * Remove a player from the server.
     *
     * @param playerId the player's player ID
     */
    public void removePlayer(final int playerId) {
        this.commToPlayer.remove(commId(playerId));
        this.playerToComm.remove(playerId);
    }

    /**
     * Retrieve a list of all currently registered communication IDs.
     *
     * @return a list of communicaton IDs.
     */
    public List<Integer> getCommIds() {
        return this.playerToComm.values().stream().toList();
    }

    /**
     * Retrieve the communication ID for a given player.
     *
     * @param playerId the player's player ID
     * @return the player's communication ID
     */
    public int commId(final int playerId) {
        return this.playerToComm.get(playerId);
    }

    public Command nextAction() throws TimeoutException {
        return this.connection.nextAction();
    }

    /////////////////////////////////////////////
    //           INDIVIDUAL EVENTS             //
    /////////////////////////////////////////////

    public void sendActionFailed(final int playerId, final String message) {
        LOGGER.warn("Command failed for player {}: '{}'", playerId, message);
        this.connection.sendActionFailed(commId(playerId), message);
    }

    public void sendConfig(final int playerId) {
        LOGGER.debug("Config sent to player {}", playerId);
        this.connection.sendConfig(commId(playerId), this.config);
    }

    public void sendDefendYourself(final int playerId) {
        LOGGER.debug("Player {} must defend herself", playerId);
        this.connection.sendDefendYourself(commId(playerId));
    }

    public void sendDigTunnel(final int playerId) {
        LOGGER.debug("Player {} must dig a tunnel", playerId);
        this.connection.sendDigTunnel(commId(playerId));
    }

    public void sendPlaceRoom(final int playerId) {
        LOGGER.debug("Player {} must place a room", playerId);
        this.connection.sendPlaceRoom(commId(playerId));
    }

    public void sendSelectMonster(final int playerId) {
        LOGGER.debug("Player {} must select a monster", playerId);
        this.connection.sendSelectMonster(commId(playerId));
    }

    public void sendSetBattleGround(final int playerId) {
        LOGGER.debug("Player {} must select a battleground", playerId);
        this.connection.sendSetBattleGround(commId(playerId));
    }

    public void sendCounterSpell(final int playerId) {
        LOGGER.debug("Player {} must send a counter spell", playerId);
        this.connection.sendCounterSpell(commId(playerId));
    }

    public void sendActNow(final int playerId) {
        LOGGER.debug("Player {} must act", playerId);
        this.connection.sendActNow(commId(playerId));
    }


    /////////////////////////////////////////////
    //            BROADCAST EVENTS             //
    /////////////////////////////////////////////

    public void sendActNow() {
        LOGGER.debug("(Broadcast) All players must act");
        for (final int commId : getCommIds()) {
            this.connection.sendActNow(commId);
        }
    }

    public void sendRegistrationAborted() {
        LOGGER.warn("(Broadcast) Registration aborted");
        for (final int commId : getCommIds()) {
            this.connection.sendRegistrationAborted(commId);
        }
    }

    public void sendGameStarted() {
        LOGGER.info("(Broadcast) Game started");
        for (final int commId : getCommIds()) {
            this.connection.sendGameStarted(commId);
        }
    }

    public void sendNextYear(final int year) {
        LOGGER.debug("(Broadcast) Year {} has started", year);
        for (final int commId : getCommIds()) {
            this.connection.sendNextYear(commId, year);
        }
    }

    public void sendNextRound(final int round) {
        for (final int commId : getCommIds()) {
            this.connection.sendNextRound(commId, round);
        }
    }

    public void sendFoodChanged(final int playerId, final int amount) {
        LOGGER.debug("(Broadcast) Food changed by {} for player {}", amount, playerId);
        for (final int commId : getCommIds()) {
            this.connection.sendFoodChanged(commId, amount, playerId);
        }
    }

    public void sendGoldChanged(final int playerId, final int amount) {
        LOGGER.debug("(Broadcast) Gold changed by {} for player {}", amount, playerId);
        for (final int commId : getCommIds()) {
            this.connection.sendGoldChanged(commId, amount, playerId);
        }
    }

    public void sendImpsChanged(final int playerId, final int amount) {
        LOGGER.debug("(Broadcast) Imps changed by {} for player {}", amount, playerId);
        for (final int commId : getCommIds()) {
            this.connection.sendImpsChanged(commId, amount, playerId);
        }
    }

    public void sendBidPlaced(final int slot, final int playerId, final BidType type) {
        LOGGER.debug("(Broadcast) Player {} placed bid {} in slot {}", playerId, type, slot);
        for (final int commId : getCommIds()) {
            this.connection.sendBidPlaced(commId, type, playerId, slot);
        }
    }

    public void sendPlayer(final int playerId, final String playerName) {
        LOGGER.debug("(Broadcast) Player {} is {}", playerId, playerName);
        for (final int commId : getCommIds()) {
            this.connection.sendPlayer(commId, playerName, playerId);
        }
    }

    public void sendTunnelDug(final int playerId, final Coordinate coordinate) {
        LOGGER.debug("(Broadcast) Player {} dug a tunnel at coordinates ({}, {})",
                playerId, coordinate.posX(), coordinate.posY());
        for (final int commId : getCommIds()) {
            this.connection.sendTunnelDug(commId, playerId, coordinate.posX(), coordinate.posY());
        }
    }

    public void sendLeft(final int playerId) {
        LOGGER.debug("(Broadcast) Player {} has left the game", playerId);
        for (final int commId : getCommIds()) {
            this.connection.sendLeft(commId, playerId);
        }
    }

    public void sendAdventurerDamaged(final int adventurer, final int amount) {
        LOGGER.debug("(Broadcast) Adventurer {} took {} points of damage", adventurer, amount);
        for (final int commId : getCommIds()) {
            this.connection.sendAdventurerDamaged(commId, adventurer, amount);
        }
    }

    public void sendAdventurerImprisoned(final int adventurer) {
        LOGGER.debug("(Broadcast) Adventurer {} was imprisoned", adventurer);
        for (final int commId : getCommIds()) {
            this.connection.sendAdventurerImprisoned(commId, adventurer);
        }
    }

    public void sendGameEnd(final int playerId, final int points) {
        LOGGER.debug("(Broadcast) The game has ended");
        for (final int commId : getCommIds()) {
            this.connection.sendGameEnd(commId, playerId, points);
        }
    }

    public void sendAdventurerArrived(final int playerId, final int adventurerId) {
        LOGGER.debug("(Broadcast) Adventurer {} arrived at player {}", adventurerId, playerId);
        for (final int commId : getCommIds()) {
            this.connection.sendAdventurerArrived(commId, adventurerId, playerId);
        }
    }

    public void sendAdventurerHealed(final int healerId, final int targetId, final int amount) {
        LOGGER.debug("(Broadcast) Adventurer {} was healed by healer {}  for {} health points",
                targetId, healerId, amount);
        for (final int commId : getCommIds()) {
            this.connection.sendAdventurerHealed(commId, amount, healerId, targetId);
        }
    }

    public void sendMonsterDrawn(final int monsterId) {
        LOGGER.debug("(Broadcast) Monster {} was drawn", monsterId);
        for (final int commId : getCommIds()) {
            this.connection.sendMonsterDrawn(commId, monsterId);
        }
    }

    public void sendAdventurerDrawn(final int adventurerId) {
        LOGGER.debug("(Broadcast) Adventurer {} was drawn", adventurerId);
        for (final int commId : getCommIds()) {
            this.connection.sendAdventurerDrawn(commId, adventurerId);
        }
    }

    public void sendBidRetrieved(final int playerId, final BidType type) {
        LOGGER.debug("(Broadcast) Player {} retrieved bid {}", playerId, type);
        for (final int commId : getCommIds()) {
            this.connection.sendBidRetrieved(commId, type, playerId);
        }
    }

    public void sendEvilnessChanged(final int playerId, final int amount) {
        LOGGER.debug("(Broadcast) Evilness changed by {} for player {}", amount, playerId);
        for (final int commId : getCommIds()) {
            this.connection.sendEvilnessChanged(commId, amount, playerId);
        }
    }

    public void sendMonsterHired(final int playerId, final int monster) {
        LOGGER.debug("(Broadcast) Player {} hired monster {}", playerId, monster);
        for (final int commId : getCommIds()) {
            this.connection.sendMonsterHired(commId, monster, playerId);
        }
    }

    public void sendTunnelConquered(final int adventurer, final Coordinate coordinate) {
        LOGGER.debug("(Broadcast) Adventurer {} conquered tunnel at coordinates ({}, {})",
                adventurer, coordinate.posX(), coordinate.posY());
        for (final int commId : getCommIds()) {
            this.connection.sendTunnelConquered(commId, adventurer, coordinate.posX(),
                    coordinate.posY());
        }
    }

    public void sendAdventurerFled(final int adventurer) {
        LOGGER.debug("(Broadcast) Adventurer {} fled from prison", adventurer);
        for (final int commId : getCommIds()) {
            this.connection.sendAdventurerFled(commId, adventurer);
        }
    }

    public void sendTrapAcquired(final int player, final int trap) {
        LOGGER.debug("(Broadcast) Player {} acquired trap {}", player, trap);
        for (final int commId : getCommIds()) {
            this.connection.sendTrapAcquired(commId, player, trap);
        }
    }

    public void sendTrapPlaced(final int player, final int trap) {
        LOGGER.debug("(Broadcast) Player {} placed trap {}", player, trap);
        for (final int commId : getCommIds()) {
            this.connection.sendTrapPlaced(commId, player, trap);
        }
    }

    public void sendBattleGroundSet(final int player, final Coordinate coordinate) {
        LOGGER.debug("(Broadcast) Player {} set battleground at coordinates ({}, {})",
                player, coordinate.posX(), coordinate.posY());
        for (final int commId : getCommIds()) {
            this.connection.sendBattleGroundSet(commId, player, coordinate.posX(),
                    coordinate.posY());
        }
    }

    public void sendMonsterPlaced(final int monster, final int player) {
        LOGGER.debug("(Broadcast) Player {} placed monster {}", player, monster);
        for (final int commId : getCommIds()) {
            this.connection.sendMonsterPlaced(commId, monster, player);
        }
    }

    public void sendRoomBuilt(final int player, final int room, final Coordinate coordinate) {
        LOGGER.debug("(Broadcast) Player {} placed room {} at coordinates ({}, {})",
                player, room, coordinate.posX(), coordinate.posY());
        for (final int commId : getCommIds()) {
            this.connection.sendRoomBuilt(commId, player, room, coordinate.posX(),
                    coordinate.posY());
        }
    }

    public void sendRoomDrawn(final int room) {
        LOGGER.debug("(Broadcast) Room {} was drawn", room);
        for (final int commId : getCommIds()) {
            this.connection.sendRoomDrawn(commId, room);
        }
    }

    public void sendRoomActivated(final int player, final int room) {
        LOGGER.debug("(Broadcast) Player {} activated room {}", player, room);
        for (final int commId : getCommIds()) {
            this.connection.sendRoomActivated(commId, player, room);
        }
    }

    public void sendBiddingStarted() {
        LOGGER.debug("(Broadcast) Bidding has started");
        for (final int commId : getCommIds()) {
            this.connection.sendBiddingStarted(commId);
        }
    }

    public void sendSpellDrawn(final int spellId) {
        LOGGER.debug("(Broadcast) Spell {} has been drawn", spellId);
        for (final int commId : getCommIds()) {
            this.connection.sendSpellDrawn(commId, spellId);
        }
    }

    public void sendSpellUnlocked(final int spellId, final int playerId) {
        LOGGER.debug("(BroadCast) Spell{} was unlocked by player {}", spellId, playerId);
        for (final int commId : getCommIds()) {
            this.connection.sendSpellUnlocked(commId, spellId, playerId);
        }
    }

    public void sendSpellCast(final int spellId, final int playerId) {
        LOGGER.debug("(BroadCast) spell{} was casted by player {}", spellId, playerId);
        for (final int commId : getCommIds()) {
            this.connection.sendSpellCast(commId, spellId, playerId);
        }
    }

    public void sendCounterSpellFound(final int playerId) {
        LOGGER.debug("(Broadcast) player {} found a spell", playerId);
        for (final int commId : getCommIds()) {
            this.connection.sendCounterSpellFound(commId, playerId);
        }
    }

    public void sendCounterSpellCast(final int playerId) {
        LOGGER.debug("(BroadCast) player {} has send a counterspell", playerId);
        for (final int commId : getCommIds()) {
            this.connection.sendCounterSpellCast(commId, playerId);
        }
    }

    public void sendRoomsBlocked(final int playerId, final int round) {
        LOGGER.debug("(BroadCast) player {} cannot activate a room in round {}", playerId, round);
        for (final int commId : getCommIds()) {
            this.connection.sendRoomsBlocked(commId, playerId, round);
        }
    }

    public void sendBidTypeBlocked(final int player, final BidType bidType, final int round) {
        LOGGER.debug("(BroadCast) Player {} cannot place a bid on BidType {}"
                + " of the biddingSeason{}", player, bidType, round);
        for (final int commId : getCommIds()) {
            this.connection.sendBidTypeBlocked(commId, player, bidType, round);
        }
    }

    public void sendRoomRemoved(final int player, final int room) {
        LOGGER.debug("(BroadCast) The room {} of the Player {} has been destroyed", player, room);
        for (final int commId : getCommIds()) {
            this.connection.sendRoomRemoved(commId, player, room);
        }
    }

    public void sendArchmageArrived(final int player) {
        LOGGER.debug("(BroadCast) The Archmage Linus arrived in the player {}'s dungeon", player);
        for (final int commId : getCommIds()) {
            this.connection.sendArchmageArrived(commId, player);
        }
    }

    public void sendMonsterRemoved(final int playerId, final int monsterId) {
        LOGGER.debug("(BroadCast) The monster{} has been removed from the player{}", monsterId,
                playerId);
        for (final int commId : getCommIds()) {
            this.connection.sendMonsterRemoved(commId, monsterId, playerId);
        }

    }


    public void close() {
        this.connection.close();
    }
}
