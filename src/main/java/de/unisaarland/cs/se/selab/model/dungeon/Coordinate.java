package de.unisaarland.cs.se.selab.model.dungeon;

import java.util.Arrays;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * A simple 2-dimensional coordinate.
 *
 * @param posX the coordinate's horizontal position
 * @param posY the coordinate's vertical position
 */
public record Coordinate(int posX, int posY) {

    public enum Direction {
        NORTH,
        EAST,
        SOUTH,
        WEST,

    }

    /**
     * Get the neighbouring coordinate in the given direction.
     *
     * @param direction the direction of the neighbor
     * @return the neighbouring coordinate
     */
    @NotNull
    public Coordinate getNeighbor(final Direction direction) {
        return switch (direction) {
            case EAST  -> new Coordinate(posX() + 1, posY());
            case NORTH -> new Coordinate(posX(), posY() - 1);
            case SOUTH -> new Coordinate(posX(), posY() + 1);
            case WEST  -> new Coordinate(posX() - 1, posY());
        };
    }

    /**
     * Get a list of neighbouring coordinates.
     *
     * @return a list of neighbouring coordinates
     */
    public List<Coordinate> getNeighbours() {
        return Arrays.stream(Direction.values()).map(this::getNeighbor).toList();
    }
}
