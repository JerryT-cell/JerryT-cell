package de.unisaarland.cs.se.selab.state;

import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.comm.TimeoutException;
import de.unisaarland.cs.se.selab.commands.ActionResult;
import de.unisaarland.cs.se.selab.commands.Command;
import de.unisaarland.cs.se.selab.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This state handles player registration.
 */
public final class RegistrationState extends State {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationState.class);

    public RegistrationState(final Model model, final ConnectionWrapper connection) {
        super(model, connection);
    }

    @Override
    public State run() {
        try {
            final Command command = connection.nextAction();
            final ActionResult result =
                    command.execute(model, connection, Phase.REGISTRATION);
            if (result == ActionResult.PROCEED) {
                return new PreGameState(this.model, this.connection);
            }
            return new RegistrationState(this.model, this.connection);
        } catch (final TimeoutException exception) {
            connection.sendRegistrationAborted();
            LOGGER.error("Timed out while waiting for players to register.");
            return new EndGameState(this.model, this.connection);
        }
    }
}
