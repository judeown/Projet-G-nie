import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents a 2D position on the simulation grid using (x, y) coordinates.
 * <p>
 * Provides utility methods for neighbor discovery and standard
 * equality/hash-code contracts so that positions can be safely used
 * in collections.
 * </p>
 */
public class Position {

    /** Row index (x-axis). */
    private int x;

    /** Column index (y-axis). */
    private int y;

    /**
     * Constructs a Position with the given coordinates.
     *
     * @param x the row index
     * @param y the column index
     */
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the row index of this position.
     *
     * @return the x coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the column index of this position.
     *
     * @return the y coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Checks whether this position is equal to another object.
     * Two positions are equal if and only if their x and y coordinates match.
     *
     * @param o the object to compare against
     * @return {@code true} if {@code o} is a {@code Position} with the same coordinates
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Position) {
            Position p = (Position) o;
            return (this.getX() == p.getX()) && (this.getY() == p.getY());
        }
        return false;
    }

    /**
     * Returns a hash code consistent with {@link #equals(Object)}.
     *
     * @return hash code derived from x and y
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y);
    }

    /**
     * Returns a human-readable string representation of this position.
     *
     * @return a string of the form {@code "(x, y)"}
     */
    @Override
    public String toString() {
        return "(" + getX() + ", " + getY() + ")";
    }

    /**
     * Returns the list of orthogonal (non-diagonal) neighbors of position {@code p}
     * that lie within the grid bounds.
     * <p>
     * At most four neighbors are returned (up, down, left, right).
     * Border cells yield fewer neighbors.
     * </p>
     *
     * @param p       the reference position
     * @param rows    the total number of rows in the grid
     * @param columns the total number of columns in the grid
     * @return a list of valid neighboring positions
     */
    public static List<Position> getNeighbors(Position p, int rows, int columns) {
        List<Position> neighbors = new ArrayList<>();
        int x = p.getX();
        int y = p.getY();
        if (x > 0)           { neighbors.add(new Position(x - 1, y)); }
        if (y > 0)           { neighbors.add(new Position(x, y - 1)); }
        if (x < rows - 1)    { neighbors.add(new Position(x + 1, y)); }
        if (y < columns - 1) { neighbors.add(new Position(x, y + 1)); }
        return neighbors;
    }

}
