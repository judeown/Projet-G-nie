import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the 2D simulation grid that stores agents.
 * Supports both bounded and toroidal (wrap-around) topologies.
 */
public class Grid implements Serializable {

    private static final long serialVersionUID = 1L;

    private       int     width;
    private       int     height;
    private       boolean toroidal;
    private       Agent[][] cells;

    /**
     * Creates a bounded grid with the given dimensions.
     *
     * @param width  number of columns
     * @param height number of rows
     */
    public Grid(int width, int height) {
        this.width    = width;
        this.height   = height;
        this.toroidal = false;
        this.cells    = new Agent[width][height];
    }

    /** @return number of columns (width) */
    public int getRows()    { return width; }

    /** @return number of rows (height) */
    public int getColumns() { return height; }

    /** @return grid width */
    public int getWidth()   { return width; }

    /** @return grid height */
    public int getHeight()  { return height; }

    /** @return true if the grid uses toroidal (wrap-around) topology */
    public boolean isToroidal()             { return toroidal; }

    /**
     * Enables or disables toroidal topology.
     *
     * @param t true to enable wrap-around edges
     */
    public void setToroidal(boolean t)      { this.toroidal = t; }

    /**
     * Returns the agent at the given cell, or null if the cell is empty or out of bounds.
     *
     * @param x column index
     * @param y row index
     * @return the agent, or null
     */
    public Agent getAgent(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) return cells[x][y];
        return null;
    }

    /**
     * Places an agent in the grid at its current position.
     * Replaces a dead agent if one already occupies the cell.
     *
     * @param a the agent to place
     */
    public void addAgent(Agent a) {
        int x = a.getPositionX(), y = a.getPositionY();
        if (x >= 0 && x < width && y >= 0 && y < height && isEmpty(x, y))
            cells[x][y] = a;
    }

    /**
     * Removes the agent at the given cell.
     *
     * @param x column index
     * @param y row index
     */
    public void removeAgent(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) cells[x][y] = null;
    }

    /**
     * Returns true if the given cell is within bounds and available for a new agent
     * (either empty or occupied by a dead agent).
     *
     * @param x column index
     * @param y row index
     * @return true if the cell can receive a new agent
     */
    public boolean isEmpty(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) return false;
        return cells[x][y] == null || cells[x][y].getState() == HealthState.DEAD;
    }

    /**
     * Removes all agents from the grid.
     */
    public void clear() {
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                cells[x][y] = null;
    }

    /**
     * Resizes the grid while preserving agents that still fit in bounds.
     *
     * @param newWidth  new number of columns
     * @param newHeight new number of rows
     */
    public void resize(int newWidth, int newHeight) {
        if (newWidth <= 0 || newHeight <= 0) {
            throw new IllegalArgumentException("Grid size must use positive integers.");
        }

        Agent[][] newCells = new Agent[newWidth][newHeight];
        int copyWidth = Math.min(width, newWidth);
        int copyHeight = Math.min(height, newHeight);

        for (int x = 0; x < copyWidth; x++) {
            for (int y = 0; y < copyHeight; y++) {
                newCells[x][y] = cells[x][y];
            }
        }

        width = newWidth;
        height = newHeight;
        cells = newCells;
    }

    /**
     * Returns a snapshot list of all living agents in the grid.
     *
     * @return list of non-dead agents
     */
    public List<Agent> getAllAgents() {
        List<Agent> all = new ArrayList<>();
        for (Agent[] row : cells)
            for (Agent agent : row)
                if (agent != null && agent.getState() != HealthState.DEAD)
                    all.add(agent);
        return all;
    }

    /**
     * Returns all living neighbor agents around the given cell.
     * Respects the current topology (bounded or toroidal).
     *
     * @param x column index
     * @param y row index
     * @return list of neighboring living agents
     */
    public List<Agent> getNeighborAgents(int x, int y) {
        List<Agent> neighbors = new ArrayList<>();
        List<Position> positions = toroidal
            ? Position.getNeighborsToroidal(new Position(x, y), width, height)
            : Position.getNeighbors(new Position(x, y), width, height);
        for (Position p : positions) {
            Agent a = cells[p.getX()][p.getY()];
            if (a != null && a.getState() != HealthState.DEAD) neighbors.add(a);
        }
        return neighbors;
    }

    /**
     * Returns all empty neighbor positions around the given cell.
     * Respects the current topology (bounded or toroidal).
     *
     * @param x column index
     * @param y row index
     * @return list of free neighboring positions
     */
    public List<Position> getFreeNeighbors(int x, int y) {
        List<Position> free = new ArrayList<>();
        List<Position> positions = toroidal
            ? Position.getNeighborsToroidal(new Position(x, y), width, height)
            : Position.getNeighbors(new Position(x, y), width, height);
        for (Position p : positions)
            if (cells[p.getX()][p.getY()] == null) free.add(p);
        return free;
    }
}
