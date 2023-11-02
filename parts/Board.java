package parts;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Board extends JPanel {

    private List<Piece> pieces = new ArrayList<>();

    private Image background;

    private EndGameListener endGameListener;

    private TurnChangeListener turnChangeListener;

    private int turn;

    private long mouseListenerTime = 0;

    public Board() {
        enableEvents(MouseEvent.MOUSE_PRESSED);
        setBounds(200,100,400,400);
        try {
            background = ImageIO.read(new File("board.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Board(List<Piece> pieces) {
        setBounds(200,150,400,400);
        try {
            background = ImageIO.read(new File("board.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.pieces = pieces;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.drawImage(background, 0 ,0 ,this);
        for(int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                if(pieces.get(getPlace(i,j)).getColor() == Piece.PieceColor.BLACK || pieces.get(getPlace(i,j)).getColor() == Piece.PieceColor.WHITE) {
                    g.drawImage(pieces.get(getPlace(i,j)).getPieceImage(), (i-1)*50,(j-1)*50,this);
                    //g.drawString(String.valueOf(getPieceColor(i,j)),(i-1)*50,(j)*50);
                }
            }
        }
    }

    private void repaintBoard() {
        this.repaint();
    }

    @Override
    protected void processMouseEvent(MouseEvent e) {
        super.processMouseEvent(e);
        if(e.getID() == MouseEvent.MOUSE_PRESSED) {
            if(System.currentTimeMillis() - mouseListenerTime > 200) {
                mouseListenerTime = System.currentTimeMillis();
                for(int i = 0; i <= 400; i += 50) {
                    for(int j = 0; j <= 400; j += 50) {
                        if((i <= e.getX() && e.getX() < (i+50)) && (j <= e.getY() && e.getY() < (j+50))) {
                            int x = (i / 50) + 1, y = (j/ 50) + 1;
                            putPiece(turn, x, y);
                        }
                    }
                }

            }
        }
    }

    public void init() {
        turn = Piece.PieceColor.BLACK;
        for(int i = 0; i < 100; i++) {
            pieces.add(new Piece(Piece.PieceColor.WALL));
        }

        for(int i = 1; i < 9; i++) {
            for(int j = 1; j < 9; j++){
                pieces.set(getPlace(i,j), new Piece(Piece.PieceColor.NO));
            }
        }

        pieces.set(getPlace(4, 4), new Piece(Piece.PieceColor.WHITE));
        pieces.set(getPlace(5, 4), new Piece(Piece.PieceColor.BLACK));
        pieces.set(getPlace(4, 5), new Piece(Piece.PieceColor.BLACK));
        pieces.set(getPlace(5, 5), new Piece(Piece.PieceColor.WHITE));
    }

    private void putPiece(int color, int x, int y) {
        if(!isPutPiece(color, x, y)) return;
        int another = getPieceColorMirror(color);
        pieces.set(getPlace(x,y), new Piece(color));
        flipPiece(color,x,y);
        repaintBoard();
        if (!isContinueGame(another)) {
            if(!isContinueGame(color)) endGameListener.onEnd(pieces);
            else {
                turn = color;
                String s;
                if(color == Piece.PieceColor.BLACK) s = "白";
                else s = "黒";
                JOptionPane.showMessageDialog(this, s + "のターンはパスです", "GameSystem", JOptionPane.INFORMATION_MESSAGE);
                turnChangeListener.onTurnChange();
            }
            return;
        }
        turn = another;
        turnChangeListener.onTurnChange();
    }

    private boolean isPutPiece(int color, int x, int y) {
        int another = getPieceColorMirror(color);
        if(getPieceColor(x, y) != Piece.PieceColor.NO) return false;
        for(int i = -1; i <= 1; i++) {
            for(int j = -1; j <= 1; j++) {
                if(getPieceColor(x+i,y+j) == another) {
                    int _x = x + i;
                    int _y = y + j;
                    while (getPieceColor(_x, _y) == another) {
                        _x += i;
                        _y += j;
                    }
                    if(getPieceColor(_x, _y) == color) {
                        return true;
                    }
                 }
            }
        }
        return false;
    }

    private boolean isContinueGame(int color) {
        for(int i = 1; i < 9; i++) {
            for(int j = 1; j < 9; j++){
                if(getPieceColor(i,j) == Piece.PieceColor.NO) {
                    if(isPutPiece(color, i, j)) return true;
                }
            }
        }
        return false;
    }

    private void flipPiece(int color, int x, int y){
        int another = getPieceColorMirror(color);

        for(int i = -1; i <= 1; i++) {
            for(int j = -1; j <= 1; j++ ) {
                if(getPieceColor(x+i,y+j) == another) {
                    flipPieceLine(color, x+i, y+j, i,j);
                }
            }
        }
    }

    private void flipPieceLine(int color, int x, int y, int i, int j) {
        int another = getPieceColorMirror(color);
        while (getPieceColor(x, y) == another) {
            x += i;
            y += j;
        }
        if(getPieceColor(x,y) != color) {
            return;
        }
        x -= i;
        y -= j;
        while (getPieceColor(x,y) == another) {
            pieces.set(getPlace(x, y), new Piece(color));
            x -= i;
            y -= j;
        }
    }

    private int getPieceColorMirror(int color) {
        if(color == Piece.PieceColor.BLACK) {
            return Piece.PieceColor.WHITE;
        } else if (color == Piece.PieceColor.WHITE) {
            return Piece.PieceColor.BLACK;
        }
        return Piece.PieceColor.NO;
    }


    private int getPieceColor(int x, int y) {
        return pieces.get(getPlace(x,y)).getColor();
    }

    private int getPlace(int x, int y) {
        return x+y*10;
    }

    public int getTurn() {
        return turn;
    }

    public void setEndGameListener(EndGameListener endGameListener) {
        this.endGameListener = endGameListener;
    }

    public void setTurnChangeListener(TurnChangeListener turnChangeListener) {
        this.turnChangeListener = turnChangeListener;
    }


    public interface TurnChangeListener {
        void onTurnChange();
    }

    public interface EndGameListener {
        void onEnd(List<Piece> pieces);
    }
}
