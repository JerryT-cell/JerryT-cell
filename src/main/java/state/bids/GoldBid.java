package de.unisaarland.cs.se.selab.state.bids;

import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.commands.ActionResult;
import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.model.Player;
import de.unisaarland.cs.se.selab.model.dungeon.Dungeon;
import de.unisaarland.cs.se.selab.model.dungeon.Tunnel;
import de.unisaarland.cs.se.selab.state.BuildingState;
import java.util.List;

/**
 * Class for gold bids.
 */
public final class GoldBid extends Bid {

    GoldBid(final Player player, final int slot) {
        super(player, slot);
    }

    @Override
    protected ActionResult bidEvalImpl(final Model model, final ConnectionWrapper connection) {
        final int availableImps = this.player.getImps();
        if (availableImps <= 0) {
            return ActionResult.PROCEED;
        }
        final Dungeon dungeon = this.player.getDungeon();
        final int allowedMiners = this.slot + 1;

        final List<Tunnel> availableTunnels = dungeon.getGraph().stream()
                .filter(tunnel -> tunnel.canMineGold() && !tunnel.isRoom() && !tunnel.isConquered())
                .toList();
        final int maxMiners =
                Math.min(allowedMiners, Math.min(availableImps, availableTunnels.size()));
        final boolean needsSupervision = maxMiners > BuildingState.NO_SUPERVISION_LIMIT;
        final boolean canAffordSupervision = availableImps >= maxMiners + 1;

        final int placedImps;
        final int actualMiners;
        if (canAffordSupervision) {
            actualMiners = maxMiners;
            placedImps = maxMiners + (needsSupervision ? 1 : 0);
            if (needsSupervision) {
                dungeon.addSupervision();
            }
        } else {
            actualMiners = maxMiners - (needsSupervision ? 1 : 0);
            placedImps = actualMiners;
        }

        if (placedImps > 0) {
            availableTunnels.stream().limit(actualMiners).forEach(Tunnel::setGoldMiner);
            this.player.changeImps(-placedImps);
            connection.sendImpsChanged(this.player.getId(), -placedImps);
        }
        return ActionResult.PROCEED;
    }
}
