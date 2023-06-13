package de.unisaarland.cs.se.selab.state;

import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.model.Model;

/**
 * This state initializes the game model with the registered players.
 */
public final class PreGameState extends State {

    public PreGameState(final Model model, final ConnectionWrapper connection) {
        super(model, connection);
    }

    @Override
    public State run() {
        connection.sendGameStarted();
        model.shuffleCards();
        model.getPlayers()
                .forEach(player -> connection.sendPlayer(player.getId(), player.getName()));
        return new BuildingState(this.model, this.connection);
    }
}
