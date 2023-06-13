package de.unisaarland.cs.se.selab;

import de.unisaarland.cs.se.selab.comm.TimeoutException;
import de.unisaarland.cs.se.selab.commands.ActionResult;
import de.unisaarland.cs.se.selab.commands.Command;
import de.unisaarland.cs.se.selab.commands.LeaveCommand;
import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.model.Player;
import de.unisaarland.cs.se.selab.state.State;

/**
 * Utility class for server connection related functionality.
 */
public final class ConnectionUtils {

    // This is a utility class that should not be instantiated.
    private ConnectionUtils() {}

    /**
     * Helper function to handle incoming commands until a sufficient command was executed.
     * <p>
     * This function relies on the {@link ActionResult}
     * returned by the commands.
     * It repeatedly polls and executes the next action until the result confirms that we should
     * continue or a timeout exception is encountered.
     * </p>
     *
     * @param model      the model to which the action based changes should apply
     * @param connection the connection to the client to transmit events
     * @param player     the player which is supposed to act
     * @return a result that indicates how the game should continue
     */
    public static ActionResult executePlayerCommand(final Model model,
                                                    final ConnectionWrapper connection,
                                                    final State.Phase phase,
                                                    final Player player) {
        try {
            connection.sendActNow(player.getId());
            Command command = connection.nextAction();
            ActionResult result = command.execute(model, connection, phase, player);
            while (result == ActionResult.RETRY) {
                if (!player.isAlive()) {
                    // the current player left, so we skip the action
                    return ActionResult.PROCEED;
                }
                if (player.getId() == command.getId()) {
                    connection.sendActNow(player.getId());
                }
                command = connection.nextAction();
                result = command.execute(model, connection, phase, player);
            }
            return result;
        } catch (final TimeoutException exception) {
            return new LeaveCommand(player.getId()).execute(model, connection, phase);
        }
    }

    /**
     * Change a player's food and notify all players.
     *
     * @param player     the player whose food changes
     * @param amount     the amount of the change
     * @param connection the connection to the client to transmit events
     */
    public static void changeFood(final Player player, final int amount,
                                  final ConnectionWrapper connection) {
        if (player.getFood() + amount >= 0) {
            player.changeFood(amount);
            connection.sendFoodChanged(player.getId(), amount);
        }
    }

    /**
     * Change a player's gold and notify all players.
     *
     * @param player     the player whose gold changes
     * @param amount     the amount of the change
     * @param connection the connection to the client to transmit events
     */
    public static void changeGold(final Player player, final int amount,
                                  final ConnectionWrapper connection) {
        if (player.getGold() + amount >= 0) {
            player.changeGold(amount);
            connection.sendGoldChanged(player.getId(), amount);
        }
    }

    /**
     * Change a player's evilness and notify all players.
     *
     * @param player     the player whose evilness changes
     * @param amount     the amount of the change
     * @param connection the connection to the client to transmit events
     */
    public static void changeEvilness(final Player player, final int amount,
                                      final ConnectionWrapper connection) {
        final int evilness = player.getEvilness() + amount;
        if (evilness >= Player.MIN_EVILNESS && evilness <= Player.MAX_EVILNESS) {
            player.changeEvilness(amount);
            connection.sendEvilnessChanged(player.getId(), amount);
        }
    }

    /**
     * Change a player's imps and notify all players.
     *
     * @param player     the player whose imps changes
     * @param amount     the amount of the change
     * @param connection the connection to the client to transmit events
     */
    public static void changeImps(final Player player, final int amount,
                                  final ConnectionWrapper connection) {
        if (amount >= 0) {
            player.changeImps(amount);
            connection.sendImpsChanged(player.getId(), amount);
        }
    }
}
