// controller;

import javafx.scene.input.MouseEvent;
// import javafx.scene.input.MouseButton;
// import model.Grid;

public class InteractionController {

    public enum Tool {

        ADD, REMOVE, BRUSH;

    }

    private Tool currentTool;

    public Tool getTool(){
        return this.currentTool;
    }

    public void setTool(Tool tool) {
        this.currentTool = tool;
    }

    public void addAgentOnClick(MouseEvent event){
        if (currentTool == Tool.ADD) {
            // Ajouter un agent à la position de la souris
            // int x = (int) event.getX();
            // int y = (int) event.getY();
            // grid.addAgent(x, y);
            
        }
    public void removeOnLeftClick(MouseEvent event){

        if (currentTool == Tool.REMOVE) {
            // Récupérer les coordonnées de la souris
            int x = (int) event.getX();
            int y = (int) event.getY();
            // Supprimer un agent de la grille à ces coordonnées
            // grid.removeAgent(x, y);
        }
    }
}


