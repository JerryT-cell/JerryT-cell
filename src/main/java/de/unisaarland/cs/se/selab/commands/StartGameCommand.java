package de.unisaarland.cs.se.selab.commands;

import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.state.State;
import java.util.Set;

/**
 * A player manually starts the game.
 */
public class StartGameCommand extends Command {

    public StartGameCommand(final int playerId) {
        super(playerId);
    }

    @Override
    public Set<State.Phase> inPhase() {
        return Set.of(State.Phase.REGISTRATION);
    }

    @Override
    public ActionResult run(final Model model, final ConnectionWrapper connection) {
        if (model.hasPlayer(getId())) {
            return ActionResult.PROCEED;
        } else {
            connection.sendActionFailed(getId(), "You are not registered!");
            return ActionResult.RETRY;
        }
    }
}
