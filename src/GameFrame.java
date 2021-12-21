import javax.swing.*;

public class GameFrame extends JFrame {
    GameFrame(){
        this.add(new GamePanel());
        this.setTitle("Snake");
        this.setResizable(false);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }
}
