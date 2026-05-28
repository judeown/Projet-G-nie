package statistics;

import mock.Agent;
import mock.Grid;
import mock.HealthState;

/**
 * Provides statistical calculations for the simulation grid.
 */
public class StatisticsManager {

    /**
     * Counts all agents currently present in the grid.
     *
     * @param grid the simulation grid
     * @return the total number of agents
     */
    public int countTotalAgents(Grid grid) {
        if (grid == null) {
            return 0;
        }

        int count = 0;

        for (int row = 0; row < grid.getRows(); row++) {
            for (int column = 0; column < grid.getColumns(); column++) {
                if (grid.getAgent(row, column) != null) {
                    count++;
                }
            }
        }

        return count;
    }

    /**
     * Counts agents with a specific health state.
     *
     * @param grid the simulation grid
     * @param state the health state to count
     * @return the number of agents with the given state
     */
    public int countByState(Grid grid, HealthState state) {
        if (grid == null || state == null) {
            return 0;
        }

        int count = 0;

        for (int row = 0; row < grid.getRows(); row++) {
            for (int column = 0; column < grid.getColumns(); column++) {
                Agent agent = grid.getAgent(row, column);

                if (agent != null && agent.getState() == state) {
                    count++;
                }
            }
        }

        return count;
    }
        /**
     * Counts all healthy agents in the grid.
     *
     * @param grid the simulation grid
     * @return the number of healthy agents
     */
    public int countHealthy(Grid grid) {
        return countByState(grid, HealthState.HEALTHY);
    }

    /**
     * Counts all infected agents in the grid.
     *
     * @param grid the simulation grid
     * @return the number of infected agents
     */
    public int countInfected(Grid grid) {
        return countByState(grid, HealthState.INFECTED);
    }
    
    /**
     * Counts all recovered agents in the grid.
     *
     * @param grid the simulation grid
     * @return the number of recovered agents
     */
    public int countRecovered(Grid grid) {
        return countByState(grid, HealthState.RECOVERED);
    }

    /**
     * Counts all dead agents in the grid.
     *
     * @param grid the simulation grid
     * @return the number of dead agents
     */
    public int countDead(Grid grid) {
        return countByState(grid, HealthState.DEAD);
    }

        /**
     * Calculates the percentage of infected agents among all agents.
     *
     * @param grid the simulation grid
     * @return the infection percentage
     */
    public double calculateInfectionPercentage(Grid grid) {
        int totalAgents = countTotalAgents(grid);

        if (totalAgents == 0) {
            return 0.0;
        }

        return countInfected(grid) * 100.0 / totalAgents;
    }

    /**
     * Calculates the percentage of dead agents among all agents.
     *
     * @param grid the simulation grid
     * @return the death percentage
     */
    public double calculateDeathPercentage(Grid grid) {
        int totalAgents = countTotalAgents(grid);

        if (totalAgents == 0) {
            return 0.0;
        }

        return countDead(grid) * 100.0 / totalAgents;
    }
}
