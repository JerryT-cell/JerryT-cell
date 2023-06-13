package de.unisaarland.cs.se.selab.spells;

import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.comm.BidType;
import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.model.Player;

public class ResourceSpell extends Spell {

    private final int food;
    private final int gold;

    public ResourceSpell(final int id, final SpellType spellType,
                         final BidType bidType, final int slot, final int food, final int gold) {
        super(id, spellType, bidType, slot);
        this.food = food;
        this.gold = gold;
        this.magicPointsNeeded = 1;
    }

    @Override
    protected void cast(final Player player, final Model model,
                        final ConnectionWrapper wrapper) {

        //sendFoodChanged
        if (food > 0 && player.getFood() > 0) {
            final int amountReduced = player.reduceFood(food);
            wrapper.sendFoodChanged(player.getId(), -amountReduced);
        }

        //sendGoldChanged
        if (gold > 0 && player.getGold() > 0) {
            final int amountReduced = player.reduceGold(gold);
            wrapper.sendGoldChanged(player.getId(), -amountReduced);
        }

    }

    public int getFood() {
        return food;
    }

    public int getGold() {
        return gold;
    }
}
