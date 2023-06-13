package de.unisaarland.cs.se.selab.state.bids;

import de.unisaarland.cs.se.selab.ConnectionUtils;
import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.commands.ActionResult;
import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.model.Player;

/**
 * Class for imp bids.
 */
public final class ImpsBid extends Bid {

    ImpsBid(final Player player, final int slot) {
        super(player, slot);
    }

    @Override
    protected ActionResult bidEvalImpl(final Model model, final ConnectionWrapper connection) {
        switch (this.slot) {
            case 1 -> {
                if (this.player.getFood() >= 1) {
                    ConnectionUtils.changeFood(this.player, -1, connection);
                    ConnectionUtils.changeImps(this.player, 1, connection);
                }
            }
            case 2 -> {
                if (this.player.getFood() >= 2) {
                    ConnectionUtils.changeFood(this.player, -2, connection);
                    ConnectionUtils.changeImps(this.player, 2, connection);
                }
            }
            case 3 -> {
                if (this.player.getFood() >= 1 && this.player.getGold() >= 1) {
                    ConnectionUtils.changeFood(this.player, -1, connection);
                    ConnectionUtils.changeGold(this.player, -1, connection);
                    ConnectionUtils.changeImps(this.player, 2, connection);
                }
            }
            default -> {
            }
        }
        return ActionResult.PROCEED;
    }
}
