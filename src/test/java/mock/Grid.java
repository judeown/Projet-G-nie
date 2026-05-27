package mock;

/**
 * Temporary mock grid used only for testing the statistics module.
 * This class must be replaced by the official project grid implementation.
 */
public class Grid {

    private Agent[][] agents;

    /**
     * Creates a temporary grid.
     *
     * @param rows the number of rows
     * @param columns the number of columns
     */
    public Grid(int rows, int columns) {
        this.agents = new Agent[rows][columns];
    }

    /**
     * Gets the number of rows in the grid.
     *
     * @return the number of rows
     */
    public int getRows() {
        return agents.length;
    }

    /**
     * Gets the number of columns in the grid.
     *
     * @return the number of columns
     */
    public int getColumns() {
        return agents[0].length;
    }

    /**
     * Gets the agent located at the given position.
     *
     * @param row the row index
     * @param column the column index
     * @return the agent at the given position, or null if the cell is empty
     */
    public Agent getAgent(int row, int column) {
        return agents[row][column];
    }

    /**
     * Places an agent at the given position.
     *
     * @param row the row index
     * @param column the column index
     * @param agent the agent to place
     */
    public void setAgent(int row, int column, Agent agent) {
        agents[row][column] = agent;
    }
}