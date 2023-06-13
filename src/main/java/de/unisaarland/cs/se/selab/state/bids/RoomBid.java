package de.unisaarland.cs.se.selab.state.bids;

import de.unisaarland.cs.se.selab.ConnectionUtils;
import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.commands.ActionResult;
import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.model.Player;
import de.unisaarland.cs.se.selab.state.State;

/**
 * Class for room bids.
 */
public final class RoomBid extends Bid {

    RoomBid(final Player player, final int slot) {
        super(player, slot);
    }

    @Override
    protected ActionResult bidEvalImpl(final Model model, final ConnectionWrapper connection) {
        if (this.slot < 3) {
            if (this.player.getGold() >= 1) {
                ConnectionUtils.changeGold(this.player, -1, connection);
            } else {
                return ActionResult.PROCEED;
            }
        }
        if (model.availableRoomsLeft()) {
            connection.sendPlaceRoom(this.player.getId());
            return ConnectionUtils.executePlayerCommand(model, connection, State.Phase.PLACING_ROOM,
                    this.player);
        }
        return ActionResult.PROCEED;
    }
}
