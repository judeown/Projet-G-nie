

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


public class GridView extends Canvas {

    private static final int CELL_SIZE = 16;

    private static final Color COLOR_EMPTY     = Color.web("#1a1a2e");
    private static final Color COLOR_HEALTHY   = Color.web("#4ade80");
    private static final Color COLOR_INFECTED  = Color.web("#f87171");
    private static final Color COLOR_RECOVERED = Color.web("#60a5fa");
    private static final Color COLOR_DEAD      = Color.web("#6b7280");
    private static final Color COLOR_GRID_LINE = Color.web("#2d2d4e");

    
    private Grid grid;

    /**
     * Creates the canvas sized to fit the grid.
     *
     * @param grid the simulation grid (from the team's Grid class)
     */
    public GridView(Grid grid) {
        // Grid.getRows() = width (columns), Grid.getColumns() = height (rows)
        super(grid.getRows() * CELL_SIZE, grid.getColumns() * CELL_SIZE);
        this.grid = grid;
    }

    public void refreshSize() {
        setWidth(grid.getRows() * CELL_SIZE);
        setHeight(grid.getColumns() * CELL_SIZE);
    }

    
    public void render() {
        refreshSize();
        GraphicsContext gc = getGraphicsContext2D();
        int cols = grid.getRows();    
        int rows = grid.getColumns(); 

        gc.setFill(COLOR_EMPTY);
        gc.fillRect(0, 0, getWidth(), getHeight());

        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                Agent agent = grid.getAgent(x, y); 

                Color color = resolveColor(agent);

                gc.setFill(color);
                gc.fillRect(
                    x * CELL_SIZE + 1,
                    y * CELL_SIZE + 1,
                    CELL_SIZE - 2,
                    CELL_SIZE - 2
                );

                gc.setStroke(COLOR_GRID_LINE);
                gc.setLineWidth(0.5);
                gc.strokeRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
    }

    /**
     * Returns the display color for a given agent.
     * Uses {@code Agent.getState()} and matches on {@code HealthState} enum values.
     *
     * @param agent the agent to color, or null for an empty cell
     * @return the color to paint
     */
    private Color resolveColor(Agent agent) {
        if (agent == null) return COLOR_EMPTY;
        switch (agent.getState()) {
            case HEALTHY:   return COLOR_HEALTHY;
            case INFECTED:  return COLOR_INFECTED;
            case RECOVERED: return COLOR_RECOVERED;
            case DEAD:      return COLOR_DEAD;
            default:        return COLOR_EMPTY;
        }
    }

    /**
     * Converts a pixel X coordinate to a grid column index.
     *
     * @param pixelX mouse X position in pixels
     * @return the corresponding grid column
     */
    public int toGridX(double pixelX) {
        return (int) (pixelX / CELL_SIZE);
    }

    /**
     * Converts a pixel Y coordinate to a grid row index.
     *
     * @param pixelY mouse Y position in pixels
     * @return the corresponding grid row
     */
    public int toGridY(double pixelY) {
        return (int) (pixelY / CELL_SIZE);
    }
}
