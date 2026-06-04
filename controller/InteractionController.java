

import javafx.scene.input.MouseEvent;


 
public class InteractionController {

    
    public enum Tool {
        ADD,
        REMOVE,
        BRUSH
    }

    private Tool currentTool;


    private final Grid grid;

    private static final int    DEFAULT_AGE    = 25;

    private static final double DEFAULT_ENERGY = 80.0;

    /**
     * Creates the controller with a reference to the team's Grid.
     *
     * @param grid the simulation grid
     */
    public InteractionController(Grid grid) {
        this.grid        = grid;
        this.currentTool = Tool.ADD;
    }

    /**
     * Returns the currently selected tool.
     *
     * @return the active tool
     */
    public Tool getTool() {
        return currentTool;
    }

    /**
     * Sets the active tool.
     *
     * @param tool the tool to activate
     */
    public void setTool(Tool tool) {
        this.currentTool = tool;
    }

    /**
     * Handles a click or drag event on the grid canvas.
     * Converts pixel coordinates to grid indices and delegates to the
     * appropriate tool method.
     *
     * @param event   the mouse event from JavaFX
     * @param cellSize the pixel size of one grid cell (used for coordinate conversion)
     */
    public void handle(MouseEvent event, int cellSize) {
        int x = (int) (event.getX() / cellSize);
        int y = (int) (event.getY() / cellSize);
        applyTool(x, y);
    }

    /**
     * Applies the current tool at the given grid coordinates.
     *
     * @param x the grid column
     * @param y the grid row
     */
    public void applyTool(int x, int y) {
        switch (currentTool) {
            case ADD:
                addAgentAt(x, y);
                break;
            case REMOVE:
                // Uses Grid.removeAgent(x, y) from partie2
                grid.removeAgent(x, y);
                break;
            case BRUSH:
                // Fill a 3×3 area centred on (x, y)
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        addAgentAt(x + dx, y + dy);
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * Adds a healthy {@link Agent} at (x, y) if the cell is empty and in bounds.
     * Uses {@code Grid.isEmpty()} and {@code Grid.addAgent()} from partie2,
     * and {@code Agent(x, y, age)} constructor from partie1.
     *
     * @param x the grid column
     * @param y the grid row
     */
    private void addAgentAt(int x, int y) {
        // Bounds check — Grid.getRows() = width, Grid.getColumns() = height
        if (x < 0 || x >= grid.getRows() || y < 0 || y >= grid.getColumns()) return;

        if (grid.isEmpty(x, y)) {
            // Agent constructor from partie1: Agent(int x, int y, int age)
            Agent agent = new Agent(x, y, DEFAULT_AGE);
            grid.addAgent(agent);
        }
    }
}
