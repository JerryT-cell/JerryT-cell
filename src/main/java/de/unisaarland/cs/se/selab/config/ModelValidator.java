package de.unisaarland.cs.se.selab.config;


import de.unisaarland.cs.se.selab.model.AttackStrategy;
import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.state.BuildingState;
import java.util.HashSet;
import java.util.Set;

/**
 * The model validator checks all constraints before inserting elements into the model builder.
 * <p>
 * The model validator combines the builder pattern with delegation to automatically validate the
 * config before creating the model.
 * It does so by delegating all function calls to another builder similar to a decorator pattern.
 * </p>
 *
 * @param <M> the model class to be built
 */
public class ModelValidator<M> implements ModelBuilderInterface<M> {

    private final ModelBuilderInterface<M> builder;
    private final Set<Integer> monsterIds;
    private final Set<Integer> adventurerIds;
    private final Set<Integer> trapIds;
    private final Set<Integer> roomIds;
    private final Set<Integer> spellIds;
    private final Set<String> setProperties;
    private int years;
    private int maxPlayers;

    public ModelValidator(final ModelBuilderInterface<M> builder) {
        this.builder = builder;
        this.monsterIds = new HashSet<>();
        this.adventurerIds = new HashSet<>();
        this.trapIds = new HashSet<>();
        this.roomIds = new HashSet<>();
        this.setProperties = new HashSet<>();
        this.spellIds = new HashSet<>();
    }

    /////////////////////////////////////////////
    //            HELPER FUNCTIONS             //
    /////////////////////////////////////////////


    private void checkPropertiesSet(final String... properties) {
        for (final String property : properties) {
            if (!setProperties.contains(property)) {
                throw new IllegalArgumentException(
                        String.format("%s is required to be present.", property));
            }
        }
    }

    private void checkBounds(final String attrName, final int lowerBound, final int upperBound,
                             final int value) {
        if (value < lowerBound || value > upperBound) {
            throw new IllegalArgumentException(
                    String.format("%s is required to be between %d and %d.", attrName, lowerBound,
                            upperBound));
        }
    }

    private void checkPositiveZero(final String attrName, final int value) {
        checkBounds(attrName, 0, Integer.MAX_VALUE, value);
    }

    private void checkPositiveNonZero(final String attrName, final int value) {
        checkBounds(attrName, 1, Integer.MAX_VALUE, value);
    }

    private void checkUniqueId(final String attrName, final Set<Integer> set, final int value) {
        if (set.contains(value)) {
            throw new IllegalArgumentException(
                    String.format("%sIds is required to be unique.", attrName));
        }
    }

    private <T> void checkLength(final String attrName, final Set<T> set, final int bound) {
        if (set.size() < bound) {
            throw new IllegalArgumentException(
                    String.format("%s is supposed to have at least %d elements.", attrName, bound));
        }
    }

    //added
    private void commonSpellcheck(final String name, final int id, final int slot,
                                  final boolean other) {
        checkUniqueId(name, this.spellIds, id);
        checkBounds(name, 1, 3, slot);
        checkAdditionalAttribute(name, other);
    }

    private void checkAdditionalAttribute(final String name, final boolean attribute) {
        if (attribute) {
            throw new IllegalArgumentException(
                    String.format("%s is not required to have additional attributes.", name)
            );
        }
    }

    private void checkNoZero(final String name, final int value1, final int value2) {
        if (value1 == 0 && value2 == 0) {
            throw new IllegalArgumentException(
                    String.format(" The values of the attributes of the %s "
                            + "should be greater than 0", name)
            );
        }
    }

    private void checkNoZero(final String name, final int value1, final int value2,
                             final int value3) {
        if (value1 == 0 && value2 == 0 && value3 == 0) {
            throw new IllegalArgumentException(
                    String.format(" The values of the attributes of the %s "
                            + "should be greater than 0", name)
            );
        }
    }


    /////////////////////////////////////////////
    //            BUILDER FUNCTIONS            //
    /////////////////////////////////////////////


    @Override
    public void addMonster(final int id, final int hunger, final int damage, final int evilness,
                           final String attack) {
        checkUniqueId("Monster", this.monsterIds, id);
        checkPositiveZero("Monster " + ModelBuilderInterface.CFG_ID, id);
        checkPositiveZero("Monster " + ModelBuilderInterface.CFG_MONSTER_HUNGER, hunger);
        checkPositiveNonZero("Monster " + ModelBuilderInterface.CFG_MONSTER_DAMAGE, damage);
        checkPositiveZero("Monster " + ModelBuilderInterface.CFG_MONSTER_EVILNESS, evilness);
        this.monsterIds.add(id);
        this.builder.addMonster(id, hunger, damage, evilness, attack);
    }

    @Override
    public void addAdventurer(final int id, final int difficulty, final int healthPoints,
                              final int healValue,
                              final int defuseValue, final boolean charge, final int magicPoints) {
        checkUniqueId("Adventurer", this.adventurerIds, id);
        checkPositiveZero("Adventurer " + ModelBuilderInterface.CFG_ID, id);
        checkPositiveZero("Adventurer " + ModelBuilderInterface.CFG_ADV_DIFFICULTY, difficulty);
        checkPositiveNonZero("Adventurer " + ModelBuilderInterface.CFG_ADV_HEALTH_POINTS,
                healthPoints);
        checkPositiveZero("Adventurer " + ModelBuilderInterface.CFG_ADV_HEAL_VALUE, healValue);
        checkPositiveZero("Adventurer " + ModelBuilderInterface.CFG_ADV_DEFUSE_VALUE, defuseValue);
        if (magicPoints != 0) {
            checkBounds("Adventurer" + ModelBuilderInterface.CFG_ADV_MAGIC_POINTS, 1, 3,
                    magicPoints);
        }
        this.adventurerIds.add(id);
        this.builder.addAdventurer(id, difficulty, healthPoints, healValue, defuseValue, charge,
                magicPoints);
    }

    @Override
    public void addTrap(final int id, final String attack, final int damage) {
        checkUniqueId("Trap", this.trapIds, id);
        checkPositiveZero("Trap " + ModelBuilderInterface.CFG_ID, id);
        checkPositiveNonZero("Trap " + ModelBuilderInterface.CFG_TRAP_DAMAGE, damage);
        if (AttackStrategy.valueOf(attack) == AttackStrategy.TARGETED) {
            throw new IllegalArgumentException(
                    String.format("Trap(%d) is targeted but has no target", id));
        }
        this.trapIds.add(id);
        this.builder.addTrap(id, attack, damage);
    }

    @Override
    public void addTrap(final int id, final String attack, final int damage, final int target) {
        checkUniqueId("Trap", this.trapIds, id);
        checkPositiveZero("Trap " + ModelBuilderInterface.CFG_ID, id);
        checkPositiveNonZero("Trap " + ModelBuilderInterface.CFG_TRAP_DAMAGE, damage);
        checkBounds("Trap " + ModelBuilderInterface.CFG_TRAP_TARGET, 1, 4, target);
        this.trapIds.add(id);
        if (AttackStrategy.valueOf(attack) != AttackStrategy.TARGETED) {
            this.builder.addTrap(id, attack, damage);
        } else {
            this.builder.addTrap(id, attack, damage, target);
        }
    }

    @Override
    public void addRoom(final int id, final int activation, final String restriction,
                        final int food, final int gold,
                        final int imps, final int niceness) {
        checkUniqueId("Room", this.roomIds, id);
        checkPositiveZero("Room " + ModelBuilderInterface.CFG_ID, id);
        checkPositiveNonZero("Room " + ModelBuilderInterface.CFG_ROOM_ACTIVATION, activation);
        checkPositiveZero("Room " + ModelBuilderInterface.CFG_ROOM_FOOD, food);
        checkPositiveZero("Room " + ModelBuilderInterface.CFG_ROOM_GOLD, gold);
        checkPositiveZero("Room " + ModelBuilderInterface.CFG_ROOM_IMPS, imps);
        checkPositiveZero("Room " + ModelBuilderInterface.CFG_ROOM_NICENESS, niceness);
        this.roomIds.add(id);
        this.builder.addRoom(id, activation, restriction, food, gold, imps, niceness);
    }

    @Override
    public void addResourceSpell(final int id, final String spellType, final String bidType,
                                 final int slot, final int food,
                                 final int gold, final boolean other) {
        commonSpellcheck("ResourceSpell ", id, slot, other);
        checkPositiveZero("ResourceSpell " + ModelBuilderInterface.CFG_SPELL_FOOD, food);
        checkPositiveZero("ResourceSpell " + ModelBuilderInterface.CFG_SPELL_GOLD, gold);
        checkNoZero("ResourceSpell", food, gold);

        this.spellIds.add(id);
        this.builder.addResourceSpell(id, spellType, bidType, slot, food, gold, false);

    }

    @Override
    public void addBuffSpell(final int id, final String spellType, final String bidType,
                             final int slot, final int healthPoints,
                             final int defuseValue, final int healValue, final boolean other) {
        commonSpellcheck("BuffSpell ", id, slot, other);
        checkPositiveZero("BuffSpell " + ModelBuilderInterface.CFG_SPELL_HEALTHPOINTS,
                healthPoints);
        checkPositiveZero("BuffSpell " + ModelBuilderInterface.CFG_SPELL_DEFUSEVALUE,
                defuseValue);
        checkPositiveZero("BuffSpell " + ModelBuilderInterface.CFG_SPELL_HEALVALUE,
                healValue);

        checkNoZero("BuffSpell ", healValue, healthPoints, defuseValue);

        this.spellIds.add(id);
        this.builder.addBuffSpell(id, spellType, bidType, slot, healthPoints, defuseValue,
                healValue, false);

    }

    @Override
    public void addRoomSpell(final int id, final String spellType, final String bidType,
                             final int slot, final boolean other) {
        commonSpellcheck("RoomSpell ", id, slot, other);
        this.spellIds.add(id);
        this.builder.addRoomSpell(id, spellType, bidType, slot, false);
    }

    @Override
    public void addBiddingSpell(final int id, final String spellType, final String bidType,
                                final int slot,
                                final String bidTypeBlocked, final boolean other) {
        commonSpellcheck("BiddingSpell ", id, slot, other);
        this.spellIds.add(id);
        this.builder.addBiddingSpell(id, spellType, bidType, slot, bidTypeBlocked, false);
    }

    @Override
    public void addStructuralSpell(final int id, final String spellType, final String bidType,
                                   final int slot,
                                   final String structureEffect, final boolean other) {
        commonSpellcheck("StructuralSpell", id, slot, other);
        this.spellIds.add(id);
        this.builder.addStructuralSpell(id, spellType, bidType, slot, structureEffect, other);
    }


    @Override
    public void setMaxPlayers(final int maxPlayers) {
        this.maxPlayers = maxPlayers;
        this.builder.setMaxPlayers(maxPlayers);
        checkPositiveNonZero(ModelBuilderInterface.CFG_MAX_PLAYERS, maxPlayers);
        setProperties.add(ModelBuilderInterface.CFG_MAX_PLAYERS);
    }

    @Override
    public void setSeed(final long seed) {
        this.builder.setSeed(seed);
        setProperties.add(CFG_SEED);
    }

    @Override
    public void setYears(final int years) {
        this.years = years;
        this.builder.setYears(years);
        checkBounds(ModelBuilderInterface.CFG_YEARS, 1, Integer.MAX_VALUE, years);
        setProperties.add(ModelBuilderInterface.CFG_YEARS);
    }

    @Override
    public void setDungeonSideLength(final int dungeonSideLength) {
        this.builder.setDungeonSideLength(dungeonSideLength);
        checkBounds(ModelBuilderInterface.CFG_DUNGEON_SIDELENGTH, 1, 15, dungeonSideLength);
        setProperties.add(ModelBuilderInterface.CFG_DUNGEON_SIDELENGTH);
    }

    @Override
    public void setInitialFood(final int food) {
        this.builder.setInitialFood(food);
        checkPositiveZero(ModelBuilderInterface.CFG_INITIAL_FOOD, food);
        setProperties.add(ModelBuilderInterface.CFG_INITIAL_FOOD);
    }

    @Override
    public void setInitialGold(final int gold) {
        this.builder.setInitialGold(gold);
        checkPositiveZero(ModelBuilderInterface.CFG_INITIAL_GOLD, gold);
        setProperties.add(ModelBuilderInterface.CFG_INITIAL_GOLD);
    }

    @Override
    public void setInitialImps(final int imps) {
        this.builder.setInitialImps(imps);
        checkPositiveZero(ModelBuilderInterface.CFG_INITIAL_IMPS, imps);
        setProperties.add(ModelBuilderInterface.CFG_INITIAL_IMPS);
    }

    @Override
    public void setInitialEvilness(final int evilness) {
        this.builder.setInitialEvilness(evilness);
        checkBounds(ModelBuilderInterface.CFG_INITIAL_EVILNESS, 0, 15, evilness);
        setProperties.add(ModelBuilderInterface.CFG_INITIAL_EVILNESS);
    }

    @Override
    public M build() {
        checkPropertiesSet(
                ModelBuilderInterface.CFG_SEED,
                ModelBuilderInterface.CFG_MAX_PLAYERS,
                ModelBuilderInterface.CFG_YEARS,
                ModelBuilderInterface.CFG_DUNGEON_SIDELENGTH,
                ModelBuilderInterface.CFG_INITIAL_FOOD,
                ModelBuilderInterface.CFG_INITIAL_GOLD,
                ModelBuilderInterface.CFG_INITIAL_IMPS,
                ModelBuilderInterface.CFG_INITIAL_EVILNESS
        );
        checkLength("Adventurers", this.adventurerIds,
                this.years * (Model.MAX_ROUNDS - 1) * this.maxPlayers);
        final int rounds = this.years * Model.MAX_ROUNDS;
        checkLength("Rooms", this.roomIds, rounds * BuildingState.ROOMS_PER_ROUND);
        checkLength("Traps", this.trapIds, rounds * 4);
        checkLength("Spells", this.spellIds, rounds * 3);
        checkLength("Monsters", this.monsterIds,
                rounds * BuildingState.MONSTERS_PER_ROUND);
        return this.builder.build();
    }

}
