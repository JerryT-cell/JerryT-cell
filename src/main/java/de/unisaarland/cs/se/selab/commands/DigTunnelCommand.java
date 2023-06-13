package de.unisaarland.cs.se.selab.commands;

import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.model.Player;
import de.unisaarland.cs.se.selab.model.dungeon.Coordinate;
import de.unisaarland.cs.se.selab.model.dungeon.Coordinate.Direction;
import de.unisaarland.cs.se.selab.model.dungeon.Dungeon;
import de.unisaarland.cs.se.selab.model.dungeon.Tunnel;
import de.unisaarland.cs.se.selab.model.dungeon.TunnelGraph;
import de.unisaarland.cs.se.selab.state.BuildingState;
import de.unisaarland.cs.se.selab.state.State;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * The player digs a tunnel.
 */
public class DigTunnelCommand extends PlayerCommand {

    private static final int MINER = 1;
    private static final int MINER_AND_SUPERVISOR = 2;
    private final Coordinate coordinate;

    public DigTunnelCommand(final int playerId, final int x, final int y) {
        super(playerId);
        this.coordinate = new Coordinate(x, y);
    }

    @Override
    public Set<State.Phase> inPhase() {
        return Set.of(State.Phase.PLACING_TUNNEL);
    }

    @Override
    public ActionResult run(final Model model, final ConnectionWrapper connection) {
        final Player player = model.getPlayerById(getId());

        if (player.getNumTunnelDigsAllowed() <= 0) {
            connection.sendActionFailed(getId(), "You have no tunnels left to place.");
            return ActionResult.RETRY;
        }

        final Dungeon dungeon = player.getDungeon();
        final TunnelGraph graph = dungeon.getGraph();
        if (DigTunnelCommand.violatesTunnelBuildingRestrictions(
                this.coordinate,
                graph,
                model.getDungeonSideLength()) || !isConnected(this.coordinate, graph)) {
            connection.sendActionFailed(getId(), "You cannot place your tunnel here.");
            return ActionResult.RETRY;
        }

        final int currentMiners = (int) graph.stream().filter(Tunnel::hasTunnelMiningImp).count();
        final boolean needSupervisor = currentMiners == BuildingState.NO_SUPERVISION_LIMIT;
        final int neededImps = needSupervisor ? MINER_AND_SUPERVISOR : MINER;
        if (player.getImps() < neededImps) {
            connection.sendActionFailed(getId(), "You do not have enough imps for that left.");
            return ActionResult.RETRY;
        }

        if (needSupervisor) {
            dungeon.addSupervision();
        }
        player.changeImps(-neededImps);
        connection.sendImpsChanged(getId(), -neededImps);
        player.digTunnel();
        final Tunnel newTunnel = new Tunnel(this.coordinate, true);
        graph.addTunnel(newTunnel);
        connection.sendTunnelDug(getId(), this.coordinate);
        if (findCounterSpell(model, newTunnel)) {
            connection.sendCounterSpellFound(player.getId());
            player.increaseCounterSpell();
        }
        if (player.getNumTunnelDigsAllowed() <= 0) {
            return ActionResult.PROCEED;
        } else {
            return ActionResult.RETRY;
        }
    }

    /**
     * Check whether the given coordinate is connected to a tunnel graph.
     *
     * @param coordinate the coordinate to check
     * @param graph      the graph where the coordinate shall be added
     * @return whether the coordinate is connected to the graph
     */
    public static boolean isConnected(final Coordinate coordinate, final TunnelGraph graph) {
        final List<Tunnel> neighbours = graph.getNeighbours(coordinate);
        return !neighbours.isEmpty();
    }

    /**
     * Checks whether the given coordinate violates any tunnel digging restrictions.
     * <p>
     * This function checks for the following restrictions:
     * - the coordinate must be in bounds
     * - the coordinate must not form any 2-by-2s
     * - the coordinate must not already be a tunnel
     * </p>
     *
     * @param coordinate the coordinate to check
     * @param graph      the graph where the coordinate shall be added
     * @param sideLength the graph's side length
     * @return whether the coordinate violates any building restrictions
     */
    public static boolean violatesTunnelBuildingRestrictions(final Coordinate coordinate,
                                                             final TunnelGraph graph,
                                                             final int sideLength) {
        // Check out of bounds
        if (coordinate.posX() > sideLength - 1
                || coordinate.posY() > sideLength - 1
                || coordinate.posX() < 0
                || coordinate.posY() < 0) {
            return true;
        }

        // Check 2-by-2s
        // The vectors are movement directions applied successively to the coordinate,
        // e.g., the first vector checks the following coordinates the following (starting at 'X'):
        //    1 --> 2
        //    ^     |
        //    |     v
        //    X     3
        final Direction[] nwVector = {Direction.NORTH, Direction.WEST, Direction.SOUTH};
        final Direction[] neVector = {Direction.NORTH, Direction.EAST, Direction.SOUTH};
        final Direction[] swVector = {Direction.SOUTH, Direction.WEST, Direction.NORTH};
        final Direction[] seVector = {Direction.SOUTH, Direction.EAST, Direction.NORTH};

        // Check if all neighbours in these direction are present, if yes then we have a two by two
        for (final Direction[] vector : List.of(nwVector, neVector, swVector, seVector)) {
            if (graph.getTunnel(coordinate.getNeighbor(vector[0]))
                    .flatMap(t1 -> graph.getTunnel(t1.getCoordinate().getNeighbor(vector[1])))
                    .flatMap(t2 -> graph.getTunnel(t2.getCoordinate().getNeighbor(vector[2])))
                    .isPresent()) {
                return true;
            }
        }

        // Check if already a tunnel
        return graph.getTunnel(coordinate).isPresent();
    }

    /**
     * to calculate the probability of finding a counter spell
     *
     * @param model  the model to use
     * @param tunnel the tunnel that has been dug
     * @return true if a counterSpell has been found
     */
    private boolean findCounterSpell(final Model model, final Tunnel tunnel) {

        final Random random = model.getRandomObject();
        final int sideLength = model.getDungeonSideLength();
        final int x = tunnel.getCoordinate().posX();
        final int y = tunnel.getCoordinate().posY();
        final int dieThrew = random.nextInt(sideLength * sideLength);

        return dieThrew < x * y;
    }
}
