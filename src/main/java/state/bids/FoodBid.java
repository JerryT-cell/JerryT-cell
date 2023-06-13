package de.unisaarland.cs.se.selab.state.bids;

import de.unisaarland.cs.se.selab.ConnectionUtils;
import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.commands.ActionResult;
import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.model.Player;

/**
 * Class for food bids.
 */
public final class FoodBid extends Bid {

    FoodBid(final Player player, final int slot) {
        super(player, slot);
    }

    @Override
    protected ActionResult bidEvalImpl(final Model model, final ConnectionWrapper connection) {
        switch (this.slot) {
            case 1 -> {
                if (this.player.getGold() >= 1) {
                    ConnectionUtils.changeGold(this.player, -1, connection);
                    ConnectionUtils.changeFood(this.player, 2, connection);
                }
            }
            case 2 -> {
                if (this.player.getEvilness() + 1 <= Player.MAX_EVILNESS) {
                    ConnectionUtils.changeEvilness(this.player, 1, connection);
                    ConnectionUtils.changeFood(this.player, 3, connection);
                }
            }
            case 3 -> {
                if (this.player.getEvilness() + 2 <= Player.MAX_EVILNESS) {
                    ConnectionUtils.changeEvilness(this.player, 2, connection);
                    ConnectionUtils.changeFood(this.player, 3, connection);
                    ConnectionUtils.changeGold(this.player, 1, connection);
                }
            }
            default -> {
            }
        }
        return ActionResult.PROCEED;
    }
}
