package de.unisaarland.cs.se.selab.systemtest.seedfinder;

public abstract class RandomNumberElement {

    private final int sides;

    public RandomNumberElement(final int sides) {
        this.sides = sides;
    }

    public int getSides() {
        return sides;
    }

    public abstract <T> T accept(RandomNumberVisitor<T> visitor);
}