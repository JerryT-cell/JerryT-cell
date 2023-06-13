package de.unisaarland.cs.se.selab.model.dungeon;

import de.unisaarland.cs.se.selab.model.Adventurer;
import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.model.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * This holds all dungeon-related data.
 */
public class Dungeon {

    private final List<Adventurer> queuingAdventurers;
    private final List<Adventurer> prison;
    private final TunnelGraph graph;
    private Tunnel battleGround;
    private int supervisingImps;
    private boolean archmageLinus;
    private int penguinVisits;

    public Dungeon() {
        this.queuingAdventurers = new ArrayList<>();
        this.prison = new ArrayList<>();
        this.graph = new TunnelGraph();
        archmageLinus = false;
        penguinVisits = 0;
    }

    public void setBattleGround(final Tunnel battleGround) {
        this.battleGround = battleGround;
    }

    public Optional<Tunnel> getBattleGround() {
        return Optional.ofNullable(this.battleGround);
    }

    public TunnelGraph getGraph() {
        return this.graph;
    }

    public void addAdventurer(final Adventurer adventurer) {
        if (adventurer.isCharging()) {
            this.queuingAdventurers.add(0, adventurer);
        } else {
            this.queuingAdventurers.add(adventurer);
        }
    }

    private void removeDefeatedAdventurers() {
        final List<Adventurer> defeatedAdventurers = this.queuingAdventurers
                .stream()
                .filter(Adventurer::isDefeated)
                .toList();
        this.queuingAdventurers.removeAll(defeatedAdventurers);
    }

    /**
     * Clear all adventurers from the dungeon.
     */
    public void clearAdventurers() {
        this.queuingAdventurers.clear();
    }

    /**
     * Get the adventurer at the given position.
     *
     * @param target the adventurer's position
     * @return the selected adventurer if available
     */
    public Optional<Adventurer> getAdventurer(final int target) {
        if (getAllAdventurers().size() < target) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(getAllAdventurers().get(target - 1));
        }
    }

    /**
     * Get all adventurers that are still alive in the dungeon.
     *
     * @return a list of alive adventurers
     */
    public List<Adventurer> getAllAdventurers() {
        removeDefeatedAdventurers();
        return new ArrayList<>(this.queuingAdventurers);
    }

    /**
     * Check whether there are any adventurers left in the dungeon.
     *
     * @return whether there are any adventurers left
     */
    public boolean adventurersLeft() {
        return !getAllAdventurers().isEmpty();
    }

    /**
     * Get the number of conquered tiles (rooms and tunnels).
     *
     * @return the number of conquered tiles
     */
    public final int getNumUnconqueredTiles() {
        return (int) this.graph.stream().filter(tunnel -> !tunnel.isConquered()).count();
    }

    /**
     * Check whether all tunnels and rooms in the dungeon are conquered.
     *
     * @return whether all tunnels and rooms are conquered
     */
    public final boolean isConquered() {
        return getNumUnconqueredTiles() == 0;
    }

    /**
     * Conquer the current battleground.
     */
    public final void conquer() {
        if (this.battleGround != null) {
            this.battleGround.conquer();
        }
    }

    /**
     * Assign an imp for supervision.
     */
    public void addSupervision() {
        this.supervisingImps++;
    }

    /**
     * Return all supervising imps from supervision.
     *
     * @return the number of imps that were supervising
     */
    private int returnSupervision() {
        final int supervisors = this.supervisingImps;
        this.supervisingImps = 0;
        return supervisors;
    }

    /**
     * Return all mining imps from their jobs.
     *
     * @return the number of imps returning from mining jobs
     */
    public int returnMiners() {
        return returnSupervision() + this.graph
                .map(Tunnel::returnMiners)
                .stream()
                .reduce(0, Integer::sum);
    }

    /**
     * Imprison the given adventurer.
     *
     * @param adventurer the defeated adventurer
     */
    public void imprisonAdventurer(final Adventurer adventurer) {
        this.prison.add(adventurer);
    }

    /**
     * Free the first adventurer from prison.
     *
     * @return the freed adventurer
     */
    public Adventurer freeAdventurer() {
        return this.prison.remove(0);
    }

    /**
     * Get all imprisoned adventurers.
     *
     * @return a list of imprisoned adventurers
     */
    public List<Adventurer> getPrison() {
        return this.prison;
    }

    /**
     * Get the number of imprisoned adventurers.
     *
     * @return the number of imprisoned adventurers
     */
    public int getNumImprisonedAdventurers() {
        return this.prison.size();
    }

    /*added*/
    public boolean linusIsPresent() {
        return archmageLinus;
    }

    public void setArchmageLinus(final boolean archmageLinus) {
        this.archmageLinus = archmageLinus;
    }

    /**
     * increase when linus arrive in the dungeon
     */
    public void increasePenguinVisit() {
        penguinVisits++;
    }

    /**
     * calculates the probability that linus arrives
     *
     * @param player the player that is fighting
     * @param model  the model that represents the game
     * @return true if linus can arrive , false if linus can't
     */

    public boolean probabilityLinusArrive(final Player player, final Model model) {
        final Random random = model.getRandomObject();
        final int upperBound = 15 + model.getMaxYear();
        final int dieRolled = random.nextInt(upperBound);
        final int toCompare = player.getEvilness() + model.getYear();

        return dieRolled < toCompare;

    }

    public int getPenguinVisits() {
        return penguinVisits;
    }
}
