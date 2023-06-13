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
import de.unisaarland.cs.se.selab.spells.StructureEffect;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

public abstract class SystemTestWrapper extends SystemTestBroadcasts {


    protected SystemTestWrapper(final Class<?> subclass, final boolean fail,
                                final Set<Integer> socketSet, final String configPath,
                                final int seed) {
        super(subclass, fail, socketSet, configPath, seed);

    }


    protected void checkSpell(final int indexSocket, final Number spell) throws TimeoutException {
        if (spell != null) {
            final int spellId = spell.intValue();
            broadCastSpellUnlock(socketToId.get(indexSocket), spellId);
        }
    }

    protected void evaluateFood(final int socket, final int slot, final Number spell)
            throws TimeoutException {
        final Player player = socketToPlayer.get(socket);
        checkSpell(socket, spell);
        switch (slot) {
            case 1 -> {
                if (player.getGold() > 0) {
                    broadcastAssertGoldChanged(-1, socketToId.get(socket));
                    broadcastAssertFoodChanged(2, socketToId.get(socket));
                    player.changeGold(-1);
                    player.changeFood(2);
                }

            }

            case 2 -> {
                if (player.getEvilness() < 15) {
                    broadcastAssertEvilnessChanged(1, socketToId.get(socket));
                    broadcastAssertFoodChanged(3, socketToId.get(socket));
                    player.changeFood(3);
                    player.changeEvilness(1);
                }

            }

            default -> throw new AssertionError("wrong slot");
        }
    }


    protected void evaluateNiceness(final int socket, final int slot, final int gold,
                                    final Number spell) throws TimeoutException {
        final Player player = socketToPlayer.get(socket);
        checkSpell(socket, spell);
        switch (slot) {
            case 1 -> {
                if (player.getEvilness() > 0) {
                    broadcastAssertEvilnessChanged(-1, socketToId.get(socket));
                    player.changeEvilness(-1);
                }

            }

            case 2 -> {
                if (player.getEvilness() > 1) {
                    broadcastAssertEvilnessChanged(-2, socketToId.get(socket));
                    player.changeEvilness(-2);
                }

            }
            case 3 -> {
                if (gold < 1) {
                    broadcastAssertGoldChanged(-1, socketToId.get(socket));
                    broadcastAssertEvilnessChanged(-2, socketToId.get(socket));
                    player.changeGold(-1);
                    player.changeEvilness(-2);
                }
            }

            default -> throw new AssertionError("wrong slot");

        }


    }

    protected void evaluateImps(final int socket, final int slot,
                                final Number spell) throws TimeoutException {
        final Player player = socketToPlayer.get(socket);
        checkSpell(socket, spell);
        switch (slot) {
            case 1 -> {
                if (player.getFood() <= 0) {
                    break;
                }
                broadcastAssertFoodChanged(-1, socketToId.get(socket));
                broadcastAssertImpChanged(1, socketToId.get(socket));
                player.changeFood(-1);
                player.changeImps(1);
            }
            case 2 -> {
                if (player.getFood() <= 1) {
                    break;
                }
                broadcastAssertFoodChanged(-2, socketToId.get(socket));
                broadcastAssertImpChanged(2, socketToId.get(socket));
                player.changeFood(-2);
                player.changeImps(2);
            }

            case 3 -> {
                if (player.getFood() <= 0 || player.getGold() <= 0) {
                    break;
                }
                broadcastAssertFoodChanged(-1, socketToId.get(socket));
                broadcastAssertGoldChanged(-1, socketToId.get(socket));
                broadcastAssertImpChanged(2, socketToId.get(socket));
            }

            default -> throw new AssertionError("not okay");

        }

    }


    //for a single tunnel evaluation
    protected void evaluateTunnel(final boolean alreadyStarted, final int socket,
                                  final Number spell, final int x,
                                  final int y, final boolean counterSpell)
            throws TimeoutException {

        final Player player = socketToPlayer.get(socket);
        checkSpell(socket, spell);
        if (!alreadyStarted) {
            this.assertDigTunnel(socket);
        }
        this.assertActNow(socket);
        this.sendDigTunnel(socket, x, y);

        final int numberOfImps =
                player.getImps() - socketToUsedImps.get(socket) - socketToRoomImps.get(socket);
        if (numberOfImps > 0) {
            broadcastAssertImpChanged(-1, socketToId.get(socket));
            broadcastAssertTunnelDug(x, y, socketToId.get(socket));
            player.getDungeon().getGraph().addTunnel(new Tunnel(new Coordinate(x, y), true));
            socketToUsedImps.put(socket, socketToUsedImps.get(socket) + 1);
            if (counterSpell) {
                this.broadCastCounterSpellFound(socketToId.get(socket));
            }
        } else {
            assertActionFailed(socket);
        }


    }

    protected void evaluateGold(final int socket, final Number spell, final int slot)
            throws TimeoutException {

        final Player player = socketToPlayer.get(socket);
        final List<Tunnel> availableTunnels = player.getDungeon().getGraph().stream()
                .filter(tunnel -> !tunnel.isRoom() && !tunnel.isConquered())
                .toList();
        checkSpell(socket, spell);
        final int blockImps = (socketToUsedImps.get(socket) + socketToRoomImps.get(socket));
        switch (slot) {

            case 1 -> {
                if (player.getImps() - blockImps > 1
                        && availableTunnels.size() > 1) {
                    broadcastAssertImpChanged(-2, socketToId.get(socket));
                    socketToUsedImps.put(socket, socketToUsedImps.get(socket) + 2);
                    goldToRetrieve.put(socket, goldToRetrieve.get(socket) + 2);
                } else if (player.getImps() - blockImps > 0
                        && !availableTunnels.isEmpty()) {
                    broadcastAssertImpChanged(-1, socketToId.get(socket));
                    socketToUsedImps.put(socket, socketToUsedImps.get(socket) + 1);
                    goldToRetrieve.put(socket, goldToRetrieve.get(socket) + 1);
                }
            }
            case 2 -> {
                if (player.getImps() - blockImps > 2
                        && availableTunnels.size() > 2) {
                    broadcastAssertImpChanged(-3, socketToId.get(socket));
                    socketToUsedImps.put(socket, socketToUsedImps.get(socket) + 3);
                    goldToRetrieve.put(socket, goldToRetrieve.get(socket) + 3);
                } else if (player.getImps() - blockImps > 1
                        && availableTunnels.size() > 1) {
                    broadcastAssertImpChanged(-2, socketToId.get(socket));
                    socketToUsedImps.put(socket, socketToUsedImps.get(socket) + 2);
                    goldToRetrieve.put(socket, goldToRetrieve.get(socket) + 2);
                } else if (player.getImps() - blockImps > 0
                        && !availableTunnels.isEmpty()) {
                    broadcastAssertImpChanged(-1, socketToId.get(socket));
                    socketToUsedImps.put(socket, socketToUsedImps.get(socket) + 1);
                    goldToRetrieve.put(socket, goldToRetrieve.get(socket) + 1);
                }
            }
            default -> {
            }

        }


    }

    protected void evaluateTrap(final int socket, final Number spell, final int slot,
                                final int trapId)
            throws TimeoutException {

        final Player player = socketToPlayer.get(socket);
        checkSpell(socket, spell);
        switch (slot) {
            case 1 -> {
                if (player.getGold() > 0) {
                    broadcastAssertGoldChanged(-1, socketToId.get(socket));
                    broadcastAssertTrapAcquired(socketToId.get(socket), trapId);
                    player.changeGold(-1);
                    player.addTrap(model.drawTrap());
                }
            }
            case 2 -> {
                broadcastAssertTrapAcquired(socketToId.get(socket), trapId);
                player.addTrap(model.drawTrap());
            }
            default -> {
            }
        }
    }

    //just for 2 players
    //does not send select monster
    //I only subtract the food of the monster

    protected void evaluateMonster(final boolean already, final int socket, final Number spell,
                                   final int monsterId,
                                   final boolean succeed)
            throws TimeoutException {
        final Player player = socketToPlayer.get(socket);
        checkSpell(socket, spell);

        if (!already) {
            this.assertSelectMonster(socket);
            this.assertActNow(socket);
        }

        this.sendHireMonster(socket, monsterId);

        if (!succeed) {
            this.assertActionFailed(socket);
            this.assertActNow(socket);
            this.sendEndTurn(socket);
        } else {
            final Monster monster = model.getAvailableMonster(monsterId).get();
            if (monster.getHunger() > 0) {
                broadcastAssertFoodChanged(-monster.getHunger(), socketToId.get(socket));
                player.changeFood(-monster.getHunger());
            }
            if (monster.getEvilness() > 0) {
                broadcastAssertEvilnessChanged(monster.getEvilness(), socketToId.get(socket));
                player.changeEvilness(monster.getEvilness());
            }

            broadcastAssertMonsterHired(monster.getId(), socketToId.get(socket));
        }

    }

    //does not send gold change or place room

    protected void evaluateRoom(final int socket, final Number spell, final int x, final int y,
                                final Room room, final boolean succeed, final boolean sendPlaceRoom)
            throws TimeoutException {
        final Player player = socketToPlayer.get(socket);
        checkSpell(socket, spell);

        if (player.getGold() > 0 && sendPlaceRoom) {
            broadcastAssertGoldChanged(-1, socketToId.get(socket));
            player.changeGold(-1);
            assertPlaceRoom(socket);
        } else {
            this.assertActionFailed(socket);
        }
        this.assertActNow(socket);
        this.sendBuildRoom(socket, x, y, room.getId());
        if (!succeed) {

            this.assertActionFailed(socket);
            this.assertActNow(socket);
        } else {

            broadCastRoomBuilt(socketToId.get(socket), x, y, room.getId());
            final Tunnel tunnel =
                    player.getDungeon().getGraph().getTunnel(new Coordinate(x, y)).get();
            tunnel.buildRoom(room);
            model.removeAvailableRoom(room);

        }

    }

    protected void endBiddingRound(final boolean twoPlayers)
            throws TimeoutException {

        for (final Player player : model.getPlayers()) {

            for (final BidType type : player.getLockedTypes()) {
                broadCastBidRetrieved(type, player.getId());
            }
            // Lock or return currently used BitTypes.
            player.unlockBidTypes();
            broadCastBidRetrieved(player.getPlacedBidTypes().get(0), player.getId());
            player.lockBidTypes();
            player.clearBidTypes();
            //remove all blockedOptions by spells
            player.removeAllBlockOptionBySpell();
        }


        for (final Map.Entry<Integer, Integer> entry : socketToUsedImps.entrySet()) {
            final int imps = entry.getValue();
            if (imps > 0) {
                broadcastAssertImpChanged(imps, socketToId.get(entry.getKey()));
            }
            final int gold = goldToRetrieve.get(entry.getKey());
            if (gold > 0) {
                broadcastAssertGoldChanged(gold, socketToId.get(entry.getKey()));
                socketToPlayer.get(entry.getKey()).changeGold(gold);

            }

        }
        socketToUsedImps.put(1, 0);
        goldToRetrieve.put(1, 0);
        if (twoPlayers) {
            socketToUsedImps.put(2, 0);
            goldToRetrieve.put(2, 0);
        }


    }

    protected void roomProduction(final int socket, final int impsForActivation, final int food,
                                  final int gold,
                                  final int impsReceived, final int niceness)
            throws TimeoutException {

        final Player player = socketToPlayer.get(socket);
        broadcastAssertImpChanged(impsForActivation, player.getId());
        player.changeImps(impsForActivation);
        if (food > 0) {
            broadcastAssertFoodChanged(food, socketToId.get(socket));
            player.changeFood(food);
        }
        if (niceness > 0) {
            broadcastAssertEvilnessChanged(-niceness, socketToId.get(socket));
            player.changeEvilness(-niceness);
        }
        if (gold > 0) {
            broadcastAssertGoldChanged(gold, socketToId.get(socket));
            player.changeGold(gold);
        }
        if (impsReceived > 0) {
            broadcastAssertImpsChanged(impsReceived, socketToId.get(socket));
            player.changeImps(impsReceived);
        }


    }

    protected void spreadAdventurer() throws TimeoutException {

        model.getPlayers().stream()
                .sorted(Comparator.comparing(Player::getEvilness).thenComparing(Player::getId))
                .forEach(player -> {
                    final Adventurer adventurer = model.popAdventurer();
                    player.getDungeon().addAdventurer(adventurer);
                    try {
                        broadCastAdventurerArrived(adventurer.getId(), player.getId());
                    } catch (TimeoutException e) {
                        Logger.getLogger("wrong");
                    }
                });
    }

    protected void placingTrap(final int socket, final int trapId, final boolean endTurn,
                               final boolean isRoom)
            throws TimeoutException {
        sendTrap(socket, trapId);
        final Player player = socketToPlayer.get(socket);
        if (isRoom) {
            if (player.getGold() > 1) {
                broadcastAssertGoldChanged(-1, player.getId());
                player.changeGold(-1);
            } else {
                assertActionFailed(1);
                assertActNow(1);
                return;
            }
        }

        broadcastAssertTrapPlaced(player.getId(), trapId);
        assertActNow(socket);
        if (endTurn) {
            sendEndTurn(socket);
        }
    }

    protected void placingMonster(final int socket, final int monsterId, final Number targeted,
                                  final boolean endTurn) throws TimeoutException {
        final Player player = socketToPlayer.get(socket);
        if (targeted != null) {
            sendMonsterTargeted(socket, monsterId, targeted.intValue());
        } else {
            sendMonster(socket, monsterId);
        }
        broadcastAssertMonsterPlaced(player.getId(), monsterId);
        assertActNow(socket);
        if (endTurn) {
            sendEndTurn(socket);
        }

    }

    protected void castResourceSpell(final int socket, final int spellId, final int food,
                                     final int gold,
                                     final boolean counterPossible, final boolean counter)
            throws TimeoutException {
        final Player player = socketToPlayer.get(socket);
        broadCastSpellCast(player.getId(), spellId);
        if (counter(socket, counter, counterPossible)) {
            return;
        }
        if (food > 0) {
            final int reduced = player.reduceFood(food);
            if (reduced > 0) {
                broadcastAssertFoodChanged(-reduced, player.getId());
            }
        }
        if (gold > 0) {
            final int reduced = player.reduceGold(gold);
            if (reduced > 0) {
                broadcastAssertGoldChanged(-reduced, player.getId());
            }
        }

    }

    protected void castBuff(final int socket, final int spellId, final int healthPoints,
                            final int healValue,
                            final int defuse, final boolean counterPossible, final boolean counter)
            throws TimeoutException {
        final Player player = socketToPlayer.get(socket);
        broadCastSpellCast(player.getId(), spellId);
        if (counter(socket, counter, counterPossible)) {
            return;
        }
        final List<Adventurer> adventurers = player.getDungeon().getAllAdventurers();
        for (final Adventurer adventurer : adventurers) {
            adventurer.increaseHealthPoints(healthPoints);
        }

        if (healValue > 0) {
            adventurers.stream().filter(adventurer -> adventurer.getMaxHealValue() > 0)
                    .forEach(ad -> ad.increaseHealValue(healValue));
        }
        if (defuse > 0) {
            adventurers.stream().filter(adventurer -> adventurer.getMaxDefuseValue() > 0)
                    .forEach(ad -> ad.increaseDefuseValue(defuse));
        }

    }

    protected void castRoomSpell(final int socket, final int spellId, final int round,
                                 final boolean counterPossible,
                                 final boolean counter) throws TimeoutException {
        final Player player = socketToPlayer.get(socket);
        broadCastSpellCast(player.getId(), spellId);
        if (counter(socket, counter, counterPossible)) {
            return;
        }
        if (model.getYear() < model.getMaxYear()
                && !socketToPlayer.get(socket).getNoActivatingSeasonList()
                .contains(round)) {

            socketToPlayer.get(socket).getNoActivatingSeasonList().add(model.getRound());
            broadCastRoomBlocked(socketToPlayer.get(socket).getId(), round);

        }


    }


    protected void biddingSpell(final int socket, final int round, final boolean lastYear,
                                final int spellId,
                                final boolean counterPossible,
                                final boolean counter, final BidType bidType)
            throws TimeoutException {
        final Player player = socketToPlayer.get(socket);
        broadCastSpellCast(player.getId(), spellId);
        if (counter(socket, counter, counterPossible)) {
            return;
        }
        if (lastYear) {
            return;
        }
        broadCastBidTypeBlocked(player.getId(), bidType, round);
        player.blockOptionBySpell(bidType, round);

    }


    protected void structureSpell(final int socket, final int spellId,
                                  final boolean counterPossible,
                                  final boolean counter, final StructureEffect structureEffect)
            throws TimeoutException {

        final Player player = socketToPlayer.get(socket);
        broadCastSpellCast(player.getId(), spellId);
        if (counter(socket, counter, counterPossible)) {
            return;
        }
        final Optional<Tunnel> battle = player.getDungeon().getBattleGround();
        if (structureEffect == StructureEffect.DESTROY) {
            final Tunnel tunnel = battle.get();
            final Optional<Tunnel> tunnelWithClosestRoom =
                    player.getDungeon().getGraph().closestRoomToAdventurer(tunnel);
            final Tunnel roomTunnel = tunnelWithClosestRoom.get();
            broadCastRoomDestroyed(player.getId(), roomTunnel.getRoom().get().getId());
            roomTunnel.setRoom(null);
        } else {
            if (player.getDungeon().getBattleGround().isPresent()) {
                player.getDungeon().conquer();
            }
        }

    }

    protected boolean counter(final int socket, final boolean counter,
                              final boolean counterPossible)
            throws TimeoutException {
        if (counterPossible) {
            assertCounterSpell(socket);
            assertActNow(socket);

        } else {
            return false;
        }
        if (counter) {
            sendCastCounterSpell(socket);
            broadCastCounterSpellCast(socketToPlayer.get(socket).getId());
            return true;
        }
        sendEndTurn(socket);
        return false;
    }

    protected void dealFatigue(final int socket, final int fatigue) throws TimeoutException {
        final Player player = socketToPlayer.get(socket);
        final List<Adventurer> adventurers = player.getDungeon().getAllAdventurers();
        for (final Adventurer ad : adventurers) {
            if (ad.getCurrentHealthPoints() - fatigue <= 0) {
                broadcastAssertAdventurerImprisoned(ad.getId());
                ad.damage(ad.getCurrentHealthPoints());
                player.getDungeon().imprisonAdventurer(ad);
            } else {
                broadcastAssertAdventurerDamaged(ad.getId(), fatigue);
                ad.damage(fatigue);
            }
        }


    }

    protected void healing(final int socket)
            throws TimeoutException {

        final List<Adventurer> adventurers =
                socketToPlayer.get(socket).getDungeon().getAllAdventurers();
        for (final Adventurer healer : adventurers) {

            int healValue = healer.getHealValue();
            for (final Adventurer target : adventurers) {
                final int healed = target.heal(healValue);
                healValue -= healed;
                if (healed > 0) {
                    broadcastAssertAdventurerHealed(healed, healer.getId(), target.getId());
                }
            }
        }

        for (final Adventurer ad : adventurers) {
            ad.restoreOriginalValues();
        }

    }

    protected void applyDamage(final int socket, final Adventurer adventurer, final int damage)
            throws TimeoutException {
        final int effectiveDamage = adventurer.damage(damage);
        if (effectiveDamage > 0) {
            if (adventurer.isDefeated()) {
                socketToPlayer.get(socket).getDungeon().imprisonAdventurer(adventurer);
                broadcastAssertAdventurerImprisoned(adventurer.getId());

            } else {
                broadcastAssertAdventurerDamaged(adventurer.getId(), damage);
            }
        }

    }

    protected void conquer(final int socket, final Dungeon dungeon, final int x, final int y)
            throws TimeoutException {
        final Player player = socketToPlayer.get(socket);
        broadcastAssertTunnelConquered(dungeon.getAllAdventurers().get(0).getId(), x, y);
        dungeon.conquer();
        broadcastAssertEvilnessChanged(-1, player.getId());
        player.changeEvilness(-1);
    }

    protected void adventurerFlee(final int socket) throws TimeoutException {
        final Player player = socketToPlayer.get(socket);
        final Dungeon dungeon = player.getDungeon();
        if (dungeon.getNumImprisonedAdventurers() > 0) {
            broadCastAdventurerFlee(dungeon.freeAdventurer().getId());
            if (player.getEvilness() > Player.MIN_EVILNESS) {
                player.changeEvilness(-1);
                broadcastAssertEvilnessChanged(player.getId(), -1);
            }
        }
    }


}





