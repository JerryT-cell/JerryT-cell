package de.unisaarland.cs.se.selab.systemtest;

import de.unisaarland.cs.se.selab.comm.BidType;
import de.unisaarland.cs.se.selab.comm.TimeoutException;
import de.unisaarland.cs.se.selab.config.ConfigParser;
import de.unisaarland.cs.se.selab.config.ModelBuilder;
import de.unisaarland.cs.se.selab.config.ModelBuilderInterface;
import de.unisaarland.cs.se.selab.config.ModelValidator;
import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.model.Player;
import de.unisaarland.cs.se.selab.systemtest.api.SystemTest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class SystemTestBroadcasts extends SystemTest {

    protected final Model model;
    protected final Map<Integer, String> socketName;
    protected final List<Integer> socketList;
    protected final String[] playerNames;
    protected final Map<Integer, Integer> socketToId;
    protected final Map<Integer, Integer> socketToUsedImps;
    protected final int seed;
    protected final Map<Integer, Player> socketToPlayer;
    protected final Map<Integer, Integer> socketToRoomImps;
    protected final Map<Integer, Integer> goldToRetrieve;

    protected SystemTestBroadcasts(final Class<?> subclass, final boolean fail,
                                   final Set<Integer> socketSet, final String configPath,
                                   final int seed) {
        super(subclass, fail);

        final List<Integer> socketListTemp = new ArrayList<>(socketSet);
        Collections.sort(socketListTemp);
        this.socketList = socketListTemp;

        socketToId = new HashMap<>(socketList.size());
        playerNames = new String[4];
        playerNames[0] = "Niklas";
        playerNames[1] = "Jerry";
        playerNames[2] = "Nick";
        playerNames[3] = "Gleb";

        socketName = new HashMap<>(socketList.size());
        socketToPlayer = new HashMap<>();
        socketToUsedImps = new HashMap<>();
        socketToRoomImps = new HashMap<>();
        goldToRetrieve = new HashMap<>();
        this.seed = seed;

        final String path;
        try {
            path = "src/main/resources/" + configPath;
            final ModelBuilderInterface<Model> builder = new ModelValidator<>(new ModelBuilder());
            final String jsonText = Files.readString(Paths.get(path), StandardCharsets.UTF_8);
            builder.setSeed(seed);
            model = ConfigParser.parse(jsonText, builder);
        } catch (IOException e) {
            throw new AssertionError(e);
        }

    }


    /**
     * BROADCASTS
     */
    protected void broadcastAssertActNow() throws TimeoutException {
        for (final Integer socket : this.socketList) {
            this.assertActNow(socket);
        }
    }


    protected void broadcastAssertGameStarted() throws TimeoutException {
        for (final Integer socket : this.socketList) {
            this.assertGameStarted(socket);
        }
    }

    protected void broadcastAssertBattleGroundSet(final int lordId, final int x, final int y)
            throws TimeoutException {
        for (final Integer socket : this.socketList) {
            this.assertBattleGroundSet(socket, lordId, x, y);
        }
    }

    protected void broadcastAssertLeft(final Iterator<Integer> socketIterator,
                                       final int leftLordId)
            throws TimeoutException {
        while (socketIterator.hasNext()) {
            this.assertLeft(socketIterator.next(), leftLordId);
        }
    }

    protected void broadcastAssertBidRetrieved(final BidType bidType, final int lordId)
            throws TimeoutException {
        for (final Integer socket : socketList) {
            this.assertBidRetrieved(socket, bidType, lordId);
        }
    }

    protected void broadcastAssertTrapPlaced(final int lordId, final int trapId)
            throws TimeoutException {
        for (final Integer socket : socketList) {
            this.assertTrapPlaced(socket, lordId, trapId);
        }
    }

    protected void broadcastAssertMonsterPlaced(final int lordId, final int monsterId)
            throws TimeoutException {
        for (final Integer socket : socketList) {
            this.assertMonsterPlaced(socket, monsterId, lordId);
        }
    }

    protected void broadcastAssertAdventurerDamaged(final int adventurerId, final int amount)
            throws TimeoutException {
        for (final Integer socket : socketList) {
            this.assertAdventurerDamaged(socket, adventurerId, amount);
        }
    }

    protected void broadcastRoomBuild(final int lordId, final int roomId, final int x,
                                      final int y) throws TimeoutException {
        for (final Integer socket : socketList) {
            this.assertRoomBuilt(socket, lordId, roomId, x, y);
        }
    }

    protected void broadcastRoomActivated(final int lordId, final int roomId)
            throws TimeoutException {
        for (final Integer socket : socketList) {
            this.assertRoomActivated(socket, lordId, roomId);
        }
    }

    protected void broadcastAssertAdventurerImprisoned(final int adventurerId)
            throws TimeoutException {
        for (final Integer socket : socketList) {
            this.assertAdventurerImprisoned(socket, adventurerId);
        }
    }

    protected void broadcastAssertTunnelConquered(final int adventurerId, final int x,
                                                  final int y) throws TimeoutException {
        for (final Integer socket : socketList) {
            this.assertTunnelConquered(socket, adventurerId, x, y);
        }
    }

    protected void broadcastAssertAdventurerHealed(final int amount, final int priestId,
                                                   final int adventurerId)
            throws TimeoutException {
        for (final Integer socket : socketList) {
            this.assertAdventurerHealed(socket, amount, priestId, adventurerId);
        }
    }

    protected void broadcastAssertNextRound(final int round) throws TimeoutException {
        for (final Integer socket : socketList) {
            this.assertNextRound(socket, round);
        }
    }

    protected void broadcastAssertNextYear(final int year) throws TimeoutException {
        for (final Integer socket : socketList) {
            this.assertNextYear(socket, year);
        }

    }

    protected void broadcastAssertBiddingStarted() throws TimeoutException {
        for (final Integer socket : socketList) {
            this.assertBiddingStarted(socket);
        }
    }

    protected void broadcastAssertImpChanged(final int amount, final int lordId)
            throws TimeoutException {
        for (final Integer socket : socketList) {
            this.assertImpsChanged(socket, amount, lordId);
        }
    }

    protected void broadcastAssertGoldChanged(final int amount, final int lordId)
            throws TimeoutException {
        for (final Integer socket : socketList) {
            this.assertGoldChanged(socket, amount, lordId);

        }

    }

    protected void broadcastAssertFoodChanged(final int amount, final int lordId)
            throws TimeoutException {
        for (final Integer socket : socketList) {
            this.assertFoodChanged(socket, amount, lordId);
        }
    }

    protected void broadcastAssertImpsChanged(final int amount, final int lordId)
            throws TimeoutException {
        for (final Integer socket : socketList) {
            this.assertImpsChanged(socket, amount, lordId);
        }
    }

    protected void broadcastAssertEvilnessChanged(final int amount, final int lordId)
            throws TimeoutException {
        for (final Integer socket : socketList) {
            this.assertEvilnessChanged(socket, amount, lordId);
        }
    }

    protected void broadcastAssertTunnelDug(final int x, final int y, final int lordId)
            throws TimeoutException {
        for (final Integer socket : socketList) {
            this.assertTunnelDug(socket, lordId, x, y);
        }
    }

    protected void broadcastAssertTrapAcquired(final int lordId, final int trapId)
            throws TimeoutException {
        for (final Integer socket : socketList) {
            this.assertTrapAcquired(socket, lordId, trapId);
        }
    }

    protected void broadcastAssertMonsterHired(final int monsterId, final int lordId)
            throws TimeoutException {
        for (final Integer socket : socketList) {
            this.assertMonsterHired(socket, monsterId, lordId);
        }
    }

    protected void broadcastAssertAdventurerArrived(final int adventurerId, final int lordId)
            throws TimeoutException {
        for (final Integer socket : socketList) {
            this.assertAdventurerArrived(socket, adventurerId, lordId);
        }
    }

    protected void broadcastAssertEvent() throws TimeoutException {
        for (final Integer socket : socketList) {
            this.assertEvent(socket);
        }
    }

    protected void broadCastBidRetrieved(final BidType bidType, final int playerId)
            throws TimeoutException {
        for (final Integer socket : socketList) {
            this.assertBidRetrieved(socket, bidType, playerId);
        }
    }

    protected void broadCastAdventurerArrived(final int adventurerId, final int playerId)
            throws TimeoutException {
        for (final Integer socket : socketList) {
            this.assertAdventurerArrived(socket, adventurerId, playerId);
        }
    }

    protected void broadCastBidPlaced(final BidType bidType, final int playerId, final int slot)
            throws TimeoutException {
        for (final Integer socket : socketList) {
            this.assertBidPlaced(socket, bidType, playerId, slot);
        }

    }

    protected void broadCastSpellUnlock(final int playerId, final int spellId)
            throws TimeoutException {
        for (final Integer socket : socketList) {
            this.assertSpellUnlocked(socket, spellId, playerId);
        }

    }

    protected void broadCastCounterSpellFound(final int playerId) throws TimeoutException {
        for (final Integer socket : socketList) {
            this.assertCounterSpellFound(socket, playerId);
        }

    }

    protected void broadCastRoomBuilt(final int playerId, final int x, final int y,
                                      final int roomId)
            throws TimeoutException {
        for (final Integer socket : socketList) {
            this.assertRoomBuilt(socket, playerId, roomId, x, y);
        }

    }

    protected void broadCastSpellCast(final int playerId, final int spellId)
            throws TimeoutException {
        for (final Integer socket : socketList) {
            this.assertSpellCast(socket, spellId, playerId);
        }
    }

    protected void broadCastLinusArrived(final int playerId) throws TimeoutException {
        for (final Integer socket : socketList) {
            this.assertArchmageArrived(socket, playerId);
        }
    }

    protected void broadCastMonsterRemoved(final int playerId, final int monsterId)
            throws TimeoutException {
        for (final Integer socket : socketList) {
            this.assertMonsterRemoved(socket, monsterId, playerId);
        }
    }

    protected void broadCastRoomBlocked(final int playerId, final int round)
            throws TimeoutException {
        for (final Integer socket : socketList) {
            this.assertRoomsBlocked(socket, playerId, round);
        }
    }

    protected void broadCastBidTypeBlocked(final int playerId, final BidType bidType,
                                           final int round)
            throws TimeoutException {
        for (final Integer socket : socketList) {
            this.assertBidTypeBlocked(socket, playerId, bidType, round);
        }
    }

    protected void broadCastRoomDestroyed(final int playerId, final int roomId)
            throws TimeoutException {
        for (final Integer socket : socketList) {
            this.assertRoomRemoved(socket, playerId, roomId);
        }
    }

    protected void broadCastCounterSpellCast(final int playerId) throws TimeoutException {
        for (final Integer socket : socketList) {
            this.assertCounterSpellCast(socket, playerId);
        }
    }

    protected void broadCastAdventurerFlee(final int adventurerId) throws TimeoutException {
        for (final Integer socket : socketList) {
            this.assertAdventurerFled(socket, adventurerId);
        }

    }

    protected void broadCastGameEnd(final int playerId, final int highestScore)
            throws TimeoutException {
        for (final Integer socket : socketList) {
            this.assertGameEnd(socket, playerId, highestScore);
        }
    }

    protected void broadCastAdventurerDrawn(final int adventurerId) throws TimeoutException {
        for (final Integer socket : socketList) {
            this.assertAdventurerDrawn(socket, adventurerId);
        }
    }

    protected void broadCastMonsterDrawn(final int monsterId) throws TimeoutException {
        for (final Integer socket : socketList) {
            this.assertMonsterDrawn(socket, monsterId);
        }
    }

    protected void broadCastRoomDrawn(final int roomId) throws TimeoutException {
        for (final Integer socket : socketList) {
            this.assertRoomDrawn(socket, roomId);
        }
    }

    protected void broadCastSpellDrawn(final int spellId) throws TimeoutException {
        for (final Integer socket : socketList) {
            this.assertSpellDrawn(socket, spellId);
        }
    }


}
