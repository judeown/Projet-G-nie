

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;


public class ControlPanel extends VBox {

    private final Button playButton;
    private final Button pauseButton;
    private final Button stepButton;
    private final Button resetButton;
    private final Button clearButton;

    private final Slider speedSlider;
    private final Label  speedValueLabel;

    private final Slider contagionSlider;
    private final Slider recoverySlider;
    private final Slider mortalitySlider;

    private final ComboBox<String> toolSelector;

    private final Label stepLabel;

   
    public ControlPanel() {
        super(12);
        setPadding(new Insets(16));
        setPrefWidth(220);
        setStyle("-fx-background-color: #16213e; -fx-border-color: #2d2d4e; -fx-border-width: 0 1 0 0;");

        playButton   = styledButton("▶  Play",   "#4ade80", "#166534");
        pauseButton  = styledButton("⏸  Pause",  "#facc15", "#713f12");
        stepButton   = styledButton("⏭  Step",   "#60a5fa", "#1e3a5f");
        resetButton  = styledButton("↺  Reset",  "#f87171", "#7f1d1d");
        clearButton  = styledButton("🗑  Clear",  "#a78bfa", "#3b0764");

        pauseButton.setDisable(true); 

        speedSlider      = buildSlider(1, 30, 5);
        speedValueLabel  = new Label("5");
        speedValueLabel.setTextFill(Color.web("#60a5fa"));
        speedValueLabel.setFont(Font.font("Monospace", 11));
        speedSlider.valueProperty().addListener((obs, o, n) ->
            speedValueLabel.setText(String.valueOf(n.intValue()))
        );

        contagionSlider = buildSlider(0.0, 1.0, 0.3);
        recoverySlider  = buildSlider(0.0, 1.0, 0.1);
        mortalitySlider = buildSlider(0.0, 1.0, 0.05);

        toolSelector = new ComboBox<>();
        toolSelector.getItems().addAll("ADD", "REMOVE", "BRUSH");
        toolSelector.setValue("ADD");
        toolSelector.setStyle(
            "-fx-background-color: #2d2d4e; -fx-text-fill: white; " +
            "-fx-border-color: #4a5568; -fx-border-radius: 4;"
        );
        toolSelector.setPrefWidth(190);

        stepLabel = new Label("Step: 0");
        stepLabel.setTextFill(Color.web("#94a3b8"));
        stepLabel.setFont(Font.font("Monospace", 11));

        Label title = new Label("CELL SIMULATION");
        title.setFont(Font.font("Monospace", FontWeight.BOLD, 13));
        title.setTextFill(Color.web("#60a5fa"));

        HBox row1 = new HBox(8, playButton, pauseButton);
        HBox row2 = new HBox(8, stepButton, resetButton);
        row1.setAlignment(Pos.CENTER_LEFT);
        row2.setAlignment(Pos.CENTER_LEFT);

        HBox speedRow = new HBox(8, speedSlider, speedValueLabel);
        speedRow.setAlignment(Pos.CENTER_LEFT);

        getChildren().addAll(
            title, stepLabel,
            sep(),
            sectionLabel("▶ Simulation"),
            row1, row2, clearButton,
            sep(),
            sectionLabel("⚡ Speed (steps/sec)"),
            speedRow,
            sep(),
            sectionLabel("🖱 Tool"),
            toolSelector,
            sep(),
            sectionLabel("⚙ Parameters"),
            sectionLabel("  Contagion"), contagionSlider,
            sectionLabel("  Recovery"),  recoverySlider,
            sectionLabel("  Mortality"), mortalitySlider
        );
    }


    /**
     * Updates the step counter display.
     *
     * @param step current step from {@code SimulationEngine.getCurrentStep()}
     */
    public void updateStep(int step) {
        stepLabel.setText("Step: " + step);
    }

    public void setRunningState() {
        playButton.setDisable(true);
        pauseButton.setDisable(false);
        stepButton.setDisable(true);
    }

    public void setPausedState() {
        playButton.setDisable(false);
        pauseButton.setDisable(true);
        stepButton.setDisable(false);
    }


    /** @return the Play button */
    public Button getPlayButton()  { return playButton; }

    /** @return the Pause button */
    public Button getPauseButton() { return pauseButton; }

    /** @return the Step button */
    public Button getStepButton()  { return stepButton; }

    /** @return the Reset button */
    public Button getResetButton() { return resetButton; }

    /** @return the Clear button */
    public Button getClearButton() { return clearButton; }

    /** @return the speed slider value (steps per second) */
    public Slider getSpeedSlider() { return speedSlider; }

    /** @return the contagion rate slider */
    public Slider getContagionSlider() { return contagionSlider; }

    /** @return the recovery rate slider */
    public Slider getRecoverySlider()  { return recoverySlider; }

    /** @return the mortality rate slider */
    public Slider getMortalitySlider() { return mortalitySlider; }

    /** @return the currently selected tool name: "ADD", "REMOVE", or "BRUSH" */
    public String getSelectedTool() { return toolSelector.getValue(); }


    private Button styledButton(String text, String fg, String bg) {
        Button b = new Button(text);
        b.setStyle(
            "-fx-background-color: " + bg + "; -fx-text-fill: " + fg + "; " +
            "-fx-font-family: Monospace; -fx-font-size: 11; " +
            "-fx-border-color: " + fg + "44; -fx-border-radius: 4; " +
            "-fx-background-radius: 4; -fx-cursor: hand;"
        );
        b.setPrefWidth(90);
        return b;
    }

    private Slider buildSlider(double min, double max, double val) {
        Slider s = new Slider(min, max, val);
        s.setShowTickMarks(true);
        s.setShowTickLabels(true);
        s.setMajorTickUnit((max - min) / 2);
        s.setStyle("-fx-control-inner-background: #2d2d4e;");
        return s;
    }

    private Label sectionLabel(String text) {
        Label l = new Label(text);
        l.setTextFill(Color.web("#94a3b8"));
        l.setFont(Font.font("Monospace", FontWeight.BOLD, 11));
        return l;
    }

    private Label sep() {
        Label l = new Label("");
        l.setStyle("-fx-border-color: #2d2d4e; -fx-border-width: 1 0 0 0;");
        l.setPrefWidth(Double.MAX_VALUE);
        return l;
    }
}
