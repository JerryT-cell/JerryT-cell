package de.unisaarland.cs.se.selab.model.dungeon;

import de.unisaarland.cs.se.selab.comm.BidType;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

/**
 * Class for rooms.
 */
public class Room {

    public enum BuildingRestriction {
        UPPER_HALF,
        OUTER_RING,
        INNER_RING,
        LOWER_HALF
    }

    public static final int MONSTER_LIMIT = 2;

    private final Map<BidType, Integer> production;
    private final BuildingRestriction restriction;
    private final int activationImps;
    private final int id;
    private boolean activated;

    public Room(final int id, final int activationImps, final BuildingRestriction restriction,
                final EnumMap<BidType, Integer> production) {
        super();
        this.activationImps = activationImps;
        this.production = production;
        this.restriction = restriction;
        this.id = id;
    }

    public BuildingRestriction getRestriction() {
        return restriction;
    }

    public int getId() {
        return this.id;
    }

    public int getActivationImps() {
        return activationImps;
    }

    public Optional<Integer> getProduction(final BidType type) {
        return Optional.ofNullable(this.production.get(type));
    }

    public void activate() {
        this.activated = true;
    }

    public boolean isActivated() {
        return this.activated;
    }

    /**
     * Clear the activated flag from the room.
     *
     * @return the number of imps that worked in this room
     */
    public int returnImps() {
        if (this.activated) {
            this.activated = false;
            return this.activationImps;
        } else {
            return 0;
        }
    }
}
