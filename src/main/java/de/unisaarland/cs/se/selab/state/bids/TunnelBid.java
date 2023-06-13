package de.unisaarland.cs.se.selab.state.bids;


import de.unisaarland.cs.se.selab.ConnectionUtils;
import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.commands.ActionResult;
import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.model.Player;
import de.unisaarland.cs.se.selab.state.State;

/**
 * Class for tunnel bids.
 */
public final class TunnelBid extends Bid {

    TunnelBid(final Player player, final int slot) {
        super(player, slot);
    }

    @Override
    protected ActionResult bidEvalImpl(final Model model, final ConnectionWrapper connection) {
        this.player.setNumTunnelDigsAllowed(this.slot + 1);
        connection.sendDigTunnel(this.player.getId());
        return ConnectionUtils.executePlayerCommand(model, connection, State.Phase.PLACING_TUNNEL,
                this.player);
    }
}
