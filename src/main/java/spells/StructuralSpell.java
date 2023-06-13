package de.unisaarland.cs.se.selab.spells;

import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.comm.BidType;
import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.model.Player;
import de.unisaarland.cs.se.selab.model.dungeon.Dungeon;
import de.unisaarland.cs.se.selab.model.dungeon.Room;
import de.unisaarland.cs.se.selab.model.dungeon.Tunnel;
import java.util.Optional;

public class StructuralSpell extends Spell {
    private final StructureEffect structureEffect;

    public StructuralSpell(final int id, final SpellType spellType,
                           final BidType bidType, final int slot,
                           final StructureEffect structureEffect) {
        super(id, spellType, bidType, slot);
        this.structureEffect = structureEffect;
        this.magicPointsNeeded = 5;
    }

    /**
     * @param player  the player It is going to affect
     * @param model   the model, on which changes are based.
     * @param wrapper the wrapper, for sending events
     */

    @Override
    protected void cast(final Player player, final Model model,
                        final ConnectionWrapper wrapper) {
        final Dungeon dungeon = player.getDungeon();
        final Optional<Tunnel> battleGroundOptional = dungeon.getBattleGround();

        if (battleGroundOptional.isEmpty()) {
            return;
        }
        final Tunnel battleGround = battleGroundOptional.get();
        // destroy or conquer
        if (structureEffect == StructureEffect.DESTROY) {
            final Optional<Tunnel> tunnelRoom =
                    dungeon.getGraph().closestRoomToAdventurer(battleGround);
            tunnelRoom.ifPresent(
                    tunnel -> destroyRoom(player.getId(), tunnel, battleGround, wrapper));

        } else {
            //conquer
            dungeon.conquer();

        }


    }

    /**
     * destroy the room and if it is the current battleGround,
     * removes a monster
     *
     * @param playerId     player fighting
     * @param tunnel       the tunnel having the closest room to the adventurers
     * @param battleGround the current battleGround
     * @param wrapper      the connection wrapper
     */
    private void destroyRoom(final int playerId, final Tunnel tunnel,
                             final Tunnel battleGround, final ConnectionWrapper wrapper) {


        assert tunnel.getRoom().isPresent();
        final Room room = tunnel.getRoom().get();
        //destroy room
        tunnel.setRoom(null);
        wrapper.sendRoomRemoved(playerId, room.getId());

        //if room is currentBattleground remove monster
        if (battleGround.getCoordinate().posX() == tunnel.getCoordinate().posX()
                && battleGround.getCoordinate().posY() == tunnel.getCoordinate().posY()) {
            if (tunnel.getMonsters().size() == 2) {
                tunnel.getMonsters().remove(0);
            }
        }
    }


}
