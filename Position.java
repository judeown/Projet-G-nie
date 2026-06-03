import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class Position {

    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Position) {
            Position p = (Position) o;
            return (this.getX() == p.getX()) && (this.getY() == p.getY());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y);
    }

    @Override
    public String toString() {
        return "(" + getX() + ", " + getY() + ")";
    }

    /**
     * CORRECTION : les paramètres s'appellent désormais "rows" et "columns"
     * pour correspondre exactement à ce que Grid.getRows() et Grid.getColumns() renvoient.
     * La logique reste identique : x est borné par rows, y est borné par columns.
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
