package panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenuPanel extends JPanel implements ActionListener {

    JButton startGameButton;

    StartGameListener startGameListener;

    public MainMenuPanel(){
        super();
        setLayout(null);
        setPreferredSize(new Dimension(800,800));
    }

    public void setup() {
        startGameButton = new JButton("Game Start");
        startGameButton.setBounds(300, 300, 200, 50);
        startGameButton.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 20));
        startGameButton.setAlignmentX(CENTER_ALIGNMENT);
        startGameButton.addActionListener(this);
        add(startGameButton);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD , 150));
        g.drawString("ÉIÉZÉç",180, 200);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(startGameButton)) {
            startGameListener.onClickButton();
        }
    }

    public void setStartGameListener(StartGameListener startGameListener) {
        this.startGameListener = startGameListener;
    }

    public interface StartGameListener {
        void onClickButton();
    }
}
