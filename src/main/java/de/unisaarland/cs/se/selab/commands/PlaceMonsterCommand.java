package de.unisaarland.cs.se.selab.commands;

import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.model.Monster;
import de.unisaarland.cs.se.selab.model.Player;
import de.unisaarland.cs.se.selab.model.dungeon.Dungeon;
import de.unisaarland.cs.se.selab.model.dungeon.Tunnel;
import de.unisaarland.cs.se.selab.state.State;
import java.util.Optional;
import java.util.Set;

/**
 * A player places a monster for defense.
 */
public abstract class PlaceMonsterCommand extends PlayerCommand {

    private final int monsterId;

    public PlaceMonsterCommand(final int playerId, final int monsterId) {
        super(playerId);
        this.monsterId = monsterId;
    }

    @Override
    public Set<State.Phase> inPhase() {
        return Set.of(State.Phase.COMBAT);
    }

    @Override
    public ActionResult run(final Model model, final ConnectionWrapper connection) {
        final Player player = model.getPlayerById(getId());
        final Optional<Monster> optMonster = player.getMonster(this.monsterId);

        if (optMonster.isEmpty()) {
            connection.sendActionFailed(getId(), "This monster does not belong to you.");
            return ActionResult.RETRY;
        }

        final Dungeon dungeon = player.getDungeon();
        final Optional<Tunnel> battleGround = dungeon.getBattleGround();
        if (battleGround.isEmpty()) {
            connection.sendActionFailed(getId(), "The battleground has not been set yet.");
            return ActionResult.RETRY;
        }

        final Monster monster = optMonster.get();
        if (monster.isUsed()) {
            connection.sendActionFailed(getId(), "This monster has already been used this year.");
            return ActionResult.RETRY;
        }
        return placeMonster(monster, dungeon, battleGround.get(), connection);
    }

    protected abstract ActionResult placeMonster(Monster monster, Dungeon dungeon,
                                                 Tunnel battleGround, ConnectionWrapper connection);
}
