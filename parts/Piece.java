package parts;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Piece{
    public static class PieceColor {
        public static final int BLACK = 0;
        public static final int WHITE = 1;
        public static final int NO = 2;
        public static final int WALL = 3;
    }

    private BufferedImage pieceImage = null;

    private int color; //0 = çï ,1 = îí

    public Piece(int color){
        this.color = color;
        try {
            if(color == PieceColor.BLACK) pieceImage = ImageIO.read(new File("piece_black.png"));
            else if(color == PieceColor.WHITE) pieceImage = ImageIO.read(new File("piece_white.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        try {
            if(color == PieceColor.BLACK) pieceImage = ImageIO.read(new File("piece_black.png"));
            else if(color == PieceColor.WHITE) pieceImage = ImageIO.read(new File("piece_white.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public BufferedImage getPieceImage() {
        return pieceImage;
    }
}
