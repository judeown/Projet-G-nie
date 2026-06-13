import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * Interactive command line interface for the 2D cell simulation.
 *
 * <p>Runs the same {@link Grid} and {@link SimulationEngine} used by the
 * JavaFX application, so the simulation model can be tested and demonstrated
 * independently of the graphical interface.
 */
public class CommandLineApp {

    private static final int GRID_WIDTH  = 40;
    private static final int GRID_HEIGHT = 20;

    /**
     * Starts the interactive command line simulation loop.
     *
     * @param args command line arguments (unused)
     */
    public static void main(String[] args) {
        Grid grid = new Grid(GRID_WIDTH, GRID_HEIGHT);
        SimulationEngine engine = new SimulationEngine(grid, 0.3, 0.1, 0.05, 0.3);
        engine.start();

        StatisticsManager stats  = new StatisticsManager();
        Scanner           scanner = new Scanner(System.in);

        printLegend();

        boolean running = true;
        while (running) {
            printGrid(grid);
            printStats(grid, stats, engine);
            printMenu();

            String choice = scanner.nextLine().trim().toLowerCase();
            switch (choice) {
                case "s":
                    engine.step();
                    break;
                case "r":
                    runSteps(engine, scanner);
                    break;
                case "f":
                    randomFill(grid, scanner);
                    break;
                case "c":
                    grid.clear();
                    engine.resetStep();
                    break;
                case "p":
                    setParameters(engine, scanner);
                    break;
                case "t":
                    engine.setToroidal(!grid.isToroidal());
                    System.out.println("Toroidal grid: " + grid.isToroidal());
                    break;
                case "v":
                    saveSimulation(grid, engine, scanner);
                    break;
                case "l":
                    loadSimulation(grid, engine, scanner);
                    break;
                case "q":
                    running = false;
                    break;
                default:
                    System.out.println("Unknown command.");
                    break;
            }
        }

        scanner.close();
        System.out.println("Goodbye!");
    }

    /**
     * Prints the symbol legend used by {@link #printGrid}.
     */
    private static void printLegend() {
        System.out.println("===== 2D Cell Simulation (CLI) =====");
        System.out.println("Legend: . empty | H healthy | X infected | R recovered | # dead");
    }

    /**
     * Prints an ASCII representation of the grid.
     *
     * @param grid the simulation grid
     */
    private static void printGrid(Grid grid) {
        System.out.println();
        for (int y = 0; y < grid.getHeight(); y++) {
            StringBuilder line = new StringBuilder();
            for (int x = 0; x < grid.getWidth(); x++) {
                line.append(symbolFor(grid.getAgent(x, y)));
            }
            System.out.println(line);
        }
    }

    /**
     * Returns the display symbol for an agent.
     *
     * @param agent the agent, or null for an empty cell
     * @return a single character representing the cell
     */
    private static char symbolFor(Agent agent) {
        if (agent == null) return '.';
        switch (agent.getState()) {
            case HEALTHY:   return 'H';
            case INFECTED:  return 'X';
            case RECOVERED: return 'R';
            case DEAD:      return '#';
            default:        return '.';
        }
    }

    /**
     * Prints current simulation statistics.
     *
     * @param grid   the simulation grid
     * @param stats  the statistics manager
     * @param engine the simulation engine
     */
    private static void printStats(Grid grid, StatisticsManager stats, SimulationEngine engine) {
        int total     = stats.countTotalAgents(grid);
        int healthy   = stats.countHealthy(grid);
        int infected  = stats.countInfected(grid);
        int recovered = stats.countRecovered(grid);
        int dead      = stats.countDead(grid);

        System.out.println();
        System.out.printf("Step: %d | Toroidal: %s%n", engine.getCurrentStep(), grid.isToroidal());
        System.out.printf("Total: %d | Healthy: %d | Infected: %d | Recovered: %d | Dead: %d%n",
            total, healthy, infected, recovered, dead);
        System.out.printf("Avg age: %.1f | Avg energy: %.1f | Min energy: %.1f | Max energy: %.1f%n",
            stats.calculateAverageAge(grid), stats.calculateAverageEnergy(grid),
            stats.getMinimumEnergy(grid), stats.getMaximumEnergy(grid));
    }

    /**
     * Prints the interactive command menu.
     */
    private static void printMenu() {
        System.out.println();
        System.out.println("[s] Step      [r] Run N steps   [f] Random fill   [c] Clear");
        System.out.println("[p] Params    [t] Toggle torus  [v] Save          [l] Load   [q] Quit");
        System.out.print("> ");
    }

    /**
     * Runs the simulation for a number of steps entered by the user.
     *
     * @param engine  the simulation engine
     * @param scanner the input scanner
     */
    private static void runSteps(SimulationEngine engine, Scanner scanner) {
        System.out.print("Number of steps: ");
        int n = readInt(scanner, 1);
        for (int i = 0; i < n; i++) engine.step();
    }

    /**
     * Fills empty cells of the grid with random agents.
     *
     * @param grid    the simulation grid
     * @param scanner the input scanner
     */
    private static void randomFill(Grid grid, Scanner scanner) {
        System.out.print("Density (0-1): ");
        double density = readDouble(scanner, 0.2);
        System.out.print("Initial infection rate (0-1): ");
        double infectionRate = readDouble(scanner, 0.05);

        int total    = grid.getWidth() * grid.getHeight();
        int toPlace  = (int) (total * density);
        int placed   = 0;
        int tries    = 0;
        int maxTries = total * 4;

        while (placed < toPlace && tries < maxTries) {
            int x = (int) (Math.random() * grid.getWidth());
            int y = (int) (Math.random() * grid.getHeight());
            if (grid.isEmpty(x, y)) {
                int age = 10 + (int) (Math.random() * 60);
                Agent agent = new Agent(x, y, age);
                if (Math.random() < infectionRate) agent.infect();
                grid.addAgent(agent);
                placed++;
            }
            tries++;
        }

        System.out.println("Placed " + placed + " agents.");
    }

    /**
     * Lets the user set the epidemic parameters of the engine.
     *
     * @param engine  the simulation engine
     * @param scanner the input scanner
     */
    private static void setParameters(SimulationEngine engine, Scanner scanner) {
        System.out.print("Contagion rate (0-1): ");
        engine.setContagionRate(readDouble(scanner, 0.3));
        System.out.print("Recovery rate (0-1): ");
        engine.setRecoveryRate(readDouble(scanner, 0.1));
        System.out.print("Mortality rate (0-1): ");
        engine.setMortalityRate(readDouble(scanner, 0.05));
    }

    /**
     * Saves the current simulation state to a binary file.
     *
     * @param grid    the simulation grid
     * @param engine  the simulation engine
     * @param scanner the input scanner
     */
    private static void saveSimulation(Grid grid, SimulationEngine engine, Scanner scanner) {
        System.out.print("File name [simulation.sim]: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) name = "simulation.sim";

        try {
            SimulationSerializer.save(new File(name), grid, engine);
            System.out.println("Saved to " + name);
        } catch (IOException e) {
            System.out.println("Error saving: " + e.getMessage());
        }
    }

    /**
     * Loads a simulation state from a binary file.
     *
     * @param grid    the simulation grid
     * @param engine  the simulation engine
     * @param scanner the input scanner
     */
    private static void loadSimulation(Grid grid, SimulationEngine engine, Scanner scanner) {
        System.out.print("File name [simulation.sim]: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) name = "simulation.sim";

        try {
            int step = SimulationSerializer.load(new File(name), grid);
            engine.setCurrentStep(step);
            System.out.println("Loaded from " + name);
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Error loading: " + e.getMessage());
        }
    }

    /**
     * Reads an integer from the scanner, returning a default value on invalid input.
     *
     * @param scanner      the input scanner
     * @param defaultValue value to use if the input is not a valid integer
     * @return the parsed integer, or {@code defaultValue}
     */
    private static int readInt(Scanner scanner, int defaultValue) {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Reads a double from the scanner, returning a default value on invalid input.
     *
     * @param scanner      the input scanner
     * @param defaultValue value to use if the input is not a valid number
     * @return the parsed double, or {@code defaultValue}
     */
    private static double readDouble(Scanner scanner, double defaultValue) {
        try {
            return Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}