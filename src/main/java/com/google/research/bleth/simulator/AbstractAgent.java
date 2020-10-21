package com.google.research.bleth.simulator;

/** Represent both observer and beacon. Responsible for the agents' movement. */
public abstract class AbstractAgent implements IAgent {
    private final IMovementStrategy IMovementStrategy;
    private Location realLocation; // the agent's location on the board, changed each time the agent moves.
    private final IAgentOwner owner;

    /**
     * @param initialLocation is the location on board where the agent is placed.
     * @param movementStrategy determines how the agent moves.
     * @param owner is the real board that represents the world in which the agent lives.
     */
    public AbstractAgent(Location initialLocation, IMovementStrategy movementStrategy, IAgentOwner owner) {
        realLocation = initialLocation;
        this.IMovementStrategy = movementStrategy;
        this.owner = owner;
    }

    @Override
    public Location getLocation() {
        return realLocation;
    }

    /**
     * Calculate the location the agent is moving to, based on its current location and its strategy.
     * @return the location on board which the agent is moving to.
     */
    @Override
    public Location moveTo() {
        return IMovementStrategy.moveTo(owner, realLocation);
    }

    @Override
    public void move() {
        Location nextMove = moveTo();
        owner.updateAgentLocation(realLocation, nextMove, this);
        realLocation = nextMove;
    }
}
