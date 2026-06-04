import java.awt.event.MouseEvent;

public class InteractionController {

    private Tool currentTool;
    private Grid grid;

    
    private int newAgentAge = 30;
    private HealthState newAgentType = HealthState.HEALTHY;

    public InteractionController(Grid grid) {
        this.currentTool = Tool.SELECT;
        this.grid = grid;
    }

    public Tool getTool() {
        return this.currentTool;
    }

    public void setTool(Tool tool) {
        this.currentTool = tool;
    }

   
    public void setNewAgentAge(int age) {
        this.newAgentAge = age;
    }

    
    public void setNewAgentType(HealthState type) {
        this.newAgentType = type;
    }

    public void addAgentOnClick(MouseEvent event) {
        if (currentTool == Tool.ADD) {
            placeAgent(event.getX(), event.getY());
        }
    }

    public void removeOnLeftClick(MouseEvent event) {
        if (currentTool == Tool.REMOVE) {
            grid.removeAgent(event.getX(), event.getY());
        }
    }

    public void brushOnDrag(MouseEvent event) {
        if (currentTool == Tool.BRUSH) {
            placeAgent(event.getX(), event.getY());
        }
    }

    public void eraseOnDrag(MouseEvent event) {
        if (currentTool == Tool.ERASER) {
            grid.removeAgent(event.getX(), event.getY());
        }
    }


    private void placeAgent(int x, int y) {
        Agent a = new Agent(x, y, newAgentAge);
        a.setState(newAgentType);
        grid.addAgent(a);
    }
}