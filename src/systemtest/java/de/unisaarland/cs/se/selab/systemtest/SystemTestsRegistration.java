package de.unisaarland.cs.se.selab.systemtest;


import de.unisaarland.cs.se.selab.systemtest.api.SystemTestManager;
import de.unisaarland.cs.se.selab.systemtest.scenario1.CombatScenario1;
import de.unisaarland.cs.se.selab.systemtest.scenario1.Scenario1Test;
import de.unisaarland.cs.se.selab.systemtest.scenario2.BiddingScenario2;
import de.unisaarland.cs.se.selab.systemtest.scenario2.CombatScenario2BlockingAndActivationSameRound;
import de.unisaarland.cs.se.selab.systemtest.scenario3.Scenario3NoMagicBidding;
import de.unisaarland.cs.se.selab.systemtest.scenario3.Scenario3NoMagicCombat;


final class SystemTestsRegistration {

    private SystemTestsRegistration() {
        // empty
    }

    static void registerSystemTests(final SystemTestManager manager) {
        //manager.registerTest(new RegistrationTest());
        //manager.registerTest(new EmptyConfigTest());
        manager.registerTest(new Scenario1Test(Scenario1Test.class));
        manager.registerTest(new CombatScenario1(CombatScenario1.class));
        manager.registerTest(new BiddingScenario2(BiddingScenario2.class));
        manager.registerTest(new CombatScenario2BlockingAndActivationSameRound(
                CombatScenario2BlockingAndActivationSameRound.class));
        manager.registerTest(new Scenario3NoMagicBidding(Scenario3NoMagicBidding.class));
        manager.registerTest(new Scenario3NoMagicCombat(Scenario3NoMagicCombat.class));
    }
}
