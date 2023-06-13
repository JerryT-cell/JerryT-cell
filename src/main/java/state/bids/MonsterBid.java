package de.unisaarland.cs.se.selab.state.bids;

import de.unisaarland.cs.se.selab.ConnectionUtils;
import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.commands.ActionResult;
import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.model.Player;
import de.unisaarland.cs.se.selab.state.State;

/**
 * Class for monster bids.
 */
public final class MonsterBid extends Bid {

    MonsterBid(final Player player, final int slot) {
        super(player, slot);
    }

    @Override
    protected ActionResult bidEvalImpl(final Model model, final ConnectionWrapper connection) {
        if (this.slot == 3) {
            if (this.player.getFood() >= 1) {
                ConnectionUtils.changeFood(this.player, -1, connection);
            } else {
                return ActionResult.PROCEED;
            }
        }

        connection.sendSelectMonster(this.player.getId());
        return ConnectionUtils.executePlayerCommand(model, connection, State.Phase.HIRING_MONSTER,
                this.player);
    }
}
