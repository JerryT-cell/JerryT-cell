package de.unisaarland.cs.se.selab.systemtest.scenario1;

import de.unisaarland.cs.se.selab.comm.BidType;
import de.unisaarland.cs.se.selab.comm.TimeoutException;
import de.unisaarland.cs.se.selab.model.dungeon.Room;
import de.unisaarland.cs.se.selab.systemtest.SystemTestsTemplate;
import de.unisaarland.cs.se.selab.systemtest.api.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * - (1)first test
 * - (2)tests the resource spell with enough and insufficient amount of food and gold in player
 * - (3)tests digging tunnel
 * - (4)tests room spell with activation in the same season
 * - (5)tests room blocking appearance in the last year
 * - (6)test linus appearance in the last season
 * - (7)test already conquered room
 */

public class Scenario1Test extends SystemTestsTemplate {

    public Scenario1Test(final Class<?> subclass) {
        super(subclass, false, Set.of(1, 2), "config1.json", 63_613);


    }

    @Override
    protected String createConfig() {
        return Utils.loadResource(Scenario1Test.class, "config1.json");
    }

    @Override
    protected long createSeed() {
        return 63_613;
    }

    @Override
    protected Set<Integer> createSockets() {
        return Set.of(1, 2);
    }

    @Override
    protected void run() throws TimeoutException, AssertionError {
        final List<BidType> bids1 =
                new ArrayList<>(Arrays.asList(BidType.FOOD, BidType.MONSTER, BidType.GOLD,
                        BidType.IMPS, BidType.TUNNEL, BidType.NICENESS,
                        BidType.GOLD, BidType.IMPS, BidType.FOOD,
                        BidType.NICENESS, BidType.MONSTER, BidType.ROOM));
        final List<BidType> bids2 =
                new ArrayList<>(Arrays.asList(BidType.TRAP, BidType.FOOD, BidType.IMPS,
                        BidType.TUNNEL, BidType.TRAP, BidType.NICENESS,
                        BidType.GOLD, BidType.MONSTER, BidType.FOOD,
                        BidType.ROOM, BidType.NICENESS, BidType.TRAP));

        bidding(true, false, true, bids1, bids2, null);

        broadcastAssertNextRound(1);
        this.assertSetBattleGround(1);
        this.assertActNow(1);
        this.sendLeave(2);
        this.assertLeft(1, 1);
        this.sendLeave(1);

    }

    @Override
    protected void evaluateRound1()
            throws TimeoutException {

        final Number spellNumber = 3;
        evaluateFood(1, 1, spellNumber);

        evaluateFood(2, 2, null);


        //evaluate Gold
        //reduce temporal imps by 1 <- to retrieve
        final Number spellNumber3 = 0;
        evaluateGold(1, spellNumber3, 1); //I send one imp because there is just one tunnel


        //Imps player2
        //reduce food of player1 by 1
        evaluateImps(2, 1, null);

        //evaluate Trap
        //reduce gold of player 1 by 1
        evaluateTrap(2, null, 1, 7);


        //evaluate monster
        //reduce evilness of player 0 by 0 and food by 3
        final Number spellNumber2 = 7;
        evaluateMonster(false, 1, spellNumber2, 4, true);



        endBiddingRound(true);
        spreadAdventurer();
        model.seasonalCleanup();

    }

    @Override
    protected void evaluateRound2()
            throws TimeoutException {
        //player 1 evaluation
        final Number spellNumber3 = 5;
        evaluateNiceness(2, 1, 0, spellNumber3);
        evaluateNiceness(1, 2, 0, null);

        //player 2
        final Number spellNumber1 = 1;
        evaluateTunnel(false, 2, spellNumber1, 0, 1, false);
        evaluateTunnel(true, 2, null, 1, 1, true);
        //player 1
        evaluateTunnel(false, 1, null, 0, 1, false);
        evaluateTunnel(true, 1, null, 1, 1, true);
        evaluateTunnel(true, 1, null, 2, 1, true);
        //player 1 imps
        evaluateImps(1, 1, null);

        //player 2
        final Number spellNumber2 = 8;
        evaluateTrap(2, spellNumber2, 1, 15);



        endBiddingRound(true);
        spreadAdventurer();
        model.seasonalCleanup();

    }

    @Override
    protected void evaluateRound3()
            throws TimeoutException {
        //player 0
        evaluateFood(1, 1, null);
        //player 1
        final Number spellNumber = 2;
        evaluateFood(2, 2, spellNumber);

        final Number spellNumber1 = 6;
        evaluateGold(1, spellNumber1, 1);

        evaluateGold(2, null, 2);

        final Number spellNumber2 = 4;
        evaluateImps(1, 1, spellNumber2);

        evaluateMonster(false, 2, null, 3, true);



        endBiddingRound(true);
        spreadAdventurer();
        model.seasonalCleanup();
    }

    @Override
    protected void evaluateRound4()
            throws TimeoutException {

        broadCastSpellUnlock(0, 9);
        final Number number = 11;
        evaluateNiceness(1, 1, 0, number);
        evaluateNiceness(2, 2, 0, null);

        evaluateTrap(2, null, 1, 8);
        final Number number2 = 10;
        evaluateMonster(false, 1, number2, 1, true);

        final Room room = model.getAvailableRoom(4).get();
        evaluateRoom(2, null, 1, 1, room, true, true);
        final Room room2 = model.getAvailableRoom(5).get();

        evaluateRoom(1, null, 1, 1, room2, false, true);
        sendEndTurn(1);


        endBiddingRound(true);
        if (model.getRound() != 4) {
            spreadAdventurer();
        }
        model.seasonalCleanup();
    }


    protected void runStartAndBidding() throws TimeoutException {
        final List<BidType> bids1 =
                new ArrayList<>(Arrays.asList(BidType.FOOD, BidType.MONSTER, BidType.GOLD,
                        BidType.IMPS, BidType.TUNNEL, BidType.NICENESS,
                        BidType.GOLD, BidType.IMPS, BidType.FOOD,
                        BidType.NICENESS, BidType.MONSTER, BidType.ROOM));
        final List<BidType> bids2 =
                new ArrayList<>(Arrays.asList(BidType.TRAP, BidType.FOOD, BidType.IMPS,
                        BidType.TUNNEL, BidType.TRAP, BidType.NICENESS,
                        BidType.GOLD, BidType.MONSTER, BidType.FOOD,
                        BidType.ROOM, BidType.NICENESS, BidType.TRAP));

        bidding(true, false, true, bids1, bids2, null);


    }

}
