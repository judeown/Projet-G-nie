

import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;


import java.util.ArrayList;
import java.util.List;

/**
 * Right side panel showing live simulation statistics.
 *
 * <p>Uses {@link StatisticsManager} exclusively for all numbers.
 * The {@code StatisticsManager} currently works with {@code mock.Grid} /
 * {@code mock.Agent}. Once the team merges and updates its imports to
 * use the real {@code Grid} / {@code Agent}, this panel will work without
 * any changes — the method signatures are identical.
 *
 * <p>Until then, a {@code TODO} comment marks the temporary workaround.
 */
public class StatsPanel extends VBox {

    /** Number of historical data points shown in the chart. */
    private static final int HISTORY_SIZE = 80;
    private static final int CHART_W      = 190;
    private static final int CHART_H      = 90;

    // ── Stat labels ───────────────────────────────────────────────────────────
    private final Label totalLabel;
    private final Label healthyLabel;
    private final Label infectedLabel;
    private final Label recoveredLabel;
    private final Label deadLabel;
    private final Label avgAgeLabel;
    private final Label avgEnergyLabel;
    private final Label minEnergyLabel;
    private final Label maxEnergyLabel;

    /** Canvas for drawing the history chart. */
    private final Canvas chartCanvas;

    /** StatisticsManager from the team's feature/statistics-cli branch. */
    private final StatisticsManager statsManager;

    // ── History lists for the chart ───────────────────────────────────────────
    private final List<Integer> healthyHistory   = new ArrayList<>();
    private final List<Integer> infectedHistory  = new ArrayList<>();
    private final List<Integer> recoveredHistory = new ArrayList<>();

    /**
     * Creates the statistics panel.
     */
    public StatsPanel() {
        super(8);
        setPadding(new Insets(16));
        setPrefWidth(230);
        setStyle("-fx-background-color: #0f3460; -fx-border-color: #2d2d4e; -fx-border-width: 0 0 0 1;");

        // Instantiate the team's StatisticsManager — no modification needed
        statsManager = new StatisticsManager();

        totalLabel     = statLabel("Total:      0");
        healthyLabel   = statLabel("🟢 Healthy:   0  (0.0%)");
        infectedLabel  = statLabel("🔴 Infected:  0  (0.0%)");
        recoveredLabel = statLabel("🔵 Recovered: 0  (0.0%)");
        deadLabel      = statLabel("⚫ Dead:      0  (0.0%)");
        avgAgeLabel    = statLabel("Avg age:    0.0");
        avgEnergyLabel = statLabel("Avg energy: 0.0");
        minEnergyLabel = statLabel("Min energy: 0.0");
        maxEnergyLabel = statLabel("Max energy: 0.0");

        chartCanvas = new Canvas(CHART_W, CHART_H);
        clearChart();

        Label title      = sectionLabel("STATISTICS");
        title.setTextFill(Color.web("#a78bfa"));
        title.setFont(Font.font("Monospace", FontWeight.BOLD, 13));

        Label legend = new Label(" 🟢 Healthy  🔴 Infected  🔵 Recovered");
        legend.setTextFill(Color.web("#64748b"));
        legend.setFont(Font.font("Monospace", 9));
        legend.setWrapText(true);

        getChildren().addAll(
            title,
            sep(),
            totalLabel,
            healthyLabel, infectedLabel, recoveredLabel, deadLabel,
            sep(),
            avgAgeLabel, avgEnergyLabel, minEnergyLabel, maxEnergyLabel,
            sep(),
            sectionLabel("History"),
            chartCanvas,
            legend
        );
    }

    /**
     * Refreshes all statistics labels and the history chart.
     *
     * <p>Calls every method of {@link StatisticsManager} using the real
     * {@code Grid} object. Because {@code StatisticsManager} currently imports
     * {@code mock.Grid}, you will need to update its import once the branches
     * are merged.
     *
     * <p><b>TODO:</b> When {@code StatisticsManager} is updated to import the
     * real {@code Grid} instead of {@code mock.Grid}, remove this comment.
     *
     * @param grid the current simulation grid
     */
    public void update(Grid grid) {
        if (grid == null) return;

        // All numbers come exclusively from StatisticsManager
        int total     = statsManager.countTotalAgents(grid);
        int healthy   = statsManager.countHealthy(grid);
        int infected  = statsManager.countInfected(grid);
        int recovered = statsManager.countRecovered(grid);
        int dead      = statsManager.countDead(grid);

        double avgAge    = statsManager.calculateAverageAge(grid);
        double avgEnergy = statsManager.calculateAverageEnergy(grid);
        double minEnergy = statsManager.getMinimumEnergy(grid);
        double maxEnergy = statsManager.getMaximumEnergy(grid);

        double pctH = statsManager.calculateInfectionPercentage(grid);
        double pctD = statsManager.calculateDeathPercentage(grid);
        double pctR = total > 0 ? recovered * 100.0 / total : 0.0;

        totalLabel.setText(String.format("Total:      %d", total));
        healthyLabel.setText(String.format("🟢 Healthy:   %3d (%4.1f%%)", healthy,   total > 0 ? healthy   * 100.0 / total : 0));
        infectedLabel.setText(String.format("🔴 Infected:  %3d (%4.1f%%)", infected,  pctH));
        recoveredLabel.setText(String.format("🔵 Recovered: %3d (%4.1f%%)", recovered, pctR));
        deadLabel.setText(String.format("⚫ Dead:      %3d (%4.1f%%)", dead,      pctD));
        avgAgeLabel.setText(String.format("Avg age:    %.1f", avgAge));
        avgEnergyLabel.setText(String.format("Avg energy: %.1f", avgEnergy));
        minEnergyLabel.setText(String.format("Min energy: %.1f", minEnergy));
        maxEnergyLabel.setText(String.format("Max energy: %.1f", maxEnergy));

        // Update chart history
        healthyHistory.add(healthy);
        infectedHistory.add(infected);
        recoveredHistory.add(recovered);
        if (healthyHistory.size()   > HISTORY_SIZE) healthyHistory.remove(0);
        if (infectedHistory.size()  > HISTORY_SIZE) infectedHistory.remove(0);
        if (recoveredHistory.size() > HISTORY_SIZE) recoveredHistory.remove(0);

        drawChart(total);
    }

    /**
     * Resets all labels and clears chart history.
     */
    public void reset() {
        healthyHistory.clear();
        infectedHistory.clear();
        recoveredHistory.clear();
        clearChart();
        totalLabel.setText("Total:      0");
        healthyLabel.setText("🟢 Healthy:   0  (0.0%)");
        infectedLabel.setText("🔴 Infected:  0  (0.0%)");
        recoveredLabel.setText("🔵 Recovered: 0  (0.0%)");
        deadLabel.setText("⚫ Dead:      0  (0.0%)");
        avgAgeLabel.setText("Avg age:    0.0");
        avgEnergyLabel.setText("Avg energy: 0.0");
        minEnergyLabel.setText("Min energy: 0.0");
        maxEnergyLabel.setText("Max energy: 0.0");
    }

    // ── Chart drawing ─────────────────────────────────────────────────────────

    private void drawChart(int maxTotal) {
        GraphicsContext gc = chartCanvas.getGraphicsContext2D();
        gc.setFill(Color.web("#0a0a1a"));
        gc.fillRect(0, 0, CHART_W, CHART_H);

        if (healthyHistory.isEmpty()) return;

        int    size   = healthyHistory.size();
        double xStep  = (double) CHART_W / HISTORY_SIZE;
        double yScale = maxTotal > 0 ? (double) (CHART_H - 4) / maxTotal : 1;

        drawLine(gc, healthyHistory,   size, xStep, yScale, Color.web("#4ade80"));
        drawLine(gc, infectedHistory,  size, xStep, yScale, Color.web("#f87171"));
        drawLine(gc, recoveredHistory, size, xStep, yScale, Color.web("#60a5fa"));
    }

    private void drawLine(GraphicsContext gc, List<Integer> data, int size,
                          double xStep, double yScale, Color color) {
        gc.setStroke(color);
        gc.setLineWidth(1.5);
        gc.beginPath();
        for (int i = 0; i < size; i++) {
            double x = i * xStep;
            double y = CHART_H - (data.get(i) * yScale) - 2;
            if (i == 0) gc.moveTo(x, y); else gc.lineTo(x, y);
        }
        gc.stroke();
    }

    private void clearChart() {
        GraphicsContext gc = chartCanvas.getGraphicsContext2D();
        gc.setFill(Color.web("#0a0a1a"));
        gc.fillRect(0, 0, CHART_W, CHART_H);
    }

    // ── Label helpers ─────────────────────────────────────────────────────────

    private Label statLabel(String text) {
        Label l = new Label(text);
        l.setTextFill(Color.web("#e2e8f0"));
        l.setFont(Font.font("Monospace", 11));
        return l;
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
