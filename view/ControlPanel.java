import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Left-side control panel containing simulation controls, tool selector,
 * parameter sliders, random fill controls, and save/load buttons.
 */
public class ControlPanel extends VBox {

    // Simulation control buttons
    private final Button playButton;
    private final Button pauseButton;
    private final Button stepButton;
    private final Button resetButton;
    private final Button clearButton;

    // Speed control
    private final Slider speedSlider;
    private final Label  speedValueLabel;

    // Epidemic parameter sliders
    private final Slider contagionSlider;
    private final Slider recoverySlider;
    private final Slider mortalitySlider;

    // Tool selector (ADD, REMOVE, BRUSH, ZONE)
    private final ComboBox<String> toolSelector;

    // Initial health state for newly placed agents
    private final ComboBox<String> stateSelector;

    // Random fill controls
    private final Button randomFillButton;
    private final Slider densitySlider;
    private final Slider infectionRateSlider;

    // Import / Export
    private final Button saveButton;
    private final Button loadButton;

    // Topology
    private final CheckBox toroidalCheckBox;

    // Step counter label
    private final Label stepLabel;

    // Grid size controls
    private final TextField gridWidthField;
    private final TextField gridHeightField;
    private final Button    applyGridSizeButton;

    /**
     * Builds the full control panel with all UI controls.
     */
    public ControlPanel(int initialGridWidth, int initialGridHeight) {
        super(10);
        setPadding(new Insets(14));
        setPrefWidth(230);
        setStyle("-fx-background-color: #16213e; -fx-border-color: #2d2d4e; -fx-border-width: 0 1 0 0;");

        // --- Simulation buttons ---
        playButton   = styledButton("▶  Play",   "#4ade80", "#166534");
        pauseButton  = styledButton("⏸  Pause",  "#facc15", "#713f12");
        stepButton   = styledButton("⏭  Step",   "#60a5fa", "#1e3a5f");
        resetButton  = styledButton("↺  Reset",  "#f87171", "#7f1d1d");
        clearButton  = styledButton("🗑  Clear",  "#a78bfa", "#3b0764");
        pauseButton.setDisable(true);

        // --- Speed ---
        speedSlider     = buildSlider(1, 30, 5);
        speedValueLabel = new Label("5");
        speedValueLabel.setTextFill(Color.web("#60a5fa"));
        speedValueLabel.setFont(Font.font("Monospace", 11));
        speedSlider.valueProperty().addListener((obs, o, n) ->
            speedValueLabel.setText(String.valueOf(n.intValue()))
        );

        // --- Epidemic parameters ---
        contagionSlider = buildSlider(0.0, 1.0, 0.3);
        recoverySlider  = buildSlider(0.0, 1.0, 0.1);
        mortalitySlider = buildSlider(0.0, 1.0, 0.05);

        // --- Tool selector ---
        toolSelector = new ComboBox<>();
        toolSelector.getItems().addAll("ADD", "REMOVE", "BRUSH", "ZONE");
        toolSelector.setValue("ADD");
        toolSelector.setStyle(
            "-fx-background-color: #2d2d4e; -fx-text-fill: white; " +
            "-fx-border-color: #4a5568; -fx-border-radius: 4;"
        );
        toolSelector.setPrefWidth(200);

        stateSelector = new ComboBox<>();
        stateSelector.getItems().addAll("🟢 Healthy", "🔴 Infected", "🔵 Recovered");
        stateSelector.setValue("🟢 Healthy");
        stateSelector.setStyle(
            "-fx-background-color: #2d2d4e; -fx-text-fill: white; " +
            "-fx-border-color: #4a5568; -fx-border-radius: 4;"
        );
        stateSelector.setPrefWidth(200);

        // --- Random fill ---
        randomFillButton   = styledButton("⚡ Fill",  "#34d399", "#065f46");
        randomFillButton.setPrefWidth(200);
        densitySlider      = buildSlider(0.0, 1.0, 0.2);
        infectionRateSlider= buildSlider(0.0, 0.5, 0.05);

        // --- Save / Load ---
        saveButton = styledButton("💾 Save", "#93c5fd", "#1e3a5f");
        loadButton = styledButton("📂 Load", "#fcd34d", "#78350f");
        saveButton.setPrefWidth(97);
        loadButton.setPrefWidth(97);

        // --- Toroidal ---
        toroidalCheckBox = new CheckBox("Toroidal grid");
        toroidalCheckBox.setTextFill(Color.web("#e2e8f0"));
        toroidalCheckBox.setFont(Font.font("Monospace", 11));
        toroidalCheckBox.setStyle("-fx-mark-color: #60a5fa;");

        // --- Step label ---
        stepLabel = new Label("Step: 0");
        stepLabel.setTextFill(Color.web("#94a3b8"));
        stepLabel.setFont(Font.font("Monospace", 11));

        // --- Grid size ---
        gridWidthField = buildNumberField(initialGridWidth, "Width");
        gridHeightField = buildNumberField(initialGridHeight, "Height");
        applyGridSizeButton = styledButton("Apply size", "#c4b5fd", "#312e81");
        applyGridSizeButton.setPrefWidth(200);

        // --- Layout assembly ---
        Label title = new Label("CELL SIMULATION");
        title.setFont(Font.font("Monospace", FontWeight.BOLD, 13));
        title.setTextFill(Color.web("#60a5fa"));

        HBox row1 = new HBox(8, playButton, pauseButton);
        HBox row2 = new HBox(8, stepButton, resetButton);
        row1.setAlignment(Pos.CENTER_LEFT);
        row2.setAlignment(Pos.CENTER_LEFT);

        HBox speedRow = new HBox(8, speedSlider, speedValueLabel);
        speedRow.setAlignment(Pos.CENTER_LEFT);

        HBox saveLoadRow = new HBox(6, saveButton, loadButton);
        saveLoadRow.setAlignment(Pos.CENTER_LEFT);

        HBox gridSizeRow = new HBox(6, gridWidthField, gridHeightField);
        gridSizeRow.setAlignment(Pos.CENTER_LEFT);

        getChildren().addAll(
            title, stepLabel,
            sep(),
            sectionLabel("Grid size (W / H)"),
            gridSizeRow,
            applyGridSizeButton,
            sep(),
            sectionLabel("▶ Simulation"),
            row1, row2, clearButton,
            sep(),
            sectionLabel("⚡ Speed (steps/sec)"),
            speedRow,
            sep(),
            sectionLabel("🖱 Tool"),
            toolSelector,
            sectionLabel("  Cell state"),
            stateSelector,
            sep(),
            sectionLabel("🎲 Random Fill"),
            randomFillButton,
            sectionLabel("  Density"),       densitySlider,
            sectionLabel("  Init. infection"),infectionRateSlider,
            sep(),
            sectionLabel("⚙ Parameters"),
            sectionLabel("  Contagion"),  contagionSlider,
            sectionLabel("  Recovery"),   recoverySlider,
            sectionLabel("  Mortality"),  mortalitySlider,
            sep(),
            toroidalCheckBox,
            sep(),
            sectionLabel("💾 File"),
            saveLoadRow
        );
    }

    // -------------------------------------------------------------------------
    // State helpers
    // -------------------------------------------------------------------------

    /**
     * Updates the step counter label.
     *
     * @param step current step from {@code SimulationEngine.getCurrentStep()}
     */
    public void updateStep(int step) { stepLabel.setText("Step: " + step); }

    /** Switches buttons to the running state (play disabled, pause enabled). */
    public void setRunningState() {
        playButton.setDisable(true);
        pauseButton.setDisable(false);
        stepButton.setDisable(true);
    }

    /** Switches buttons to the paused state (play enabled, pause disabled). */
    public void setPausedState() {
        playButton.setDisable(false);
        pauseButton.setDisable(true);
        stepButton.setDisable(false);
    }

    // -------------------------------------------------------------------------
    // Getters for MainView to wire event handlers
    // -------------------------------------------------------------------------

    /** @return the Play button */
    public Button getPlayButton()            { return playButton; }

    /** @return the Pause button */
    public Button getPauseButton()           { return pauseButton; }

    /** @return the Step button */
    public Button getStepButton()            { return stepButton; }

    /** @return the Reset button */
    public Button getResetButton()           { return resetButton; }

    /** @return the Clear button */
    public Button getClearButton()           { return clearButton; }

    /** @return the button that applies the grid size */
    public Button getApplyGridSizeButton()   { return applyGridSizeButton; }

    /** @return the speed slider (steps per second) */
    public Slider getSpeedSlider()           { return speedSlider; }

    /** @return the contagion rate slider */
    public Slider getContagionSlider()       { return contagionSlider; }

    /** @return the recovery rate slider */
    public Slider getRecoverySlider()        { return recoverySlider; }

    /** @return the mortality rate slider */
    public Slider getMortalitySlider()       { return mortalitySlider; }

    /** @return the currently selected tool name: "ADD", "REMOVE", "BRUSH", or "ZONE" */
    public String getSelectedTool()          { return toolSelector.getValue(); }

    /**
     * Returns the health state chosen for newly placed agents.
     *
     * @return HEALTHY, INFECTED, or RECOVERED
     */
    public HealthState getSelectedState() {
        String v = stateSelector.getValue();
        if (v != null && v.contains("Infected"))  return HealthState.INFECTED;
        if (v != null && v.contains("Recovered")) return HealthState.RECOVERED;
        return HealthState.HEALTHY;
    }

    /** @return the random fill button */
    public Button getRandomFillButton()      { return randomFillButton; }

    /** @return the fill density slider [0, 1] */
    public Slider getDensitySlider()         { return densitySlider; }

    /** @return the initial infection rate slider [0, 0.5] */
    public Slider getInfectionRateSlider()   { return infectionRateSlider; }

    /** @return the Save button */
    public Button getSaveButton()            { return saveButton; }

    /** @return the Load button */
    public Button getLoadButton()            { return loadButton; }

    /** @return the toroidal topology checkbox */
    public CheckBox getToroidalCheckBox()    { return toroidalCheckBox; }

    /** @return the entered grid width */
    public String getGridWidthText()         { return gridWidthField.getText(); }

    /** @return the entered grid height */
    public String getGridHeightText()        { return gridHeightField.getText(); }

    /**
     * Updates the displayed grid size values.
     *
     * @param width  grid width
     * @param height grid height
     */
    public void setGridSizeFields(int width, int height) {
        gridWidthField.setText(String.valueOf(width));
        gridHeightField.setText(String.valueOf(height));
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

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
        s.setMajorTickUnit((max - min) / 2.0);
        s.setPrefWidth(190);
        s.setStyle("-fx-control-inner-background: #2d2d4e;");
        return s;
    }

    private TextField buildNumberField(int value, String promptText) {
        TextField field = new TextField(String.valueOf(value));
        field.setPromptText(promptText);
        field.setPrefWidth(97);
        field.setStyle(
            "-fx-background-color: #2d2d4e; -fx-text-fill: white; " +
            "-fx-prompt-text-fill: #94a3b8; -fx-font-family: Monospace; -fx-font-size: 11; " +
            "-fx-border-color: #4a5568; -fx-border-radius: 4; -fx-background-radius: 4;"
        );
        return field;
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
