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
     * Creates a sample grid with a few agents for testing.
     *
     * @return a populated grid
     */
    private static Grid createSampleGrid() {
        Grid grid = new Grid(3, 3);

        Agent a0 = new Agent(0, 0, 20); grid.addAgent(a0);
        Agent a1 = new Agent(0, 1, 30); a1.infect();   grid.addAgent(a1);
        Agent a2 = new Agent(0, 2, 40); a2.infect();   grid.addAgent(a2);
        Agent a3 = new Agent(1, 0, 25); a3.recover();  grid.addAgent(a3);
        Agent a4 = new Agent(1, 1, 70); a4.die();      grid.addAgent(a4);

        return grid;
    }

    /**
     * Prints simulation statistics to standard output.
     *
     * @param grid              the simulation grid
     * @param statisticsManager the statistics manager
     */
    private static void printStatistics(Grid grid, StatisticsManager statisticsManager) {
        System.out.println("===== Simulation Statistics =====");
        System.out.println("Total agents:        " + statisticsManager.countTotalAgents(grid));
        System.out.println("Healthy agents:      " + statisticsManager.countHealthy(grid));
        System.out.println("Infected agents:     " + statisticsManager.countInfected(grid));
        System.out.println("Recovered agents:    " + statisticsManager.countRecovered(grid));
        System.out.println("Dead agents:         " + statisticsManager.countDead(grid));
        System.out.println("Infection %:         " + statisticsManager.calculateInfectionPercentage(grid) + "%");
        System.out.println("Death %:             " + statisticsManager.calculateDeathPercentage(grid) + "%");
        System.out.println("Average age:         " + statisticsManager.calculateAverageAge(grid));
        System.out.println("Average energy:      " + statisticsManager.calculateAverageEnergy(grid));
        System.out.println("Min energy:          " + statisticsManager.getMinimumEnergy(grid));
        System.out.println("Max energy:          " + statisticsManager.getMaximumEnergy(grid));
    }
}
