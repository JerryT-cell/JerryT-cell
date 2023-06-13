package de.unisaarland.cs.se.selab.commands;

import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.model.Player;
import de.unisaarland.cs.se.selab.state.State;

/**
 * Superclass for commands that a player may only submit if it is his/her turn.
 */
public abstract class PlayerCommand extends Command {

    public PlayerCommand(final int playerId) {
        super(playerId);
    }

    public boolean checkPlayer(final Player player) {
        return getId() == player.getId();
    }

    /**
     * Execute a player command/action.
     * <p>
     * This overrides the default behavior to check whether it is the given player's turn.
     * </p>
     *
     * @param model      the model to which the action based changes should apply
     * @param connection the connection to the client to transmit events
     * @param phase      the game phase in which this command/action was triggered
     * @param player     the current player for this action
     * @return a result that indicates how the game should continue
     */
    @Override
    public ActionResult execute(final Model model, final ConnectionWrapper connection,
                                final State.Phase phase, final Player player) {
        if (checkPlayer(player)) {
            return execute(model, connection, phase);
        } else {
            connection.sendActionFailed(getId(), "It's not your turn");
        }
        return ActionResult.RETRY;
    }
}