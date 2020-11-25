# BLETH

## Overview

**BLETH 2020** is an open-source web-application project designated for visualizing and analyzing the **BLE protocol** in various abstract key scenarios. This is done by running a simulator, which is being built based on user defined parameters and rules. In general, a simulator is a ‘world’ full of beacons and observers interacting with each other (both will be referred to as ‘agents’). The world is represented by a board (a matrix of agents for locations representation), and the simulation runs in discrete time (e.g in rounds) and computes some relevant statistical data to be accessible for the end user for further analysis.

## Getting Started

[BLETH Homepage](https://bleth-2020.ew.r.appspot.com)


### Create a New BLETH Simulation

* Navigate to [Create New Simulation](https://bleth-2020.ew.r.appspot.com/new_simulation.html).
* Fill in all required parameters.
* Hit 'submit' and wait for confirmation message.

### Visualize Your BLETH Simulation.

* Navigate to [Visualize Existing Simulation](https://bleth-2020.ew.r.appspot.com/simulations.html).
* Choose your (or any other existing) simulation.
* Hit 'Visualize Simulation'.
* Watch the global resolver estimates the moving beacons (the left board is the real while the right board presents the resolver's estimations).

## Ongoing Work

* Improving boards' visualization.
* Providing statistical data per simulation.
* Providing aggregated statistical data over multiple simulation matching a set of conditions.
