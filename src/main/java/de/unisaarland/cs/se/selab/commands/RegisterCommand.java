package de.unisaarland.cs.se.selab.commands;

import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.state.State;

/**
 * Register a player with the game.
 */
public class RegisterCommand extends Command {

    private final String playerName;
    private final boolean withCommId;

    public RegisterCommand(final int playerId, final String playerName, final boolean withCommId) {
        super(playerId);
        this.playerName = playerName;
        this.withCommId = withCommId;
    }

    @Override
    public ActionResult execute(final Model model, final ConnectionWrapper connection,
                                final State.Phase phase) {
        if (this.withCommId) {
            if (phase != State.Phase.REGISTRATION) {
                return ActionResult.RETRY;
            }
            final int playerId = model.addPlayer(this.playerName);
            connection.addPlayer(getId(), playerId);
            connection.sendConfig(playerId);
            if (model.maxPlayersReached()) {
                return ActionResult.PROCEED;
            }
        } else {
            connection.sendActionFailed(getId(), "You are already registered!");
            return ActionResult.RETRY;
        }
        return ActionResult.RETRY;
    }

    @Override
    protected ActionResult run(final Model model, final ConnectionWrapper connection) {
        throw new IllegalStateException("Method is unused.");
    }
}
