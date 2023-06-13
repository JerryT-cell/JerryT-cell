package de.unisaarland.cs.se.selab.spells;

import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.comm.BidType;
import de.unisaarland.cs.se.selab.model.Adventurer;
import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.model.Player;
import java.util.List;

public class BuffSpell extends Spell {

    private final int healthPoints;
    private final int defuseValue;
    private final int healValue;

    public BuffSpell(final int id, final SpellType spellType,
                     final BidType bidType, final int slot, final int healthPoints,
                     final int defuseValue, final int healValue) {
        super(id, spellType, bidType, slot);
        this.healthPoints = healthPoints;
        this.defuseValue = defuseValue;
        this.healValue = healValue;
        this.magicPointsNeeded = 2;
    }

    @Override
    protected void cast(final Player player, final Model model,
                        final ConnectionWrapper wrapper) {


        final List<Adventurer> adventurers = player.getDungeon().getAllAdventurers();

        for (final Adventurer adventurer : adventurers) {
            adventurer.increaseHealthPoints(healthPoints);
        }

        if (healValue > 0) {
            adventurers.stream().filter(adventurer -> adventurer.getMaxHealValue() > 0)
                    .forEach(ad -> ad.increaseHealValue(healValue));
        }
        if (defuseValue > 0) {
            adventurers.stream().filter(adventurer -> adventurer.getMaxDefuseValue() > 0)
                    .forEach(ad -> ad.increaseDefuseValue(defuseValue));
        }


    }


}
