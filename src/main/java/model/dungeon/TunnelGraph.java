package de.unisaarland.cs.se.selab.model.dungeon;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Graph representation of the dungeon's tunnels and rooms.
 */
public class TunnelGraph {

    private static final int ENTRY_X = 0;
    private static final int ENTRY_Y = 0;

    private final Map<Coordinate, Tunnel> coordinateTunnelMap;
    private final Map<Tunnel, Coordinate> tunnelCoordinateMap;
    private final Tunnel entryPoints;

    public TunnelGraph() {
        final Coordinate coordinate = new Coordinate(ENTRY_X, ENTRY_Y);
        final Tunnel entryPoint = new Tunnel(coordinate, false);
        this.coordinateTunnelMap = new HashMap<>();
        this.coordinateTunnelMap.put(coordinate, entryPoint);
        this.tunnelCoordinateMap = new HashMap<>();
        this.tunnelCoordinateMap.put(entryPoint, coordinate);
        this.entryPoints = entryPoint;
    }

    public Optional<Tunnel> getTunnel(final Coordinate coordinate) {
        return Optional.ofNullable(this.coordinateTunnelMap.get(coordinate));
    }

    /**
     * Get all rooms of the dungeon sorted by their id.
     *
     * @return the sorted list of rooms
     */
    public List<Room> getRooms() {
        return this.stream()
                .map(Tunnel::getRoom)
                .filter(Optional::isPresent).map(Optional::get)
                .sorted(Comparator.comparing(Room::getId))
                .toList();
    }

    public int getNumRooms() {
        return (int) this.stream().filter(Tunnel::isRoom).count();
    }

    public int getNumTunnels() {
        return this.coordinateTunnelMap.size() - getNumRooms();
    }

    public List<Tunnel> getNeighbours(final Coordinate coordinate) {
        return coordinate.getNeighbours().stream()
                .map(this.coordinateTunnelMap::get)
                .filter(Objects::nonNull)
                .toList();
    }

    public void addTunnel(final Tunnel tunnel) {
        this.coordinateTunnelMap.put(tunnel.getCoordinate(), tunnel);
        this.tunnelCoordinateMap.put(tunnel, tunnel.getCoordinate());
    }

    /**
     * Create a stream with all tunnels in the dungeon.
     *
     * @return a stream of all tunnels in the dungeon
     */
    public Stream<Tunnel> stream() {
        return this.tunnelCoordinateMap.keySet().stream();
    }

    /**
     * Check whether the given tunnel matches the conditions for being the next battleground.
     * <p>
     * The conditions are:
     * 1. the tunnel is unconquered
     * 2. there is no other tunnel with a shorter distance to the entrance (0, 0)
     * <p>
     * This function performs an optimized BFS through the graph that exploits the fact that in each
     * step, the worklist ({@code depthLevel}) contains all nodes with the same distance from the
     * origin.
     * That means, we can exit the loop as soon as we find one unconquered tile and then only need
     * to check whether the given tile is contained in the worklist.
     * </p>
     *
     * @param tunnel the tunnel to check
     * @return whether the tunnel is a valid battleground
     */
    public boolean isClosestUnconqueredTile(final Tunnel tunnel) {
        // Provided invalid tunnel tile or no unconquered tiles are left at all.
        if (!this.tunnelCoordinateMap.containsKey(tunnel)
                || this.stream().allMatch(Tunnel::isConquered)) {
            return false;
        }

        final Set<Tunnel> visited = new HashSet<>();
        Set<Tunnel> depthLevel = new HashSet<>(List.of(this.entryPoints));
        while (!depthLevel.isEmpty()) {
            visited.addAll(depthLevel);
            final Set<Tunnel> unconqueredTunnels =
                    depthLevel.stream().filter(t -> !t.isConquered()).collect(Collectors.toSet());

            if (!unconqueredTunnels.isEmpty()) {
                return unconqueredTunnels.contains(tunnel);
            }

            // Next level contains all unvisited neighbors of the current level
            depthLevel = depthLevel.stream()
                    .flatMap(node -> getNeighbours(node.getCoordinate()).stream())
                    .filter(node -> !visited.contains(node))
                    .collect(Collectors.toSet());
        }
        return false;
    }

    public <T> List<T> map(final Function<Tunnel, T> function) {
        final List<T> result = new ArrayList<>(this.tunnelCoordinateMap.size());
        for (final Tunnel tunnel : this.tunnelCoordinateMap.keySet()) {
            result.add(function.apply(tunnel));
        }
        return result;
    }

    /**
     * check for the closest room to the adventurer in the dungeon
     *
     * @param battleGround where the adventurers are found
     * @return an optional Tunnel, which has the closest Room
     * or an optional empty if no room was found
     */
    public Optional<Tunnel> closestRoomToAdventurer(final Tunnel battleGround) {


        final Set<Tunnel> visited = new HashSet<>();
        Set<Tunnel> depthLevel = new HashSet<>(List.of(battleGround));
        while (!depthLevel.isEmpty()) {
            visited.addAll(depthLevel);
            final Set<Tunnel> tunnelsWithRooms =
                    depthLevel.stream().filter(Tunnel::isRoom).collect(Collectors.toSet());

            if (!tunnelsWithRooms.isEmpty()) {
                return tunnelsWithRooms.stream()
                        .min(Comparator.comparing(t -> t.getRoom().get().getId()));
            }

            // Next level contains all unvisited neighbors of the current level
            depthLevel = depthLevel.stream()
                    .flatMap(node -> getNeighbours(node.getCoordinate()).stream())
                    .filter(node -> !visited.contains(node))
                    .collect(Collectors.toSet());
        }

        return Optional.empty();
    }


}
