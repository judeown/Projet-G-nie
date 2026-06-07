import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * Handles mouse interactions on the simulation grid canvas.
 * Supports four tools: ADD (single cell), REMOVE, BRUSH (3×3 area), and ZONE (rectangle).
 */
public class InteractionController {
    public enum Tool {
   
    ADD,
  
    REMOVE,
   
    BRUSH,
   
    ZONE
}


    private Tool        currentTool;
    private HealthState placeState;
    private final Grid  grid;

    private static final int DEFAULT_AGE = 25;

    /** Coordinates of the zone-selection start cell (ZONE tool only). */
    private int zoneStartX = -1;
    private int zoneStartY = -1;

    /**
     * Creates the controller with a reference to the simulation grid.
     *
     * @param grid the simulation grid
     */
    public InteractionController(Grid grid) {
        this.grid        = grid;
        this.currentTool = Tool.ADD;
        this.placeState  = HealthState.HEALTHY;
    }

    /**
     * Sets the health state applied to newly created agents.
     *
     * @param state HEALTHY, INFECTED, or RECOVERED
     */
    public void setPlaceState(HealthState state) {
        this.placeState = (state != null) ? state : HealthState.HEALTHY;
    }

    /**
     * Returns the currently active tool.
     *
     * @return the active tool
     */
    public Tool getTool() { return currentTool; }

    /**
     * Sets the active tool.
     *
     * @param tool the tool to activate
     */
    public void setTool(Tool tool) { this.currentTool = tool; }

    /**
     * Handles a click or drag event on the grid canvas.
     * For ADD / REMOVE / BRUSH, the action is applied immediately.
     * For ZONE, use {@link #handlePressed} and {@link #handleReleased} instead.
     *
     * @param event    the JavaFX mouse event
     * @param cellSize pixel width/height of one grid cell
     */
    public void handle(MouseEvent event, int cellSize) {
        if (currentTool == Tool.ZONE) return;
        int x = toGrid(event.getX(), cellSize);
        int y = toGrid(event.getY(), cellSize);
        applyTool(x, y);
    }

    /**
     * Records the zone start position when the mouse is pressed (ZONE tool).
     *
     * @param event    the mouse-pressed event
     * @param cellSize pixel width/height of one grid cell
     */
    public void handlePressed(MouseEvent event, int cellSize) {
        zoneStartX = toGrid(event.getX(), cellSize);
        zoneStartY = toGrid(event.getY(), cellSize);
    }

    /**
     * Fills the rectangle from the start to the released position (ZONE tool).
     *
     * @param event    the mouse-released event
     * @param cellSize pixel width/height of one grid cell
     */
    public void handleReleased(MouseEvent event, int cellSize) {
        if (zoneStartX < 0 || zoneStartY < 0) return;
        int endX = toGrid(event.getX(), cellSize);
        int endY = toGrid(event.getY(), cellSize);

        int x1 = Math.min(zoneStartX, endX);
        int x2 = Math.max(zoneStartX, endX);
        int y1 = Math.min(zoneStartY, endY);
        int y2 = Math.max(zoneStartY, endY);

        boolean remove = (event.getButton() == MouseButton.SECONDARY);
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                if (remove) grid.removeAgent(x, y);
                else        addAgentAt(x, y);
            }
        }
        zoneStartX = -1;
        zoneStartY = -1;
    }

    /**
     * Applies the current tool at the given grid coordinates.
     *
     * @param x grid column
     * @param y grid row
     */
    public void applyTool(int x, int y) {
        switch (currentTool) {
            case ADD:
                addAgentAt(x, y);
                break;
            case REMOVE:
                grid.removeAgent(x, y);
                break;
            case BRUSH:
                for (int dx = -1; dx <= 1; dx++)
                    for (int dy = -1; dy <= 1; dy++)
                        addAgentAt(x + dx, y + dy);
                break;
            default:
                break;
        }
    }

    /**
     * Adds a healthy agent at the given cell if it is empty and in bounds.
     *
     * @param x grid column
     * @param y grid row
     */
    private void addAgentAt(int x, int y) {
        if (x < 0 || x >= grid.getRows() || y < 0 || y >= grid.getColumns()) return;
        if (grid.isEmpty(x, y)) {
            Agent agent = new Agent(x, y, DEFAULT_AGE);
            if (placeState == HealthState.INFECTED)  agent.infect();
            if (placeState == HealthState.RECOVERED) agent.recover();
            grid.addAgent(agent);
        }
    }

    private static int toGrid(double pixel, int cellSize) {
        return (int) (pixel / cellSize);
    }
}
