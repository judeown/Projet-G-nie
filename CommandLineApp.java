


/**
 * Provides a command line entry point for testing the statistics module.
 */
public class CommandLineApp {

    /**
     * Starts the command line application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        Grid grid = createSampleGrid();
        StatisticsManager statisticsManager = new StatisticsManager();

        printStatistics(grid, statisticsManager);
    }

    /**
     * Creates a sample grid used to test the statistics module.
     *
     * @return a grid containing sample agents
     */
    private static Grid createSampleGrid() {
        Grid grid = new Grid(3, 3);

        grid.setAgent(0, 0, new Agent(HealthState.HEALTHY, 20, 90.0));
        grid.setAgent(0, 1, new Agent(HealthState.INFECTED, 30, 60.0));
        grid.setAgent(0, 2, new Agent(HealthState.INFECTED, 40, 50.0));
        grid.setAgent(1, 0, new Agent(HealthState.RECOVERED, 25, 80.0));
        grid.setAgent(1, 1, new Agent(HealthState.DEAD, 70, 0.0));

        return grid;
    }

    /**
     * Prints the simulation statistics in the terminal.
     *
     * @param grid the simulation grid
     * @param statisticsManager the statistics manager
     */
    private static void printStatistics(Grid grid, StatisticsManager statisticsManager) {
        System.out.println("===== Simulation Statistics =====");
        System.out.println("Total agents: " + statisticsManager.countTotalAgents(grid));
        System.out.println("Healthy agents: " + statisticsManager.countHealthy(grid));
        System.out.println("Infected agents: " + statisticsManager.countInfected(grid));
        System.out.println("Recovered agents: " + statisticsManager.countRecovered(grid));
        System.out.println("Dead agents: " + statisticsManager.countDead(grid));
        System.out.println("Infection percentage: " + statisticsManager.calculateInfectionPercentage(grid) + "%");
        System.out.println("Death percentage: " + statisticsManager.calculateDeathPercentage(grid) + "%");
        System.out.println("Average age: " + statisticsManager.calculateAverageAge(grid));
        System.out.println("Average energy: " + statisticsManager.calculateAverageEnergy(grid));
        System.out.println("Minimum energy: " + statisticsManager.getMinimumEnergy(grid));
        System.out.println("Maximum energy: " + statisticsManager.getMaximumEnergy(grid));
    }
}