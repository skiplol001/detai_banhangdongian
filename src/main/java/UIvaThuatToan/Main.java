
package UIvaThuatToan;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameUI game = new GameUI();
            game.setVisible(true);
        });
    }
}