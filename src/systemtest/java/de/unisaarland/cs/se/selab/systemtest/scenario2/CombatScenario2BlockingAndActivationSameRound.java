package de.unisaarland.cs.se.selab.systemtest.scenario2;

import de.unisaarland.cs.se.selab.comm.BidType;
import de.unisaarland.cs.se.selab.comm.TimeoutException;
import de.unisaarland.cs.se.selab.model.Adventurer;
import de.unisaarland.cs.se.selab.model.Player;
import de.unisaarland.cs.se.selab.model.dungeon.Coordinate;
import de.unisaarland.cs.se.selab.model.dungeon.Dungeon;
import de.unisaarland.cs.se.selab.model.dungeon.Tunnel;
import de.unisaarland.cs.se.selab.systemtest.api.Utils;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * This class closes up the second scenario of bidding with the 2 combat phases of the game
 * It tests :
 * - If blocking bids is possible (In this case FOOD)
 * - If an already blocked BidType can be blocked again by the spell in the same Round
 */
public class CombatScenario2BlockingAndActivationSameRound extends BiddingScenario2 {


    public CombatScenario2BlockingAndActivationSameRound(final Class<?> subclass) {
        super(subclass);
    }

    @Override
    protected String createConfig() {
        return Utils.loadResource(CombatScenario2BlockingAndActivationSameRound.class,
                "config2.json");
    }


    @Override
    protected void run() throws TimeoutException, AssertionError {
        biddingRound(true);
        player1Grounds = new ArrayList<>(Arrays.asList(
                new Coordinate(0, 0), new Coordinate(0, 1), new Coordinate(1, 1),
                new Coordinate(2, 1)
        ));
        combat(false);

        model.hasNextYear();
        biddingRound(false);
        player1Grounds.clear();
        player1Grounds.addAll(Arrays.asList(new Coordinate(2, 2), new Coordinate(2, 3),
                new Coordinate(3, 3), new Coordinate(3, 4)));
        combat(true);
        assertGameEnd(1, 0, 36);

    }


    @Override
    protected void round1Player1(final Player player) throws TimeoutException {
        final Dungeon dungeon = player.getDungeon();
        final Tunnel battleGround = battleGroundDefendYourself(1, 0, true);
        //defenders
        final Number numberTarget = 1;
        placingMonster(1, 5, numberTarget, true);
        //spells
        biddingSpell(1, 1, false, 12, true, false, BidType.FOOD);
        // deviation ! since same bid, no casting again
        broadCastSpellCast(player.getId(), 19);
        assertCounterSpell(1);
        assertActNow(1);
        sendEndTurn(1);
        castBuff(1, 21, 0, 1, 0, true, false);
        //damage
        applyDamage(1, dungeon.getAdventurer(1).get(), 4);
        //linus
        broadCastLinusArrived(player.getId());
        broadCastMonsterRemoved(player.getId(), 5);
        //fatigue and conquer
        dealFatigue(1, 1);
        conquer(1, dungeon, battleGround.getCoordinate().posX(),
                battleGround.getCoordinate().posY());

        //HealByPriest
        healing(1);

    }

    @Override
    protected void round2Player1(final Player player) throws TimeoutException {
        final Dungeon dungeon = player.getDungeon();
        final Tunnel battleGround = battleGroundDefendYourself(1, 1, true);

        //monster placing
        placingMonster(1, 1, null, true);

        //spell
        castRoomSpell(1, 14, 2, true, false);
        castRoomSpell(1, 4, 2, true, false);

        //no spell, damage directly
        applyDamage(1, dungeon.getAdventurer(1).get(), 1);
        //fatigue and conquer
        applyDamage(1, dungeon.getAdventurer(1).get(), 1);
        applyDamage(1, dungeon.getAdventurer(2).get(), 1);
        conquer(1, dungeon, battleGround.getCoordinate().posX(),
                battleGround.getCoordinate().posY());

        //healing
        healing(1);

    }

    @Override
    protected void round3Player1(final Player player) throws TimeoutException {
        final Dungeon dungeon = player.getDungeon();
        final Tunnel battleGround = battleGroundDefendYourself(1, 2, true);

        //place monster
        sendEndTurn(1);

        //spell
        biddingSpell(1, 3, false, 3, true, false, BidType.TRAP);

        //damage
        applyDamage(1, dungeon.getAllAdventurers().get(0), 1);
        applyDamage(1, dungeon.getAllAdventurers().get(1), 1);
        conquer(1, dungeon, battleGround.getCoordinate().posY(),
                battleGround.getCoordinate().posY());
        //
        healing(1);

    }

    @Override
    protected void round4Player1(final Player player) throws TimeoutException {
        final Dungeon dungeon = player.getDungeon();
        final Tunnel battleGround = battleGroundDefendYourself(1, 3, true);

        //place monster
        sendEndTurn(1);

        //damage
        applyDamage(1, dungeon.getAllAdventurers().get(0), 1);
        applyDamage(1, dungeon.getAllAdventurers().get(1), 1);
        conquer(1, dungeon, battleGround.getCoordinate().posX(),
                battleGround.getCoordinate().posY());
        //
        healing(1);


    }


    @Override
    protected void round1Player1Next(final Player player) throws TimeoutException {
        final Dungeon dungeon = player.getDungeon();
        final Tunnel battleGround = battleGroundDefendYourself(1, 0, true);
        //defender
        placingMonster(1, 17, null, true);
        //spell
        castRoomSpell(1, 10, 1, true, true);
        //damage
        for (final Adventurer ad : dungeon.getAllAdventurers()) {
            applyDamage(1, ad, 2);
        }
        for (final Adventurer ad : dungeon.getAllAdventurers()) {
            applyDamage(1, ad, 2);
        }
        conquer(1, dungeon, battleGround.getCoordinate().posX(),
                battleGround.getCoordinate().posY());
        healing(1);


    }

    @Override
    protected void round2Player1Next(final Player player) throws TimeoutException {
        final Dungeon dungeon = player.getDungeon();
        final Tunnel battleGround = battleGroundDefendYourself(1, 1, true);
        //defender
        placingMonster(1, 14, null, true);

        applyDamage(1, dungeon.getAdventurer(1).get(), 3);


        //fatigue
        applyDamage(1, dungeon.getAdventurer(1).get(), 2);
        broadCastLinusArrived(0);
        broadCastMonsterRemoved(0, 17);
        applyDamage(1, dungeon.getAllAdventurers().get(0), 1);

        conquer(1, dungeon, battleGround.getCoordinate().posX(),
                battleGround.getCoordinate().posY());
        healing(1);


    }

    @Override
    protected void round3Player1Next(final Player player) throws TimeoutException {
        final Dungeon dungeon = player.getDungeon();
        battleGroundDefendYourself(1, 2, true);


        placingTrap(1, 17, true, false);

        castRoomSpell(1, 22, 3, true, true);

        for (final Adventurer ad : dungeon.getAllAdventurers()) {
            applyDamage(1, ad, 1);
        }

        for (final Adventurer ad : dungeon.getAllAdventurers()) {
            applyDamage(1, ad, 1);
        }


    }

    @Override
    protected void round4Player1Next(final Player player) throws TimeoutException {
        //TODO


    }


}















