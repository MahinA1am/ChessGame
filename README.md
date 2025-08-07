# ♟️ Java Chess Game (No Libraries Used)

This is a classic two-player **Chess game** built entirely from scratch in **Java**, using only **Java Swing** for the GUI. No external chess engines or libraries were used — all logic, rules, and board mechanics are custom-implemented.

---
---

## 🧩 Features

- Two-player mode (no AI — perfect for local matches)
- Fully functional chessboard with all standard rules
- Drag-and-drop piece movement
- All pieces implemented: King, Queen, Rook, Bishop, Knight, Pawn
- Legal move validation
- Java Swing GUI
- Custom piece icons (located in `res/pieces/`)

---

## 📁 Project Structure
Chess
├── bin/ # Compiled .class files
├── res/pieces/ # Piece icons (PNG format)
│ ├── Bbishop.png
│ ├── ...
│ └── Wrook.png
├── src/
│ ├── main/
│ │ ├── Board.java
│ │ ├── GamePanel.java
│ │ ├── Main.java
│ │ ├── Mouse.java
│ │ └── Type.java
│ ├── piece/
│ │ ├── Bishop.java
│ │ ├── King.java
│ │ ├── Knight.java
│ │ ├── Pawn.java
│ │ ├── Piece.java
│ │ ├── Queen.java
│ │ └── Rook.java
│ └── module-info.java


---

## 🚀 How to Run

### ✅ Prerequisites
- JDK 17+ installed
- Java compiler (`javac`) and runtime (`java`) added to system PATH

---

### 🛠️ Steps

1. **Clone the repository:**
   ```bash
   git clone https://github.com/MahinA1am/ChessGame.git
   cd Chess
   javac -d bin -sourcepath src src/main/Main.java
   java -cp bin main.Main

⚙️ Notes
Make sure the res folder is in the correct relative path when running, as images are loaded from res/pieces/.

If you’re using an IDE (like IntelliJ or Eclipse), set the res folder as a resources directory or adjust the path in the code accordingly.

📌 To-Do / Ideas for Future
Add single-player mode with AI (e.g., Minimax or Stockfish integration)

Add castling, en passant, promotion UI

Highlight legal moves

Move history and undo feature

Game timer

🧑‍💻 Author
Mahin Alam
📧 
🧠 Made with Java, logic, and no shortcuts



