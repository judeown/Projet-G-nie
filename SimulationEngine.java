import java.util.List;
import java.util.ArrayList;

/**
 * Controls the progression of the epidemic simulation.
 * <p>
 * Each call to {@link #step()} advances the simulation by one tick:
 * infected agents may die or recover, healthy agents may contract the disease
 * from infected neighbors, and mobile agents move to a random free adjacent cell.
 * All probability rates are clamped to the range [0, 1] at construction time.
 * </p>
 */
public class SimulationEngine {

    /** The grid on which the simulation runs. */
    private Grid grid;

    /** Probability that a healthy agent adjacent to an infected one becomes infected. */
    private double contagionRate;

    /** Probability that an infected agent recovers in a given step. */
    private double recoveryRate;

    /** Probability that an infected agent dies in a given step. */
    private double mortalityRate;

    /** Probability that a living agent attempts to move each step. */
    private double moveProbability;

    /** Whether the simulation is currently running. */
    private boolean running;

    /** Number of steps that have been executed so far. */
    private int currentStep;

    /**
     * Constructs a SimulationEngine with the given parameters.
     * All rate values are clamped to [0, 1].
     *
     * @param grid             the grid containing the agents
     * @param contagionRate    probability of disease transmission per step
     * @param recoveryRate     probability of recovery per step for infected agents
     * @param mortalityRate    probability of death per step for infected agents
     * @param moveProbability  probability that an agent moves each step
     */
    public SimulationEngine(Grid grid, double contagionRate, double recoveryRate,
                            double mortalityRate, double moveProbability) {
        this.grid = grid;
        this.running = false;
        this.currentStep = 0;
        this.contagionRate = Math.min(Math.abs(contagionRate), 1);
        this.recoveryRate = Math.min(Math.abs(recoveryRate), 1);
        this.mortalityRate = Math.min(Math.abs(mortalityRate), 1);
        this.moveProbability = Math.min(Math.abs(moveProbability), 1);
    }

    /**
     * Starts (or resumes) the simulation.
     * Subsequent calls to {@link #step()} will process agent updates.
     */
    public void start() {
        this.running = true;
    }

    /**
     * Pauses the simulation.
     * Calls to {@link #step()} will have no effect until {@link #start()} is called again.
     */
    public void pause() {
        this.running = false;
    }

    /**
     * Returns whether the simulation is currently running.
     *
     * @return {@code true} if the simulation is active
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Returns the number of steps executed since the simulation started.
     *
     * @return the current step count
     */
    public int getCurrentStep() {
        return currentStep;
    }

    /**
     * Attempts to move agent {@code a} to a random free neighboring cell.
     * The move is skipped if the agent cannot move or no free neighbor exists.
     *
     * @param a the agent to move
     */
    private void moveAgent(Agent a) {
        if (a.canMove()) {
            List<Position> positions = Position.getNeighbors(
                new Position(a.getPositionX(), a.getPositionY()),
                grid.getRows(), grid.getColumns()
            );
            List<Position> freePositions = new ArrayList<>();
            for (Position p : positions) {
                if (grid.isEmpty(p.getX(), p.getY())) {
                    freePositions.add(p);
                }
            }
            if (!freePositions.isEmpty()) {
                int idx = (int) (Math.random() * freePositions.size());
                Position chosen = freePositions.get(idx);
                grid.removeAgent(a.getPositionX(), a.getPositionY());
                a.setPositionX(chosen.getX());
                a.setPositionY(chosen.getY());
                grid.addAgent(a);
            }
        }
    }

    /**
     * Updates the health state of a single agent for one simulation step.
     * <p>
     * Dead agents are skipped. Infected agents may die (checked first) or recover
     * according to the configured rates. Healthy agents adjacent to at least one
     * infected neighbor may become infected. Finally, living agents are moved.
     * </p>
     *
     * @param a the agent to update
     */
    private void updateAgent(Agent a) {
        if (a.getState() == HealthState.DEAD) {
            return;
        }
        if (a.getState() == HealthState.INFECTED) {
            a.incrementInfectionTime();
            double random = Math.random();
            if (random < mortalityRate) {
                a.die();
                grid.removeAgent(a.getPositionX(), a.getPositionY());
            } else if (random < recoveryRate) {
                a.recover();
            }
        } else if (a.getState() == HealthState.HEALTHY) {
            List<Agent> infectedNeighbors = grid.getNeighborAgents(
                a.getPositionX(), a.getPositionY()
            );
            boolean hasInfectedNeighbor = false;
            for (Agent neighbor : infectedNeighbors) {
                if (neighbor.getState() == HealthState.INFECTED) {
                    hasInfectedNeighbor = true;
                    break;
                }
            }
            if (hasInfectedNeighbor) {
                if (Math.random() < contagionRate) {
                    a.infect();
                }
            }
        }
        if (a.getState() != HealthState.DEAD) {
            moveAgent(a);
        }
    }

    /**
     * Advances the simulation by one step if it is currently running.
     * <p>
     * A snapshot of all living agents is taken before processing to avoid
     * concurrent-modification issues. Each agent is then updated via
     * {@link #updateAgent(Agent)}. The step counter is incremented after
     * all updates are complete.
     * </p>
     */
    public void step() {
        if (isRunning()) {
            List<Agent> copy = new ArrayList<>(grid.getAllAgents());
            for (Agent a : copy) {
                updateAgent(a);
            }
            currentStep++;
        }
    }

}
