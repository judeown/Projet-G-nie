
enum State {
    HEALTHY,
    INFECTED,
    RECOVERED,
    DEAD
}

public class Agent {
    private int positionX;
    private int positionY;
    private int age;
    private State state = null;
    public int infectionProbability = 30;

    public Agent(int x, int y, int age) {
        this.positionX = x;
        this.positionY = y;
        this.age = age;
    }
    public void setState(State state) {
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










    public int getAge() {
        return age;
    }

    public State getState() {
        return state;
    }
   
    public int getPositionX() {
        return positionX;
    }
    public int getPositionY() {
        return positionY;
    }


}