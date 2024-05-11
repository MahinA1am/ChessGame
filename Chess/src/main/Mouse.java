package main;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Mouse extends MouseAdapter {
    public int x, y;
    //public int prevX, prevY;
    public boolean pressed;
    public boolean moved; // Flag to indicate mouse movement
//    private GamePanel gamePanel;
//    public Mouse(GamePanel gamePanel) {
//        this.gamePanel = gamePanel;
//    }

    @Override
    public void mousePressed(MouseEvent e) {
        pressed = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        pressed = false;

    }

    @Override
    public void mouseDragged(MouseEvent e) {
      //  prevX = x;
       // prevY = y;
    	x = e.getX();
        y = e.getY();
        moved = true;

    }
//    public void mouseDragged(MouseEvent e) {
//        x = e.getX();
//        y = e.getY();
//        moved = true;
//
//        // Trigger the simulate() method in the GamePanel
//        gamePanel.simulate();
//    }

    @Override
    public void mouseMoved(MouseEvent e) {
        x = e.getX();
        y = e.getY();
      
    }
}
