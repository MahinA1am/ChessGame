package piece;

import main.GamePanel;
import main.Type;

public class Bishop extends Piece{

	public Bishop(int color, int col, int row) {
		super(color, col, row);
		
		type = Type.BISHOP;
		
		if(color == GamePanel.WHITE) {
			image = getImage("/piece/Wbishop");
		}
		else {
			image = getImage("/piece/Bbishop");
		}
	}
	public boolean canMove(int targetCol, int targetRow) {
		
		if(isWithinBound(targetCol,targetRow) && isSameSquare(targetCol,targetRow) == false) {
			if(Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow)) {
				if(isValidSquare(targetCol, targetRow) && pieceIsOnDiagonalLine(targetCol, targetRow) == false) {
					return true;
				}
			}
		}
		return false;
	}

}
