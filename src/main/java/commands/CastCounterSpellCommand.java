package de.unisaarland.cs.se.selab.commands;

import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.model.Player;
import de.unisaarland.cs.se.selab.state.State;
import java.util.Set;

public class CastCounterSpellCommand extends PlayerCommand {
    public CastCounterSpellCommand(final int playerId) {
        super(playerId);
    }

    @Override
    protected ActionResult run(final Model model, final ConnectionWrapper connection) {
        final Player player = model.getPlayerById(getId());
        if (player.getCounterSpells() > 0) {
            player.useCounterSpell();
            connection.sendCounterSpellCast(player.getId());
            return ActionResult.PROCEED;
        } else {
            connection.sendActionFailed(player.getId(), "player don't have counter spells");
            return ActionResult.RETRY;
        }
    }

    @Override
    public Set<State.Phase> inPhase() {
        return Set.of(State.Phase.SPELL);
    }
}
