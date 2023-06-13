package de.unisaarland.cs.se.selab.commands;

import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.model.Model;

/**
 * A dummy command that does nothing.
 */
public class EmptyCommand extends Command {

    public EmptyCommand() {
        super(Integer.MIN_VALUE);
    }

    @Override
    public ActionResult run(final Model model, final ConnectionWrapper connection) {
        return ActionResult.RETRY;
    }
}
