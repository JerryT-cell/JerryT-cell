package de.unisaarland.cs.se.selab.systemtest.seedfinder;

public class EmptyRandomNumber extends RandomNumberElement {

    public EmptyRandomNumber(final int sides) {
        super(sides);
    }

    @Override
    public <T> T accept(final RandomNumberVisitor<T> visitor) {
        return visitor.visitEmpty(this);
    }
}