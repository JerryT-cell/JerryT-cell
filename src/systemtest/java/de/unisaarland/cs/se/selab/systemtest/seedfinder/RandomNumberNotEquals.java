package de.unisaarland.cs.se.selab.systemtest.seedfinder;

public class RandomNumberNotEquals extends RandomNumberElement {

    private final int number;

    public RandomNumberNotEquals(final int sides, final int number) {
        super(sides);
        this.number = number;
    }

    @Override
    public <T> T accept(final RandomNumberVisitor<T> visitor) {
        return visitor.visitRandomNumberNotEqual(this);
    }

    public int getNumber() {
        return number;
    }
}
