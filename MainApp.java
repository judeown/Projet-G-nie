import javafx.application.Application;
import javafx.stage.Stage;


public class MainApp extends Application {

    private static final int GRID_WIDTH  = 40;

    private static final int GRID_HEIGHT = 30;

    /**
     * Builds and shows the main window.
     *
     * @param primaryStage the primary stage provided by JavaFX
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            Grid              grid   = new Grid(GRID_WIDTH, GRID_HEIGHT);
            SimulationEngine  engine = new SimulationEngine(grid, 0.3, 0.1, 0.05, 0.3);

            MainView mainView = new MainView(primaryStage, grid, engine);
            mainView.show();
        } catch (Exception e) {
            System.err.println("[MainApp] Failed to start: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Application main entry point.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
