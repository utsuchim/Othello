import panel.GamePanel;
import panel.MainMenuPanel;
import panel.ResultPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class MainWindow extends JFrame {

    public enum ScreenMode {
        MainMenu,
        Game,
        Result,
    }

    public static final int WIDTH = 800;
    public static final int HEIGHT = 800;

    private final CardLayout cardLayout = new CardLayout();

    MainMenuPanel mainMenuPanel;
    GamePanel gamePanel;
    ResultPanel resultPanel;

    MainWindow() {
        this.setTitle("ƒIƒZƒ");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(cardLayout);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        pack();
        setLocationRelativeTo(null);
    }

    private void setUpScreen() {
        mainMenuPanel = new MainMenuPanel();
        mainMenuPanel.setup();
        mainMenuPanel.setStartGameListener(() -> {
            setScreenMode(ScreenMode.Game);
        });
        this.add(mainMenuPanel, "MainMenu");

        gamePanel = new GamePanel();
        gamePanel.init();
        gamePanel.setEndGameListener((pieces) -> {
            resultPanel.setPieces(pieces);
            setScreenMode(ScreenMode.Result);
        });
        this.add(gamePanel, "Game");


        resultPanel = new ResultPanel();
        resultPanel.setup();
        resultPanel.setButtonListener(screenMode -> {
            if(Objects.equals(screenMode, "Game")) {
                setUpScreen();
                setScreenMode(ScreenMode.Game);
            } else if(Objects.equals(screenMode, "MainMenu")) {
                setUpScreen();
                setScreenMode(ScreenMode.MainMenu);
            }
        });
        this.add(resultPanel, "Result");
        pack();
    }

    public void setScreenMode(ScreenMode screenMode) {
        switch (screenMode) {
            case MainMenu -> {
                cardLayout.show(getContentPane(), "MainMenu");
                mainMenuPanel.requestFocus();
            }
            case Game -> {
                cardLayout.show(getContentPane(), "Game");
                gamePanel.requestFocus();
            }
            case Result -> {
                cardLayout.show(getContentPane(), "Result");
                resultPanel.requestFocus();
            }
        }
    }

    public static void main(String[] args) {
        MainWindow mainWindow = new MainWindow();
        mainWindow.setUpScreen();
        mainWindow.setScreenMode(ScreenMode.MainMenu);
        mainWindow.setVisible(true);
    }
}

