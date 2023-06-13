package de.unisaarland.cs.se.selab.model;

import de.unisaarland.cs.se.selab.comm.BidType;
import de.unisaarland.cs.se.selab.model.dungeon.Dungeon;
import de.unisaarland.cs.se.selab.model.dungeon.Room;
import de.unisaarland.cs.se.selab.spells.Spell;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

/**
 * The main game model class containing all relevant information about the game.
 */
public class Model {

    public static final int BID_LIMIT = 3;
    public static final int MAX_ROUNDS = 4;
    private static final int MIN_PLAYER_ID = 0;

    private final Map<Integer, Player> players;
    private final List<Monster> monsters;
    private final List<Adventurer> adventurers;
    private final List<Trap> traps;
    private final List<Room> rooms;
    private final Set<Integer> activeIds;
    private final Random random;
    private final List<Monster> availableMonsters;
    private final List<Room> availableRooms;
    private final List<Adventurer> queueingAdventurers;
    private final int maxPlayers;
    private final int maxYear;
    private final int dungeonSideLength;
    private final int initialFood;
    private final int initialGold;
    private final int initialImps;
    private final int initialEvilness;
    private int currentMaxId;
    private int round;
    private int year;
    private int startingPlayer;
    private final List<Spell> spells;
    private final List<Spell> availableSpells;


    public Model(final Collection<Monster> monsters,
                 final Collection<Adventurer> adventurers,
                 final Collection<Trap> traps,
                 final Collection<Room> rooms,
                 final long randomSeed,
                 final int maxPlayers,
                 final int maxYear,
                 final int dungeonSideLength,
                 final int initialFood,
                 final int initialGold,
                 final int initialImps,
                 final int initialEvilness,
                 final Collection<Spell> spells) {
        this.monsters = new ArrayList<>(monsters);
        this.adventurers = new ArrayList<>(adventurers);
        this.traps = new ArrayList<>(traps);
        this.rooms = new ArrayList<>(rooms);
        this.players = new HashMap<>();
        this.activeIds = new HashSet<>();
        this.availableMonsters = new ArrayList<>();
        this.availableRooms = new ArrayList<>();
        this.queueingAdventurers = new ArrayList<>();
        this.random = new Random(randomSeed);
        this.maxPlayers = maxPlayers;
        this.round = 0;
        this.year = 1;
        this.startingPlayer = Model.MIN_PLAYER_ID;
        this.currentMaxId = Model.MIN_PLAYER_ID - 1;
        this.maxYear = maxYear;
        this.dungeonSideLength = dungeonSideLength;
        this.initialFood = initialFood;
        this.initialGold = initialGold;
        this.initialImps = initialImps;
        this.initialEvilness = initialEvilness;
        this.spells = new ArrayList<>(spells);
        this.availableSpells = new ArrayList<>();
    }

    /**
     * Check whether the maximum number of players has been reached.
     *
     * @return whether the maximum number of players has been reached
     */
    public final boolean maxPlayersReached() {
        return this.players.size() >= this.maxPlayers;
    }

    /**
     * Check whether the current building phase or combat phase has a next round.
     *
     * @return whether the current building phase or combat phase has a next round
     */
    public final boolean hasNextRound() {
        if (this.round++ == MAX_ROUNDS) {
            this.round = 0;
            return false;
        }
        return true;
    }

    /**
     * Get the number of the current season or combat round.
     *
     * @return the number of the current season or combat round
     */
    public final int getRound() {
        return this.round;
    }

    /**
     * Check whether the game has a next year.
     *
     * @return whether the game has a next year
     */
    public final boolean hasNextYear() {
        return this.year++ < this.maxYear;
    }

    /**
     * Get the number of the current year.
     *
     * @return the number of the current year
     */
    public final int getYear() {
        return this.year;
    }

    private int nextId() {
        return this.currentMaxId + 1;
    }

    /**
     * Advance the starting player marker to the next player.
     */
    public final void nextPlayer() {
        if (!this.activeIds.isEmpty()) {
            // Find next matching id.
            do {
                this.startingPlayer++;
                if (this.startingPlayer > this.currentMaxId) {
                    this.startingPlayer = Model.MIN_PLAYER_ID;
                }
            } while (!this.activeIds.contains(this.startingPlayer));
        }
    }

    /**
     * Retrieve a player by his/her player  ID.
     *
     * @param playerId the player's player ID
     * @return the player object
     */
    public final Player getPlayerById(final int playerId) {
        return this.players.values().stream()
                .filter(player -> player.getId() == playerId)
                .findFirst()
                .orElseThrow(() -> {
                    throw new IllegalArgumentException("Invalid PlayerId.");
                });
    }

    /**
     * Get a list of all alive players sorted by player ID.
     *
     * @return the sorted list of alive players
     */
    public final List<Player> getPlayers() {
        final List<Player> players = new ArrayList<>(this.players.values());
        players.sort(Comparator.comparingInt(Player::getId));
        return new ArrayList<>(players
                .stream()
                .filter(Player::isAlive)
                .toList());
    }

    /**
     * Get a list of all players in running order starting with the current starting player.
     *
     * @return the list of players in running order
     */
    public final List<Player> getPlayersFromStarting() {
        final List<Player> players = getPlayers();
        if (this.players.containsKey(this.startingPlayer)) {
            while (players.get(0).getId() != this.startingPlayer) {
                players.add(players.remove(0));
            }
        }
        return players;
    }

    /**
     * Get the monster with the given ID if currently available for employment.
     *
     * @param id the monster's ID
     * @return the monster if available or {@code Optional.empty()} otherwise
     */
    public final Optional<Monster> getAvailableMonster(final int id) {
        return this.availableMonsters.stream()
                .filter(monster -> monster.getId() == id)
                .findFirst();
    }

    /**
     * Get the room with the given ID if currently available for construction.
     *
     * @param id the room's ID
     * @return the room if available or {@code Optional.empty()} otherwise
     */
    public final Optional<Room> getAvailableRoom(final int id) {
        return this.availableRooms.stream().filter(room -> room.getId() == id).findFirst();
    }

    /**
     * Check whether the player with the given ID is in the game.
     *
     * @param playerId the player's player ID
     * @return whether the player is in the game
     */
    public final boolean hasPlayer(final int playerId) {
        return this.players.containsKey(playerId);
    }

    /**
     * Add a player to the game.
     *
     * @param playerName the player's name
     * @return the player's player ID
     */
    public final int addPlayer(final String playerName) {
        this.currentMaxId = nextId();
        this.players.putIfAbsent(this.currentMaxId,
                new Player(this.currentMaxId,
                        playerName,
                        this.initialFood,
                        this.initialGold,
                        this.initialImps,
                        this.initialEvilness,
                        new Dungeon()));
        this.activeIds.add(this.currentMaxId);
        return this.currentMaxId;
    }

    /**
     * Remove a player from the game.
     *
     * @param playerId the player's player ID
     */
    public final void removePlayer(final int playerId) {
        this.players.remove(playerId);
        this.activeIds.remove(playerId);
        if (this.startingPlayer == playerId) {
            nextPlayer();
        }
    }

    /**
     * Draw a monster from the stack and add it to the available monsters for this round.
     *
     * @return the drawn monster
     */
    public final Monster drawMonster() {
        final Monster monster = this.monsters.remove(0);
        this.availableMonsters.add(monster);
        return monster;
    }

    /**
     * Draw an adventurer from the stack and add it to the available adventurers for this round.
     *
     * @return the drawn adventurer
     */
    public final Adventurer drawAdventurer() {
        final Adventurer adventurer = this.adventurers.remove(0);
        this.queueingAdventurers.add(adventurer);
        return adventurer;
    }

    /**
     * Draw a trap from the stack.
     *
     * @return the drawn trap
     */
    public final Trap drawTrap() {
        return this.traps.remove(0);
    }

    /**
     * Draw a room from the stack and add it to the available rooms for this round.
     *
     * @return the drawn room
     */
    public final Room drawRoom() {
        final Room room = this.rooms.remove(0);
        this.availableRooms.add(room);
        return room;
    }

    /**
     * Check whether there are any rooms left for construction this round.
     *
     * @return whether there are any rooms left for construction
     */
    public final boolean availableRoomsLeft() {
        return !this.availableRooms.isEmpty();
    }

    /**
     * Remove a monster that was available for employment this round.
     *
     * @param monster the monster to remove
     */
    public final void removeAvailableMonster(final Monster monster) {
        this.availableMonsters.remove(monster);
    }

    /**
     * Remove a room that was available for construction this round.
     *
     * @param room the room to remove
     */
    public final void removeAvailableRoom(final Room room) {
        this.availableRooms.remove(room);
    }

    /**
     * Get the next most difficult adventurer available this round.
     *
     * @return the adventurer
     */
    public Adventurer popAdventurer() {
        this.queueingAdventurers.sort(
                Comparator.comparing(Adventurer::getDifficulty)
                        .thenComparing(Adventurer::getId));
        return this.queueingAdventurers.remove(0);
    }

    /**
     * Get the side length allowed for dungeons.
     *
     * @return the dungeon side length
     */
    public int getDungeonSideLength() {
        return this.dungeonSideLength;
    }

    /**
     * Clear all cards that were drawn for the current season.
     */
    public void seasonalCleanup() {
        this.availableRooms.clear();
        this.availableMonsters.clear();
        this.queueingAdventurers.clear();
        this.availableSpells.clear();
    }

    /**
     * Shuffle all card decks in the correct order.
     */
    public void shuffleCards() {
        Collections.shuffle(this.monsters, this.random);
        Collections.shuffle(this.adventurers, this.random);
        Collections.shuffle(this.traps, this.random);
        Collections.shuffle(this.rooms, this.random);
        Collections.shuffle(this.spells, this.random);
    }

    public Random getRandomObject() {
        return random;
    }

    /* Added functions*/

    /**
     * remove one spell from the list of spells to add to the
     * available spells
     *
     * @return the spell removed
     */
    public Spell drawSpell() {
        final Spell spellDrawn = spells.remove(0);
        availableSpells.add(spellDrawn);
        return spellDrawn;
    }

    /**
     * getting a list of spell triggered in a specific slot
     *
     * @param bidType the bidType of the slot
     * @param slot    the specific slot
     * @return a list of spells triggered in this slot
     */

    public List<Spell> getAvailableSpell(final BidType bidType, final int slot) {

        return this.availableSpells.stream()
                .filter(s -> s.getBidType() == bidType && s.getSlot() == slot).toList();

    }

    public void removeAvailableSpell(final List<Spell> spell) {
        for (final Spell s : spell) {
            this.availableSpells.remove(s);
        }
    }

    public int getMaxYear() {
        return maxYear;
    }

    public List<Adventurer> getAdventurers() {
        return adventurers;
    }

    public List<Spell> getSpells() {
        return spells;
    }
    /*added for system test*/

    public Set<Integer> getActiveIds() {
        return activeIds;
    }

    public List<Monster> getAvailableMonsters() {
        return availableMonsters;
    }

    public List<Room> getAvailableRooms() {
        return availableRooms;
    }

    public List<Adventurer> getQueueingAdventurers() {
        return queueingAdventurers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getInitialFood() {
        return initialFood;
    }

    public int getInitialGold() {
        return initialGold;
    }

    public int getInitialImps() {
        return initialImps;
    }

    public List<Monster> getMonsters() {
        return monsters;
    }

    public List<Trap> getTraps() {
        return traps;
    }

    public List<Room> getRooms() {
        return rooms;
    }


}
