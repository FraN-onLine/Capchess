import javax.swing.*;
import java.awt.*;


public class CapturedPieces extends JFrame{

    private JTextArea capturedPiecesArea;

    public CapturedPieces(BasilioCertifiedLinkedList<Piece> capturedPieces) {

        setTitle("Move Menu");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 600);
        setLayout(new BorderLayout());

        capturedPiecesArea = new JTextArea();
        capturedPiecesArea.setEditable(false);
        JScrollPane capturedScrollPane = new JScrollPane(capturedPiecesArea);

        // Panel for captured pieces
        JPanel capturedPanel = new JPanel(new BorderLayout());
        capturedPanel.add(new JLabel("Captured Pieces"), BorderLayout.NORTH);
        capturedPanel.add(capturedScrollPane, BorderLayout.CENTER);




        StringBuilder capturedText = new StringBuilder();
        for (int i = 1; i <= capturedPieces.size(); i++) { //this iterates over the linkedlist
            if (capturedPieces.getatPos(i) != null) {
                capturedText.append(capturedPieces.getatPos(i).getColor())
                            .append(" ")
                            .append(capturedPieces.getatPos(i).getType())
                            .append("\n");
            }
        }

        // Display captured pieces in the JTextArea
        capturedPiecesArea.setText(capturedText.toString());

        add(capturedPanel, BorderLayout.CENTER);
        setVisible(true);
    }

}
