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

    public static List<Position> getNeighbors(Position p, int width, int height) {
        List<Position> neighbors = new ArrayList<>();
        int x = p.getX();
        int y = p.getY();
        if (x > 0)          { neighbors.add(new Position(x - 1, y)); }
        if (y > 0)          { neighbors.add(new Position(x, y - 1)); }
        if (x < width - 1)  { neighbors.add(new Position(x + 1, y)); }
        if (y < height - 1) { neighbors.add(new Position(x, y + 1)); }
        return neighbors;
    }

}
