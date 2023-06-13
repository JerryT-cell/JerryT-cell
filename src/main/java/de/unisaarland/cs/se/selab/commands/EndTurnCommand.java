package de.unisaarland.cs.se.selab.commands;

import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.state.State;
import java.util.Set;

/**
 * Ends the current turn for a player.
 */
public class EndTurnCommand extends PlayerCommand {

    public EndTurnCommand(final int playerId) {
        super(playerId);
    }

    @Override
    public Set<State.Phase> inPhase() {
        return Set.of(State.Phase.PLACING_ROOM,
                State.Phase.PLACING_TUNNEL,
                State.Phase.HIRING_MONSTER,
                State.Phase.COMBAT, State.Phase.SPELL);
    }

    @Override
    public ActionResult run(final Model model, final ConnectionWrapper connection) {
        return ActionResult.PROCEED;
    }
}
