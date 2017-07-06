package Tetris;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;                                           // TU CHYBA WSZYSTKO

/**
 * 
 * W klasie Tetris ustawiamy gre
 *
 */
public class Tetris extends JFrame {

    JLabel statusbar;

    /** 
     *  Konstruktor tworzacy plansze
     */

    public Tetris() {

        statusbar = new JLabel(" 0");
        add(statusbar, BorderLayout.SOUTH);
        Board board = new Board(this);
        add(board);
        board.start();

        setSize(250, 500);
        setTitle("Tetris");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
   }

    /**
     * Tworzy pasek na ktorym wyswietlany jest wynik
     * 
     * @return  wlasnie ten pasek
     */
    
   public JLabel getStatusBar() {
       return statusbar;
   }

   /**
    * Funkcja main tworzy nowa gre
    * 
    * @param args
    */
    public static void main(String[] args) {

        Tetris game = new Tetris();
        game.setLocationRelativeTo(null);
        game.setVisible(true);

    } 
}
