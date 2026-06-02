public AgentException{
    public AgentException(String message) {
        super(message);
    }
}


public class Agent {
    private int positionX;
    private int positionY;
    private int age;
    private HealthState state;
    public int infectionProbability = 30;
    private double energy;
    private double moveProbability = 0.3;
    private int infectionTime = 0;
    private double deadProbability = 0.1; // Assuming this should be a double

    public Agent(int x, int y, int age) {
        if(age < 0 || age > 110) {
            throw new AgentException("Age must be between 0 and 100.");
        }
        if(x < 0 || y < 0) {
            throw new AgentException("Position coordinates must be non-negative.");
        }
        this.positionX = x;
        this.positionY = y;
        this.age = age;
        this.state = HealthState.HEALTHY;
        if (age < 20) {
            this.energy = 100.0; // Initial energy level 
        } else if (age < 40) {
            this.energy = 90.0; // Initial energy level 
        } else if (age < 60) {
            this.energy = 70.0; // Initial energy level 
        } else {
            this.energy = 50.0; // Initial energy level 
        }
        this.energy = 100.0; // Initial energy level   
    }
    public void setState(HealthState state) {
            this.state = state;
        }
    
    public void getInfectionProbability() {
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

    public void canMove(){
        
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

    public int getInfectionProbabilityValue() {
        return infectionProbability;
    }
    

}