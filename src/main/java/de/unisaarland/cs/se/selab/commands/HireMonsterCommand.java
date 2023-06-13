package de.unisaarland.cs.se.selab.commands;

import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.model.Monster;
import de.unisaarland.cs.se.selab.model.Player;
import de.unisaarland.cs.se.selab.state.State;
import java.util.Optional;
import java.util.Set;

/**
 * A player hires a monster.
 */
public class HireMonsterCommand extends PlayerCommand {

    private final int monsterId;

    public HireMonsterCommand(final int playerId, final int monsterId) {
        super(playerId);
        this.monsterId = monsterId;
    }

    @Override
    public Set<State.Phase> inPhase() {
        return Set.of(State.Phase.HIRING_MONSTER);
    }

    @Override
    public ActionResult run(final Model model, final ConnectionWrapper connection) {
        final Player player = model.getPlayerById(getId());
        final Optional<Monster> optMonster = model.getAvailableMonster(this.monsterId);

        if (optMonster.isPresent()) {
            final Monster monster = optMonster.get();
            if (player.getFood() >= monster.getHunger()
                    && player.getEvilness() + monster.getEvilness() <= Player.MAX_EVILNESS) {
                player.changeFood(-monster.getHunger());
                player.changeEvilness(monster.getEvilness());
                player.addMonster(monster);
                model.removeAvailableMonster(monster);
                if (monster.getHunger() > 0) {
                    connection.sendFoodChanged(getId(), -monster.getHunger());
                }
                if (monster.getEvilness() > 0) {
                    connection.sendEvilnessChanged(getId(), monster.getEvilness());
                }
                connection.sendMonsterHired(getId(), this.monsterId);
                return ActionResult.PROCEED;
            } else {
                connection.sendActionFailed(getId(), "You cannot feed this monster.");
            }
        } else {
            connection.sendActionFailed(getId(), "This monster is not available to hire.");
        }
        return ActionResult.RETRY;
    }
}
