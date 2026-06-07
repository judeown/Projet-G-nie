import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

/**
 * Main application window. Composes the GridView, ControlPanel, and StatsPanel,
 * and wires all user interactions to the simulation engine.
 */
public class MainView {

    private static final int CELL_SIZE = 16;

    private final Stage                  stage;
    private final Grid                   grid;
    private final SimulationEngine       engine;
    private final GridView               gridView;
    private final ControlPanel           controlPanel;
    private final StatsPanel             statsPanel;
    private final InteractionController  interactionController;

    private AnimationTimer timer;
    private long lastStepTime = 0;

    /**
     * Creates the main view and wires all controls.
     *
     * @param stage  the primary JavaFX stage
     * @param grid   the simulation grid
     * @param engine the simulation engine
     */
    public MainView(Stage stage, Grid grid, SimulationEngine engine) {
        this.stage                = stage;
        this.grid                 = grid;
        this.engine               = engine;
        this.gridView             = new GridView(grid);
        this.controlPanel         = new ControlPanel();
        this.statsPanel           = new StatsPanel();
        this.interactionController = new InteractionController(grid);

        buildTimer();
        wireControls();
        wireMouse();
    }

    /**
     * Builds and shows the main window.
     */
    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1a1a2e;");

        ScrollPane scroll = new ScrollPane(gridView);
        scroll.setStyle("-fx-background: #1a1a2e; -fx-background-color: #1a1a2e;");
        scroll.setPadding(new Insets(16));

        ScrollPane leftScroll = new ScrollPane(controlPanel);
        leftScroll.setStyle("-fx-background: #16213e; -fx-background-color: #16213e;");
        leftScroll.setFitToWidth(true);
        leftScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        root.setLeft(leftScroll);
        root.setCenter(scroll);
        root.setRight(statsPanel);

        Scene scene = new Scene(root, 1200, 680);
        scene.setFill(Color.web("#1a1a2e"));

        stage.setTitle("2D Cell Simulation");
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> { if (timer != null) timer.stop(); });
        stage.show();

        gridView.render();
        statsPanel.update(grid);
    }

    // -------------------------------------------------------------------------
    // Control wiring
    // -------------------------------------------------------------------------

    private void wireControls() {

        // Play
        controlPanel.getPlayButton().setOnAction(e -> {
            engine.start();
            controlPanel.setRunningState();
            timer.start();
        });

        // Pause
        controlPanel.getPauseButton().setOnAction(e -> {
            engine.pause();
            controlPanel.setPausedState();
            timer.stop();
        });

        // Step-by-step
        controlPanel.getStepButton().setOnAction(e -> {
            engine.start();
            engine.step();
            engine.pause();
            refreshDisplay();
        });

        // Reset: stop sim, clear grid, reset counter
        controlPanel.getResetButton().setOnAction(e -> {
            timer.stop();
            engine.pause();
            engine.resetStep();
            grid.clear();
            controlPanel.setPausedState();
            controlPanel.updateStep(0);
            statsPanel.reset();
            gridView.render();
            statsPanel.update(grid);
        });

        // Clear: remove all agents without resetting step counter
        controlPanel.getClearButton().setOnAction(e -> {
            grid.clear();
            gridView.render();
            statsPanel.update(grid);
        });

        // Connect epidemic parameter sliders to engine
        controlPanel.getContagionSlider().valueProperty().addListener((obs, o, n) ->
            engine.setContagionRate(n.doubleValue())
        );
        controlPanel.getRecoverySlider().valueProperty().addListener((obs, o, n) ->
            engine.setRecoveryRate(n.doubleValue())
        );
        controlPanel.getMortalitySlider().valueProperty().addListener((obs, o, n) ->
            engine.setMortalityRate(n.doubleValue())
        );

        // Toroidal topology toggle
        controlPanel.getToroidalCheckBox().selectedProperty().addListener((obs, o, n) ->
            engine.setToroidal(n)
        );

        // Random fill
        controlPanel.getRandomFillButton().setOnAction(e -> {
            double density       = controlPanel.getDensitySlider().getValue();
            double infectionRate = controlPanel.getInfectionRateSlider().getValue();
            randomFill(density, infectionRate);
            gridView.render();
            statsPanel.update(grid);
        });

        // Save
        controlPanel.getSaveButton().setOnAction(e -> saveSimulation());

        // Load
        controlPanel.getLoadButton().setOnAction(e -> loadSimulation());
    }

    // -------------------------------------------------------------------------
    // Mouse handling
    // -------------------------------------------------------------------------

    private void wireMouse() {
        gridView.setOnMouseClicked(this::handleMouse);
        gridView.setOnMouseDragged(this::handleMouse);
        gridView.setOnMousePressed(this::handleMousePressed);
        gridView.setOnMouseReleased(this::handleMouseReleased);
    }

    private void syncTool() {
        String toolName = controlPanel.getSelectedTool();
        switch (toolName) {
            case "ADD":    interactionController.setTool(Tool.ADD);    break;
            case "REMOVE": interactionController.setTool(Tool.REMOVE); break;
            case "BRUSH":  interactionController.setTool(Tool.BRUSH);  break;
            case "ZONE":   interactionController.setTool(Tool.ZONE);   break;
            default: break;
        }
        interactionController.setPlaceState(controlPanel.getSelectedState());
    }

    private void handleMouse(MouseEvent event) {
        syncTool();
        interactionController.handle(event, CELL_SIZE);
        gridView.render();
        statsPanel.update(grid);
    }

    private void handleMousePressed(MouseEvent event) {
        syncTool();
        if (interactionController.getTool() == Tool.ZONE)
            interactionController.handlePressed(event, CELL_SIZE);
    }

    private void handleMouseReleased(MouseEvent event) {
        if (interactionController.getTool() == Tool.ZONE) {
            interactionController.handleReleased(event, CELL_SIZE);
            gridView.render();
            statsPanel.update(grid);
        }
    }

    // -------------------------------------------------------------------------
    // Animation timer
    // -------------------------------------------------------------------------

    private void buildTimer() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                int stepsPerSecond = (int) controlPanel.getSpeedSlider().getValue();
                long interval = 1_000_000_000L / stepsPerSecond;
                if (now - lastStepTime >= interval) {
                    engine.step();
                    refreshDisplay();
                    lastStepTime = now;
                }
            }
        };
    }

    private void refreshDisplay() {
        gridView.render();
        statsPanel.update(grid);
        controlPanel.updateStep(engine.getCurrentStep());
    }

    // -------------------------------------------------------------------------
    // Random fill
    // -------------------------------------------------------------------------

    /**
     * Fills the grid randomly with healthy (and optionally infected) agents.
     *
     * @param density       fraction of cells to fill [0, 1]
     * @param infectionRate fraction of placed agents that start infected [0, 1]
     */
    private void randomFill(double density, double infectionRate) {
        int total = grid.getWidth() * grid.getHeight();
        int toPlace = (int) (total * density);
        int placed = 0;
        int maxTries = total * 4;
        int tries = 0;

        while (placed < toPlace && tries < maxTries) {
            int x = (int) (Math.random() * grid.getWidth());
            int y = (int) (Math.random() * grid.getHeight());
            if (grid.isEmpty(x, y)) {
                int age = 10 + (int) (Math.random() * 60);
                Agent a = new Agent(x, y, age);
                if (Math.random() < infectionRate) a.infect();
                grid.addAgent(a);
                placed++;
            }
            tries++;
        }
    }

    // -------------------------------------------------------------------------
    // Save / Load
    // -------------------------------------------------------------------------

    private void saveSimulation() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save simulation");
        chooser.getExtensionFilters().add(new ExtensionFilter("Simulation files", "*.sim"));
        chooser.setInitialFileName("simulation.sim");
        File file = chooser.showSaveDialog(stage);
        if (file == null) return;

        try {
            SimulationSerializer.save(file, grid, engine);
        } catch (IOException ex) {
            showError("Save failed", ex.getMessage());
        }
    }

    private void loadSimulation() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Load simulation");
        chooser.getExtensionFilters().add(new ExtensionFilter("Simulation files", "*.sim"));
        File file = chooser.showOpenDialog(stage);
        if (file == null) return;

        boolean wasRunning = engine.isRunning();
        if (wasRunning) { timer.stop(); engine.pause(); }

        try {
            int savedStep = SimulationSerializer.load(file, grid);
            engine.setCurrentStep(savedStep);
            // Sync toroidal checkbox to loaded state
            controlPanel.getToroidalCheckBox().setSelected(grid.isToroidal());
            controlPanel.setPausedState();
            controlPanel.updateStep(savedStep);
            statsPanel.reset();
            refreshDisplay();
        } catch (IOException | IllegalArgumentException ex) {
            showError("Load failed", ex.getMessage());
            if (wasRunning) { engine.start(); timer.start(); }
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
