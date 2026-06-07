import java.io.Serializable;

/**
 * Represents an agent (cell) in the 2D simulation grid.
 * Each agent has a position, age, energy level, and a health state.
 */
public class Agent implements Serializable {

    private static final long serialVersionUID = 1L;

    private int positionX;
    private int positionY;
    private int age;
    private HealthState state;
    private double energy;
    private double moveProbability;
    private int infectionTime;

    /**
     * Creates a new healthy agent at the given position with the given age.
     *
     * @param x   grid column
     * @param y   grid row
     * @param age initial age of the agent
     */
    public Agent(int x, int y, int age) {
        this.positionX       = x;
        this.positionY       = y;
        this.age             = age;
        this.state           = HealthState.HEALTHY;
        this.energy          = 80.0;
        this.moveProbability = 0.3;
        this.infectionTime   = 0;
    }

    /**
     * Returns the infection probability based on the agent's age.
     * Older agents are more susceptible to infection.
     *
     * @return infection probability as a fraction in [0, 1]
     */
    public double computeInfectionProbability() {
        if (age < 20)      return 0.25;
        else if (age < 40) return 0.50;
        else if (age < 60) return 0.75;
        else               return 1.00;
    }

    /**
     * Sets the agent's health state to INFECTED.
     */
    public void infect()  { this.state = HealthState.INFECTED;  }

    /**
     * Sets the agent's health state to RECOVERED.
     */
    public void recover() { this.state = HealthState.RECOVERED; }

    /**
     * Sets the agent's health state to DEAD.
     */
    public void die()     { this.state = HealthState.DEAD;      }

    /**
     * Returns true if the agent is not dead.
     *
     * @return true if alive
     */
    public boolean isAlive() { return this.state != HealthState.DEAD; }

    /**
     * Increments the agent's age by one year.
     */
    public void incrementAge() { this.age++; }

    /**
     * Increments the time spent in the infected state.
     */
    public void incrementInfectionTime() { this.infectionTime++; }

    /**
     * Adjusts the agent's energy by the given delta, clamped to [0, 100].
     *
     * @param delta positive to gain energy, negative to lose energy
     */
    public void adjustEnergy(double delta) {
        this.energy = Math.max(0.0, Math.min(100.0, this.energy + delta));
    }

    /**
     * Sets the agent's energy level, clamped to [0, 100].
     *
     * @param e new energy value
     */
    public void setEnergy(double e) { this.energy = Math.max(0.0, Math.min(100.0, e)); }

    /**
     * Sets the agent's X position.
     *
     * @param x new column index
     */
    public void setPositionX(int x) { this.positionX = x; }

    /**
     * Sets the agent's Y position.
     *
     * @param y new row index
     */
    public void setPositionY(int y) { this.positionY = y; }

    /**
     * Sets the agent's health state directly.
     *
     * @param s the new state
     */
    public void setState(HealthState s) { this.state = s; }

    /**
     * Sets the probability that the agent moves each simulation step.
     *
     * @param p probability in [0, 1]
     */
    public void setMoveProbability(double p) {
        this.moveProbability = Math.max(0.0, Math.min(1.0, p));
    }

    /** @return current energy level */
    public double getEnergy()          { return energy; }

    /** @return current age */
    public int getAge()                { return age; }

    /** @return current health state */
    public HealthState getState()      { return state; }

    /** @return grid column */
    public int getPositionX()          { return positionX; }

    /** @return grid row */
    public int getPositionY()          { return positionY; }

    /** @return number of steps spent infected */
    public int getInfectionTime()      { return infectionTime; }

    /** @return movement probability */
    public double getMoveProbability() { return moveProbability; }
}
