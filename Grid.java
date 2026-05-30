import java.util.List;
import java.util.ArrayList;

public class Grid {

    private int width;
    private int height;
    private Agent[][] cells;

    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        this.cells = new Agent[width][height];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getRows() {
        return width;
    }

    public int getColumns() {
        return height;
    }

    public Agent getAgent(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return cells[x][y];
        }
        return null;
    }

    public void addAgent(Agent a) {
        int x = a.getPositionX();
        int y = a.getPositionY();
        if (x >= 0 && x < width && y >= 0 && y < height && cells[x][y] == null) {
            cells[x][y] = a;
        }
    }

    public void removeAgent(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            cells[x][y] = null;
        }
    }

    public boolean isEmpty(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return cells[x][y] == null;
        }
        return false;
    }

    public List<Agent> getAllAgents() {
        List<Agent> all = new ArrayList<>();
        for (Agent[] row : cells) {
            for (Agent agent : row) {
                if (agent != null && agent.getState() != State.DEAD) {
                    all.add(agent);
                }
            }
        }
        return all;
    }

    public List<Agent> getNeighborAgents(int x, int y) {
        List<Agent> neighbors = new ArrayList<>();
        List<Position> positions = Position.getNeighbors(new Position(x, y), width, height);
        for (Position p : positions) {
            Agent a = cells[p.getX()][p.getY()];
            if (a != null && a.getState() != State.DEAD) {
                neighbors.add(a);
            }
        }
        return neighbors;
    }

}
