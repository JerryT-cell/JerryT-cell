package de.unisaarland.cs.se.selab.systemtest.seedfinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class EvalVisitor implements RandomNumberVisitor<Boolean> {

    final Random random;

    public EvalVisitor(final Random random) {
        this.random = random;
    }

    @Override
    public Boolean visitNumber(final RandomNumber randomNumber) {
        final var r = random.nextInt(randomNumber.getSides());
        return r == randomNumber.getNumber();
    }

    @Override
    public Boolean visitEmpty(final EmptyRandomNumber emptyRandomNumber) {
        return random.nextInt(emptyRandomNumber.getSides()) >= 0; // Need to return true and use r
    }

    @Override
    public Boolean visitShuffle(final ShuffleElement shuffleElement) {
        final var list = new ArrayList<Integer>();

        for (int i = 0; i < shuffleElement.getSides(); i++) {
            list.add(i);
        }

        Collections.shuffle(list, random);
        return Boolean.TRUE;
    }

    @Override
    public Boolean visitRange(final RandomNumberRange randomNumberRange) {
        final var r = random.nextInt(randomNumberRange.getSides());
        return r <= randomNumberRange.getEnd() && r >= randomNumberRange.getStart();
    }

    @Override
    public Boolean visitRandomNumberNotEqual(final RandomNumberNotEquals randomNumberNotEquals) {
        final var r = random.nextInt(randomNumberNotEquals.getSides());
        return r != randomNumberNotEquals.getNumber();
    }
}
