import java.util.List;
import java.util.ArrayList;

/**
 * Represents the 2D simulation grid that holds agents at discrete cell positions.
 * <p>
 * The grid is modelled as a 2D array of {@link Agent} references.
 * Each cell can hold at most one agent. Dead agents are excluded from
 * traversal methods to keep the simulation state consistent.
 * </p>
 */
public class Grid {

    /** Number of rows in the grid. */
    private int rows;

    /** Number of columns in the grid. */
    private int columns;

    /** Internal cell storage indexed by [row][column]. */
    private Agent[][] cells;

    /**
     * Constructs an empty grid with the specified dimensions.
     *
     * @param rows    the number of rows
     * @param columns the number of columns
     */
    public Grid(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.cells = new Agent[rows][columns];
    }

    /**
     * Returns the number of rows in the grid.
     *
     * @return total row count
     */
    public int getRows() {
        return rows;
    }

    /**
     * Returns the number of columns in the grid.
     *
     * @return total column count
     */
    public int getColumns() {
        return columns;
    }

    /**
     * Returns the agent occupying cell ({@code x}, {@code y}), or {@code null}
     * if the cell is empty or the coordinates are out of bounds.
     *
     * @param x the row index
     * @param y the column index
     * @return the agent at that cell, or {@code null}
     */
    public Agent getAgent(int x, int y) {
        if (x >= 0 && x < rows && y >= 0 && y < columns) {
            return cells[x][y];
        }
        return null;
    }

    /**
     * Places an agent onto the grid at its current position.
     * The operation is ignored if the target cell is already occupied
     * or the position is out of bounds.
     *
     * @param a the agent to add
     */
    public void addAgent(Agent a) {
        int x = a.getPositionX();
        int y = a.getPositionY();
        if (x >= 0 && x < rows && y >= 0 && y < columns && cells[x][y] == null) {
            cells[x][y] = a;
        }
    }

    /**
     * Removes the agent from cell ({@code x}, {@code y}), leaving it empty.
     * Does nothing if the coordinates are out of bounds.
     *
     * @param x the row index
     * @param y the column index
     */
    public void removeAgent(int x, int y) {
        if (x >= 0 && x < rows && y >= 0 && y < columns) {
            cells[x][y] = null;
        }
    }

    /**
     * Checks whether cell ({@code x}, {@code y}) is unoccupied.
     *
     * @param x the row index
     * @param y the column index
     * @return {@code true} if the cell is within bounds and contains no agent
     */
    public boolean isEmpty(int x, int y) {
        if (x >= 0 && x < rows && y >= 0 && y < columns) {
            return cells[x][y] == null;
        }
        return false;
    }

    /**
     * Returns a list of all living agents currently on the grid.
     * Agents whose state is {@link HealthState#DEAD} are excluded.
     *
     * @return list of living agents in row-major order
     */
    public List<Agent> getAllAgents() {
        List<Agent> all = new ArrayList<>();
        for (Agent[] row : cells) {
            for (Agent agent : row) {
                if (agent != null && agent.getState() != HealthState.DEAD) {
                    all.add(agent);
                }
            }
        }
        return all;
    }

    /**
     * Returns the living agents that occupy the orthogonal neighbors of cell
     * ({@code x}, {@code y}).
     * Dead agents are excluded from the result.
     *
     * @param x the row index of the reference cell
     * @param y the column index of the reference cell
     * @return list of living neighboring agents
     */
    public List<Agent> getNeighborAgents(int x, int y) {
        List<Agent> neighbors = new ArrayList<>();
        List<Position> positions = Position.getNeighbors(new Position(x, y), rows, columns);
        for (Position p : positions) {
            Agent a = cells[p.getX()][p.getY()];
            if (a != null && a.getState() != HealthState.DEAD) {
                neighbors.add(a);
            }
        }
        return neighbors;
    }

} 
