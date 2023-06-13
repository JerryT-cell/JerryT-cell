package de.unisaarland.cs.se.selab.state;

import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.model.Model;

/**
 * This state indicates that the game is over and should be terminated.
 */
public final class EndGameState extends State {

    public EndGameState(final Model model, final ConnectionWrapper connection) {
        super(model, connection);
    }

    @Override
    public State run() {
        return new EndGameState(this.model, this.connection);
    }

    @Override
    public boolean isEndState() {
        return true;
    }
}
