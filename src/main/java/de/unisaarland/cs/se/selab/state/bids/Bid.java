package de.unisaarland.cs.se.selab.state.bids;

import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.comm.BidType;
import de.unisaarland.cs.se.selab.commands.ActionResult;
import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.model.Player;

/**
 * Superclass for all bid types.
 * <p>
 * Bids are implemented with a command pattern.
 * Bids are created by the factory function {@link Bid#createBid(BidType, Player, int)}.
 * The function {@link Bid#evaluate(Model, ConnectionWrapper)} is the command pattern's
 * {@code execute()} function.
 * Here, it performs some general checks and then calls the function
 * {@link Bid#bidEvalImpl(Model, ConnectionWrapper)} which is overridden by all subclasses and
 * contains the actual implementation of bid evaluation.
 * </p>
 */
public abstract class Bid {

    protected final Player player;
    protected final int slot;

    protected Bid(final Player player, final int slot) {
        this.slot = slot;
        this.player = player;
    }

    /**
     * Evaluate a bid.
     * <p>
     * This method implements some general checks, e.g., player is still alive.
     * If the checks succeed, this method calls {@link Bid#bidEvalImpl(Model, ConnectionWrapper)}.
     * </p>
     *
     * @param model      the model to which the action based changes should apply
     * @param connection the connection to the client to transmit events
     * @return a result that indicates how the game should continue
     */
    public final ActionResult evaluate(final Model model, final ConnectionWrapper connection) {
        if (this.player.isAlive()) {
            return bidEvalImpl(model, connection);
        }
        return ActionResult.PROCEED;
    }

    /**
     * This function contains the implementation of the bid's evaluation functionality and is called
     * when a bid is being evaluated.
     *
     * @param model      the model to which the action based changes should apply
     * @param connection the connection to the client to transmit events
     * @return a result that indicates how the game should continue
     */
    protected abstract ActionResult bidEvalImpl(Model model, ConnectionWrapper connection);

    /**
     * Factory function for bids.
     *
     * @param type   the type of bid to create
     * @param player the player creating the bid
     * @param slot   the slot of the bid on the bidding square
     * @return the created bid
     */
    public static Bid createBid(final BidType type, final Player player, final int slot) {
        return switch (type) {
            case FOOD -> new FoodBid(player, slot);
            case GOLD -> new GoldBid(player, slot);
            case IMPS -> new ImpsBid(player, slot);
            case MONSTER -> new MonsterBid(player, slot);
            case NICENESS -> new NicenessBid(player, slot);
            case ROOM -> new RoomBid(player, slot);
            case TRAP -> new TrapBid(player, slot);
            case TUNNEL -> new TunnelBid(player, slot);
        };
    }

    public int getSlot() {
        return slot;
    }
}
