package de.unisaarland.cs.se.selab.state.bids;

import de.unisaarland.cs.se.selab.ConnectionUtils;
import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.commands.ActionResult;
import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.model.Player;

/**
 * Class for niceness bids.
 */
public final class NicenessBid extends Bid {

    NicenessBid(final Player player, final int slot) {
        super(player, slot);
    }

    @Override
    protected ActionResult bidEvalImpl(final Model model, final ConnectionWrapper connection) {
        switch (this.slot) {
            case 1 -> {
                if (this.player.getEvilness() - 1 >= Player.MIN_EVILNESS) {
                    ConnectionUtils.changeEvilness(this.player, -1, connection);
                }
            }
            case 2 -> {
                if (this.player.getEvilness() - 2 >= Player.MIN_EVILNESS) {
                    ConnectionUtils.changeEvilness(this.player, -2, connection);
                }
            }
            case 3 -> {
                if (this.player.getGold() >= 1
                        && this.player.getEvilness() - 2 >= Player.MIN_EVILNESS) {
                    ConnectionUtils.changeGold(this.player, -1, connection);
                    ConnectionUtils.changeEvilness(this.player, -2, connection);
                }
            }
            default -> {
            }
        }
        return ActionResult.PROCEED;
    }
}
