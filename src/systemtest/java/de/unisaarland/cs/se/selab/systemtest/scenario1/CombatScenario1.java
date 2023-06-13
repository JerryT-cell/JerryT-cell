package de.unisaarland.cs.se.selab.systemtest.scenario1;

import de.unisaarland.cs.se.selab.comm.TimeoutException;
import de.unisaarland.cs.se.selab.model.Adventurer;
import de.unisaarland.cs.se.selab.model.Player;
import de.unisaarland.cs.se.selab.model.dungeon.Coordinate;
import de.unisaarland.cs.se.selab.model.dungeon.Dungeon;
import de.unisaarland.cs.se.selab.model.dungeon.Tunnel;
import de.unisaarland.cs.se.selab.spells.StructureEffect;
import de.unisaarland.cs.se.selab.systemtest.api.Utils;
import java.util.ArrayList;
import java.util.Arrays;


public class CombatScenario1 extends Scenario1Test {


    public CombatScenario1(final Class<?> subclass) {
        super(subclass);
    }

    @Override
    protected String createConfig() {
        return Utils.loadResource(CombatScenario1.class, "config1.json");
    }


    @Override
    protected void run() throws TimeoutException, AssertionError {
        runStartAndBidding();
        player1Grounds = new ArrayList<>(Arrays.asList(
                new Coordinate(0, 0), new Coordinate(0, 1), new Coordinate(1, 1),
                new Coordinate(2, 1)
        ));
        player2Grounds = new ArrayList<>(Arrays.asList(
                new Coordinate(0, 0), new Coordinate(0, 1), new Coordinate(1, 1)
        ));
        combat(false);


        broadCastGameEnd(0, 16);
    }

    @Override
    protected void round1Player1(final Player player) throws TimeoutException {
        final Dungeon dungeon = player.getDungeon();
        final Tunnel battleGround = battleGroundDefendYourself(1, 0, true);

        //monster placing
        final Number numberTarget = 1;
        placingMonster(1, 4, numberTarget, true);

        //deal with spells
        //spell 1
        castResourceSpell(1, 3, 9, 9, true, false);
        //spell 2
        castResourceSpell(1, 0, 0, 1, true, false);
        //spell3

        castBuff(1, 7, 0, 2, 0, true, false);
        //damage
        broadcastAssertAdventurerImprisoned(0);
        final Adventurer adventurer = player.getDungeon().getAllAdventurers().get(0);
        adventurer.damage(3);
        player.getDungeon().imprisonAdventurer(adventurer);

        //linus
        broadCastLinusArrived(player.getId());
        broadCastMonsterRemoved(player.getId(), 4);

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

        //deal spell
        castRoomSpell(1, 6, 3, true, true);
        castRoomSpell(1, 4, 3, true, false);

        //damage
        applyDamage(1, dungeon.getAllAdventurers().get(0), 1);
        applyDamage(1, dungeon.getAllAdventurers().get(1), 1);
        conquer(1, dungeon, battleGround.getCoordinate().posX(),
                battleGround.getCoordinate().posY());
        //
        healing(1);

    }

    @Override
    protected void round4Player1(final Player player) throws TimeoutException {
        final Dungeon dungeon = player.getDungeon();
        final Tunnel battleGround = battleGroundDefendYourself(1, 3, true);

        //deviation
        sendCastCounterSpell(1);
        assertActionFailed(1);
        assertActNow(1);

        //place monster
        sendEndTurn(1);

        //spells
        biddingSpell(1, 4, true, 9, true, false, null);
        biddingSpell(1, 4, true, 11, true, true, null);
        biddingSpell(1, 4, true, 10, false, false, null);

        //damage
        applyDamage(1, dungeon.getAllAdventurers().get(0), 1);
        applyDamage(1, dungeon.getAllAdventurers().get(1), 1);
        conquer(1, dungeon, battleGround.getCoordinate().posX(),
                battleGround.getCoordinate().posY());
        //
        healing(1);
    }

    @Override
    protected void round1Player2(final Player player) throws TimeoutException {
        final Dungeon dungeon = player.getDungeon();
        final Tunnel battleGround = battleGroundDefendYourself(2, 0, false);
        //placing defenders
        placingMonster(2, 3, null, false);
        placingTrap(2, 8, true, false);

        //no spell -> damage by trap
        //applyDamage(2, dungeon.getAllAdventurers().get(0), 2);
        //damage by monster
        applyDamage(2, dungeon.getAllAdventurers().get(0), 3);
        //fatigue
        applyDamage(2, dungeon.getAllAdventurers().get(0), 2);
        applyDamage(2, dungeon.getAllAdventurers().get(1), 2);
        applyDamage(2, dungeon.getAllAdventurers().get(2), 2);
        //conquer
        conquer(2, dungeon, battleGround.getCoordinate().posX(),
                battleGround.getCoordinate().posY());

        //healing
        healing(2);

    }

    @Override
    protected void round2Player2(final Player player) throws TimeoutException {
        final Dungeon dungeon = player.getDungeon();
        final Tunnel battleGround = battleGroundDefendYourself(2, 1, false);
        //place trap
        placingTrap(2, 7, true, false);

        //dealSpell
        structureSpell(2, 5, true, false, StructureEffect.DESTROY);
        structureSpell(2, 1, true, false, StructureEffect.CONQUER);
        structureSpell(2, 8, true, false, StructureEffect.CONQUER);


        //conquer
        conquer(2, dungeon, battleGround.getCoordinate().posX(),
                battleGround.getCoordinate().posY());

    }

    @Override
    protected void round3Player2(final Player player) throws TimeoutException {
        //room
        final Dungeon dungeon = player.getDungeon();
        final Tunnel battleGround = battleGroundDefendYourself(2, 2, false);
        //place trap
        placingTrap(2, 15, true, false);
        //spell
        castResourceSpell(2, 2, 0, 1, true, true);

        //no spell -> damage by trap
        applyDamage(2, dungeon.getAllAdventurers().get(0), 2);
        //damage by fatigue
        applyDamage(2, dungeon.getAllAdventurers().get(0), 2);
        applyDamage(2, dungeon.getAllAdventurers().get(1), 2);
        applyDamage(2, dungeon.getAllAdventurers().get(2), 2);
        //conquer
        conquer(2, dungeon, battleGround.getCoordinate().posX(),
                battleGround.getCoordinate().posY());
        //healing
        healing(2);

    }

    @Override
    protected void round4Player2(final Player player) throws TimeoutException {
        //no battleGround adventurer must flee
        adventurerFlee(2);
    }


}
