# 2D Cell Simulation

## Project Description

This project is a Java and JavaFX application developed for the P.G.L. course.
The goal is to simulate the evolution of cells on a configurable 2D grid.

Cells can evolve over time according to their state, age, energy, movement, neighborhood and probabilistic rules. The user can interact visually with the grid, add or remove cells, run the simulation, pause it, execute it step by step, observe live statistics, and save or restore a simulation.

---

## Main Features

* Graphical interface using JavaFX
* Configurable 2D grid
* Bounded or toroidal grid topology
* Several cell states:

  * Healthy
  * Infected
  * Recovered
  * Dead
* Individual cell creation and removal
* Brush mode for adding cells around a selected position
* Zone mode for adding cells in a rectangular area
* Random cell generation
* Simulation controls:

  * Start
  * Pause
  * Resume
  * Step-by-step execution
  * Speed control
* Cell evolution rules:

  * Movement
  * Infection propagation
  * Recovery
  * Death depending on time, energy and probabilities
  * Cell division
  * Interactions with neighboring cells
* Live statistics:

  * Number of cells by state
  * Percentage of cells by state
  * Average age
  * Average energy
  * Minimum and maximum energy
* Simple graphical statistics display
* Binary save and load system
* Command-line version for testing the model without JavaFX

---

## Technologies Used

* Java
* JavaFX
* Object-Oriented Programming
* Git / GitHub
* Binary serialization for save/load

---

## Project Structure

```text
Projet-G-nie/
│
├── Agent.java
├── Grid.java
├── HealthState.java
├── Position.java
├── SimulationEngine.java
├── SimulationSerializer.java
├── StatisticsManager.java
├── CommandLineApp.java
├── MainApp.java
│
├── controller/
│   └── InteractionController.java
│
├── view/
│   ├── ControlPanel.java
│   ├── GridView.java
│   ├── MainView.java
│   └── StatsPanel.java
│
├── javafx-sdk-21.0.11/
│   └── lib/
│
└── README.md
```

---

## Main Classes

### `Agent`

Represents a cell in the simulation. An agent has a position, an age, an energy level, a movement probability and a health state.

### `Grid`

Represents the 2D simulation grid. It stores the cells and manages positions, empty cells, neighbors and toroidal or bounded behavior.

### `HealthState`

Enumeration representing the possible states of a cell:

```java
HEALTHY, INFECTED, RECOVERED, DEAD
```

### `SimulationEngine`

Contains the main simulation logic. At each step, it updates the cells according to movement, infection, recovery, death, division and neighborhood interaction rules.

### `StatisticsManager`

Computes global statistics about the grid, such as the number of cells per state, percentages, average age and energy values.

### `SimulationSerializer`

Handles binary save and load operations. It allows the user to save the current simulation and restore it later.

### `CommandLineApp`

Provides a command-line version of the project. It is used to test the model and statistics without launching the JavaFX interface.

### `MainApp`

Main entry point of the JavaFX application.

### `InteractionController`

Connects user actions with the grid. It manages tools such as add, remove, brush and zone selection.

### `MainView`, `GridView`, `ControlPanel`, `StatsPanel`

These classes build the graphical interface, display the grid, provide simulation controls and show live statistics.

---

## Requirements

Before running the project, make sure the following tools are available:

* Java JDK 17 or higher
* Git, if the project is cloned from GitHub

The JavaFX SDK is included in the project folder:

```text
javafx-sdk-21.0.11/
```

Check Java installation:

```powershell
java --version
javac --version
```

---

## How to Compile and Run the JavaFX Version

From the root folder of the project, run the following commands in PowerShell.

### 1. Create the output folder

```powershell
if (!(Test-Path out)) { mkdir out }
```

### 2. Compile the project

```powershell
javac -encoding UTF-8 --module-path "javafx-sdk-21.0.11\lib" --add-modules javafx.controls -d out Agent.java HealthState.java Position.java Grid.java SimulationEngine.java StatisticsManager.java SimulationSerializer.java MainApp.java CommandLineApp.java controller\*.java view\*.java
```

If no error message appears, the compilation succeeded.

### 3. Run the JavaFX application

```powershell
java --module-path "javafx-sdk-21.0.11\lib" --add-modules javafx.controls -cp out MainApp
```

---

## How to Run the Command-Line Version

After compiling the project, run:

```powershell
java -cp out CommandLineApp
```

The command-line version tests the model and displays statistics without opening the JavaFX interface.

---

## Clean Compiled Files

The `out` folder contains generated `.class` files. It can be deleted safely because it is recreated during compilation.

```powershell
Remove-Item -Recurse -Force out -ErrorAction SilentlyContinue
```

---

## How to Use the Application

1. Launch the JavaFX application.
2. Choose the desired interaction mode:

   * Add
   * Remove
   * Brush
   * Zone
3. Select the cell state to place on the grid.
4. Click on the grid to interact with cells.
5. Use the simulation buttons to start, pause, resume or execute one step.
6. Adjust the simulation speed if needed.
7. Observe the live statistics panel.
8. Save the simulation to a binary file when needed.
9. Load a saved file to restore a previous simulation state.

---

## Simulation Rules

At each simulation step, cells may evolve according to several rules:

* Healthy cells can become infected when they are close to infected cells.
* Infected cells can recover according to a recovery probability.
* Cells can die depending on age, energy or mortality probability.
* Cells can move to nearby empty positions.
* Some cells can divide and create new cells.
* Neighboring cells can influence each other through proximity-based interactions.

The rules include probabilities in order to create dynamic and non-deterministic behavior.

---

## Save and Load

The application supports binary import/export.

* Save: stores the current simulation state on disk.
* Load: restores a saved simulation and allows the user to continue from the previous state.

This feature is implemented with a binary format containing the grid dimensions, topology, simulation step count and agent data.

---

## Example Demonstration Scenario

A possible demonstration scenario is:

1. Clone the GitHub repository.
2. Compile the project from the command line.
3. Launch the JavaFX application.
4. Fill the grid randomly or add cells manually.
5. Add several healthy cells.
6. Add a few infected cells.
7. Start the simulation.
8. Observe the propagation of infection.
9. Pause the simulation.
10. Execute one step manually.
11. Show the statistics panel.
12. Save the simulation.
13. Load the saved simulation.
14. Continue the simulation from the restored state.

---

## Authors

Project developed by an ING1-GI student group for the P.G.L. Cellules 2D project.

Team members:

* Laurent Minkoulou: Cells / Agents
* Rihem Sahli: Grid and simulation engine
* Asaad Nasri: JavaFX interface
* Jude Owona Assoumou: User interactions
* Youssef Missaoui: Statistics, save/load system and command-line version

---

## Notes

The project must be compiled and launched from the command line for the final demonstration.

The `out` folder is not part of the source code. It is generated during compilation and should not be committed to Git.

The JavaFX SDK path used in this project is:

```text
javafx-sdk-21.0.11\lib
```
