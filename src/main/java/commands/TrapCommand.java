package de.unisaarland.cs.se.selab.commands;

import de.unisaarland.cs.se.selab.ConnectionUtils;
import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.model.Player;
import de.unisaarland.cs.se.selab.model.Trap;
import de.unisaarland.cs.se.selab.model.dungeon.Dungeon;
import de.unisaarland.cs.se.selab.model.dungeon.Tunnel;
import de.unisaarland.cs.se.selab.state.State;
import java.util.Optional;
import java.util.Set;

/**
 * A player sets a trap for defense.
 */
public class TrapCommand extends PlayerCommand {

    private static final int TRAP_ROOM_COST = 1;
    private final int trapId;

    public TrapCommand(final int playerId, final int trapId) {
        super(playerId);
        this.trapId = trapId;
    }

    @Override
    public Set<State.Phase> inPhase() {
        return Set.of(State.Phase.COMBAT);
    }

    @Override
    public ActionResult run(final Model model, final ConnectionWrapper connection) {
        final Player player = model.getPlayerById(getId());
        final Optional<Trap> optTrap = player.getTrap(this.trapId);

        if (optTrap.isEmpty()) {
            connection.sendActionFailed(getId(), "This trap does not belong to you.");
            return ActionResult.RETRY;
        }

        final Dungeon dungeon = player.getDungeon();
        final Optional<Tunnel> battleGround = dungeon.getBattleGround();
        if (battleGround.isEmpty()) {
            connection.sendActionFailed(getId(), "The battleground has not been set yet.");
            return ActionResult.RETRY;
        }

        if (battleGround.get().isRoom() && player.getGold() < TRAP_ROOM_COST) {
            connection.sendActionFailed(getId(), "Placing traps in rooms costs a gold.");
            return ActionResult.RETRY;
        }

        final Trap trap = optTrap.get();
        if (!battleGround.get().addTrap(trap)) {
            connection.sendActionFailed(getId(), "You cannot place any more trap here.");
            return ActionResult.RETRY;
        }

        player.getTraps().remove(trap);
        if (battleGround.get().isRoom()) {
            ConnectionUtils.changeGold(player, -TRAP_ROOM_COST, connection);
        }
        connection.sendTrapPlaced(getId(), this.trapId);
        return ActionResult.RETRY;

    }
}
