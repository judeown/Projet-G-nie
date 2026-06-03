import java.util.List;
import java.util.ArrayList;

public class Grid {

    private int rows;
    private int columns;
    private Agent[][] cells;

    public Grid(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.cells = new Agent[rows][columns];
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public Agent getAgent(int x, int y) {
        if (x >= 0 && x < rows && y >= 0 && y < columns) {
            return cells[x][y];
        }
        return null;
    }

    public void addAgent(Agent a) {
        int x = a.getPositionX();
        int y = a.getPositionY();
        if (x >= 0 && x < rows && y >= 0 && y < columns && cells[x][y] == null) {
            cells[x][y] = a;
        }
    }

    public void removeAgent(int x, int y) {
        if (x >= 0 && x < rows && y >= 0 && y < columns) {
            cells[x][y] = null;
        }
    }

    public boolean isEmpty(int x, int y) {
        if (x >= 0 && x < rows && y >= 0 && y < columns) {
            return cells[x][y] == null;
        }
        return false;
    }

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
