import java.util.List;
import java.util.ArrayList;

public class SimulationEngine {

    private Grid grid;
    private double contagionRate;
    private double recoveryRate;
    private double mortalityRate;
    private double moveProbability;
    private boolean running;
    private int currentStep;

    public SimulationEngine(Grid grid, double contagionRate, double recoveryRate,
                            double mortalityRate, double moveProbability) {
        this.grid = grid;
        this.running = false;
        this.currentStep = 0;
        this.contagionRate = Math.min(Math.abs(contagionRate), 1);
        this.recoveryRate = Math.min(Math.abs(recoveryRate), 1);
        this.mortalityRate = Math.min(Math.abs(mortalityRate), 1);
        this.moveProbability = Math.min(Math.abs(moveProbability), 1);
    }

    public void start() { this.running = true; }
    public void pause() { this.running = false; }
    public boolean isRunning() { return running; }
    public int getCurrentStep() { return currentStep; }
    public Grid getGrid() { return grid; }

    public void setContagionRate(double r) { this.contagionRate = Math.min(Math.abs(r), 1); }
    public void setRecoveryRate(double r)  { this.recoveryRate  = Math.min(Math.abs(r), 1); }
    public void setMortalityRate(double r) { this.mortalityRate = Math.min(Math.abs(r), 1); }

    private void moveAgent(Agent a) {
        if (Math.random() < a.getMoveProbability()) {
            List<Position> positions = Position.getNeighbors(
                new Position(a.getPositionX(), a.getPositionY()),
                grid.getWidth(), grid.getHeight()
            );
            List<Position> free = new ArrayList<>();
            for (Position p : positions)
                if (grid.isEmpty(p.getX(), p.getY())) free.add(p);
            if (!free.isEmpty()) {
                Position chosen = free.get((int) (Math.random() * free.size()));
                grid.removeAgent(a.getPositionX(), a.getPositionY());
                a.setPositionX(chosen.getX());
                a.setPositionY(chosen.getY());
                grid.addAgent(a);
            }
        }
    }

    private void updateAgent(Agent a) {
        if (a.getState() == HealthState.DEAD) return;
        if (a.getState() == HealthState.INFECTED) {
            a.incrementInfectionTime();
            double r = Math.random();
            if (r < mortalityRate) {
                a.die();
                grid.removeAgent(a.getPositionX(), a.getPositionY());
            } else if (r < recoveryRate) {
                a.recover();
            }
        } else if (a.getState() == HealthState.HEALTHY) {
            List<Agent> neighbors = grid.getNeighborAgents(a.getPositionX(), a.getPositionY());
            if (neighbors.size() > 0)
                if (Math.random() < contagionRate) a.infect();
        }
        moveAgent(a);
    }

    public void step() {
        if (isRunning()) {
            List<Agent> copy = new ArrayList<>(grid.getAllAgents());
            for (Agent a : copy) updateAgent(a);
            currentStep++;
        }
    }
}
