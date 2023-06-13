package de.unisaarland.cs.se.selab.commands;

/**
 * This enum indicates how the state machine should continue after executing an action.
 */
public enum ActionResult {
    /**
     * Proceed with the next step in the game.
     */
    PROCEED,

    /**
     * Another action is required before proceeding with the game.
     */
    RETRY,

    /**
     * The game should be aborted.
     */
    END_GAME
}
