package de.unisaarland.cs.se.selab.commands;

import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.model.Player;
import de.unisaarland.cs.se.selab.state.State;
import java.util.Set;

/**
 * Superclass for all commands/actions.
 */
public abstract class Command {

    private final int playerId;

    /**
     * Create a new command that was sent by the player with the given player ID.
     *
     * @param playerId the player's player ID
     */
    public Command(final int playerId) {
        this.playerId = playerId;
    }

    /**
     * Returns a set of game phases in which this command is valid, i.e., may be executed.
     *
     * @return the set of game phases
     */
    public Set<State.Phase> inPhase() {
        return Set.of(State.Phase.values());
    }

    /**
     * Execute a player command/action.
     * <p>
     * Certain commands/actions are executed for a single player individually.
     * This version of the {@code execute()} function takes that player as an additional parameter.
     * The default behavior is the same as
     * {@link Command#execute(Model, ConnectionWrapper, State.Phase)}.
     * However, the {@link PlayerCommand} overrides this function to check whether it is the given
     * player's turn.
     * </p>
     *
     * @param model      the model to which the action based changes should apply
     * @param connection the connection to the client to transmit events
     * @param phase      the game phase in which this command/action was triggered
     * @param player     the current player for this action
     * @return a result that indicates how the game should continue
     * @see PlayerCommand
     */
    public ActionResult execute(final Model model, final ConnectionWrapper connection,
                                final State.Phase phase, final Player player) {
        return execute(model, connection, phase);
    }

    /**
     * Execute a command/action.
     * <p>
     * This method implements some general checks, e.g., whether this command may be executed in the
     * current game phase.
     * If the checks succeed, this method calls {@link Command#run(Model, ConnectionWrapper)}.
     * So, for most commands it suffices to override the {@code run() function}.
     * </p>
     *
     * @param model      the model to which the action based changes should apply
     * @param connection the connection to the client to transmit events
     * @param phase      the game phase in which this command/action was triggered
     * @return a result that indicates how the game should continue
     */
    public ActionResult execute(final Model model, final ConnectionWrapper connection,
                                final State.Phase phase) {
        if (inPhase().contains(phase)) {
            return this.run(model, connection);
        }
        connection.sendActionFailed(getId(), "This action is not applicable now.");
        return ActionResult.RETRY;
    }

    /**
     * This function is called when a command/action is executed.
     *
     * @param model      the model to which the action based changes should apply
     * @param connection the connection to the client to transmit events
     * @return a result that indicates how the game should continue
     */
    protected abstract ActionResult run(Model model, ConnectionWrapper connection);

    /**
     * Get the player ID of the player who sent this command/action.
     *
     * @return the sender's player ID
     */
    public final int getId() {
        return this.playerId;
    }

}
