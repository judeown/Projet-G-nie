import java.util.Objects;

public class Agent {
    private int positionX;
    private int positionY;
    private int age;
    private HealthState state;
    private int infectionProbability = 30;
    private double energy;
    private double moveProbability = 0.3;
    private int infectionTime = 0;
    private double deadProbability = 0.1; // Assuming this should be a double

    public Agent(int x, int y, int age) throws AgentException {
        if(age < 0 || age > 110) {
            throw new AgentException("Age must be between 0 and 110.");
        }
        if(x < 0 || y < 0) {
            throw new AgentException("Position coordinates must be non-negative.");
        }
        this.positionX = x;
        this.positionY = y;
        this.age = age;
        this.state = HealthState.HEALTHY;
        setInfectionProbability();
        updateDeadProbability();
        if (age < 20) {
            this.energy = 100.0; // Initial energy level 
            this.moveProbability = 0.5; // Higher move probability for younger agents
        } else if (age < 40) {
            this.energy = 90.0; // Initial energy level 
            this.moveProbability = 0.3; // Normal move probability for middle-aged agents
        } else if (age < 60) {
            this.energy = 70.0; // Initial energy level 
            this.moveProbability = 0.2; // Lower move probability for older agents
        } else {
            this.energy = 50.0; // Initial energy level 
            this.moveProbability = 0.1; // Lowest move probability for oldest agents
        }
    }

    public void setState(HealthState state) {
            this.state = state;
        }
    
    public void setInfectionProbability() {
        if (age < 20) {
            infectionProbability = 10;
        } else if (age < 40) {
            infectionProbability = 20;
        } else if (age < 60) {
            infectionProbability = 30;
        } else {
            infectionProbability = 40;
        }
    }

    public void updateDeadProbability() {
        if (age < 20) {
            deadProbability = 0.01;
        } else if (age < 40) {
            deadProbability = 0.05;
        } else if (age < 60) {
            deadProbability = 0.1;
        } else {
            deadProbability = 0.2;
        }
    }



    public void updateEnergy() {
        if (state == HealthState.INFECTED) {
            energy -= 5.0; // Energy decreases by 5 units per time step when infected
        } else {
            energy -= 1.0; // Energy decreases by 1 unit per time step when healthy or recovered
        }
        if (energy < 0) {
            energy = 0; // Ensure energy does not go below 0
        }
    }

   public boolean canMove() {
        updateMoveProbability();
        return Math.random() < moveProbability;
    }

    public void updateMoveProbability() {
        double base;

        if (age < 20) {
            base = 0.7;
        } else if (age < 40) {
            base = 0.5;
        } else if (age < 60) {
            base = 0.3;
        } else {
            base = 0.1;
        }

        if (energy < 10) {
            base *= 0.5;
        }

        moveProbability = base;
    }
    
    public void infect() {
        this.state = HealthState.INFECTED;
    }

    public void recover() {
        this.state = HealthState.RECOVERED;
    }

    public void die() {
        this.state = HealthState.DEAD;
    }

    public boolean isAlive() {
        return this.state != HealthState.DEAD;
    }

    public void setPositionX(int x) {
        this.positionX = x;
    }

    public void setPositionY(int y) {
        this.positionY = y;
    }

    public void incrementInfectionTime() {
        this.infectionTime++;
    }





    public double getEnergy() {
        return energy;
    }

    public int getAge() {
        return age;
    }

    public HealthState getState() {
        return state;
    }
   
    public int getPositionX() {
        return positionX;
    }
    public int getPositionY() {
        return positionY;
    }

    public int getInfectionTime() {
        return infectionTime;
    }

    public double getMoveProbability() {
        return moveProbability;
    }

    public double getDeadProbability() {
        return deadProbability;
    }

    public int getInfectionProbability() {
        return infectionProbability;
    }
    
    @Override
    public String toString() {
        return "Agent{" +
                "positionX=" + positionX +
                ", positionY=" + positionY +
                ", age=" + age +
                ", state=" + state +
                ", energy=" + energy +
                ", moveProbability=" + moveProbability +
                ", infectionTime=" + infectionTime +
                ", deadProbability=" + deadProbability +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Agent agent = (Agent) obj;
        return positionX == agent.positionX &&
                positionY == agent.positionY &&
                age == agent.age &&
                state == agent.state &&
                Double.compare(agent.energy, energy) == 0 &&
                Double.compare(agent.moveProbability, moveProbability) == 0 &&
                infectionTime == agent.infectionTime &&
                Double.compare(agent.deadProbability, deadProbability) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            positionX,
            positionY,
            age,
            state,
            energy,
            moveProbability,
            infectionTime,
            deadProbability
        );
    }

}