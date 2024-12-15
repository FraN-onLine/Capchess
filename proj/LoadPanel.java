
import javax.swing.*;
import java.awt.*;

public class LoadPanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            drawPatternBackground(g);

            g.setColor(Color.BLACK);
            g.setFont(new Font("ELEPHANT", Font.BOLD, 26));
            g.drawString("CAPCHESS", getWidth() / 2 - 69, getHeight() / 3);

            g.setFont(new Font("Calibri Light", Font.BOLD, 15));
            g.drawString("Chess for dummies", getWidth() / 2 - 50, getHeight() / 2);

        }

        private void drawPatternBackground(Graphics g) {
            int width = getWidth();
            int height = getHeight();
            int tileSize = 110;
    
            for (int y = 0; y < height; y += tileSize) {
                for (int x = 0; x < width; x += tileSize) {
                    if ((x + y) % (2 * tileSize) == 0) {
                        g.setColor(new Color(0xC4E499));
                    } else {
                        g.setColor(new Color(0xfff9e7));
                    }
                    g.fillRect(x, y, tileSize, tileSize);
                }
            }
        }
    
    }