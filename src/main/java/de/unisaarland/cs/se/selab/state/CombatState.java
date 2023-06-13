package de.unisaarland.cs.se.selab.state;

import de.unisaarland.cs.se.selab.ConnectionUtils;
import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.commands.ActionResult;
import de.unisaarland.cs.se.selab.model.Adventurer;
import de.unisaarland.cs.se.selab.model.DefensiveMeasure;
import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.model.Monster;
import de.unisaarland.cs.se.selab.model.Player;
import de.unisaarland.cs.se.selab.model.dungeon.Coordinate;
import de.unisaarland.cs.se.selab.model.dungeon.Dungeon;
import de.unisaarland.cs.se.selab.model.dungeon.Tunnel;
import de.unisaarland.cs.se.selab.spells.Spell;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * This state handles the combat for all players.
 */
public final class CombatState extends State {

    private static final int EVILNESS_WHEN_CONQUERED = -1;
    private static final int FATIGUE_DAMAGE = 2;

    public CombatState(final Model model, final ConnectionWrapper connection) {
        super(model, connection);
    }

    @Override
    public State run() {
        for (final Player player : model.getPlayers()) {
            if (handleCombatForPlayer(player) == ActionResult.END_GAME) {
                return new EndGameState(this.model, this.connection);
            }
        }

        if (model.hasNextYear()) {
            return new BuildingState(this.model, this.connection);
        } else {
            evaluateTitles();
            return new EndGameState(this.model, this.connection);
        }
    }

    /**
     * Simulate all combat rounds for one player.
     *
     * @param player the player who does combat
     * @return a result that indicates how the game should continue
     */
    private ActionResult handleCombatForPlayer(final Player player) {
        while (model.hasNextRound()) {
            final Dungeon dungeon = player.getDungeon();
            if (player.isAlive() && dungeon.adventurersLeft()) {
                connection.sendNextRound(model.getRound());
                if (dungeon.isConquered()) {
                    prisonerFlees(player);
                } else {
                    if (fight(player) == ActionResult.END_GAME) {
                        return ActionResult.END_GAME;
                    }
                }
            }
        }
        player.getDungeon().setArchmageLinus(false);
        player.removeAllSpellsToWithstand();
        return ActionResult.PROCEED;
    }

    private void prisonerFlees(final Player player) {
        final Dungeon dungeon = player.getDungeon();
        if (dungeon.getNumImprisonedAdventurers() > 0) {
            connection.sendAdventurerFled(dungeon.freeAdventurer().getId());
            if (player.getEvilness() > Player.MIN_EVILNESS) {
                player.changeEvilness(EVILNESS_WHEN_CONQUERED);
                connection.sendEvilnessChanged(player.getId(), EVILNESS_WHEN_CONQUERED);
            }
        }
    }

    /**
     * Simulate a single combat round for a player.
     *
     * @param player the player who does combat
     * @return a result that indicates how the game should continue
     */
    private ActionResult fight(final Player player) {
        // Player can select a battleground.
        connection.sendSetBattleGround(player.getId());
        final ActionResult battlegroundResult = ConnectionUtils.executePlayerCommand(
                model, connection, Phase.SET_BATTLEGROUND, player);
        if (!player.isAlive()) {
            return battlegroundResult;
        }
        // Player can place monsters or traps.
        connection.sendDefendYourself(player.getId());
        final ActionResult defendResult =
                ConnectionUtils.executePlayerCommand(model, connection, Phase.COMBAT, player);
        if (!player.isAlive()) {
            return defendResult;
        }

        // casting of spells                       <-change
        if (player.roundSpellsPresent(model.getRound())) {
            final ActionResult resultOfSpell = dealWithSpells(player);
            if (resultOfSpell == ActionResult.END_GAME) {
                return ActionResult.END_GAME;
            }
            if (!player.isAlive()) {
                return resultOfSpell;
            }
        }
        assert player.getDungeon().getBattleGround().isPresent();
        final Tunnel battleGround = player.getDungeon().getBattleGround().get();

        if (battleGround.isConquered()) {
            if (battleGround.getTrap().isPresent()) {
                player.addTrap(battleGround.getTrap().get());
            }
            adventurersConquer(player);
        } else {
            damageAdventurers(player);
            adventurersConquer(player);
            priestsHeal(player.getDungeon().getAllAdventurers());
        }
        for (final Adventurer ad : player.getDungeon().getAllAdventurers()) {
            ad.restoreOriginalValues();
        }
        return ActionResult.PROCEED;
    }

    /**
     * cast all spells that a player has to withstand
     *
     * @param player the player fighting
     * @return ENDGAME if all players left,
     * PROCEED is the game can proceed
     */

    private ActionResult dealWithSpells(final Player player) {
        final List<Adventurer> adventurersInGame = player.getDungeon().getAllAdventurers();
        int magicPoints = 0;
        for (final Adventurer ad : adventurersInGame) {
            magicPoints += ad.getMagicPoints();
        }
        //if linus is present
        if (player.getDungeon().linusIsPresent()) {
            magicPoints += 3;
        }
        final List<Spell> spellsToWithstand = player.spellsToWithstand(model.getRound());
        for (final Spell spells : spellsToWithstand) {
            if (magicPoints >= spells.getMagicPointsNeeded()) {

                if (spells.cast(player.getId(), model, connection) == ActionResult.END_GAME) {
                    return ActionResult.END_GAME;
                }
            }
        }

        return ActionResult.PROCEED;

    }


    private void priestsHeal(final Iterable<Adventurer> adventurers) {
        for (final Adventurer healer : adventurers) {
            int healValue = healer.getHealValue();
            for (final Adventurer target : adventurers) {
                final int healed = target.heal(healValue);
                healValue -= healed;
                if (healed > 0) {
                    connection.sendAdventurerHealed(healer.getId(), target.getId(), healed);
                }
            }
        }
    }

    private void adventurersConquer(final Player player) {
        final Dungeon dungeon = player.getDungeon();
        final Optional<Tunnel> battleGround = dungeon.getBattleGround();
        if (battleGround.isPresent()) {
            final Optional<Adventurer> attacker = dungeon.getAdventurer(1);
            if (attacker.isPresent()) {
                dungeon.conquer();
                final Coordinate coordinate = battleGround.get().getCoordinate();
                connection.sendTunnelConquered(attacker.get().getId(), coordinate);
                if (player.getEvilness() > Player.MIN_EVILNESS) {
                    player.changeEvilness(EVILNESS_WHEN_CONQUERED);
                    connection.sendEvilnessChanged(player.getId(), EVILNESS_WHEN_CONQUERED);
                }
            }
            battleGround.get().clearDefenders();
        }
    }

    private void damageAdventurers(final Player player) {
        final Dungeon dungeon = player.getDungeon();
        final Optional<Tunnel> battleGround = dungeon.getBattleGround();
        if (battleGround.isPresent()) {
            final int reduction = dungeon.getAllAdventurers()
                    .stream()
                    .map(Adventurer::getDefuseValue)
                    .reduce(0, Integer::sum);
            // Damage from trap if there is one.
            battleGround.get().getTrap()
                    .ifPresent(trap -> evaluateDefense(trap, player, reduction));
            // Damage from placed monsters.
            int result = -1;
            for (final Monster monster : battleGround.get().getMonsters()) {
                result = evaluateDefense(monster, player, 0);
            }
            if (result != 1) {
                final int finalResult = result;
                battleGround.get().getMonsters().removeIf(m -> m.getId() == finalResult);
            }
        }
        // Deal fatigue damage
        for (final Adventurer adventurer : dungeon.getAllAdventurers()) {
            if (dungeon.linusIsPresent()) {
                hurtAdventurer(adventurer, player, 1);
            } else {
                hurtAdventurer(adventurer, player, CombatState.FATIGUE_DAMAGE);
            }
        }
    }

    //changed to int. return value is -1 if no monster is removed, not -1 if a monster is removed
    private int evaluateDefense(final DefensiveMeasure defense, final Player player,
                                final int totalReduction) {
        int result = -1;
        final Dungeon dungeon = player.getDungeon();
        switch (defense.getAttackStrategy()) {
            case BASIC -> {
                final int reducedDamage = Math.max(0, defense.getDamage() - totalReduction);
                final Optional<Adventurer> attacker = dungeon.getAdventurer(1);
                if (attacker.isPresent()) {
                    result = hurtAdventurer(attacker.get(), player,
                            reducedDamage);
                }
            }
            case MULTI -> {
                int alreadyReduced = 0;
                for (final Adventurer adventurer : dungeon.getAllAdventurers()) {
                    final int reductionLeft = totalReduction - alreadyReduced;
                    result = hurtAdventurer(adventurer, player,
                            Math.max(defense.getDamage() - reductionLeft, 0));
                    if (result != -1) {
                        return result;
                    }
                    alreadyReduced += Math.min(defense.getDamage(), reductionLeft);
                }
            }
            case TARGETED -> {
                if (defense.hasTarget()) {
                    final int reducedDamage = Math.max(0, defense.getDamage() - totalReduction);
                    final Optional<Adventurer> attacker =
                            dungeon.getAdventurer(defense.getTarget());
                    if (attacker.isPresent()) {
                        result = hurtAdventurer(attacker.get(), player,
                                reducedDamage);
                    }
                    // attacker.ifPresent(adventurer -> hurtAdventurer(adventurer, player,
                    //       reducedDamage));
                }
            }
            default -> {
            }
        }
        return result;
    }

    //changed to int. return value is -1 if no monster is removed, not -1 if a monster is removed
    private int hurtAdventurer(final Adventurer adventurer, final Player player,
                               final int damage) {
        final int effectiveDamage = adventurer.damage(damage);
        if (effectiveDamage > 0) {
            if (adventurer.isDefeated()) {
                player.getDungeon().imprisonAdventurer(adventurer);
                connection.sendAdventurerImprisoned(adventurer.getId());
                return handleLinus(adventurer, player);
            } else {
                connection.sendAdventurerDamaged(adventurer.getId(), effectiveDamage);
            }
        }
        return -1;
    }
    //changed to int. return value is -1 if no monster is removed, not -1 if a monster is removed

    private int handleLinus(final Adventurer adventurer, final Player player) {
        if (adventurer.getMagicPoints() == 0 || player.getDungeon().linusIsPresent()) {
            return -1;
        }
        int monsterId = -1;
        final Dungeon dungeon = player.getDungeon();
        if (dungeon.probabilityLinusArrive(player, model)) {
            dungeon.setArchmageLinus(true);
            dungeon.increasePenguinVisit();
            connection.sendArchmageArrived(player.getId());
            //remove monsters
            if (player.getMonsters().size() > 0) {
                final List<Monster> monsters = player.getMonsters();
                final int randomIndex = model.getRandomObject().nextInt(monsters.size());
                final Monster monster = monsters.remove(randomIndex);
                connection.sendMonsterRemoved(player.getId(), monster.getId());
                //also remove in tunnel if present
                assert dungeon.getBattleGround().isPresent();
                final Tunnel battleGround = dungeon.getBattleGround().get();
                final List<Monster> battleGroundMonsterList = battleGround.getMonsters().stream()
                        .filter(m -> m.getId() == monster.getId()).toList();

                if (!battleGroundMonsterList.isEmpty()) {
                    monsterId = battleGroundMonsterList.get(0).getId();
                }
                if (dungeon.getAllAdventurers().isEmpty()) {
                    dungeon.setArchmageLinus(false);
                }
            }
        }
        return monsterId;
    }


    /**
     * Calculates player scores and awards titles at the end of a game.
     */
    private void evaluateTitles() {
        final List<Player> players = model.getPlayers();

        // Scoring individual points
        for (final Player player : players) {
            // 1 point per monster.
            player.changeScorePoints(player.getMonsters().size());
            // 2 points per room.
            player.changeScorePoints(2 * player.getDungeon().getGraph().getNumRooms());
            // -2 points per conquered tunnel! tile.
            player.changeScorePoints(
                    -2 * (int) player.getDungeon().getGraph().stream()
                            .filter(tunnel -> tunnel.isConquered() && !tunnel.isRoom())
                            .count());
            // 2 points per imprisoned adventurer.
            player.changeScorePoints(2 * player.getDungeon().getNumImprisonedAdventurers());
        }

        // Define all titles.
        final List<Function<Player, Integer>> titles = new ArrayList<>();      // The Lord of ...
        titles.add(Player::getEvilness);                                       // Dark Deeds
        titles.add(player -> player.getDungeon().getGraph().getNumRooms());    // Halls
        titles.add(player -> player.getDungeon().getGraph().getNumTunnels());  // Tunnels
        titles.add(player -> player.getMonsters().size());                     // Monsters
        titles.add(Player::getImps);                                           // Imps
        titles.add(player -> player.getGold() + player.getFood());             // Riches
        titles.add(player -> player.getDungeon().getNumUnconqueredTiles());    // Battle
        titles.add(Player::getWithstoodSpells);                                // magic Proof
        titles.add(player -> player.getDungeon().getPenguinVisits());          //penguinVisit
        titles.add(Player::getCounteredSpells);                                //counter Strike


        // Evaluate all titles.
        for (final Function<Player, Integer> title : titles) {
            processTitle(players, title);
        }

        // Find winner.
        final Optional<Player> optWinner =
                players.stream().max(Comparator.comparing(Player::getScorePoints));

        if (optWinner.isPresent()) {
            final int highestScore = optWinner.get().getScorePoints();
            for (final Player player : model.getPlayers()) {
                if (player.getScorePoints() == highestScore) {
                    connection.sendGameEnd(player.getId(), highestScore);
                }
            }
        }
    }

    private void processTitle(final Collection<Player> players,
                              final Function<Player, Integer> title) {
        final Optional<Player> optWinner = players.stream()
                .max(Comparator.comparing(title));
        if (optWinner.isPresent()) {
            final int highestScore = title.apply(optWinner.get());
            int amountOfWinners = 0;
            for (final Player player : players) {
                if (title.apply(player) == highestScore) {
                    amountOfWinners++;
                    player.changeScorePoints(2);
                }
            }
            if (amountOfWinners == 1) {
                optWinner.get().changeScorePoints(1);
            }
        }
    }
}
