package de.unisaarland.cs.se.selab.systemtest;


import de.unisaarland.cs.se.selab.comm.TimeoutException;
import de.unisaarland.cs.se.selab.systemtest.api.SystemTest;
import de.unisaarland.cs.se.selab.systemtest.api.Utils;
import java.util.Set;

/**
 * Register 2 Players and Leave
 */
public class RegistrationTest extends SystemTest {

    private final Set<Integer> socketSet;

    RegistrationTest() {
        super(RegistrationTest.class, false);
        socketSet = Set.of(1, 2);
    }

    @Override
    public String createConfig() {
        return Utils.loadResource(RegistrationTest.class, "config1.json");
    }

    @Override
    public long createSeed() {
        return 149;
    }

    @Override
    protected Set<Integer> createSockets() {
        return Set.of(1, 2);
    }

    @Override
    public void run() throws TimeoutException {
        final String config = createConfig();
        this.sendRegister(1, "Niklas");
        this.assertConfig(1, config);
        this.sendRegister(2, "Maxi");
        this.assertConfig(2, config);
        this.sendStartGame(2);
        this.assertGameStarted(2);
        this.assertGameStarted(1);
        this.assertPlayer(2, "Niklas", 0);
        this.assertPlayer(1, "Niklas", 0);
        this.assertPlayer(2, "Maxi", 1);
        this.assertPlayer(1, "Maxi", 1);
        this.assertNextYear(2, 1);
        this.assertNextYear(1, 1);
        this.assertNextRound(2, 1);
        this.assertNextRound(1, 1);
        adventurerDrawn();
        monsterDrawn();
        room();
        spell();
        this.assertBiddingStarted(2);
        this.assertBiddingStarted(1);
        this.assertActNow(2);
        this.assertActNow(1);
        this.sendLeave(1);
        this.assertLeft(2, 0);
        this.sendLeave(2);
    }

    private void adventurerDrawn() throws TimeoutException {

        for (final Integer so : socketSet) {
            this.assertAdventurerDrawn(so, 29);
        }
        for (final Integer so : socketSet) {
            this.assertAdventurerDrawn(so, 23);
        }
    }

    private void monsterDrawn() throws TimeoutException {
        for (final Integer so : socketSet) {
            this.assertMonsterDrawn(so, 23);
        }
        for (final Integer so : socketSet) {
            this.assertMonsterDrawn(so, 13);
        }
        for (final Integer so : socketSet) {
            this.assertMonsterDrawn(so, 9);
        }
    }

    private void room() throws TimeoutException {
        for (final Integer so : socketSet) {
            this.assertRoomDrawn(so, 5);
        }
        for (final Integer so : socketSet) {
            this.assertRoomDrawn(so, 4);
        }

    }

    private void spell() throws TimeoutException {
        for (final Integer so : socketSet) {
            this.assertSpellDrawn(so, 19);
        }
        for (final Integer so : socketSet) {
            this.assertSpellDrawn(so, 23);
        }
        for (final Integer so : socketSet) {
            this.assertSpellDrawn(so, 7);
        }
    }


}
