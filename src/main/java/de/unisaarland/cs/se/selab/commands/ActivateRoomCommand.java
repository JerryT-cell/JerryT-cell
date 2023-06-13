package de.unisaarland.cs.se.selab.commands;

import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.model.Player;
import de.unisaarland.cs.se.selab.model.dungeon.Dungeon;
import de.unisaarland.cs.se.selab.model.dungeon.Room;
import de.unisaarland.cs.se.selab.model.dungeon.Tunnel;
import de.unisaarland.cs.se.selab.state.State;
import java.util.Optional;
import java.util.Set;

/**
 * Activate a room during the bidding phase.
 */
public class ActivateRoomCommand extends Command {

    private final int roomId;

    public ActivateRoomCommand(final int playerId, final int roomId) {
        super(playerId);
        this.roomId = roomId;
    }

    @Override
    public Set<State.Phase> inPhase() {
        return Set.of(State.Phase.BUILDING,
                State.Phase.PLACING_ROOM,
                State.Phase.PLACING_TUNNEL,
                State.Phase.HIRING_MONSTER);
    }

    @Override
    public ActionResult run(final Model model, final ConnectionWrapper connection) {
        final Player player = model.getPlayerById(getId());
        final Dungeon dungeon = player.getDungeon();
        final Optional<Tunnel> optTunnel = dungeon.getGraph().stream()
                .filter(tunnel -> tunnel.getRoom()
                        .map(room -> room.getId() == this.roomId)
                        .orElse(Boolean.FALSE)
                ).findAny();

        if (optTunnel.isEmpty()) {
            connection.sendActionFailed(getId(), "You don't own a room with this id.");
            return ActionResult.RETRY;
        }

        if (optTunnel.get().isConquered()) {
            connection.sendActionFailed(getId(), "This room is already conquered.");
            return ActionResult.RETRY;
        }

        // The filter above ensures that this room must be present
        final Room room = optTunnel.get().getRoom().get();
        if (room.isActivated()) {
            connection.sendActionFailed(getId(), "This room is already activated.");
            return ActionResult.RETRY;
        }

        if (player.getNoActivatingSeasonList().contains(model.getRound())) {
            connection.sendActionFailed(getId(), "This room has been blocked");
            return ActionResult.RETRY;
        }

        final int activation = room.getActivationImps();
        if (player.getImps() < activation) {
            connection.sendActionFailed(getId(),
                    "You don't have enough imps to activate this room.");
            return ActionResult.RETRY;
        }

        player.changeImps(-activation);
        connection.sendImpsChanged(getId(), -activation);
        room.activate();
        connection.sendRoomActivated(getId(), this.roomId);
        return ActionResult.RETRY;
    }
}
