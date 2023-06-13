package datatest;

import de.unisaarland.cs.se.selab.config.ModelBuilder;
import de.unisaarland.cs.se.selab.config.ModelBuilderInterface;
import de.unisaarland.cs.se.selab.config.ModelValidator;
import de.unisaarland.cs.se.selab.model.Model;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ModelValidatorTest {

    ModelBuilderInterface<Model> builder;


    @BeforeEach
    void setUp() {
        builder = new ModelValidator<>(new ModelBuilder());

    }

    @Test
    void adventurerTest() {

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> builder.addAdventurer(0, 2, 2, 2, 0, true, -2),
                "magic points is less than zero");

        builder.addAdventurer(0, 2, 2, 2, 0, true, 3);
    }


    @Test
    void resourceSpellTest() {
        //test unique id
        builder.addResourceSpell(0, "RESOURCE", "FOOD", 2, 2, 3, false);
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> builder.addResourceSpell(0, "RESOURCE", "GOLD", 1, 4, 0, false),
                "id not unique");

        builder.addResourceSpell(1, "RESOURCE", "GOLD", 2, 2, 3, false);

        //test bidType
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> builder.addResourceSpell(2, "RESOURCE", "noBidType", 2, 2, 3, false),
                "slot should be between 1 - 3");

        //test slot
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> builder.addResourceSpell(3, "RESOURCE", "NICENESS", 0, 2, 3, false),
                "slot should be between 1 - 3");
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> builder.addResourceSpell(4, "RESOURCE", "NICENESS", 4, 2, 3, false),
                "slot should be between 1 - 3");

        //test food, gold
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> builder.addResourceSpell(5, "RESOURCE", "NICENESS", 1, -1, 3, false),
                "food should be >= 0");
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> builder.addResourceSpell(6, "RESOURCE", "NICENESS", 2, 0, -2, false),
                "gold should be >= 0");
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> builder.addResourceSpell(7, "RESOURCE", "NICENESS", 3, 0, 0, false),
                "one of gold or food should be >0");

        //test other
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> builder.addResourceSpell(8, "RESOURCE", "NICENESS", 3, 0, 3, true),
                "other details are present");


    }

    @Test
    void buffSpellTest() {

        //test details
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> builder.addBuffSpell(0, "BUFF", "NICENESS", 1, -1, 3, 4, false),
                "healthPoints >=0");
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> builder.addBuffSpell(1, "BUFF", "NICENESS", 2, 0, -1, 4, false),
                "defuseValue >=0 ");
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> builder.addBuffSpell(2, "BUFF", "NICENESS", 3, 0, 2, -1, false),
                "healValue >=0 ");

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> builder.addBuffSpell(3, "BUFF", "NICENESS", 2, 0, 0, 0, false));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> builder.addBuffSpell(4, "BUFF", "NICENESS", 3, 0, 3, 0, true),
                "other details are present");

    }

    @Test
    void roomSpellTest() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> builder.addRoomSpell(2, "ROOM", "NICENESS", 3, true),
                "other details are present");
        builder.addRoomSpell(3, "ROOM", "FOOD", 2, false);


    }

    @Test
    void structuralSpell() {
        //empty structure effect
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> builder.addStructuralSpell(2, "STRUCTURE", "NICENESS", 3, "", false),
                "no structural effect");

        //other details
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> builder.addStructuralSpell(0, "STRUCTURE", "NICENESS", 3, "CONQUER", true),
                "other details are present");
        builder.addStructuralSpell(3, "STRUCTURE", "NICENESS", 2, "CONQUER", false);


    }

    @Test
    void biddingSpellTest() {
        //bidType empty
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> builder.addBiddingSpell(3, "BIDDING", "tunnel", 1, "", false));

        //other details
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> builder.addBiddingSpell(2, "BIDDING", "NICENESS", 3, "FOOD", true),
                "other details are present");

        builder.addBiddingSpell(4, "BIDDING", "NICENESS", 2, "GOLD", false);

    }

}
