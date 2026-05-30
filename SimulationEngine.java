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

    public void start() {
        this.running = true;
    }

    public void pause() {
        this.running = false;
    }

    public boolean isRunning() {
        return running;
    }

    public int getCurrentStep() {
        return currentStep;
    }

    private void moveAgent(Agent a) {
        if (Math.random() < a.getMoveProbability()) {
            List<Position> positions = Position.getNeighbors(
                new Position(a.getPositionX(), a.getPositionY()),
                grid.getWidth(), grid.getHeight()
            );
            List<Position> freePositions = new ArrayList<>();
            for (Position p : positions) {
                if (grid.isEmpty(p.getX(), p.getY())) {
                    freePositions.add(p);
                }
            }
            if (!freePositions.isEmpty()) {
                int idx = (int) (Math.random() * freePositions.size());
                Position chosen = freePositions.get(idx);
                grid.removeAgent(a.getPositionX(), a.getPositionY());
                a.setPositionX(chosen.getX());
                a.setPositionY(chosen.getY());
                grid.addAgent(a);
            }
        }
    }

    private void updateAgent(Agent a) {
        if (a.getState() == State.DEAD) {
            return;
        }
        if (a.getState() == State.INFECTED) {
            a.incrementInfectionTime();
            double random = Math.random();
            if (random < mortalityRate) {
                a.die();
                grid.removeAgent(a.getPositionX(), a.getPositionY());
            } else if (random < recoveryRate) {
                a.recover();
            }
        } else if (a.getState() == State.HEALTHY) {
            List<Agent> infectedNeighbors = grid.getNeighborAgents(
                a.getPositionX(), a.getPositionY()
            );
            if (infectedNeighbors.size() > 0) {
                if (Math.random() < contagionRate) {
                    a.infect();
                }
            }
        }
        moveAgent(a);
    }

    public void step() {
        if (isRunning()) {
            List<Agent> copy = new ArrayList<>(grid.getAllAgents());
            for (Agent a : copy) {
                updateAgent(a);
            }
            currentStep++;
        }
    }

}
