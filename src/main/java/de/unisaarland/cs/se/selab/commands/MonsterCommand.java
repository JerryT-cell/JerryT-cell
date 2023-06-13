package de.unisaarland.cs.se.selab.commands;

import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.model.AttackStrategy;
import de.unisaarland.cs.se.selab.model.Monster;
import de.unisaarland.cs.se.selab.model.dungeon.Dungeon;
import de.unisaarland.cs.se.selab.model.dungeon.Tunnel;

/**
 * A player sets a monster with untargeted attack for defense.
 */
public class MonsterCommand extends PlaceMonsterCommand {

    public MonsterCommand(final int playerId, final int monsterId) {
        super(playerId, monsterId);
    }

    @Override
    protected ActionResult placeMonster(final Monster monster, final Dungeon dungeon,
                                   final Tunnel battleGround,
                                   final ConnectionWrapper connection) {
        if (monster.getAttackStrategy() == (AttackStrategy.TARGETED)) {
            connection.sendActionFailed(getId(), "This monster needs no target to fight.");
            return ActionResult.RETRY;
        }

        if (!battleGround.addMonster(monster)) {
            connection.sendActionFailed(getId(), "You cannot place any more monsters here.");
            return ActionResult.RETRY;
        }
        monster.setUsed(true);
        connection.sendMonsterPlaced(monster.getId(), getId());
        return ActionResult.RETRY;
    }
}
