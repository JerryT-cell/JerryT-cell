package de.unisaarland.cs.se.selab.state.bids;

import de.unisaarland.cs.se.selab.ConnectionUtils;
import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.commands.ActionResult;
import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.model.Player;
import de.unisaarland.cs.se.selab.model.Trap;

/**
 * Class for trap bids.
 */
public final class TrapBid extends Bid {

    TrapBid(final Player player, final int slot) {
        super(player, slot);
    }

    @Override
    protected ActionResult bidEvalImpl(final Model model, final ConnectionWrapper connection) {
        switch (this.slot) {
            case 1 -> {
                if (this.player.getGold() >= 1) {
                    ConnectionUtils.changeGold(this.player, -1, connection);
                    drawTrap(this.player, 1, model, connection);
                }
            }
            case 2 -> drawTrap(this.player, 1, model, connection);
            case 3 -> {
                if (this.player.getGold() >= 2) {
                    ConnectionUtils.changeGold(this.player, -2, connection);
                    drawTrap(this.player, 2, model, connection);

                }
            }
            default -> {
            }
        }
        return ActionResult.PROCEED;
    }

    private static void drawTrap(final Player player, final int amount, final Model model,
                                final ConnectionWrapper connection) {
        for (int i = 0; i < amount; i++) {
            final Trap trap = model.drawTrap();
            player.addTrap(trap);
            connection.sendTrapAcquired(player.getId(), trap.getId());
        }
    }
}
