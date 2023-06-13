package de.unisaarland.cs.se.selab.commands;

import de.unisaarland.cs.se.selab.comm.ActionFactory;
import de.unisaarland.cs.se.selab.comm.BidType;
import java.util.Map;
import java.util.Optional;

public class CommandFactory implements ActionFactory<Command> {

    private final Map<Integer, Integer> commToPlayer;

    private Optional<Integer> playerId(final int commID) {
        return Optional.ofNullable(this.commToPlayer.get(commID));
    }

    public CommandFactory(final Map<Integer, Integer> idMap) {
        this.commToPlayer = idMap;
    }

    @Override
    public Command createRegister(final int commId, final String playerName) {
        final Optional<Integer> playerId = playerId(commId);
        return playerId.map(id -> new RegisterCommand(id, playerName, false))
                .orElseGet(() -> new RegisterCommand(commId, playerName, true));
    }

    @Override
    public Command createLeave(final int commId) {
        final Optional<Integer> playerId = playerId(commId);
        return playerId.isPresent()
                ? new LeaveCommand(playerId.get())
                : new EmptyCommand();
    }

    @Override
    public Command createStartGame(final int commId) {
        final Optional<Integer> playerId = playerId(commId);
        return playerId.isPresent()
                ? new StartGameCommand(playerId.get())
                : new EmptyCommand();
    }

    @Override
    public Command createPlaceBid(final int commId, final BidType order, final int slot) {
        final Optional<Integer> playerId = playerId(commId);
        return playerId.isPresent()
                ? new PlaceBidCommand(playerId.get(), order, slot)
                : new EmptyCommand();
    }

    @Override
    public Command createDigTunnel(final int commId, final int x, final int y) {
        final Optional<Integer> playerId = playerId(commId);
        return playerId.isPresent()
                ? new DigTunnelCommand(playerId.get(), x, y)
                : new EmptyCommand();
    }

    @Override
    public Command createHireMonster(final int commId, final int monster) {
        final Optional<Integer> playerId = playerId(commId);
        return playerId.isPresent()
                ? new HireMonsterCommand(playerId.get(), monster)
                : new EmptyCommand();
    }

    @Override
    public Command createActivateRoom(final int commId, final int room) {
        final Optional<Integer> playerId = playerId(commId);
        return playerId.isPresent()
                ? new ActivateRoomCommand(playerId.get(), room)
                : new EmptyCommand();
    }

    @Override
    public Command createBattleGround(final int commId, final int x, final int y) {
        final Optional<Integer> playerId = playerId(commId);
        return playerId.isPresent()
                ? new BattleGroundCommand(playerId.get(), x, y)
                : new EmptyCommand();
    }

    @Override
    public Command createBuildRoom(final int commId, final int x, final int y, final int room) {
        final Optional<Integer> playerId = playerId(commId);
        return playerId.isPresent()
                ? new BuildRoomCommand(playerId.get(), x, y, room)
                : new EmptyCommand();
    }

    @Override
    public Command createEndTurn(final int commId) {
        final Optional<Integer> playerId = playerId(commId);
        return playerId.isPresent()
                ? new EndTurnCommand(playerId.get())
                : new EmptyCommand();
    }

    @Override
    public Command createMonster(final int commId, final int monster) {
        final Optional<Integer> playerId = playerId(commId);
        return playerId.isPresent()
                ? new MonsterCommand(playerId.get(), monster)
                : new EmptyCommand();
    }

    @Override
    public Command createMonsterTargeted(final int commId, final int monster, final int position) {
        final Optional<Integer> playerId = playerId(commId);
        return playerId.isPresent()
                ? new MonsterTargetedCommand(playerId.get(), monster, position)
                : new EmptyCommand();
    }

    @Override
    public Command createTrap(final int commId, final int trap) {
        final Optional<Integer> playerId = playerId(commId);
        return playerId.isPresent()
                ? new TrapCommand(playerId.get(), trap)
                : new EmptyCommand();
    }

    @Override
    public Command createCastCounterSpell(final int i) {
        final Optional<Integer> playerId = playerId(i);
        return playerId.isPresent()
                ? new CastCounterSpellCommand(playerId.get())
                : new EmptyCommand();
    }
}
