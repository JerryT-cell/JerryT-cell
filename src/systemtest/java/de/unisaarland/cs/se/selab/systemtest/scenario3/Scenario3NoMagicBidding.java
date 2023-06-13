package de.unisaarland.cs.se.selab.systemtest.scenario3;

import de.unisaarland.cs.se.selab.comm.BidType;
import de.unisaarland.cs.se.selab.comm.TimeoutException;
import de.unisaarland.cs.se.selab.model.dungeon.Room;
import de.unisaarland.cs.se.selab.systemtest.SystemTestsTemplate;
import de.unisaarland.cs.se.selab.systemtest.api.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Scenario3NoMagicBidding extends SystemTestsTemplate {

    public Scenario3NoMagicBidding(final Class<?> subclass) {
        super(subclass, false, Set.of(1), "config3.json", 15);
    }


    @Override
    protected String createConfig() {
        return Utils.loadResource(Scenario3NoMagicBidding.class, "config3.json");
    }

    @Override
    protected long createSeed() {
        return 15;
    }

    @Override
    protected Set<Integer> createSockets() {
        return Set.of(1);
    }

    @Override
    protected void run() throws TimeoutException, AssertionError {
        final List<BidType> bids =
                new ArrayList<>(Arrays.asList(BidType.FOOD, BidType.TUNNEL, BidType.NICENESS,
                        BidType.ROOM, BidType.GOLD, BidType.FOOD,
                        BidType.MONSTER, BidType.TUNNEL, BidType.IMPS,
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
        final Number spell1 = 8;
        evaluateFood(1, 1, spell1);

        //evaluate niceness
        final Number spell2 = 2;
        evaluateNiceness(1, 1, 0, spell2);


        //evaluate tunnel

        evaluateTunnel(false, 1, null, 0, 1, false);
        evaluateTunnel(true, 1, null, 1, 1, true);


        endBiddingRound(false);
        spreadAdventurer();
        model.seasonalCleanup();
    }

    @Override
    protected void evaluateRound2() throws TimeoutException {
        //food

        evaluateFood(1, 1, null);
        //gold
        evaluateGold(1, null, 1);

        //rooms
        final Number spell = 0;
        final Optional<Room> optRoom = model.getAvailableRoom(1);
        final Room room = optRoom.get();
        evaluateRoom(1, spell, 0, 0, room, true, true);


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

        evaluateImps(1, 1, null);

        final Number spellNumber = 3;
        //evaluate monster
        evaluateMonster(false, 1, spellNumber, 4, true);


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
        evaluateMonster(false, 1, null, 8, true);


        endBiddingRound(false);
        model.seasonalCleanup();


    }

    protected void bidding() throws TimeoutException {


        final List<BidType> bids =
                new ArrayList<>(Arrays.asList(BidType.FOOD, BidType.TUNNEL, BidType.NICENESS,
                        BidType.ROOM, BidType.GOLD, BidType.FOOD,
                        BidType.MONSTER, BidType.TUNNEL, BidType.IMPS,
                        BidType.FOOD, BidType.MONSTER, BidType.GOLD));


        bidding(true, false, false, bids, null, null);


    }
}
