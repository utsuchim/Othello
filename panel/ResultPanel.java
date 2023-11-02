package panel;

import parts.Board;
import parts.Piece;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ResultPanel extends JPanel {

    private Board board;
    private int countBlack;
    private int countWhite;

    private ButtonListener buttonListener;

    public ResultPanel() {
        super();
        setLayout(null);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setFont(new Font (Font.SANS_SERIF, Font.BOLD, 40));
        String s;
        if(countBlack == countWhite) {
            s = "引き分け!";
        } else if(countBlack > countWhite) {
            s = "黒の勝ち!";
        } else {
            s = "白の勝ち";
        }
        g.drawString(s,300,80);
        g.setFont(new Font (Font.SANS_SERIF, Font.BOLD, 20));
        g.drawString("黒: " + countBlack, 200, 130);
        g.drawString("白: " + countWhite, 540, 130);
    }

    public void setup() {
        JButton againButton = new JButton("もう一度");
        againButton.setFont(new Font(Font.SERIF, Font.BOLD, 20));
        againButton.setBounds(100, 650,200, 50);
        againButton.addActionListener(e -> buttonListener.onClick("Game"));
        add(againButton);

        JButton mainMenuButton = new JButton("メインメニューへ");
        mainMenuButton.setFont(new Font(Font.SERIF, Font.BOLD, 20));
        mainMenuButton.setBounds(500, 650,200, 50);
        mainMenuButton.addActionListener(e -> buttonListener.onClick("MainMenu"));
        add(mainMenuButton);
    }

    public void setPieces(List<Piece> pieces) {
        List<Piece> resultPieces = new ArrayList<>();
        List<Piece> _pieces = new ArrayList<>();
        for(int i = 0; i < 100; i++) {
            resultPieces.add(new Piece(Piece.PieceColor.WALL));
        }

        for(int i = 1; i < 9; i++) {
            for(int j = 1; j < 9; j++) {
                if(pieces.get(i*10+j).getColor() == Piece.PieceColor.BLACK) {
                    _pieces.add(new Piece(Piece.PieceColor.BLACK));
                    countBlack++;
                }
            }
        }
        for(int i = 1; i < 9; i++) {
            for(int j = 1; j < 9; j++) {
                if(pieces.get(i*10+j).getColor() == Piece.PieceColor.WHITE) {
                    _pieces.add(new Piece(Piece.PieceColor.WHITE));
                    countWhite++;
                }
            }
        }
        int n = 0;
        for(int i = 1; i < 9; i++) {
            if(n >= _pieces.size()) break;
            for(int j = 1; j < 9; j++) {
                if(n >= _pieces.size()) break;
                resultPieces.set(i*10+j, _pieces.get(n));
                n++;
            }
        }
        
        board = new Board(resultPieces);
        add(board);
    }

    public void setButtonListener(ButtonListener buttonListener) {
        this.buttonListener = buttonListener;
    }

    public interface ButtonListener {
        void onClick(String screenMode);
    }
}
