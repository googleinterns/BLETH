package com.google.research.bleth.simulator;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableMap.toImmutableMap;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import java.util.function.Function;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Tracing Simulation's Resolver, which receives information from its observers and estimate the beacons' locations according to it. */
public final class GlobalResolver implements IGlobalResolver {
    private final EstimatedBoard estimatedBoard;
    private final Multimap<Transmission, Location> currentRoundTransmissions = ArrayListMultimap.create();
    private final Map<Transmission, Beacon> transmissionsToBeacons;
    private final Map<Beacon, Location> beaconsToEstimatedLocations = new HashMap<>();

    /**
     * A wrapper method to create new global resolver for a tracing simulation.
     * The new resolver has a board for storing the estimated beacons' locations.
     * @param rowsNum is number of rows of both the simulation's board and the estimated board.
     * @param colsNum is number of columns of both the simulation's board and the estimated board.
     * @param beacons is a list of the simulation's beacons.
     */
    public static GlobalResolver create(int rowsNum, int colsNum, List<Beacon> beacons) {
        checkNotNull(beacons);
        EstimatedBoard estimatedBoard = new EstimatedBoard(rowsNum, colsNum);
        Map<Transmission, Beacon> transmissionsToBeacons =
                beacons.stream().collect(toImmutableMap(Beacon::transmit, Function.identity()));
        return new GlobalResolver(estimatedBoard, transmissionsToBeacons);
    }

    @Override
    public void receiveInformation(Location observerLocation, List<Transmission> transmissions) {
        for (Transmission transmission : transmissions) {
            currentRoundTransmissions.put(transmission, observerLocation);
        }
    }

    @Override
    public void estimate() {
        // Update only the beacons that there's new information about their location
        for (Transmission transmission : currentRoundTransmissions.keySet()) {
            Beacon beacon = transmissionsToBeacons.get(transmission);
            // Take into consideration the current estimated location of the beacon if there's such
            if (beaconsToEstimatedLocations.containsKey(beacon)) {
                currentRoundTransmissions.put(transmission, beaconsToEstimatedLocations.get(beacon));
            }
            Location newLocation = estimateNewLocation(beacon);

            if (!beaconsToEstimatedLocations.containsKey(beacon)) {
                estimatedBoard.placeAgent(newLocation, beacon);
            } else {
                estimatedBoard.moveAgent(beaconsToEstimatedLocations.get(beacon), newLocation, beacon);
            }
            beaconsToEstimatedLocations.put(beacon, newLocation);
        }

        currentRoundTransmissions.clear();
    }

    @Override
    public EstimatedBoard getBoard() {
        return estimatedBoard;
    }

    private GlobalResolver(EstimatedBoard estimatedBoard, Map<Transmission, Beacon> transmissionsToBeacons) {
        this.estimatedBoard = estimatedBoard;
        this.transmissionsToBeacons = transmissionsToBeacons;
    }

    private Location estimateNewLocation(Beacon beacon) {
        List<Location> locations = (List<Location>) currentRoundTransmissions.get(beacon.transmit());
        int newRow = (int) Math.round((locations.stream().mapToDouble(location -> location.row).average().getAsDouble()));
        int newCol = (int) Math.round((locations.stream().mapToDouble(location -> location.col).average().getAsDouble()));
        return new Location(newRow, newCol);
    }
}