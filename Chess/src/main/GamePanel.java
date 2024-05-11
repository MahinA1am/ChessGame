package main;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import piece.*;

public class GamePanel extends JPanel implements Runnable{
	
	public static final int WIDTH = 1200;
	public static final int HEIGHT = 800;
	private static final int FPS = 60;
	Thread gameThread;
	Board board = new Board();
	Mouse mouse = new Mouse();
	
	//Pieces
	public static ArrayList<Piece> pieces = new ArrayList<>();
	public static ArrayList<Piece> simPieces = new ArrayList<>();
	ArrayList<Piece> promotePieces = new ArrayList<>();
	Piece activePiece, checkingP;
	public static Piece castlingPiece;
	
	//Color
	public static final int WHITE = 0;
	public static final int BLACK = 1;
	int currentColor = WHITE;
	
	//Boolean
	boolean canMove;
	boolean validSquare;
	boolean promotion;
	boolean gameover;
	boolean stalemate;

	
	public GamePanel() {
		setPreferredSize(new Dimension(WIDTH,HEIGHT));
		setBackground(Color.BLACK);
		addMouseMotionListener(mouse);
		addMouseListener(mouse);
		//Setting pieces and copying
		//setPieces();
		//testPromotion();
		testIllegal();
		copyPieces(pieces,simPieces);
	}
	
	public void launchGame() {
		gameThread = new Thread(this);
		gameThread.start();
	}
	
	public void setPieces() {
	    for (int i = 0; i < 8; i++) {
	        pieces.add(new Pawn(WHITE, i, 6)); // White pawns
	        pieces.add(new Pawn(BLACK, i, 1)); // Black pawns
	    }
	    pieces.add(new Rook(WHITE, 0, 7));
	    pieces.add(new Rook(WHITE, 7, 7));
//	    pieces.add(new Knight(WHITE, 1, 7));
//	    pieces.add(new Knight(WHITE, 6, 7));
//	    pieces.add(new Bishop(WHITE, 2, 7));
//	    pieces.add(new Bishop(WHITE, 5, 7));
//	    pieces.add(new Queen(WHITE, 3, 7));
	    pieces.add(new King(WHITE, 4, 7));

	    pieces.add(new Rook(BLACK, 0, 0));
	    pieces.add(new Rook(BLACK, 7, 0));
	    pieces.add(new Knight(BLACK, 1, 0));
	    pieces.add(new Knight(BLACK, 6, 0));
	    pieces.add(new Bishop(BLACK, 2, 0));
	    pieces.add(new Bishop(BLACK, 5, 0));
	    pieces.add(new Queen(BLACK, 3, 0));
	    pieces.add(new King(BLACK, 4, 0));
	}
	
	public void testPromotion() {
		pieces.add(new Pawn(WHITE, 0, 3));
		pieces.add(new Pawn(BLACK, 5, 4));
	}
	
	public void testIllegal() {
		pieces.add(new Pawn(WHITE, 7, 6));
		pieces.add(new Pawn(WHITE, 6, 6));
		pieces.add(new King(WHITE, 3, 7));
		pieces.add(new King(BLACK, 0, 3));
		pieces.add(new Bishop(BLACK, 1, 4));
		pieces.add(new Queen(BLACK, 4, 5));
		pieces.add(new Pawn(BLACK, 7, 4));
	}
	
	private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target) {
		target.clear();
		for(int i = 0; i < source.size(); i++) {
			target.add(source.get(i));
		}
	}
	
	@Override
	public void run() {
		double FRAME_TIME = 1000000000/ FPS; 
		double delta = 0;
	    long lastTime = System.nanoTime();
	    long currentTime;
	    long elapsedTime;
	 
	    while (gameThread != null) {
	        currentTime = System.nanoTime();
	        elapsedTime = currentTime - lastTime;
	        delta += elapsedTime/FRAME_TIME;
	        lastTime = currentTime;
	        
	        if(delta >= 1) {
	        	  Update();
		          repaint();
		          delta--;
	        }
	     
	        }
	     
	    }
	
		
	private void Update() {
		
		
		if(promotion) {
				promoting();
			
		}
		else if(gameover == false && stalemate == false) {
			//MOUSE PRESSED
			if(mouse.pressed) {
				if(activePiece == null) {
					//If activePieces is null check if piece can be picked up
					for(Piece piece : simPieces) {
						//If mouse on ally piece pick it as active
						if(piece.color == currentColor && piece.col == mouse.x/Board.SQUARE_SIZE && piece.row == mouse.y/Board.SQUARE_SIZE) {
							activePiece = piece;
							
						}
					}
				}
			}
			else {
				//Player holding a piece
			    if(activePiece != null) { // Only call simulate() if an active piece exists
			    	//repaint();
			    	simulate();
		        }
				
			}
			//Mouse button released
			if(mouse.pressed == false) {
				if(activePiece != null) {
					if(validSquare) {
						
						//Confirmed Move
						//Update the piece in case a piece is captured
						copyPieces(simPieces, pieces);
						activePiece.updatePosition();
						if(castlingPiece != null) {
							castlingPiece.updatePosition();
						}
						
						if(isKingInCheck() && isCheckMate()) {
							gameover = true;
						}
						else if(isStaleMate() && !isKingInCheck()){
							stalemate = true;
							
						}

						else { // Game Goes On
							if(canPromote()) {
								activePiece.updatePosition();
								promotion = true;
							}
							else {
								changePlayer();
							}
						}
						
					}
					else {
						//Move cancelled or invalid.Reset everything
						copyPieces(simPieces, pieces); 
						activePiece.resetPosition();
						activePiece = null;
					}				
				}
			}
			
		}	

	}
	

	private void simulate() {
		
		//System.out.println("Checking piece...");
		canMove = false;
		validSquare = false;
		//Reset
		copyPieces(simPieces, pieces); 
		
		//Reset Castling Piece Position
		if(castlingPiece != null) {
			castlingPiece.col = castlingPiece.preCol;
			castlingPiece.x = castlingPiece.getX(castlingPiece.col);
			castlingPiece = null;
		}
		//Update position of piece
		activePiece.x = mouse.x - Board.HALF_SQAURE_SIZE;
		activePiece.y = mouse.y - Board.HALF_SQAURE_SIZE;
		activePiece.col = activePiece.getCol(activePiece.x);
		activePiece.row = activePiece.getRow(activePiece.y);
		
	    //if active piece is on a reachable square
		if(activePiece.canMove(activePiece.col, activePiece.row)) {
			canMove = true;
			//if hitting a piece remove it from the list
			if(activePiece.hittingP != null) {
				simPieces.remove(activePiece.hittingP.getIndex());
			}
			
			checkCastling();
			if(isIllegal(activePiece) == false && opponentCanCaptureKIng() == false) {
				validSquare = true;
			}
			
			//System.out.println(validSquare);
			
		}

	}

	private boolean isIllegal(Piece king) {
		
		if(king.type == Type.KING) {
			for(Piece piece : simPieces) {
				if(piece != king && piece.color != king.color && piece.canMove(king.col, king.row)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean opponentCanCaptureKIng(){
		
		Piece king = getKing(false);
		for(Piece piece : simPieces) {
			if(piece.color != king.color && piece.canMove(king.col, king.row)) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isKingInCheck() {
		
		Piece king = getKing(true);
		
		if(activePiece.canMove(king.col, king.row)) {
			checkingP = activePiece;
			return true;
		}
		else {
			checkingP = null;
		}
		
		return false;
	}
	
	private Piece getKing(boolean opponent) {
		
		Piece king = null;
		
		for(Piece piece : simPieces) {
			if(opponent) {
				if(piece.type == Type.KING &&piece.color != currentColor) {
					king = piece;
				}
			}
			else {
				if(piece.type == Type.KING &&piece.color == currentColor) {
					king = piece;
				}
			}
		}
		return king;
	}
	
	private boolean isCheckMate() {
		
		Piece king = getKing(true);
		if(kingCanMove(king)) {
			return false;
		}
		else {
			//Check if it possible to block by another piece
			
			//Check the position of attacking piece
			int colDiff = Math.abs(checkingP.col - king.col);
			int rowDiff = Math.abs(checkingP.row - king.row);
			
			if(colDiff == 0) {
				//Piece attacking vertically
				if(checkingP.row < king.row) {
					//Checking Piece is above king
					for(int row = checkingP.row; row < king.row; row++) {
						for(Piece piece : simPieces) {
							if(piece != king && piece.color != currentColor && piece.canMove(checkingP.col, row)) {
								return false;
							}
						}
					}
				}
				
				if(checkingP.row > king.row) {
					//Checking Piece is below king
					for(int row = checkingP.row; row > king.row; row--) {
						for(Piece piece : simPieces) {
							if(piece != king && piece.color != currentColor && piece.canMove(checkingP.col, row)) {
								return false;
							}
						}
					}
				}
			}
			else if(rowDiff == 0) {
				//Piece attacking horizontally
				if(checkingP.col < king.col) {
					//Checking Piece is on left
					for(int col = checkingP.col; col < king.col; col++) {
						for(Piece piece : simPieces) {
							if(piece != king && piece.color != currentColor && piece.canMove(col, checkingP.row)) {
								return false;
							}
						}
					}
				}
				if(checkingP.col > king.col) {
					//Checking Piece is on right
					for(int col = checkingP.col; col > king.col; col--) {
						for(Piece piece : simPieces) {
							if(piece != king && piece.color != currentColor && piece.canMove(col, checkingP.row)) {
								return false;
							}
						}
					}
				}
			}
			else if(colDiff == rowDiff) {
				//Piece attacking diagonally
				if(checkingP.row < king.row) {
					//Top Left
					if(checkingP.col < king.col) {
						for(int col = checkingP.col, row = checkingP.row; col < king.col; col++, row++) {
							for(Piece piece : simPieces) {
								if(piece != king && piece.color != currentColor && piece.canMove(col, row)) {
									return false;
								}
							}
						}
					}
					//Top Right
					if(checkingP.col > king.col) {
						for(int col = checkingP.col, row = checkingP.row; col > king.col; col--, row++) {
							for(Piece piece : simPieces) {
								if(piece != king && piece.color != currentColor && piece.canMove(col, row)) {
									return false;
								}
							}
						}
					}
				}
				
				if(checkingP.row > king.row) {
					//Bottom Left
					if(checkingP.row < king.row) {
						for(int col = checkingP.col, row = checkingP.row; col < king.col; col++, row--) {
							for(Piece piece : simPieces) {
								if(piece != king && piece.color != currentColor && piece.canMove(col, row)) {
									return false;
								}
							}
						}	
					}
					//Bottom Right
					if(checkingP.col > king.col) {
						if(checkingP.row < king.row) {
							for(int col = checkingP.col, row = checkingP.row; col > king.col; col--, row--) {
								for(Piece piece : simPieces) {
									if(piece != king && piece.color != currentColor && piece.canMove(col, row)) {
										return false;
									}
								}
							}
					}
				}
			}
			}
			else {
				//Knight attack
			}
		}
		return true;
	}
	
	private boolean kingCanMove(Piece king) {
		
		//Simulate if king can move to any safe square
		if(isValidMove(king, -1, -1)) { return true;}
		if(isValidMove(king, 0, -1)) { return true;}
		if(isValidMove(king, 1, -1)) { return true;}
		if(isValidMove(king, -1, 0)) { return true;}
		if(isValidMove(king, -1, 1)) { return true;}
		if(isValidMove(king, 1, 0)) { return true;}
		if(isValidMove(king, 0, 1)) { return true;}
		if(isValidMove(king, 1, 1)) { return true;}
		
		return false;
	}
	
	private boolean isValidMove(Piece king, int colPlus, int rowPlus) {
		
		boolean isValidMove = false;
		 //Update kings position temporarily
		king.col += colPlus;
		king.row += rowPlus;
		
		if(king.canMove(king.col, king.row)) {
			if(king.hittingP != null) {
				simPieces.remove(king.hittingP.getIndex());
			}
			if(isIllegal(king) == false) {
				isValidMove = true;
			}
		}
		king.resetPosition();
		copyPieces(pieces, simPieces);
		
		return isValidMove;
	}
	
	private boolean isStaleMate() {
		int count = 0;
		boolean checker = true;
		boolean isKnight = false,isRook = false, isBishop = false, isQueen = false, isPawn = false;
		for(Piece piece : simPieces) {
			if(piece.color != currentColor) {
				count++;
			}
		}
		if(count ==1) {
			if(kingCanMove(getKing(true)) == false) {
				return true;
			}
		}
		if(count > 1){   //Check if any other piece is alive
			for(Piece piece : simPieces) {
				if(piece.color != currentColor) {
					if(piece.type == Type.QUEEN) {
						isQueen = true;
					}
					if(piece.type == Type.ROOK) {
						isRook = true;
					}
					if(piece.type == Type.BISHOP) {
						isBishop = true;
					}
					if(piece.type == Type.KNIGHT) {
						isKnight = true;
					}
					if(piece.type == Type.PAWN) {
						isPawn = true;
					}
				}
		}
			if(isPawn && !isQueen && !isRook && !isKnight && !isBishop) { //Check if only pawn is alive with king
				for(Piece piece : simPieces) {
					if(piece.color != currentColor && piece.type != Type.KING) //Check if any pawn has valid move
					for(int col = 0; col < 8; col++) {
						for(int row = 0; row < 8; row++) {
							if(piece.canMove(col, row)) {  
								checker = false;  //if valid move exists for pawn set it to true
							}
						}
					}
				}
				if(checker) { //if no valid move is there check if king can move
					if(kingCanMove(getKing(true)) == false) {
						return true;
				}
				
			}
			
		}
	
		}
		return false;
	 
	}


	private void checkCastling() {
		if(castlingPiece != null) {
			if(castlingPiece.col == 0) {
				castlingPiece.col += 3;
			}
			else if(castlingPiece.col == 7) {
				castlingPiece.col -= 2;
			}
			castlingPiece.x = castlingPiece.getX(castlingPiece.col);
		}
	}

    private void changePlayer() {
    	if(currentColor == WHITE){
    		currentColor = BLACK;
    		
    		//Reset black;s two stepped status
    		for(Piece piece : pieces) {
    			if(piece.color == BLACK) {
        			piece.twoStepped = false;
        		}
    		}
    	}
    	else {
    		currentColor = WHITE;
    		//Reset white;s two stepped status
    		for(Piece piece : pieces) {
    			if(piece.color == WHITE) {
        			piece.twoStepped = false;
        		}
    		}
    	}
    	activePiece = null;
   
    }
    
    private boolean canPromote() {
    	
    	if(activePiece.type == Type.PAWN) {
    		if(currentColor == WHITE && activePiece.row == 0 || currentColor == BLACK && activePiece.row == 7) {
    			promotePieces.clear();
    			promotePieces.add(new Rook(currentColor, 9 ,2));
    			promotePieces.add(new Queen(currentColor, 9 ,3));
    			promotePieces.add(new Bishop(currentColor, 9 ,4));
    			promotePieces.add(new Knight(currentColor, 9 ,5));
    		   
    			return true;
    			
    		}
    	}
    	
    	return false;
    }
    

    private void promoting() {
  
        // Check if the mouse is pressed and activePiece is not null
        if (mouse.pressed) {
            for (Piece piece : promotePieces) {
                if (piece.col == mouse.x / Board.SQUARE_SIZE && piece.row == mouse.y / Board.SQUARE_SIZE) {
                    switch (piece.type) {
                        case ROOK: simPieces.add(new Rook(currentColor, activePiece.col, activePiece.row)); break;
                        case KNIGHT: simPieces.add(new Knight(currentColor, activePiece.col, activePiece.row));break;
                        case BISHOP: simPieces.add(new Bishop(currentColor, activePiece.col, activePiece.row)); break;
                        case QUEEN: simPieces.add(new Queen(currentColor, activePiece.col, activePiece.row)); break;
                        default: break;
                    }
                    simPieces.remove(activePiece.getIndex());
                    copyPieces(simPieces, pieces);
                    activePiece = null;
                    promotion = false;
                    changePlayer();
                }
            }
        }
    }


	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		//BOARD
		board.draw(g2);
		//Pieces
		for(Piece p : simPieces) {
			p.draw(g2);
		}

	    if(activePiece != null && mouse.moved && promotion == false && gameover == false && stalemate == false) {
	        activePiece.x = mouse.x - Board.HALF_SQAURE_SIZE;
	        activePiece.y = mouse.y - Board.HALF_SQAURE_SIZE;
	        activePiece.col = activePiece.getCol(activePiece.x);
	        activePiece.row = activePiece.getRow(activePiece.y);
	    }
		if(activePiece != null) {
			if(canMove) {
				if(isIllegal(activePiece) || opponentCanCaptureKIng()) {
					g2.setColor(Color.RED);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
					g2.fillRect(activePiece.col*Board.SQUARE_SIZE, activePiece.row*Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
					//Draw piece again or it would be hidden by colored square or board
					activePiece.draw(g2);
				}
				else {
					g2.setColor(Color.blue);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
					g2.fillRect(activePiece.col*Board.SQUARE_SIZE, activePiece.row*Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
					//Draw piece again or it would be hidden by colored square or board
					activePiece.draw(g2);
				}
	
			}

		}
		
		//Status Message
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setFont(new Font("Book Antiqua", Font.PLAIN, 40));
		g2.setColor(Color.white);
		
		if(promotion) {
			g2.drawString("Promote to :", 870, 150);
			for(Piece piece : promotePieces) {
				g2.drawImage(piece.image, piece.getX(piece.col), piece.getY(piece.row), Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
			}
		}
		else {
			if(currentColor == WHITE) {
				g2.drawString("White's Turn", 870 ,750);
				if(checkingP != null && checkingP.color == BLACK) {
					g2.setColor(Color.red);
					g2.drawString("The King", 870, 550);
					g2.drawString("Is In Check", 870, 600);
				}
			}
			else {
				g2.drawString("Black's Turn", 870, 50);
				if(checkingP != null && checkingP.color == WHITE) {
					g2.setColor(Color.red);
					g2.drawString("The King", 870, 150);
					g2.drawString("Is In Check", 870, 200);
				}
			}
			}
		if(gameover) {
			String s= "";
			if(currentColor == WHITE) {
				s = "White Wins";				
			}
			else {
				s = "Black Wins"; 
			}
			g2.setFont(new Font("Arial", Font.PLAIN, 90));
			g2.setColor(Color.green);
			g2.drawString(s, 200, 420);
		}
		if(stalemate) {
			String s = "StaleMate";
			g2.setFont(new Font("Arial", Font.PLAIN, 90));
			g2.setColor(Color.cyan);
			g2.drawString(s, 200, 420);
		}

		}
	
		
   	
	private static final long serialVersionUID = 1L;

}
