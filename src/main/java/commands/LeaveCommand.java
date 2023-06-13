package de.unisaarland.cs.se.selab.commands;

import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.model.Adventurer;
import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.model.Player;
import de.unisaarland.cs.se.selab.state.State.Phase;

/**
 * A player leaves the game.
 */
public class LeaveCommand extends Command {

    public LeaveCommand(final int playerId) {
        super(playerId);
    }

    @Override
    public ActionResult execute(final Model model, final ConnectionWrapper connection,
                                final Phase phase) {

        final Player player = model.getPlayerById(getId());
        player.kill();
        model.removePlayer(getId());
        connection.removePlayer(getId());

        if (!model.getPlayers().isEmpty()) {
            if (phase != Phase.REGISTRATION) {
                connection.sendLeft(getId());
            }
            for (final Adventurer adventurer : player.getDungeon().getPrison()) {
                connection.sendAdventurerFled(adventurer.getId());
            }
            return ActionResult.RETRY;
        } else {
            return ActionResult.END_GAME;
        }
    }

    @Override
    protected ActionResult run(final Model model, final ConnectionWrapper connection) {
        throw new IllegalStateException("Method is unused.");
    }
}
