package de.unisaarland.cs.se.selab.model;

/**
 * Class for monsters.
 */
public class Monster extends DefensiveMeasure {

    private final int hunger;
    private final int evilness;
    private boolean used;


    public Monster(final int id, final int hunger, final int damage, final int evilness,
                   final AttackStrategy attackStrategy) {
        super(id, damage, attackStrategy);
        this.hunger = hunger;
        this.evilness = evilness;
        this.used = false;
    }

    public int getHunger() {
        return this.hunger;
    }

    public int getEvilness() {
        return this.evilness;
    }

    public void setUsed(final boolean used) {
        this.used = used;
    }

    public boolean isUsed() {
        return this.used;
    }
}
