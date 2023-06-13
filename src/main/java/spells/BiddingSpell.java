package de.unisaarland.cs.se.selab.spells;

import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.comm.BidType;
import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.model.Player;

public class BiddingSpell extends Spell {
    private final BidType bidTypeToBlock;

    public BiddingSpell(final int id, final SpellType spellType,
                        final BidType bidType, final int slot, final BidType bidTypeToBlock) {
        super(id, spellType, bidType, slot);
        this.bidTypeToBlock = bidTypeToBlock;
        this.magicPointsNeeded = 4;
    }

    @Override
    protected void cast(final Player player, final Model model,
                        final ConnectionWrapper wrapper) {
        //if last year

        if (model.getYear() < model.getMaxYear()
                && player.canBlockBid(bidTypeToBlock, model.getRound())) {
            player.blockOptionBySpell(bidTypeToBlock, model.getRound());
            wrapper.sendBidTypeBlocked(player.getId(), bidTypeToBlock, model.getRound());
        }

    }


}
