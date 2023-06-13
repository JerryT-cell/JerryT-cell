package de.unisaarland.cs.se.selab.systemtest.seedfinder;

import java.util.ArrayList;
import java.util.List;

public class RandomNumberBuilder {

    private final List<RandomNumberElement> randomList;

    public RandomNumberBuilder() {
        this.randomList = new ArrayList<>();
    }

    public void addNumber(final int sides, final int number) {
        this.randomList.add(new RandomNumber(sides, number));
    }

    public void addMultipleNumbers(final List<Integer> listOfSides,
            final List<Integer> listOfNumbers) {
        if (listOfNumbers.size() != listOfSides.size()) {
            return;
        }

        for (int i = 0; i < listOfNumbers.size(); i++) {
            addNumber(listOfSides.get(i), listOfNumbers.get(i));
        }
    }

    public void addEmpty(final int sides) {
        this.randomList.add(new EmptyRandomNumber(sides));
    }

    public void addMultipleEmpty(final int sides, final int amount) {
        for (int i = 0; i < amount; i++) {
            addEmpty(sides);
        }
    }

    public void addMultipleEmpty(final Iterable<Integer> listOfSides) {
        for (final var sides : listOfSides) {
            addEmpty(sides);
        }
    }

    public void addShuffle(final int length) {
        this.randomList.add(new ShuffleElement(length));
    }

    public void addRange(final int sides, final int start, final int end) {
        this.randomList.add(new RandomNumberRange(start, end, sides));
    }

    public void addNotEqual(final int sides, final int number) {
        this.randomList.add(new RandomNumberNotEquals(sides, number));
    }

    public List<RandomNumberElement> get() {
        return this.randomList;
    }
}
