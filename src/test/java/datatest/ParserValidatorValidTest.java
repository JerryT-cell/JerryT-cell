package datatest;

import de.unisaarland.cs.se.selab.comm.BidType;
import de.unisaarland.cs.se.selab.config.ConfigParser;
import de.unisaarland.cs.se.selab.config.ModelBuilder;
import de.unisaarland.cs.se.selab.config.ModelBuilderInterface;
import de.unisaarland.cs.se.selab.config.ModelValidator;
import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.spells.ResourceSpell;
import de.unisaarland.cs.se.selab.spells.Spell;
import de.unisaarland.cs.se.selab.spells.SpellType;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the correct configParser
 */
class ParserValidatorValidTest {

    ModelBuilderInterface<Model> builder;
    Model model;


    @BeforeEach
    void setup() throws IOException {
        final String path;
        path = "src/main/resources/configuration.json";
        builder = new ModelValidator<>(new ModelBuilder());
        final String jsonText = Files.readString(Paths.get(path), StandardCharsets.UTF_8);
        builder.setSeed(42);
        model = ConfigParser.parse(jsonText, builder);


    }

    @Test
    void generalMagicPointsTest() {
        Assertions.assertEquals(1, model.getAdventurers().get(0).getMagicPoints());
        Assertions.assertEquals(0, model.getAdventurers().get(1).getMagicPoints());
        Assertions.assertEquals(3, model.getAdventurers().get(6).getMagicPoints());
        Assertions.assertEquals(1, model.getAdventurers().get(13).getMagicPoints());
        Assertions.assertEquals(0, model.getAdventurers().get(15).getMagicPoints());
        Assertions.assertEquals(3, model.getAdventurers().get(21).getMagicPoints());

    }

    @Test
    void generalSpellTest() {
        //size
        Assertions.assertEquals(24, model.getSpells().size());

        //per spell
        Spell spell = model.getSpells().get(0);
        Assertions.assertEquals(0, spell.getId());
        Assertions.assertEquals(SpellType.RESOURCE, spell.getSpellType());
        Assertions.assertEquals(BidType.FOOD, spell.getBidType());
        Assertions.assertEquals(2, spell.getSlot());
        Assertions.assertEquals(2, ((ResourceSpell) spell).getFood());

        spell = model.getSpells().get(2);
        Assertions.assertEquals(2, spell.getId());
        Assertions.assertEquals(SpellType.STRUCTURE, spell.getSpellType());
        Assertions.assertEquals(BidType.TRAP, spell.getBidType());
        Assertions.assertEquals(1, spell.getSlot());

        spell = model.getSpells().get(19);
        Assertions.assertEquals(19, spell.getId());
        Assertions.assertEquals(SpellType.BUFF, spell.getSpellType());
        Assertions.assertEquals(BidType.ROOM, spell.getBidType());
        Assertions.assertEquals(3, spell.getSlot());

        spell = model.getSpells().get(15);
        Assertions.assertEquals(15, spell.getId());
        Assertions.assertEquals(SpellType.BIDDING, spell.getSpellType());
        Assertions.assertEquals(BidType.NICENESS, spell.getBidType());
        Assertions.assertEquals(2, spell.getSlot());

        spell = model.getSpells().get(16);
        Assertions.assertEquals(16, spell.getId());
        Assertions.assertEquals(SpellType.ROOM, spell.getSpellType());
        Assertions.assertEquals(BidType.TUNNEL, spell.getBidType());
        Assertions.assertEquals(1, spell.getSlot());


    }


}
