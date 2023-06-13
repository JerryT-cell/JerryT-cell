package de.unisaarland.cs.se.selab.state;

import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.model.Model;

/**
 * The superclass for all game states.
 * <p>
 * The state's logic is implemented in the {@link State#run()} function.
 * This function always returns the next state that should be run.
 * Every state has access to the current game model and server connection.
 * </p>
 */
public abstract class State {

    /**
     * This enum is used to determine when certain actions can be executed.
     */
    public enum Phase {
        REGISTRATION,
        BUILDING,
        COMBAT,
        OFF,
        HIRING_MONSTER,
        PLACING_TUNNEL,
        PLACING_ROOM,
        SET_BATTLEGROUND,
        SPELL
    }

    protected final Model model;
    protected final ConnectionWrapper connection;

    protected State(final Model model, final ConnectionWrapper connection) {
        this.model = model;
        this.connection = connection;
    }

    /**
     * This method is called by the {@link de.unisaarland.cs.se.selab.Server} and contains all the
     * functionality of the state.
     *
     * @return the next state
     */
    public abstract State run();

    /**
     * This method indicates whether this state is an end state.
     * <p>
     * If an end state is encountered, the server will exit without calling {@code run()} on the
     * state.
     * </p>
     *
     * @return whether this state is an end state
     */
    public boolean isEndState() {
        return false;
    }

}
