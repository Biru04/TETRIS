package Tetris;

import java.util.Random;
import java.lang.Math;


/**
 * 
 * Klasa Shape dostarcza informacji o kawałku Tetrisa
 *
 */
public class Shape {
	
	
    enum Tetrominoes { NoShape, SShape, ZShape, LineShape,              // enum pozwala on na definiowane wybranego zbioru możliwych wartości. 
               TShape, SquareShape, LShape, MirroredLShape };            // 4 współrzedne takich pounktów, które po połączeniu dadza nam Tetriminosy :D
               
               
    private Tetrominoes pieceShape;
    private int coords[][];
    private int coordsTable[][][] ;
    
    /**
     * Konstruktor, Tablica coordsow zawiera rzeczywiste wspolrzędne kawalka Tetrisa.
     */

    public Shape() {                

        coords = new int[4][2];
        setShape(Tetrominoes.NoShape);

    }

    /**
     * Funkcja Tworzaca poszczegolne klocki zwane Tetrominoesami: 4 współrzedne takich pounktów, które po połączeniu dadza nam Tetriminosy :D
     * @param shape ten jeden kwadracik z ktorego tworzony jest Tetrominoes
     */
    public void setShape(Tetrominoes shape) {                 // zmienna funkcji shape typu Tetromino

         coordsTable = new int[][][] {
            { { 0, 0 },   { 0, 0 },   { 0, 0 },   { 0, 0 } },    //   NoShape
            { { 0, -1 },  { 0, 0 },   { -1, 0 },  { -1, 1 } },   //   SShape
            { { 0, -1 },  { 0, 0 },   { 1, 0 },   { 1, 1 } },    //   ZShape
            { { 0, -1 },  { 0, 0 },   { 0, 1 },   { 0, 2 } }, 	 //   LineShape
            { { -1, 0 },  { 0, 0 },   { 1, 0 },   { 0, 1 } }, 	 //   TShape
            { { 0, 0 },   { 1, 0 },   { 0, 1 },   { 1, 1 } },	 //   SquareShape
            { { -1, -1 }, { 0, -1 },  { 0, 0 },   { 0, 1 } },    //   MirroredLShape
            { { 1, -1 },  { 0, -1 },  { 0, 0 },   { 0, 1 } } 	 //   LShape
        };

        for (int i = 0; i < 4 ; i++) {
            for (int j = 0; j < 2; ++j) {                                         // ++j -> j = j+1
                coords[i][j] = coordsTable[shape.ordinal()][i][j];
            }
        }
        pieceShape = shape;
 
    }

    private void setX(int index, int x) { coords[index][0] = x; }                 	
    private void setY(int index, int y) { coords[index][1] = y; }
    public int x(int index) { return coords[index][0]; }
    public int y(int index) { return coords[index][1]; }
    public Tetrominoes getShape()  { return pieceShape; }

    
    /**
     * Funkcja losujaca dany Tetrmionoes
     */
    public void setRandomShape()
    {
        Random r = new Random();
        int x = Math.abs(r.nextInt()) % 7 + 1;
        Tetrominoes[] values = Tetrominoes.values(); 
        setShape(values[x]);
    }

    /**
     * 
     * @return
     */
    public int minX()
    {
      int m = coords[0][0];
      for (int i=0; i < 4; i++) {
          m = Math.min(m, coords[i][0]);
      }
      return m;
    }

/**
 * 
 * 
 * @return
 */
    public int minY() 
    {
      int m = coords[0][1];
      for (int i=0; i < 4; i++) {
          m = Math.min(m, coords[i][1]);
      }
      return m;
    }

    /**
     * Funkcja rotujaca nasz klocek w lewo, chyba że jest kwadratem
     * @return   zwraca obrocony klocek
     */
    public Shape rotateLeft() 
    {
        if (pieceShape == Tetrominoes.SquareShape)
            return this;

        Shape result = new Shape();
        result.pieceShape = pieceShape;

        for (int i = 0; i < 4; ++i) {
            result.setX(i, y(i));
            result.setY(i, -x(i));
        }
        return result;
    }

    /**
     * Funkcja rotujaca nasz klocek w prawo, chyab że jest kwadratem
     * @return   zwraca obrocony klocek
     */
    public Shape rotateRight()
    {
        if (pieceShape == Tetrominoes.SquareShape)
            return this;

        Shape result = new Shape();
        result.pieceShape = pieceShape;

        for (int i = 0; i < 4; ++i) {
            result.setX(i, -y(i));
            result.setY(i, x(i));
        }
        return result;
    }
}
