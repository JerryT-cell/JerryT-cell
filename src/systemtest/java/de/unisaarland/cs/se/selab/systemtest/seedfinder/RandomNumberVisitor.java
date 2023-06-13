package de.unisaarland.cs.se.selab.systemtest.seedfinder;

public interface RandomNumberVisitor<T> {

    public T visitEmpty(EmptyRandomNumber emptyRandomNumber);

    public T visitNumber(RandomNumber randomNumber);

    public T visitShuffle(ShuffleElement shuffleElement);

    public T visitRange(RandomNumberRange randomNumberRange);

    public T visitRandomNumberNotEqual(RandomNumberNotEquals randomNumberNotEquals);
}
