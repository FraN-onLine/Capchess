
import javax.swing.*;
import java.awt.*;


public class Load extends JFrame {

    private JProgressBar progressBar = new JProgressBar();
    
    //The loading screen graphics
    Load(){
        new JFrame();
        setSize(700,750);
        ImageIcon icon = new ImageIcon(getClass().getResource("/resources/white_pawn_a.png"));
        Image img = icon.getImage();
        setIconImage(img);
        setVisible(true);
        setTitle("Capchess");
        getContentPane().setBackground(Color.WHITE);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        LoadPanel loadpanel = new LoadPanel();
        loadpanel.setLayout(null);

        
        
        progressBar.setMinimum(0);
        progressBar.setMaximum(99);
        progressBar.setValue(0);
        progressBar.setForeground(new Color(0xC4E499));
        progressBar.setBounds(getWidth() / 4 - 3, 304 ,getWidth() / 2 + 3,20);
        progressBar.setBorderPainted(false);
        add(loadpanel);
        loadpanel.add(progressBar);

        for (int i = 0; i <= 33; i++) {
            progressBar.setValue(i * 3);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean checkProgress(){
       if(progressBar.getValue() == progressBar.getMaximum()){
        return true;
       } else
       return false;
    }

   }
