package Tetris;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import Tetris.Shape.Tetrominoes;

/**
 * 
 * W klasie Board tworzymy plansze na ktorej jest rysowana logika gry
 * 
 * 
 * @author Biru
 *
 */
public class Board extends JPanel implements ActionListener {      // JPanel narzedzie do rysowania   Board zawiera logike gry



    final int BoardWidth = 12;
    final int BoardHeight = 24;

    Timer timer;
    boolean isFallingFinished = false;
    boolean isStarted = false;
    boolean isPaused = false;
    int numLinesRemoved = 0;
    int curX = 0;
    int curY = 0;
    JLabel statusbar;
    Shape curPiece;
    Tetrominoes[] board;

/** Konstruktor
 * 
 * @param parent klasy Tetris
 */

    public Board(Tetris parent) {

       setFocusable(true);
       curPiece = new Shape();
       timer = new Timer(450, this);     // timer mówi co ile milisekund nastepuje jedno zdarzenie, w naszym wypadku 450
       timer.start(); 

       statusbar =  parent.getStatusBar();
       board = new Tetrominoes[BoardWidth * BoardHeight];
       addKeyListener(new TAdapter());
       clearBoard();  
    }

    /**
     * Funkcja wykonujaca akcje spadniecia klocka
     */
    public void actionPerformed(ActionEvent e) {  
        if (isFallingFinished) {
            isFallingFinished = false;
            newPiece();
        } else {
            oneLineDown();
        }
    }


    int squareWidth() { return (int) getSize().getWidth() / BoardWidth; }
    int squareHeight() { return (int) getSize().getHeight() / BoardHeight; }
    Tetrominoes shapeAt(int x, int y) { return board[(y * BoardWidth) + x]; }          


    /**
     * Funkcja startujaca gre, gdy tylko oknko pojawi sie na ekranie
     */
    public void start()
    {
        if (isPaused)
            return;

        isStarted = true;
        isFallingFinished = false;
        numLinesRemoved = 0;
        clearBoard();

        newPiece();
        timer.start();
    }
/**
 * Funkcja pauzujaca gre
 */
    private void pause()
    {
        if (!isStarted)
            return;

        isPaused = !isPaused;
        if (isPaused) {
            timer.stop();
            statusbar.setText("paused");
        } else {
            timer.start();
            statusbar.setText(String.valueOf(numLinesRemoved));
        }
        repaint();
    }

    /**
     * Funkcja paint rysuje wszystkie obiekty na planszy
     */
    public void paint(Graphics g)                 
    { 
        super.paint(g);

        Dimension size = getSize();
        int boardTop = (int) size.getHeight() - BoardHeight * squareHeight();


        for (int i = 0; i < BoardHeight; ++i) {
            for (int j = 0; j < BoardWidth; ++j) {
                Tetrominoes shape = shapeAt(j, BoardHeight - i - 1);
                if (shape != Tetrominoes.NoShape)
                    drawSquare(g, 0 + j * squareWidth(),
                               boardTop + i * squareHeight(), shape);
            }
        }

        if (curPiece.getShape() != Tetrominoes.NoShape) {
            for (int i = 0; i < 4; ++i) {
                int x = curX + curPiece.x(i);
                int y = curY - curPiece.y(i);
                drawSquare(g, 0 + x * squareWidth(),
                           boardTop + (BoardHeight - y - 1) * squareHeight(),
                           curPiece.getShape());
            }
        }
    }
    
    
    /**
     * Funkcja po nacisnieciu spacji zdropuje klocek na sam doł lub do najbliższego klocka
     */
    
    private void dropDown()        
    {
        int newY = curY;
        while (newY > 0) {
            if (!tryMove(curPiece, curX, newY - 1))
                break;
            --newY;
        }
        pieceDropped();
    }

    private void oneLineDown()
    {
        if (!tryMove(curPiece, curX, curY - 1))
            pieceDropped();
    }

/**
 * Funkcja wypelnia płytę pustymi plikami NoSpapes. To jest pozniej uzywane w wykrywaniu kolizji.
 */
    private void clearBoard()   
    {
        for (int i = 0; i < BoardHeight * BoardWidth; ++i)
            board[i] = Tetrominoes.NoShape;
    }

    /**
     *  Funkcja umieszcze spadajacy elemnt w tablicy
     */
    private void pieceDropped()
    {
        for (int i = 0; i < 4; ++i) {
            int x = curX + curPiece.x(i);
            int y = curY - curPiece.y(i);
            board[(y * BoardWidth) + x] = curPiece.getShape();
        }

        removeFullLines();

        if (!isFallingFinished)
            newPiece();
    }

    /**
     *  Funkcja tworzaca nowy klocek, nastepnie obliczamy polozenie curX i curY, jelsi nie mozemy przejsc na poczatkowe pozycje, gra konczy sie
     */
   
    public void newPiece(){
		curPiece.setRandomShape();
		curX = BoardWidth / 2 + 1;
		curY = BoardHeight - 1 + curPiece.minY();
		
		if(!tryMove(curPiece, curX, curY -1)){
			curPiece.setShape(Tetrominoes.NoShape);
			timer.stop();
			isStarted = false;
			if(numLinesRemoved == 1){
				JOptionPane.showMessageDialog(Board.this, "Koniec Gry! Twoj wynik wyniosl " + numLinesRemoved + " punkt!", "Tetris", JOptionPane.INFORMATION_MESSAGE);}
			else if(numLinesRemoved == 2 || numLinesRemoved == 3 || numLinesRemoved == 4){
				JOptionPane.showMessageDialog(Board.this, "Koniec Gry! Twoj wynik wyniosl " + numLinesRemoved + " punkty!", "Tetris", JOptionPane.INFORMATION_MESSAGE);
				}
			else JOptionPane.showMessageDialog(Board.this, "Koniec Gry! Twoj wynik wyniosl " + numLinesRemoved + " punktow!", "Tetris", JOptionPane.INFORMATION_MESSAGE);
			System.exit(0);
		}
    }
    

    /**
     *  próbuje przesunąć kawałek tetrisa. Metoda zwraca wartość false, jeśli osiągnęła granice planszy lub sąsiaduje z już spadającymi kawałkami tetrisa.
     * @param newPiece kawałek ktory ma zostac przesuniety
     * @param newX
     * @param newY
     * @return
     */
    private boolean tryMove(Shape newPiece, int newX, int newY)
    {
        for (int i = 0; i < 4; ++i) {
            int x = newX + newPiece.x(i);
            int y = newY - newPiece.y(i);
            if (x < 0 || x >= BoardWidth || y < 0 || y >= BoardHeight)
                return false;
            if (shapeAt(x, y) != Tetrominoes.NoShape)
                return false;
        }

        curPiece = newPiece;
        curX = newX;
        curY = newY;
        repaint();
        return true;
    }
/**
 * Funkcja sprawdzająca czy mozemy usunac pelna linie w planszy
 */
    private void removeFullLines()
    {
        int numFullLines = 0;

        for (int i = BoardHeight - 1; i >= 0; --i) {
            boolean lineIsFull = true;

            for (int j = 0; j < BoardWidth; ++j) {
                if (shapeAt(j, i) == Tetrominoes.NoShape) {
                    lineIsFull = false;
                    break;
                }
            }

            if (lineIsFull) {
                ++numFullLines;
                for (int k = i; k < BoardHeight - 1; ++k) {
                    for (int j = 0; j < BoardWidth; ++j)
                         board[(k * BoardWidth) + j] = shapeAt(j, k + 1);
                }
            }
        }

        if (numFullLines > 0) {
            numLinesRemoved += numFullLines;
            statusbar.setText(String.valueOf(numLinesRemoved));
            isFallingFinished = true;
            curPiece.setShape(Tetrominoes.NoShape);
            repaint();
        }
     }

    /**
     * Kazdy kawałek Tetris ma cztery kwadraty. Kazdy z kwadratow jest rysowany metoda drawSquare (). Tetrominoesy maja rozne kolory.
     * @param g
     * @param x
     * @param y
     * @param shape
     */
    private void drawSquare(Graphics g, int x, int y, Tetrominoes shape){
		
    	Color colors[] = { new Color(0, 0, 0), new Color(111, 44, 116), 
                new Color(24, 228, 14), new Color(0, 0, 225), 
                new Color(255, 0, 128), new Color(236, 236, 0), 
                new Color(0, 185, 185), new Color(255, 0, 0)
    	 };
    	
    	Color color = colors[shape.ordinal()];
		
		
		g.setColor(color);
		g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);
	    g.setColor(color.darker());
	}
    
    
   /**
    * Kontrolujemy grę przy użyciu klawiatury. Mechanizm sterowania jest zaimplementowany za pomocą KeyAdaptera.
    * Jest to klasa wewnetrzna, która zastępuje metode keyPressed ().
    *
    */

    class TAdapter extends KeyAdapter {
         public void keyPressed(KeyEvent e) {

             if (!isStarted || curPiece.getShape() == Tetrominoes.NoShape) {  
                 return;
             }

             int keycode = e.getKeyCode();

             if (keycode == 'p' || keycode == 'P') {
                 pause();
                 return;
             }

             if (isPaused)
                 return;

             switch (keycode) {
             case KeyEvent.VK_LEFT:
                 tryMove(curPiece, curX - 1, curY);
                 break;
             case KeyEvent.VK_RIGHT:
                 tryMove(curPiece, curX + 1, curY);
                 break;
             case KeyEvent.VK_DOWN:
                 tryMove(curPiece.rotateRight(), curX, curY);
                 break;
             case KeyEvent.VK_UP:
                 tryMove(curPiece.rotateLeft(), curX, curY);
                 break;
             case KeyEvent.VK_SPACE:
                 dropDown();
                 break;
             case 'd':
                 oneLineDown();
                 break;
             case 'D':
                 oneLineDown();
                 break;
             }

         }
     }
}
