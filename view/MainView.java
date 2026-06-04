

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Main application window.
 *
 * <p>Assembles {@link GridView}, {@link ControlPanel}, and {@link StatsPanel}.
 * Wires every button and slider to the team's existing classes:
 * <ul>
 *   <li>{@code SimulationEngine.start()} → Play button</li>
 *   <li>{@code SimulationEngine.pause()} → Pause button</li>
 *   <li>{@code SimulationEngine.step()}  → Step button (one step at a time)</li>
 *   <li>{@code SimulationEngine.getCurrentStep()} → step counter label</li>
 *   <li>{@code Grid.clear()} → Clear button  (TODO: add clear() to Grid if not present)</li>
 *   <li>{@code StatisticsManager} methods → StatsPanel (via StatsPanel.update)</li>
 * </ul>
 *
 * <p>No simulation logic lives here — this class only orchestrates the UI.
 */
public class MainView {

    /** Pixel size of one grid cell — must match GridView.CELL_SIZE. */
    private static final int CELL_SIZE = 16;

    private final Stage               stage;
    private final Grid                grid;
    private final SimulationEngine    engine;
    private final GridView            gridView;
    private final ControlPanel        controlPanel;
    private final StatsPanel          statsPanel;
    private final InteractionController interactionController;

    /** JavaFX animation timer driving the simulation loop. */
    private AnimationTimer timer;

    /** Timestamp of the last executed simulation step (nanoseconds). */
    private long lastStepTime = 0;

    /**
     * Creates the main view.
     *
     * @param stage  the primary JavaFX stage
     * @param grid   the team's Grid object
     * @param engine the team's SimulationEngine object
     */
    public MainView(Stage stage, Grid grid, SimulationEngine engine) {
        this.stage  = stage;
        this.grid   = grid;
        this.engine = engine;

        this.gridView             = new GridView(grid);
        this.controlPanel         = new ControlPanel();
        this.statsPanel           = new StatsPanel();
        this.interactionController = new InteractionController(grid);

        buildTimer();
        wireControls();
        wireMouse();
    }

    /**
     * Builds and displays the scene.
     */
    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1a1a2e;");

        ScrollPane scroll = new ScrollPane(gridView);
        scroll.setStyle("-fx-background: #1a1a2e; -fx-background-color: #1a1a2e;");
        scroll.setPadding(new Insets(16));

        root.setLeft(controlPanel);
        root.setCenter(scroll);
        root.setRight(statsPanel);

        Scene scene = new Scene(root, 1150, 620);
        scene.setFill(Color.web("#1a1a2e"));

        stage.setTitle("2D Cell Simulation");
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> { if (timer != null) timer.stop(); });
        stage.show();

        // Initial render with empty grid
        gridView.render();
        statsPanel.update(grid);
    }

    // ── Control wiring ────────────────────────────────────────────────────────

    /**
     * Wires all ControlPanel buttons and sliders to the SimulationEngine.
     */
    private void wireControls() {

        // ── Play → SimulationEngine.start() ──────────────────────────────────
        controlPanel.getPlayButton().setOnAction(e -> {
            engine.start();
            controlPanel.setRunningState();
            timer.start();
        });

        // ── Pause → SimulationEngine.pause() ─────────────────────────────────
        controlPanel.getPauseButton().setOnAction(e -> {
            engine.pause();
            controlPanel.setPausedState();
            timer.stop();
        });

        // ── Step → one manual step ────────────────────────────────────────────
        // SimulationEngine.step() only runs if isRunning() == true,
        // so we temporarily start, step once, then pause again.
        controlPanel.getStepButton().setOnAction(e -> {
            engine.start();
            engine.step();
            engine.pause();
            refreshDisplay();
        });

        // ── Reset ─────────────────────────────────────────────────────────────
        // TODO: add reset() / clear() to SimulationEngine and Grid once merged.
        // For now we pause and clear the display.
        controlPanel.getResetButton().setOnAction(e -> {
            timer.stop();
            engine.pause();
            // grid.clear(); // ← uncomment once Grid.clear() is available
            controlPanel.setPausedState();
            controlPanel.updateStep(0);
            statsPanel.reset();
            gridView.render();
        });

        // ── Clear grid ────────────────────────────────────────────────────────
        controlPanel.getClearButton().setOnAction(e -> {
            // grid.clear(); // ← uncomment once Grid.clear() is available
            gridView.render();
            statsPanel.update(grid);
        });

        // ── Parameter sliders → SimulationEngine setters ─────────────────────
        // TODO: expose setContagionRate / setRecoveryRate / setMortalityRate
        // in SimulationEngine once merged. For now these are wired but no-op.
        controlPanel.getContagionSlider().valueProperty().addListener((obs, o, n) -> {
            // engine.setContagionRate(n.doubleValue()); // uncomment when available
        });
        controlPanel.getRecoverySlider().valueProperty().addListener((obs, o, n) -> {
            // engine.setRecoveryRate(n.doubleValue());
        });
        controlPanel.getMortalitySlider().valueProperty().addListener((obs, o, n) -> {
            // engine.setMortalityRate(n.doubleValue());
        });

        // ── Tool selector → InteractionController ────────────────────────────
        // Kept in sync via getSelectedTool() at click time (see wireMouse)
    }

    /**
     * Attaches mouse click and drag handlers to the GridView canvas.
     */
    private void wireMouse() {
        gridView.setOnMouseClicked(this::handleMouse);
        gridView.setOnMouseDragged(this::handleMouse);
    }

    /**
     * Handles a mouse event on the grid canvas.
     * Reads the selected tool from ControlPanel and delegates to
     * {@link InteractionController}.
     *
     * @param event the JavaFX mouse event
     */
    private void handleMouse(MouseEvent event) {
        // Sync the tool from the UI selector
        String toolName = controlPanel.getSelectedTool();
        switch (toolName) {
            case "ADD":    interactionController.setTool(InteractionController.Tool.ADD);    break;
            case "REMOVE": interactionController.setTool(InteractionController.Tool.REMOVE); break;
            case "BRUSH":  interactionController.setTool(InteractionController.Tool.BRUSH);  break;
            default: break;
        }

        interactionController.handle(event, CELL_SIZE);
        gridView.render();
        statsPanel.update(grid);
    }

    // ── Animation timer ───────────────────────────────────────────────────────

    /**
     * Builds the animation timer that calls {@code SimulationEngine.step()}
     * at the rate chosen by the speed slider.
     */
    private void buildTimer() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                int stepsPerSecond = (int) controlPanel.getSpeedSlider().getValue();
                long interval = 1_000_000_000L / stepsPerSecond;
                if (now - lastStepTime >= interval) {
                    engine.step(); // ← team's SimulationEngine.step()
                    refreshDisplay();
                    lastStepTime = now;
                }
            }
        };
    }

    /**
     * Refreshes the GridView and StatsPanel after a simulation step.
     * Also updates the step counter using {@code SimulationEngine.getCurrentStep()}.
     */
    private void refreshDisplay() {
        gridView.render();
        statsPanel.update(grid);
        controlPanel.updateStep(engine.getCurrentStep()); // team's getCurrentStep()
    }
}
