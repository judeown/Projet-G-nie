

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class MainView {

    private static final int CELL_SIZE = 16;

    private final Stage               stage;
    private final Grid                grid;
    private final SimulationEngine    engine;
    private final GridView            gridView;
    private final ControlPanel        controlPanel;
    private final StatsPanel          statsPanel;
    private final InteractionController interactionController;

    private AnimationTimer timer;

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

        gridView.render();
        statsPanel.update(grid);
    }


    
    private void wireControls() {

        controlPanel.getPlayButton().setOnAction(e -> {
            engine.start();
            controlPanel.setRunningState();
            timer.start();
        });

        controlPanel.getPauseButton().setOnAction(e -> {
            engine.pause();
            controlPanel.setPausedState();
            timer.stop();
        });

        
        controlPanel.getStepButton().setOnAction(e -> {
            engine.start();
            engine.step();
            engine.pause();
            refreshDisplay();
        });

        
        controlPanel.getResetButton().setOnAction(e -> {
            timer.stop();
            engine.pause();
            controlPanel.setPausedState();
            controlPanel.updateStep(0);
            statsPanel.reset();
            gridView.render();
        });

        controlPanel.getClearButton().setOnAction(e -> {
            gridView.render();
            statsPanel.update(grid);
        });

        
        controlPanel.getContagionSlider().valueProperty().addListener((obs, o, n) -> {
        });
        controlPanel.getRecoverySlider().valueProperty().addListener((obs, o, n) -> {
        });
        controlPanel.getMortalitySlider().valueProperty().addListener((obs, o, n) -> {
        });

       
    }

   
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

    
    private void refreshDisplay() {
        gridView.render();
        statsPanel.update(grid);
        controlPanel.updateStep(engine.getCurrentStep()); // team's getCurrentStep()
    }
}
