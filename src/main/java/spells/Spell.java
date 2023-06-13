package de.unisaarland.cs.se.selab.spells;

import de.unisaarland.cs.se.selab.ConnectionUtils;
import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.comm.BidType;
import de.unisaarland.cs.se.selab.commands.ActionResult;
import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.model.Player;
import de.unisaarland.cs.se.selab.state.State;

/**
 * Super class for all spells
 */
public abstract class Spell {

    protected final int id;
    protected final SpellType spellType;
    protected final BidType bidType;
    protected final int slot;
    protected int magicPointsNeeded;

    /**
     * create a new spell in general
     *
     * @param id        id of the spell
     * @param spellType what kind of spell it represents
     * @param bidType   the bidType of the triggering
     * @param slot      the specific slot
     */

    public Spell(final int id, final SpellType spellType, final BidType bidType, final int slot) {
        this.id = id;
        this.spellType = spellType;
        this.bidType = bidType;
        this.slot = slot;
    }

    /**
     * this method describes the algorithm of casting a spell. It will be overwritten
     * by every spell subclasses.
     *
     * @param playerId the player It is going to affect
     * @param model    the model, on which changes are based.
     * @param wrapper  the wrapper, for sending events
     * @return the result that indicates how the game should proceed
     */
    public ActionResult cast(final int playerId, final Model model,
                             final ConnectionWrapper wrapper) {
        final Player player = model.getPlayerById(playerId);
        final boolean result = castAndCounterSpell(player, model, wrapper);
        if (model.getPlayers().isEmpty()) { //if all players left during the command "counter spell"
            return ActionResult.END_GAME;
        } else if (!result && player.isAlive()) { //if player sends an endTurn and player is there:
            cast(player, model, wrapper);
        }

        return ActionResult.PROCEED;

    }

    protected abstract void cast(Player player, Model model, ConnectionWrapper wrapper);

    public int getId() {
        return id;
    }

    public SpellType getSpellType() {
        return spellType;
    }

    public BidType getBidType() {
        return bidType;
    }

    public int getSlot() {
        return slot;
    }

    public int getMagicPointsNeeded() {
        return magicPointsNeeded;
    }

    /**
     * sends the castSpell event and
     * handle the counter spells if present
     *
     * @param player  the player fighting
     * @param model   the model used
     * @param wrapper the wrapper connection
     * @return true if player used the counter spell and false if there's an endTurn
     * or player left
     */

    protected boolean castAndCounterSpell(final Player player, final Model model,
                                          final ConnectionWrapper wrapper) {
        wrapper.sendSpellCast(id, player.getId());
        player.increaseWithstoodSpell();
        boolean result = false;
        final int initialCounterSpells = player.getCounterSpells();
        if (player.getCounterSpells() > 0) {
            wrapper.sendCounterSpell(player.getId());
            ConnectionUtils.executePlayerCommand(model, wrapper, State.Phase.SPELL,
                    player);
            result = player.isAlive() && player.getCounterSpells() < initialCounterSpells;
        }
        return result;
    }

}
