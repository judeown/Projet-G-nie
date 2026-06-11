import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Controls the simulation logic: agent movement, infection spread,
 * recovery, death, and cell division at each time step.
 */
public class SimulationEngine implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Grid grid;
    private double  contagionRate;
    private double  recoveryRate;
    private double  mortalityRate;
    private double  divisionProbability;
    private boolean running;
    private int     currentStep;

    /** Energy lost by every agent each simulation step. */
    private static final double ENERGY_LOSS_PER_STEP     = 0.05;
    /** Energy gained when an infected agent recovers. */
    private static final double ENERGY_GAIN_ON_RECOVERY  = 15.0;
    /** Minimum energy required for a healthy agent to divide. */
    private static final double ENERGY_MIN_FOR_DIVISION  = 70.0;
    /** Energy transferred from parent to child on division. */
    private static final double ENERGY_COST_DIVISION     = 25.0;

    /**
     * Creates the simulation engine.
     *
     * @param grid           the 2D grid of agents
     * @param contagionRate  probability that a healthy agent next to an infected one becomes infected
     * @param recoveryRate   probability that an infected agent recovers each step
     * @param mortalityRate  probability that an infected agent dies each step
     * @param moveProbability default movement probability (stored in each Agent)
     */
    public SimulationEngine(Grid grid, double contagionRate, double recoveryRate,
                            double mortalityRate, double moveProbability) {
        this.grid                = grid;
        this.running             = false;
        this.currentStep         = 0;
        this.contagionRate       = clamp(contagionRate);
        this.recoveryRate        = clamp(recoveryRate);
        this.mortalityRate       = clamp(mortalityRate);
        this.divisionProbability = 0.02;
    }

    private static double clamp(double v) { return Math.min(Math.abs(v), 1.0); }

    /** Starts the simulation. */
    public void start()  { this.running = true; }

    /** Pauses the simulation. */
    public void pause()  { this.running = false; }

    /** @return true if the simulation is currently running */
    public boolean isRunning()    { return running; }

    /** @return the current step count */
    public int getCurrentStep()   { return currentStep; }

    /** @return the underlying grid */
    public Grid getGrid()         { return grid; }

    /**
     * Sets the contagion rate.
     *
     * @param r value in [0, 1]
     */
    public void setContagionRate(double r)       { this.contagionRate       = clamp(r); }

    /**
     * Sets the recovery rate.
     *
     * @param r value in [0, 1]
     */
    public void setRecoveryRate(double r)        { this.recoveryRate        = clamp(r); }

    /**
     * Sets the mortality rate.
     *
     * @param r value in [0, 1]
     */
    public void setMortalityRate(double r)       { this.mortalityRate       = clamp(r); }

    /**
     * Sets the division probability for healthy agents with sufficient energy.
     *
     * @param r value in [0, 1]
     */
    public void setDivisionProbability(double r) { this.divisionProbability = clamp(r); }

    /**
     * Enables or disables the toroidal topology on the grid.
     *
     * @param t true for wrap-around edges
     */
    public void setToroidal(boolean t)           { grid.setToroidal(t); }

    /**
     * Resets the step counter to zero.
     */
    public void resetStep()                      { this.currentStep = 0; }

    /**
     * Sets the step counter to a specific value (used when restoring a saved state).
     *
     * @param step the step value to restore
     */
    public void setCurrentStep(int step)         { this.currentStep = Math.max(0, step); }

    // -------------------------------------------------------------------------
    // Private step logic
    // -------------------------------------------------------------------------

    private void moveAgent(Agent a) {
        if (Math.random() >= a.getMoveProbability()) return;
        List<Position> free = grid.getFreeNeighbors(a.getPositionX(), a.getPositionY());
        if (free.isEmpty()) return;
        Position chosen = free.get((int) (Math.random() * free.size()));
        grid.removeAgent(a.getPositionX(), a.getPositionY());
        a.setPositionX(chosen.getX());
        a.setPositionY(chosen.getY());
        grid.addAgent(a);
    }

    private void tryDivide(Agent a, List<Agent> newAgents) {
        if (a.getEnergy() < ENERGY_MIN_FOR_DIVISION)  return;
        if (Math.random() >= divisionProbability)      return;
        List<Position> free = grid.getFreeNeighbors(a.getPositionX(), a.getPositionY());
        if (free.isEmpty()) return;
        Position pos = free.get((int) (Math.random() * free.size()));
        a.adjustEnergy(-ENERGY_COST_DIVISION);
        Agent child = new Agent(pos.getX(), pos.getY(), 0);
        child.setEnergy(ENERGY_COST_DIVISION);
        newAgents.add(child);
    }

    private void updateAgent(Agent a, List<Agent> newAgents) {
        if (a.getState() == HealthState.DEAD) return;

        // Passive energy drain
        a.adjustEnergy(-ENERGY_LOSS_PER_STEP);
        if (a.getEnergy() <= 0.0) {
            a.die();
            return;
        }

        // Age by 1 year every 50 steps
        if (currentStep > 0 && currentStep % 50 == 0) a.incrementAge();

        switch (a.getState()) {
            case INFECTED:
                a.incrementInfectionTime();
                double roll = Math.random();
                if (roll < mortalityRate) {
                    a.die();
                    return;
                } else if (Math.random() < recoveryRate) {
                    a.recover();
                    a.adjustEnergy(ENERGY_GAIN_ON_RECOVERY);
                }
                break;

            case HEALTHY:
                List<Agent> neighbors = grid.getNeighborAgents(a.getPositionX(), a.getPositionY());
                for (Agent neighbor : neighbors) {
                    if (neighbor.getState() == HealthState.INFECTED) {
                        double ageFactor = a.computeInfectionProbability();
                        if (Math.random() < contagionRate * ageFactor) {
                            a.infect();
                            break;
                        }
                    }
                }
                if (a.getState() == HealthState.HEALTHY) {
                    tryDivide(a, newAgents);
                }
                break;

            default:
                break;
        }

        moveAgent(a);
    }

    /**
     * Advances the simulation by one step.
     * Does nothing if the simulation is paused.
     */
    public void step() {
        if (!isRunning()) return;
        List<Agent> copy      = new ArrayList<>(grid.getAllAgents());
        List<Agent> newAgents = new ArrayList<>();
        for (Agent a : copy) updateAgent(a, newAgents);
        for (Agent a : newAgents) grid.addAgent(a);
        currentStep++;
    }
}
