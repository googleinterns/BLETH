package com.google.research.bleth.simulator;

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
