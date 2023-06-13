package de.unisaarland.cs.se.selab.commands;

import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.comm.BidType;
import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.model.Player;
import de.unisaarland.cs.se.selab.state.State;
import java.util.Set;

/**
 * A player places a bid.
 */
public class PlaceBidCommand extends Command {

    private final BidType type;
    private final int slot;

    public PlaceBidCommand(final int playerId, final BidType type, final int slot) {
        super(playerId);
        this.type = type;
        this.slot = slot;
    }

    @Override
    public Set<State.Phase> inPhase() {
        return Set.of(State.Phase.BUILDING);
    }

    @Override
    public ActionResult run(final Model model, final ConnectionWrapper connection) {
        if (this.slot < 1 || this.slot > Model.BID_LIMIT) {
            connection.sendActionFailed(getId(), "Invalid bid slot");
            return ActionResult.RETRY;
        }

        final Player player = model.getPlayerById(getId());

        if (player.getPlacedBidTypes().size() >= Model.BID_LIMIT) {
            connection.sendActionFailed(getId(), "You already placed all your bids.");
            return ActionResult.RETRY;

        }
        /* model.getRound() has been added as parameter
         * */
        if (player.isLocked(this.type, model.getRound())) {
            connection.sendActionFailed(getId(), "This bid is locked for you.");
            return ActionResult.RETRY;
        }

        if (player.placeBid(this.type, this.slot)) {
            connection.sendBidPlaced(this.slot, getId(), this.type);
            return ActionResult.PROCEED;
        } else {
            connection.sendActionFailed(getId(), "You already placed on bid on that.");
            return ActionResult.RETRY;
        }
    }
}
