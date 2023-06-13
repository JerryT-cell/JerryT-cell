package datatest;

import de.unisaarland.cs.se.selab.comm.BidType;
import de.unisaarland.cs.se.selab.model.dungeon.Coordinate;
import de.unisaarland.cs.se.selab.model.dungeon.Room;
import de.unisaarland.cs.se.selab.model.dungeon.Tunnel;
import de.unisaarland.cs.se.selab.model.dungeon.TunnelGraph;
import java.util.EnumMap;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class TunnelGraphTest {
    TunnelGraph tunnelGraph;

    void createDungeon(final TunnelGraph tunnelGraph, final int[][] tileSettingArray) {
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                if (tileSettingArray[y][x] == 1) {
                    final Tunnel tunnel = new Tunnel(new Coordinate(x, y), false);
                    tunnelGraph.addTunnel(tunnel);
                } else if (tileSettingArray[y][x] == 2) {
                    final Tunnel tunnel = new Tunnel(new Coordinate(x, y), false);
                    tunnel.setConquered(true);
                    tunnelGraph.addTunnel(tunnel);
                }
            }
        }
    }


    @BeforeEach
    void setUp() {
        tunnelGraph = new TunnelGraph();

    }

    @Test
    void testDestroyRoom() {

        /*
         *  1 means unconquered, 2 means conquered 3 means unconquered
         *  and room is present, 4 means conquered and room is present
         *  0 = null
         * */

        final int[][] tileSettingArray = {
                {2, 2, 2, 1},
                {2, 0, 1, 0},
                {2, 1, 1, 1},
                {1, 0, 1, 0},
        };

        createDungeon(tunnelGraph, tileSettingArray);
        final Optional<Tunnel> tunnelOp = tunnelGraph.getTunnel(new Coordinate(1, 0));
        if (tunnelOp.isEmpty()) {
            throw new AssertionError("not worked");
        }
        final Tunnel tunnel = tunnelOp.get();
        final EnumMap<BidType, Integer> production = new EnumMap<>(BidType.class);
        production.put(BidType.FOOD, 2);
        tunnel.setRoom(new Room(0, 2,
                Room.BuildingRestriction.UPPER_HALF, production));
        tunnel.setConquered(true);
        final Optional<Tunnel> tunnelOp1 = tunnelGraph.getTunnel(new Coordinate(2, 3));
        if (tunnelOp1.isEmpty()) {
            throw new AssertionError("not worked");
        }
        final Tunnel tunnel1 = tunnelOp1.get();
        tunnel1.setRoom(new Room(1, 3,
                Room.BuildingRestriction.UPPER_HALF, production));
        final Optional<Tunnel> tunnelOp3 = tunnelGraph.getTunnel(new Coordinate(3, 2));
        if (tunnelOp3.isEmpty()) {
            throw new AssertionError("not worked");
        }
        final Tunnel tunnel2 = tunnelOp3.get();
        tunnel2.setRoom(new Room(2, 4,
                Room.BuildingRestriction.UPPER_HALF, production));

        final Optional<Tunnel> tunnelOp4 = tunnelGraph.getTunnel(new Coordinate(0, 2));
        if (tunnelOp4.isEmpty()) {
            throw new AssertionError("not worked");
        }
        final Tunnel tunnel3 = tunnelOp4.get();
        tunnel3.setConquered(true);
        tunnel3.setRoom(new Room(3, 4,
                Room.BuildingRestriction.UPPER_HALF, production));

        /*
         * Representation of Tunnel
         * 2 4 2 1
         * 2 0 1 0
         * 4 1 1 3
         * 1 0 3 0
         * */

        Assertions.assertEquals(3,
                tunnelGraph.closestRoomToAdventurer(new Tunnel(new Coordinate(1, 2), false)).get()
                        .getRoom().get().getId());
        Assertions.assertNotEquals(2,
                tunnelGraph.closestRoomToAdventurer(new Tunnel(new Coordinate(1, 2), false)).get()
                        .getRoom().get().getId());


    }


}
