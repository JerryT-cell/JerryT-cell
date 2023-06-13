package de.unisaarland.cs.se.selab.config;

import de.unisaarland.cs.se.selab.spells.SpellType;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Parses the config file and builds the model from the config using a {@link ModelBuilder}.
 */
public class ConfigParser {

    public static <M> M parse(final String config, final ModelBuilderInterface<M> builder) {
        final JSONObject json = new JSONObject(config);
        builder.setMaxPlayers(json.getInt(ModelBuilderInterface.CFG_MAX_PLAYERS));
        builder.setYears(json.getInt(ModelBuilderInterface.CFG_YEARS));
        builder.setDungeonSideLength(json.getInt(ModelBuilderInterface.CFG_DUNGEON_SIDELENGTH));
        builder.setInitialFood(json.optInt(ModelBuilderInterface.CFG_INITIAL_FOOD, 3));
        builder.setInitialGold(json.optInt(ModelBuilderInterface.CFG_INITIAL_GOLD, 3));
        builder.setInitialImps(json.optInt(ModelBuilderInterface.CFG_INITIAL_IMPS, 3));
        builder.setInitialEvilness(json.optInt(ModelBuilderInterface.CFG_INITIAL_EVILNESS, 5));
        final JSONArray monsters = json.getJSONArray(ModelBuilderInterface.CFG_MONSTERS);
        for (int i = 0; i < monsters.length(); i++) {
            ConfigParser.parseMonster(monsters.getJSONObject(i), builder);
        }
        final JSONArray adventurers = json.getJSONArray(ModelBuilderInterface.CFG_ADVENTURERS);
        for (int i = 0; i < adventurers.length(); i++) {
            ConfigParser.parseAdventurer(adventurers.getJSONObject(i), builder);
        }
        final JSONArray traps = json.getJSONArray(ModelBuilderInterface.CFG_TRAPS);
        for (int i = 0; i < traps.length(); i++) {
            ConfigParser.parseTrap(traps.getJSONObject(i), builder);
        }
        final JSONArray rooms = json.getJSONArray(ModelBuilderInterface.CFG_ROOMS);
        for (int i = 0; i < rooms.length(); i++) {
            ConfigParser.parseRoom(rooms.getJSONObject(i), builder);
        }
        final JSONArray spells = json.getJSONArray(ModelBuilderInterface.CFG_SPELL);
        for (int i = 0; i < spells.length(); i++) {
            ConfigParser.parseSpell(spells.getJSONObject(i), builder);
        }

        return builder.build();
    }

    private static <M> void parseMonster(final JSONObject json,
                                         final ModelBuilderInterface<M> builder) {
        final int id = json.getInt(ModelBuilderInterface.CFG_ID);
        final int hunger = json.optInt(ModelBuilderInterface.CFG_MONSTER_HUNGER);
        final int damage = json.getInt(ModelBuilderInterface.CFG_MONSTER_DAMAGE);
        final int evilness = json.optInt(ModelBuilderInterface.CFG_MONSTER_EVILNESS);
        final String attack = json.getString(ModelBuilderInterface.CFG_MONSTER_ATK_STRATEGY);
        builder.addMonster(id, hunger, damage, evilness, attack);
    }

    private static <M> void parseAdventurer(final JSONObject json,
                                            final ModelBuilderInterface<M> builder) {
        final int id = json.getInt(ModelBuilderInterface.CFG_ID);
        final int difficulty = json.getInt(ModelBuilderInterface.CFG_ADV_DIFFICULTY);
        final int healthPoints = json.getInt(ModelBuilderInterface.CFG_ADV_HEALTH_POINTS);
        final int healValue = json.optInt(ModelBuilderInterface.CFG_ADV_HEAL_VALUE);
        final int defuseValue = json.optInt(ModelBuilderInterface.CFG_ADV_DEFUSE_VALUE);
        final Number magicPoints = json.optNumber(ModelBuilderInterface.CFG_ADV_MAGIC_POINTS);
        final boolean charge = json.optBoolean(ModelBuilderInterface.CFG_ADV_CHARGE);
        builder.addAdventurer(id, difficulty, healthPoints, healValue, defuseValue, charge,
                transNumber(magicPoints));
    }

    private static <M> void parseTrap(final JSONObject json,
                                      final ModelBuilderInterface<M> builder) {
        final int id = json.getInt(ModelBuilderInterface.CFG_ID);
        final String attack = json.getString(ModelBuilderInterface.CFG_TRAP_ATK_STRATEGY);
        final int damage = json.getInt(ModelBuilderInterface.CFG_TRAP_DAMAGE);
        if (json.has(ModelBuilderInterface.CFG_TRAP_TARGET)) {
            builder.addTrap(
                    id, attack, damage, json.getInt(ModelBuilderInterface.CFG_TRAP_TARGET));
        } else {
            builder.addTrap(id, attack, damage);
        }
    }

    private static <M> void parseRoom(final JSONObject json,
                                      final ModelBuilderInterface<M> builder) {
        final int id = json.getInt(ModelBuilderInterface.CFG_ID);
        final int activation = json.getInt(ModelBuilderInterface.CFG_ROOM_ACTIVATION);
        final String restriction = json.getString(ModelBuilderInterface.CFG_ROOM_RESTRICTION);
        final int food = json.optInt(ModelBuilderInterface.CFG_ROOM_FOOD);
        final int gold = json.optInt(ModelBuilderInterface.CFG_ROOM_GOLD);
        final int imps = json.optInt(ModelBuilderInterface.CFG_ROOM_IMPS);
        final int niceness = json.optInt(ModelBuilderInterface.CFG_ROOM_NICENESS);
        builder.addRoom(id, activation, restriction, food, gold, imps, niceness);
    }

    private static <M> void parseSpell(final JSONObject json,
                                       final ModelBuilderInterface<M> builder) {

        final int id = json.getInt(ModelBuilderInterface.CFG_ID);
        final String spellType = json.getString(ModelBuilderInterface.CFG_SPELL_SPELLTYPE);
        final String bidType = json.getString(ModelBuilderInterface.CFG_SPELL_BIDTYPE);
        final int slot = json.getInt(ModelBuilderInterface.CFG_SPELL_SLOT);
        final Number food = json.optNumber(ModelBuilderInterface.CFG_SPELL_FOOD);
        final Number gold = json.optNumber(ModelBuilderInterface.CFG_SPELL_GOLD);
        final Number healthPoints = json.optNumber(ModelBuilderInterface.CFG_SPELL_HEALTHPOINTS);
        final Number defuseValue = json.optNumber(ModelBuilderInterface.CFG_SPELL_DEFUSEVALUE);
        final Number healValue = json.optNumber(ModelBuilderInterface.CFG_SPELL_HEALVALUE);
        final String structureEffect =
                json.optString(ModelBuilderInterface.CFG_SPELL_STRUCTEFFECT);
        final String bidTypeBlocked =
                json.optString(ModelBuilderInterface.CFG_SPELL_BIDTYPEBLOCKED);
        //build the correct spell
        final SpellType actualSpellType = SpellType.valueOf(spellType);

        //these booleans make sure that there's no other detail apart from what the spells
        // require

        final boolean noOtherResource = healthPoints != null || defuseValue != null
                || healValue != null || !("".equals(structureEffect))
                || !("".equals(bidTypeBlocked));

        final boolean noOtherBuff = food != null || gold != null || !("".equals(structureEffect))
                || !("".equals(bidTypeBlocked));

        final boolean noOtherStructure = food != null || gold != null || healthPoints != null
                || defuseValue != null || healValue != null || !("".equals(bidTypeBlocked));

        final boolean noOtherRoom = food != null || gold != null || healthPoints != null
                || defuseValue != null || healValue != null || !("".equals(structureEffect))
                || !("".equals(bidTypeBlocked));

        final boolean noOtherBidding = food != null || gold != null || healthPoints != null
                || defuseValue != null || healValue != null || !("".equals(structureEffect));


        switch (actualSpellType) {
            case RESOURCE -> {

                builder.addResourceSpell(id, spellType, bidType, slot, transNumber(food),
                        transNumber(gold), noOtherResource);
            }


            case BUFF -> {
                builder.addBuffSpell(id, spellType, bidType, slot, transNumber(healthPoints),
                        transNumber(defuseValue), transNumber(healValue), noOtherBuff);

            }

            case STRUCTURE -> {

                builder.addStructuralSpell(id, spellType, bidType, slot, structureEffect,
                        noOtherStructure);
            }

            case ROOM -> {

                builder.addRoomSpell(id, spellType, bidType, slot, noOtherRoom);
            }

            case BIDDING -> {
                builder.addBiddingSpell(id, spellType, bidType, slot, bidTypeBlocked,
                        noOtherBidding);
            }

            default -> {
                throw new AssertionError("this kind of spell don't exist");
            }

        }

    }

    /**
     * if the attribute == 0 change to -1, if number is null change to zero, if number is present,
     * return it.
     *
     * @param number the attribute
     * @return the value of the attribute of the spell
     */

    private static int transNumber(final Number number) {
        if (number != null) {
            if (number.intValue() == 0) {
                return -1;
            } else {
                return number.intValue();
            }
        }
        return 0;
    }


}
