package de.unisaarland.cs.se.selab.model.dungeon;

import de.unisaarland.cs.se.selab.model.Monster;
import de.unisaarland.cs.se.selab.model.Trap;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

/**
 * A tunnel represents any mined tile in the dungeon regardles whether it is a room or not.
 */
public class Tunnel {

    private static final int MONSTER_LIMIT = 1;

    private final Coordinate coordinate;
    private boolean goldMiningImp;
    private boolean tunnelMiningImp;
    private boolean conquered;
    @Nullable
    private Room room;
    @Nullable
    private Trap trap;
    private final List<Monster> monsters;

    public Tunnel(final Coordinate coordinate, final boolean isBeingMined) {
        this.monsters = new ArrayList<>();
        this.tunnelMiningImp = isBeingMined;
        this.coordinate = coordinate;
    }

    public Coordinate getCoordinate() {
        return this.coordinate;
    }

    private int getMonsterLimit() {
        return isRoom() ? Room.MONSTER_LIMIT : Tunnel.MONSTER_LIMIT;
    }

    /**
     * Try to set a monster defending this tunnel.
     *
     * @param monster the defending monster
     * @return whether the monster could be placed or not
     */
    public boolean addMonster(final Monster monster) {
        if (this.monsters.size() < getMonsterLimit()) {
            this.monsters.add(monster);
            return true;
        } else {
            return false;
        }
    }

    public List<Monster> getMonsters() {
        return monsters;
    }

    /**
     * Try to set a trap defending this tunnel.
     *
     * @param trap the defending trap
     * @return whether the trap could be placed or not
     */
    public boolean addTrap(final Trap trap) {
        if (this.trap == null) {
            this.trap = trap;
            return true;
        }
        return false;
    }

    public Optional<Trap> getTrap() {
        return Optional.ofNullable(this.trap);
    }

    /**
     * Remove all defenders from the tunnel.
     */
    public void clearDefenders() {
        this.trap = null;
        this.monsters.clear();
    }

    public boolean hasGoldMiningImp() {
        return this.goldMiningImp;
    }

    public boolean canMineGold() {
        return !isRoom() && !this.goldMiningImp;
    }

    public void setGoldMiner() {
        this.goldMiningImp = true;
    }

    public boolean hasTunnelMiningImp() {
        return this.tunnelMiningImp;
    }

    /**
     * Remove all working imps from this tunnel.
     *
     * @return the number of imps that worked in this tunnel.
     */
    public int returnMiners() {
        final int imps = (this.goldMiningImp ? 1 : 0) + (this.tunnelMiningImp ? 1 : 0);
        this.goldMiningImp = false;
        this.tunnelMiningImp = false;
        return imps;
    }

    public void buildRoom(final Room room) {
        this.room = room;
    }

    public boolean isRoom() {
        return this.room != null;
    }

    public Optional<Room> getRoom() {
        return Optional.ofNullable(this.room);
    }

    public boolean isConquered() {
        return this.conquered;
    }

    public void setRoom(final @Nullable Room room) {
        this.room = room;
    }

    public void conquer() {
        this.conquered = true;
    }

    public void setConquered(final boolean conquered) {
        this.conquered = conquered;
    }
}
