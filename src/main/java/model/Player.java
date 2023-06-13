package de.unisaarland.cs.se.selab.model;

import de.unisaarland.cs.se.selab.comm.BidType;
import de.unisaarland.cs.se.selab.model.dungeon.Dungeon;
import de.unisaarland.cs.se.selab.spells.Spell;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * This holds all player-related data.
 */
public class Player {

    public static final int MIN_EVILNESS = 0;
    public static final int MAX_EVILNESS = 15;

    private final int id;
    private final String name;
    private final List<Monster> monsters;
    private final List<Trap> traps;
    private final Dungeon dungeon;
    private final List<Optional<BidType>> bids;
    private final List<BidType> lockedTypes;
    private int evilness;
    private int imps;
    private int gold;
    private int food;
    private int numTunnelDigsAllowed;
    private boolean alive;
    private int scorePoints;
    private final Map<Integer, List<Spell>> spellMap;
    //represent the counterSpells
    private int counterSpells;
    // represents the spells countered
    private int counteredSpells;
    //represent the seasons where the room cannot be activated
    private final List<Integer> noActivatingSeason;
    //represent blocked options It is a map, of season to bidTypes
    private final Map<Integer, List<BidType>> blockedOptionBySpell;
    private int withstoodSpells;

    public Player(final int id,
                  final String name,
                  final int initialFood,
                  final int initialGold,
                  final int initialImps,
                  final int initialEvilness,
                  final Dungeon dungeon) {
        this.id = id;
        this.name = name;
        this.monsters = new ArrayList<>();
        this.traps = new ArrayList<>();
        this.dungeon = dungeon;
        this.lockedTypes = new ArrayList<>();
        this.food = initialFood;
        this.gold = initialGold;
        this.imps = initialImps;
        this.evilness = initialEvilness;
        this.bids = new ArrayList<>(Model.BID_LIMIT);
        this.alive = true;
        spellMap = new HashMap<>();
        noActivatingSeason = new ArrayList<>();
        blockedOptionBySpell = new HashMap<>();
        this.counterSpells = 0;
        clearBidTypes();
    }

    public final int getId() {
        return this.id;
    }

    public final String getName() {
        return this.name;
    }

    /**
     * Place a bid a player sent via a place bid command.
     *
     * @param type     the bid's type
     * @param priority the bid's priority, i.e., whether it is the first, second, or third bid
     * @return {@code true} if the bid could be placed or {@code false} if the bid or priority is
     * not available
     */
    public final boolean placeBid(final BidType type, final int priority) {
        // Bid priorities start at 1 but list indices start at 0, hence the -1
        if (this.bids.get(priority - 1).isEmpty() && checkBidType(type)) {
            this.bids.set(priority - 1, Optional.of(type));
            return true;
        }
        return false;
    }

    /**
     * Check whether the given bid type was already placed.
     *
     * @param type the bid type to check.
     * @return whether the bid is still available for placing, or not
     */
    private boolean checkBidType(final BidType type) {
        return getPlacedBidTypes().stream().noneMatch(placedBid -> placedBid == type);
    }

    /**
     * Check whether the player is still alive and finished with bidding.
     *
     * @return whether the player is finished with bidding
     */
    public final boolean finishedBidding() {
        return isAlive() && !hasToBid();
    }

    /**
     * Check whether the player still has to bid.
     *
     * @return whether the player still has to bid
     */
    public final boolean hasToBid() {
        return isAlive() && getPlacedBidTypes().size() != Model.BID_LIMIT;
    }

    /**
     * Get all placed bid types for this player.
     *
     * @return the list of placed bid types
     */
    public final List<BidType> getPlacedBidTypes() {
        return this.bids.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    /**
     * Clear the list of placed bids.
     */
    public final void clearBidTypes() {
        this.bids.clear();
        for (int i = 0; i < Model.BID_LIMIT; i++) {
            this.bids.add(Optional.empty());
        }
    }

    /**
     * Lock the bids of second and third priority.
     */
    public final void lockBidTypes() {
        // Bid priorities start at 1 but list indices start at 0, hence the off-by-one
        this.bids.get(1).ifPresent(this.lockedTypes::add);
        this.bids.get(2).ifPresent(this.lockedTypes::add);
    }

    /**
     * Unlock previously locked bid types.
     */
    public final void unlockBidTypes() {
        this.lockedTypes.clear();
    }

    /**
     * Check whether the given bid type is locked.
     *
     * @param type   the bid type to check
     * @param season the season of bidding
     * @return whether the bid type is locked
     */
    public final boolean isLocked(final BidType type, final int season) {
        if (!blockedOptionBySpell.containsKey(season)) {
            return this.lockedTypes.contains(type);
        }
        final List<BidType> blockList = blockedOptionBySpell.get(season);
        return this.lockedTypes.contains(type)
                || blockList.contains(type);
    }

    /**
     * Get a list of currently locked bid types.
     *
     * @return the list of locked bid types
     */
    public final List<BidType> getLockedTypes() {
        return this.lockedTypes;
    }

    public int getEvilness() {
        return this.evilness;
    }

    public void changeEvilness(final int evilness) {
        this.evilness += evilness;
    }

    public int getImps() {
        return this.imps;
    }

    public void changeImps(final int imps) {
        this.imps += imps;
    }

    public int getGold() {
        return this.gold;
    }

    public void changeGold(final int gold) {
        this.gold += gold;
    }

    public int getFood() {
        return this.food;
    }

    public void changeFood(final int food) {
        this.food += food;
    }

    public final List<Monster> getMonsters() {
        return this.monsters;
    }

    public final Optional<Monster> getMonster(final int monsterId) {
        return this.monsters.stream()
                .filter(monster -> monster.getId() == monsterId)
                .findFirst();
    }

    /**
     * Make all monsters available for being set on defense again.
     */
    public final void wakeUpMonsters() {
        this.monsters.forEach(monster -> monster.setUsed(false));
    }

    public final void addMonster(final Monster monster) {
        this.monsters.add(monster);
    }

    public List<Trap> getTraps() {
        return this.traps;
    }

    public final Optional<Trap> getTrap(final int trapId) {
        return this.traps.stream()
                .filter(trap -> trap.getId() == trapId)
                .findFirst();
    }

    public void addTrap(final Trap trap) {
        this.traps.add(trap);
    }

    /**
     * Set the number of tunnels the player is allowed to dig.
     *
     * @param numTunnelDigsAllowed the number of tunnels a player may dig
     */
    public final void setNumTunnelDigsAllowed(final int numTunnelDigsAllowed) {
        this.numTunnelDigsAllowed = numTunnelDigsAllowed;
    }

    /**
     * Get the number of tunnels the player is allowed to dig.
     *
     * @return the number of tunnels a player may dig
     */
    public final int getNumTunnelDigsAllowed() {
        return this.numTunnelDigsAllowed;
    }

    /**
     * Reduces the number of tunnels the player is allowed to dig by 1.
     */
    public final void digTunnel() {
        this.numTunnelDigsAllowed--;
    }

    public final Dungeon getDungeon() {
        return this.dungeon;
    }

    /**
     * Indicate that this player has left the game.
     */
    public void kill() {
        this.alive = false;
    }

    /**
     * Check whether this player still participates in the game.
     *
     * @return whether this player still participates in the game
     */
    public boolean isAlive() {
        return this.alive;
    }

    public int getScorePoints() {
        return this.scorePoints;
    }

    public void changeScorePoints(final int amount) {
        this.scorePoints += amount;
    }

    /* Added functions */

    public void increaseCounterSpell() {
        counterSpells++;
    }

    public void useCounterSpell() {
        counterSpells--;
        counteredSpells++;
    }

    /**
     * This function assigns the spells to the lord, who triggered them.
     *
     * @param season           the season, in which it was triggered
     * @param spellToWithstand the spell triggered
     */

    public void addSpell(final int season, final Spell spellToWithstand) {
        final List<Spell> listSpell = this.spellMap.getOrDefault(season, new ArrayList<>());
        listSpell.add(spellToWithstand);
        this.spellMap.put(season, listSpell);
    }

    public int reduceFood(final int amount) {
        if (this.food - amount < 0) {
            final int amountReduced = this.food;
            this.food = 0;
            return amountReduced;
        } else {
            food -= amount;
            return amount;
        }
    }

    public int reduceGold(final int amount) {
        if (this.gold - amount < 0) {
            final int amountReduced = this.gold;
            this.gold = 0;
            return amountReduced;
        } else {
            gold -= amount;
            return amount;
        }
    }

    /**
     * block a bidType by the structural spell
     *
     * @param bidType bidType to block
     * @param season  represents the season, in which it should be blocked
     */
    public void blockOptionBySpell(final BidType bidType, final int season) {
        /* if(lockedTypes.contains(bidType)){
            return false;
        } to ask */
        final List<BidType> blockedOptions =
                blockedOptionBySpell.getOrDefault(season, new ArrayList<>());
        //if not blocked
        if (blockedOptions.isEmpty()) {
            blockedOptions.add(bidType);
            blockedOptionBySpell.put(season, blockedOptions);

            //if season is present but list does not contain the bidType
        } else if (!blockedOptions.contains(bidType)) {
            blockedOptions.add(bidType);
            blockedOptionBySpell.put(season, blockedOptions);

        }


    }

    /**
     * block a bidType by the structural spell
     *
     * @param bidType bidType to block
     * @param season  represents the season, in which it should be blocked
     * @return true if the blocking is possible
     */

    public boolean canBlockBid(final BidType bidType, final int season) {
        final List<BidType> blockedOptions =
                blockedOptionBySpell.getOrDefault(season, new ArrayList<>());
        return !blockedOptions.contains(bidType);
    }

    /**
     * cleans up the list of block options by spells and
     * cleans the list of seasons, in which the player can't activate a room
     */
    public void buildingEnd() {
        removeAllBlockOptionBySpell();
        noActivatingSeason.clear();
    }

    public void removeAllBlockOptionBySpell() {
        blockedOptionBySpell.clear();
    }

    public List<Integer> getNoActivatingSeasonList() {
        return noActivatingSeason;
    }

    public int getCounterSpells() {
        return counterSpells;
    }

    public int getCounteredSpells() {
        return counteredSpells;
    }

    /**
     * describes the spells that have to be withstood
     *
     * @param round the round, in which the spell has to be withstood
     * @return returns a list of spells which have to be withstood in this round
     */

    public List<Spell> spellsToWithstand(final int round) {
        return this.spellMap.remove(round);
    }

    public void removeAllSpellsToWithstand() {
        this.spellMap.clear();
    }

    public boolean roundSpellsPresent(final int round) {
        return this.spellMap.containsKey(round);
    }

    public void increaseWithstoodSpell() {
        withstoodSpells++;
    }

    public int getWithstoodSpells() {
        return withstoodSpells;
    }

    public Map<Integer, List<BidType>> getBlockedOptionBySpell() {
        return blockedOptionBySpell;
    }
}
