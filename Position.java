import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Immutable (x, y) position on the simulation grid.
 * Provides static helpers for computing neighbor lists in various topologies.
 */
public class Position implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int x;
    private final int y;

    /**
     * Creates a position at the given coordinates.
     *
     * @param x column index
     * @param y row index
     */
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /** @return column index */
    public int getX() { return x; }

    /** @return row index */
    public int getY() { return y; }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Position) {
            Position p = (Position) o;
            return this.x == p.x && this.y == p.y;
        }
        return false;
    }

    @Override
    public int hashCode() { return Objects.hash(x, y); }

    @Override
    public String toString() { return "(" + x + ", " + y + ")"; }

    /**
     * Returns the 4 orthogonal bounded neighbors of a position.
     * Out-of-bound positions are excluded.
     *
     * @param p      center position
     * @param width  grid width
     * @param height grid height
     * @return list of valid neighbor positions
     */
    public static List<Position> getNeighbors(Position p, int width, int height) {
        List<Position> neighbors = new ArrayList<>();
        int x = p.x, y = p.y;
        if (x > 0)          neighbors.add(new Position(x - 1, y));
        if (x < width - 1)  neighbors.add(new Position(x + 1, y));
        if (y > 0)          neighbors.add(new Position(x, y - 1));
        if (y < height - 1) neighbors.add(new Position(x, y + 1));
        return neighbors;
    }

    /**
     * Returns up to 8 (orthogonal + diagonal) bounded neighbors of a position.
     * Out-of-bound positions are excluded.
     *
     * @param p      center position
     * @param width  grid width
     * @param height grid height
     * @return list of valid neighbor positions
     */
    public static List<Position> getNeighbors8(Position p, int width, int height) {
        List<Position> neighbors = new ArrayList<>();
        int x = p.x, y = p.y;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx, ny = y + dy;
                if (nx >= 0 && nx < width && ny >= 0 && ny < height)
                    neighbors.add(new Position(nx, ny));
            }
        }
        return neighbors;
    }

    /**
     * Returns the 4 orthogonal toroidal neighbors of a position.
     * Positions wrap around the grid edges.
     *
     * @param p      center position
     * @param width  grid width
     * @param height grid height
     * @return list of 4 toroidal neighbor positions
     */
    public static List<Position> getNeighborsToroidal(Position p, int width, int height) {
        List<Position> neighbors = new ArrayList<>();
        int x = p.x, y = p.y;
        neighbors.add(new Position((x - 1 + width)  % width,  y));
        neighbors.add(new Position((x + 1)           % width,  y));
        neighbors.add(new Position(x,                 (y - 1 + height) % height));
        neighbors.add(new Position(x,                 (y + 1)          % height));
        return neighbors;
    }
}
