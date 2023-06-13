package de.unisaarland.cs.se.selab;

import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.state.RegistrationState;
import de.unisaarland.cs.se.selab.state.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The server class manages the game's state machine.
 */
public class Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    private final ConnectionWrapper connection;
    private final Model model;

    public Server(final Model model, final ConnectionWrapper connection) {
        this.connection = connection;
        this.model = model;
    }

    /**
     * This function contains the game loop.
     * It starts with the registration phase and ends if it encounters an end state.
     */
    public final void run() {
        State currentState = new RegistrationState(this.model, this.connection);
        while (!currentState.isEndState()) {
            LOGGER.info("Calling run on {}", this);
            currentState = currentState.run();
        }
    }

}
