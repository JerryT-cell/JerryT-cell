package de.unisaarland.cs.se.selab.commands;

import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.model.Player;
import de.unisaarland.cs.se.selab.model.dungeon.Coordinate;
import de.unisaarland.cs.se.selab.model.dungeon.Dungeon;
import de.unisaarland.cs.se.selab.model.dungeon.Tunnel;
import de.unisaarland.cs.se.selab.state.State;
import java.util.Optional;
import java.util.Set;

/**
 * Set the battleground for a player
 */
public class BattleGroundCommand extends PlayerCommand {

    private final Coordinate coordinate;

    public BattleGroundCommand(final int playerId, final int x, final int y) {
        super(playerId);
        this.coordinate = new Coordinate(x, y);
    }

    @Override
    public Set<State.Phase> inPhase() {
        return Set.of(State.Phase.SET_BATTLEGROUND);
    }

    @Override
    public ActionResult run(final Model model, final ConnectionWrapper connection) {
        final Player player = model.getPlayerById(getId());
        final Dungeon dungeon = player.getDungeon();

        final Optional<Tunnel> battleGround = dungeon.getGraph().getTunnel(this.coordinate);
        if (battleGround.isPresent()) {
            if (dungeon.getGraph().isClosestUnconqueredTile(battleGround.get())) {
                dungeon.setBattleGround(battleGround.get());
                connection.sendBattleGroundSet(getId(), this.coordinate);
                return ActionResult.PROCEED;
            } else {
                connection.sendActionFailed(getId(),
                        "This tunnel is not amongst the closest to the entrance.");
            }
        } else {
            connection.sendActionFailed(getId(),
                    "You can only battle adventurers in tunnels or rooms.");
        }
        return ActionResult.RETRY;
    }
}
