package com.google.research.bleth.simulator;

/**
 * A deterministic movement strategy using for testing.
 * Moves the agent up whenever this is a legal mo, otherwise stays in the same location.
 */
public class UpMovementStrategy implements IMovementStrategy {

    @Override
    public Location moveTo(IAgentOwner owner, Location currentLocation) {
        Location newLocation = currentLocation.moveInDirection(Direction.UP);
        if (owner.isLocationValid(newLocation)) {
            return newLocation;
        }
        return currentLocation;
    }
}
