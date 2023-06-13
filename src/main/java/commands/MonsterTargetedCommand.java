package de.unisaarland.cs.se.selab.commands;

import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.model.AttackStrategy;
import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.model.Monster;
import de.unisaarland.cs.se.selab.model.dungeon.Dungeon;
import de.unisaarland.cs.se.selab.model.dungeon.Tunnel;

/**
 * A player sets a monster with a targeted attack for defense.
 */
public class MonsterTargetedCommand extends PlaceMonsterCommand {
    private final int position;

    public MonsterTargetedCommand(final int playerId, final int monsterId, final int position) {
        super(playerId, monsterId);
        this.position = position;
    }


    @Override
    protected ActionResult placeMonster(final Monster monster, final Dungeon dungeon,
                                   final Tunnel battleGround, final ConnectionWrapper connection) {
        if (monster.getAttackStrategy() != (AttackStrategy.TARGETED)) {
            connection.sendActionFailed(getId(), "This monster needs no target to fight.");
            return ActionResult.RETRY;
        }

        if (this.position < 1 || this.position > (Model.MAX_ROUNDS - 1)) {
            connection.sendActionFailed(getId(), "Invalid Target");
            return ActionResult.RETRY;
        }
        if (!battleGround.addMonster(monster)) {
            connection.sendActionFailed(getId(), "You cannot place any more monsters here.");
            return ActionResult.RETRY;
        }
        monster.setTarget(this.position);
        monster.setUsed(true);
        connection.sendMonsterPlaced(monster.getId(), getId());
        return ActionResult.RETRY;
    }
}
