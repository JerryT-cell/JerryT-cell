package de.unisaarland.cs.se.selab.systemtest.scenario2;

import de.unisaarland.cs.se.selab.comm.BidType;
import de.unisaarland.cs.se.selab.comm.TimeoutException;
import de.unisaarland.cs.se.selab.model.dungeon.Room;
import de.unisaarland.cs.se.selab.systemtest.SystemTestsTemplate;
import de.unisaarland.cs.se.selab.systemtest.api.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class BiddingScenario2 extends SystemTestsTemplate {
    public BiddingScenario2(final Class<?> subclass) {
        super(subclass, false, Set.of(1), "config2.json", 58_498);
    }

    @Override
    protected String createConfig() {
        return Utils.loadResource(BiddingScenario2.class, "config2.json");
    }

    @Override
    protected long createSeed() {
        return 58_498;
    }

    @Override
    protected Set<Integer> createSockets() {
        return Set.of(1);
    }

    @Override
    protected void run() throws TimeoutException, AssertionError {
        final List<BidType> bids =
                new ArrayList<>(Arrays.asList(BidType.FOOD, BidType.TUNNEL, BidType.NICENESS,
                        BidType.MONSTER, BidType.GOLD, BidType.FOOD,
                        BidType.ROOM, BidType.TUNNEL, BidType.IMPS,
                        BidType.FOOD, BidType.MONSTER, BidType.GOLD));

        bidding(true, false, false, bids, null, null);

        broadcastAssertNextRound(1);
        this.assertSetBattleGround(1);
        this.assertActNow(1);
        this.sendLeave(1);

    }


    @Override
    protected void evaluateRound1() throws TimeoutException {
        //evaluate food
        evaluateFood(1, 1, null);

        //evaluate niceness

        broadCastSpellUnlock(0, 12);
        final Number spell2 = 19;
        evaluateNiceness(1, 1, 0, spell2);
        //deviationForTest

        //evaluate tunnel
        final Number spell3 = 21;
        evaluateTunnel(false, 1, spell3, 0, 1, false);
        evaluateTunnel(true, 1, null, 1, 1, true);


        endBiddingRound(false);
        spreadAdventurer();
        model.seasonalCleanup();
    }

    @Override
    protected void evaluateRound2() throws TimeoutException {
        //food

        broadCastSpellUnlock(0, 14);
        final Number spell2 = 4;
        evaluateFood(1, 1, spell2);
        //gold
        evaluateGold(1, null, 1);


        //evaluate monster
        evaluateMonster(false, 1, null, 5, true);


        endBiddingRound(false);
        spreadAdventurer();
        model.seasonalCleanup();


    }

    @Override
    protected void evaluateRound3() throws TimeoutException {
        //tunnel evaluation
        this.assertDigTunnel(1);
        this.assertActNow(1);
        this.sendCastCounterSpell(1);
        this.assertActionFailed(1);
        evaluateTunnel(true, 1, null, 2, 1, false);
        evaluateTunnel(true, 1, null, 2, 2, true);

        //imps
        final Number number = 3;
        evaluateImps(1, 1, number);

        //rooms
        final Optional<Room> optRoom = model.getAvailableRoom(7);
        final Room room = optRoom.get();
        evaluateRoom(1, null, 0, 0, room, true, true);


        endBiddingRound(false);
        spreadAdventurer();
        model.seasonalCleanup();


    }

    @Override
    protected void evaluateRound4() throws TimeoutException {
        //evaluate food
        evaluateFood(1, 1, null);

        //gold
        evaluateGold(1, null, 1);

        //monster
        evaluateMonster(false, 1, null, 1, true);


        endBiddingRound(false);
        model.seasonalCleanup();


    }

    @Override
    protected void evaluateRound1Next() throws TimeoutException {

        evaluateNiceness(1, 1, 0, null);
        evaluateImps(1, 1, null);


        final Room room = model.getAvailableRoom(13).get();
        final Number spellNumber = 10;
        evaluateRoom(1, spellNumber, 2, 2, room, true, true);


        endBiddingRound(false);
        spreadAdventurer();
        model.seasonalCleanup();

    }

    @Override
    protected void evaluateRound2Next() throws TimeoutException {
        evaluateFood(1, 1, null);

        evaluateTunnel(false, 1, null, 2, 3, false);
        evaluateTunnel(true, 1, null, 3, 3, true);

        //deviation
        this.assertSelectMonster(1);
        this.assertActNow(1);
        this.sendActivateRoom(1, 13);
        this.assertActionFailed(1);
        this.assertActNow(1);
        evaluateMonster(true, 1, null, 14, true);


        endBiddingRound(false);
        spreadAdventurer();
        model.seasonalCleanup();


    }

    @Override
    protected void evaluateRound3Next() throws TimeoutException {
        final Number number = 22;
        evaluateGold(1, number, 1);

        evaluateImps(1, 1, null);
        //deviation
        this.assertSelectMonster(1);
        this.assertActNow(1);
        this.sendActivateRoom(1, 13);
        socketToRoomImps.put(1, socketToUsedImps.get(1) + 3);
        broadcastAssertImpChanged(-3, 0);
        broadcastRoomActivated(0, 13);
        this.assertActNow(1);
        evaluateMonster(true, 1, null, 17, true);


        endBiddingRound(false);
        roomProduction(1, 3, 0, 0, 1, 0);
        spreadAdventurer();
        model.seasonalCleanup();


    }

    @Override
    protected void evaluateRound4Next() throws TimeoutException {

        evaluateFood(1, 1, null);

        evaluateTunnel(false, 1, null, 3, 4, true);
        evaluateTunnel(true, 1, null, 4, 4, true);
        final Number number = 2;
        evaluateTrap(1, number, 1, 17);


        endBiddingRound(false);
        model.seasonalCleanup();


    }

    protected void biddingRound(final boolean isFirstBidding) throws TimeoutException {
        final List<BidType> bids = new ArrayList<>();
        final Map<Integer, BidType> blockBids = new LinkedHashMap<>();
        if (isFirstBidding) {

            bids.addAll(Arrays.asList(BidType.FOOD, BidType.TUNNEL, BidType.NICENESS,
                    BidType.MONSTER, BidType.GOLD, BidType.FOOD,
                    BidType.ROOM, BidType.TUNNEL, BidType.IMPS,
                    BidType.FOOD, BidType.MONSTER, BidType.GOLD));
            bidding(true, false, false, bids, null, null);


        } else {
            bids.addAll(Arrays.asList(BidType.IMPS, BidType.NICENESS, BidType.ROOM,
                    BidType.MONSTER, BidType.TUNNEL, BidType.FOOD,
                    BidType.GOLD, BidType.MONSTER, BidType.IMPS,
                    BidType.TUNNEL, BidType.FOOD, BidType.TRAP));
            blockBids.put(1, BidType.FOOD);
            blockBids.put(3, BidType.TRAP);
            bidding(false, false, false, bids, null, blockBids);

        }


    }
}












