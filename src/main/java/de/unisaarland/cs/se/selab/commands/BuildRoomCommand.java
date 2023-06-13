package de.unisaarland.cs.se.selab.commands;

import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.model.Player;
import de.unisaarland.cs.se.selab.model.dungeon.Coordinate;
import de.unisaarland.cs.se.selab.model.dungeon.Room;
import de.unisaarland.cs.se.selab.model.dungeon.Tunnel;
import de.unisaarland.cs.se.selab.model.dungeon.TunnelGraph;
import de.unisaarland.cs.se.selab.state.State;
import java.util.Optional;
import java.util.Set;

/**
 * A player builds a room.
 */
public class BuildRoomCommand extends PlayerCommand {

    private final int roomId;
    private final Coordinate coordinate;

    public BuildRoomCommand(final int playerId, final int x, final int y, final int roomId) {
        super(playerId);
        this.coordinate = new Coordinate(x, y);
        this.roomId = roomId;
    }

    @Override
    public Set<State.Phase> inPhase() {
        return Set.of(State.Phase.PLACING_ROOM);
    }

    @Override
    public ActionResult run(final Model model, final ConnectionWrapper connection) {
        final Optional<Room> optRoom = model.getAvailableRoom(this.roomId);

        if (optRoom.isEmpty()) {
            connection.sendActionFailed(getId(), "This room is not available.");
            return ActionResult.RETRY;
        }

        final Room room = optRoom.get();
        final Player player = model.getPlayerById(getId());
        final TunnelGraph graph = player.getDungeon().getGraph();
        if (!BuildRoomCommand.isValidBuildLocation(room, this.coordinate, graph,
                model.getDungeonSideLength())) {
            connection.sendActionFailed(getId(), "You cannot place your room here.");
            return ActionResult.RETRY;
        }

        model.removeAvailableRoom(room);
        graph.getTunnel(this.coordinate).ifPresent(tunnel -> tunnel.buildRoom(room));
        connection.sendRoomBuilt(getId(), this.roomId, this.coordinate);
        return ActionResult.PROCEED;
    }

    /**
     * Checks whether a room may be placed at a given coordinate or not.
     *
     * @param room       the room to be placed
     * @param coordinate the coordinate where the room shall be placed
     * @param graph      the graph the room shall be added to
     * @param sideLength the graph's side length
     * @return {@code true} if the room may be placed at the coordinate or {@code false} otherwise
     */
    public static boolean isValidBuildLocation(final Room room, final Coordinate coordinate,
                                               final TunnelGraph graph, final int sideLength) {
        // A room cannot have an adjacent room.
        for (final Tunnel tunnel : graph.getNeighbours(coordinate)) {
            if (tunnel.isRoom()) {
                return false;
            }
        }
        // A room can only replace an unconquered tunnel.
        final Optional<Tunnel> tunnel = graph.getTunnel(coordinate);
        if (tunnel.isEmpty()
                || tunnel.get().isRoom()
                || tunnel.get().isConquered()) {
            return false;
        }
        return BuildRoomCommand.checkRoomRestriction(room, coordinate, sideLength);
    }

    /**
     * Checks whether a given coordinate fulfills a room's placement restrictions.
     *
     * @param room       the room to be placed
     * @param coordinate the coordinate where the room shall be placed
     * @param sideLength the graph's side length
     * @return {@code true} if the coordinate fulfills the placement restriction or {@code false}
     *         otherwise
     */
    public static boolean checkRoomRestriction(final Room room, final Coordinate coordinate,
                                               final int sideLength) {
        final int middle = (sideLength / 2) - 1;
        return switch (room.getRestriction()) {
            case UPPER_HALF -> coordinate.posY() <= middle;
            case LOWER_HALF -> coordinate.posY() > middle;
            case OUTER_RING -> (coordinate.posX() == 0 || coordinate.posX() == sideLength - 1)
                    || (coordinate.posY() == 0 || coordinate.posY() == sideLength - 1);
            case INNER_RING -> (coordinate.posX() != 0 && coordinate.posX() != sideLength - 1)
                    && (coordinate.posY() != 0 && coordinate.posY() != sideLength - 1);
        };
    }

}
