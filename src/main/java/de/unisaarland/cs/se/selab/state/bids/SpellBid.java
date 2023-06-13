package de.unisaarland.cs.se.selab.state.bids;

import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.commands.ActionResult;
import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.spells.Spell;
import java.util.List;

public class SpellBid extends SpellDecorator {
    public SpellBid(
            final Bid bid,
            final List<Spell> spellTriggered) {
        super(bid, spellTriggered);
    }

    /**
     * evaluate the bid that has been decorated with a spell
     *
     * @param model      the model to which the action based changes should apply
     * @param connection the connection to the client to transmit events
     * @return the return type of the evaluation of the bid
     */

    @Override
    protected ActionResult bidEvalImpl(final Model model, final ConnectionWrapper connection) {
        for (final Spell spell : spellTriggered) {
            connection.sendSpellUnlocked(spell.getId(), player.getId());
            player.addSpell(model.getRound(), spell);
        }
        return bid.bidEvalImpl(model, connection);
    }


}
