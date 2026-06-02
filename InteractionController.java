import java.awt.event.MouseEvent;

public class InteractionController {

    private Tool currentTool;

    public InteractionController() {
        this.currentTool = Tool.SELECT; // Default tool
    }

    public Tool getTool() {
        return this.currentTool;
    }

    public void setTool(Tool tool) {
        this.currentTool = tool;
    }

    public void addAgentOnClick(MouseEvent event) {
        if (this.currentTool == Tool.ADD) {
            // grid.addAgent((int) event.getX(), (int) event.getY());
        }
    }

    public void removeOnLeftClick(MouseEvent event) {
        if (currentTool == Tool.REMOVE) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            // grid.removeAgent(x, y);
        }
    }
}