package de.unisaarland.cs.se.selab.model;

/**
 * Class for traps.
 */
public class Trap extends DefensiveMeasure {

    public Trap(final int id, final AttackStrategy attackStrategy, final int damage) {
        super(id, damage, attackStrategy);
    }

    public Trap(final int id, final AttackStrategy attackStrategy, final int damage,
                final int target) {
        super(id, damage, attackStrategy);
        setTarget(target);
    }
}
