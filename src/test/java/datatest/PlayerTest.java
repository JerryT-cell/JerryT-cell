package datatest;

import de.unisaarland.cs.se.selab.comm.BidType;
import de.unisaarland.cs.se.selab.model.Player;
import de.unisaarland.cs.se.selab.model.dungeon.Dungeon;
import de.unisaarland.cs.se.selab.spells.BuffSpell;
import de.unisaarland.cs.se.selab.spells.ResourceSpell;
import de.unisaarland.cs.se.selab.spells.Spell;
import de.unisaarland.cs.se.selab.spells.SpellType;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class PlayerTest {
    Player player = new Player(0, "Jerry", 3, 3, 3, 5, new Dungeon());


    @Test
    void addSpellTest() {
        //add and remove spells to withstand
        player.addSpell(1, new ResourceSpell(1, SpellType.RESOURCE, BidType.FOOD, 2, 2, 2));
        final List<Spell> spells = player.spellsToWithstand(1);
        final Spell spell = spells.remove(0);
        Assertions.assertEquals(spell.getId(), 1);

        //test if it has been removed from the map
        final List<Spell> spells2 = player.spellsToWithstand(1);
        Assertions.assertNull(spells2);

        //add many spells the same season
        player.addSpell(1, new ResourceSpell(1, SpellType.RESOURCE, BidType.FOOD, 2, 2, 2));
        player.addSpell(1, new BuffSpell(2, SpellType.BUFF, BidType.FOOD, 2, 2, 2, 2));
        final List<Spell> spells3 = player.spellsToWithstand(1);

        //assert that the list size is 2
        Assertions.assertEquals(2, spells3.size());

        Assertions.assertEquals(1, spells3.get(0).getId());
        Assertions.assertEquals(2, spells3.get(1).getId());

    }

    @Test
    void blockOptionBySpellTest() {
        player.blockOptionBySpell(BidType.FOOD, 1);
        player.blockOptionBySpell(BidType.IMPS, 1);

        Assertions.assertTrue(player.isLocked(BidType.FOOD, 1));
        Assertions.assertTrue(player.isLocked(BidType.IMPS, 1));
        Assertions.assertFalse(player.isLocked(BidType.NICENESS, 1));

        Assertions.assertFalse(player.canBlockBid(BidType.FOOD, 1));
        Assertions.assertTrue(player.canBlockBid(BidType.NICENESS, 1));


    }

}
