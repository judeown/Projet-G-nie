package mock;

/**
 * Temporary mock class used only for testing the statistics module.
 * This class must not be used in the final simulation model.
 */
public class Agent {

    private HealthState state;
    private int age;
    private double energy;

    /**
     * Creates a temporary agent for statistics tests.
     *
     * @param state the health state of the agent
     * @param age the age of the agent
     * @param energy the energy level of the agent
     */
    public Agent(HealthState state, int age, double energy) {
        this.state = state;
        this.age = age;
        this.energy = energy;
    }

    /**
     * Gets the health state of the agent.
     *
     * @return the health state
     */
    public HealthState getState() {
        return state;
    }

    /**
     * Gets the age of the agent.
     *
     * @return the age
     */
    public int getAge() {
        return age;
    }

    /**
     * Gets the energy level of the agent.
     *
     * @return the energy level
     */
    public double getEnergy() {
        return energy;
    }
}