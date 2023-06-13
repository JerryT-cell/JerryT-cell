package de.unisaarland.cs.se.selab.systemtest.scenario3;


import de.unisaarland.cs.se.selab.comm.TimeoutException;
import de.unisaarland.cs.se.selab.model.Player;
import de.unisaarland.cs.se.selab.model.dungeon.Coordinate;
import de.unisaarland.cs.se.selab.model.dungeon.Dungeon;
import de.unisaarland.cs.se.selab.model.dungeon.Tunnel;
import de.unisaarland.cs.se.selab.systemtest.api.Utils;
import java.util.ArrayList;
import java.util.Arrays;



public class Scenario3NoMagicCombat extends Scenario3NoMagicBidding {


    public Scenario3NoMagicCombat(final Class<?> subclass) {
        super(subclass);
    }

    @Override
    protected String createConfig() {
        return Utils.loadResource(Scenario3NoMagicBidding.class, "config3.json");
    }

    @Override
    protected void run() throws TimeoutException {
        bidding();
        player1Grounds = new ArrayList<>(Arrays.asList(
                new Coordinate(0, 0), new Coordinate(0, 1), new Coordinate(1, 1),
                new Coordinate(2, 1)
        ));
        combat(false);
        assertGameEnd(1, 0, 36);


    }

    @Override
    protected void round1Player1(final Player player) throws TimeoutException {
        final Dungeon dungeon = player.getDungeon();
        final Tunnel battleGround = battleGroundDefendYourself(1, 0, true);

        //monster placing
        final Number numberTarget = 1;
        placingMonster(1, 4, numberTarget, true);
        //damage
        applyDamage(1, dungeon.getAdventurer(1).get(), 3);
        //fatigue
        dealFatigue(1, 2);
        conquer(1, dungeon, battleGround.getCoordinate().posX(),
                battleGround.getCoordinate().posY());
        healing(1);


    }

    @Override
    protected void round2Player1(final Player player) throws TimeoutException {
        final Dungeon dungeon = player.getDungeon();
        final Tunnel battleGround = battleGroundDefendYourself(1, 1, true);

        //monster placing
        placingMonster(1, 8, null, true);

        //no spell, damage directly
        applyDamage(1, dungeon.getAdventurer(1).get(), 2);

        dealFatigue(1, 2);
        conquer(1, dungeon, battleGround.getCoordinate().posX(),
                battleGround.getCoordinate().posY());
        healing(1);


    }

    @Override
    protected void round3Player1(final Player player) throws TimeoutException {
        final Dungeon dungeon = player.getDungeon();
        final Tunnel battleGround = battleGroundDefendYourself(1, 2, true);
        //place monster
        sendEndTurn(1);

        //damage
        dealFatigue(1, 2);
        conquer(1, dungeon, battleGround.getCoordinate().posX(),
                battleGround.getCoordinate().posY());
        //
        healing(1);


    }

    @Override
    protected void round4Player1(final Player player) throws TimeoutException {
        battleGroundDefendYourself(1, 3, true);
        //place monster
        sendEndTurn(1);

        //damage
        dealFatigue(1, 2);

        //
        healing(1);
    }

}
