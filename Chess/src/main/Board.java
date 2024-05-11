package main;
import java.awt.*;

public class Board {
	
	final int MAX_ROW = 8;
	final int MAX_COL = 8;
	public static final int SQUARE_SIZE = 100;
	public static final int HALF_SQAURE_SIZE =  SQUARE_SIZE/2;
	
	public void draw(Graphics2D g2){
        for (int row = 0; row < MAX_ROW; row++){
            for (int col = 0; col < MAX_COL; col++){
               if ((row + col) % 2 == 0){
                   g2.setColor(Color.decode("#FFFACD"));}
               else {
                    g2.setColor(Color.decode("#593E1A"));}
                g2.fillRect(col * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
                }
            }
        }

}
