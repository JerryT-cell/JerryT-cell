package de.unisaarland.cs.se.selab.spells;

import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.comm.BidType;
import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.model.Player;

public class RoomSpell extends Spell {


    public RoomSpell(final int id, final SpellType spellType,
                     final BidType bidType, final int slot) {
        super(id, spellType, bidType, slot);
        magicPointsNeeded = 3;
    }

    @Override
    protected void cast(final Player player, final Model model,
                        final ConnectionWrapper wrapper) {


        //if the season is not present in the no activation list
        if (model.getYear() < model.getMaxYear()
                && !player.getNoActivatingSeasonList().contains(model.getRound())) {

            player.getNoActivatingSeasonList().add(model.getRound());
            wrapper.sendRoomsBlocked(player.getId(), model.getRound());
        }
    }
}
