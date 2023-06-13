package de.unisaarland.cs.se.selab.model;

/**
 * Class for adventurers.
 */
public class Adventurer {

    private final int id;
    private final int difficulty;
    private int healValue;
    private int defuseValue;
    private final boolean charge;
    private final int maxHealthPoints;
    private int currentHealthPoints;
    private boolean defeated;
    private final int magicPoints;
    private final int maxHealValue;
    private final int maxDefuseValue;

    public Adventurer(final int id, final int difficulty, final int healthPoints,
                      final int healValue, final int defuseValue, final boolean charge,
                      final int magicPoints) {
        this.id = id;
        this.difficulty = difficulty;
        this.maxHealthPoints = healthPoints;
        this.currentHealthPoints = this.maxHealthPoints;
        this.healValue = healValue;
        this.defuseValue = defuseValue;
        this.charge = charge;
        this.defeated = false;
        this.maxHealValue = healValue;
        this.maxDefuseValue = defuseValue;
        this.magicPoints = magicPoints;

    }

    public int getId() {
        return id;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getHealValue() {
        return healValue;
    }

    public int getDefuseValue() {
        return defuseValue;
    }

    /**
     * Heal an adventurer by the given amount.
     * <p>
     * This performs a bounds-check for the adventurers maximal health points before healing.
     * </p>
     *
     * @param amount the amount to heal
     * @return the actual amount of health points the player was healed (after bounds-check)
     */
    public int heal(final int amount) {
        final int effectiveHeal = Math.min(this.maxHealthPoints - this.currentHealthPoints, amount);
        this.currentHealthPoints += effectiveHeal;
        return effectiveHeal;
    }

    /**
     * Damage an adventurer by the given amount.
     * <p>
     * This performs a bounds-check for the adventurers minimal health points before damaging and
     * sets the adventurer's defeated flag if the health points drop to 0.
     * </p>
     *
     * @param amount the amount of damage
     * @return the actual amount of health points the player was damaged (after bounds-check)
     */
    public int damage(final int amount) {
        final int effectiveDamage = Math.min(this.currentHealthPoints, amount);
        this.currentHealthPoints -= effectiveDamage;
        if (this.currentHealthPoints <= 0) {
            this.defeated = true;
        }
        return effectiveDamage;
    }

    public boolean isDefeated() {
        return this.defeated;
    }

    public boolean isCharging() {
        return this.charge;
    }
    /* added */

    public void increaseHealthPoints(final int amount) {
        currentHealthPoints += amount;
    }

    public void increaseDefuseValue(final int amount) {
        defuseValue += amount;
    }

    public void increaseHealValue(final int amount) {
        healValue += amount;
    }

    public void restoreOriginalValues() {

        if (currentHealthPoints > maxHealthPoints) {
            currentHealthPoints = maxHealthPoints;
        }
        if (healValue > maxHealValue) {
            healValue = maxHealValue;
        }
        if (defuseValue > maxDefuseValue) {
            defuseValue = maxDefuseValue;
        }

    }

    public int getMagicPoints() {
        return magicPoints;
    }

    public int getMaxHealValue() {
        return maxHealValue;
    }

    public int getMaxDefuseValue() {
        return maxDefuseValue;
    }

    public int getCurrentHealthPoints() {
        return currentHealthPoints;
    }
}
