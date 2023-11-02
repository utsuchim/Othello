package panel;

import parts.Board;
import parts.Piece;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GamePanel extends JPanel {

    private Board board;

    private EndGameListener endGameListener;

    public GamePanel() {
        super();
        setLayout(null);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setFont(new Font (Font.SANS_SERIF, Font.BOLD, 40));
        String color = null;
        if(board.getTurn() == Piece.PieceColor.BLACK) {
            color = "•";
        } else if(board.getTurn() == Piece.PieceColor.WHITE){
            color = "”’";
        }
        g.drawString(color + "‚Ìƒ^[ƒ“",300,70);
    }

    public void init() {
        board = new Board();
        board.init();
        board.setEndGameListener((pieces) -> endGameListener.onStop(pieces));
        board.setTurnChangeListener(super::repaint);
        add(board);
    }

    public void setEndGameListener(EndGameListener endGameListener) {
        this.endGameListener = endGameListener;
    }

    public interface EndGameListener {
        void onStop(List<Piece> pieces);
    }
}
