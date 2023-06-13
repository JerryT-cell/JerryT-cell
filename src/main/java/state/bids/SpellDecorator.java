package de.unisaarland.cs.se.selab.state.bids;

import de.unisaarland.cs.se.selab.spells.Spell;
import java.util.List;

public abstract class SpellDecorator extends Bid {

    protected final Bid bid;
    protected final List<Spell> spellTriggered;

    protected SpellDecorator(final Bid bidToDecorate, final List<Spell> spellTriggered) {
        super(bidToDecorate.player, bidToDecorate.getSlot());
        this.bid = bidToDecorate;
        this.spellTriggered = spellTriggered;

    }


}
