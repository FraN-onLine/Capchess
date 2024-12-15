
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.*;

class Piece {
    public enum Type { KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN }
    public enum Color { WHITE, BLACK }

    private Type type;
    private Color color;
    private ImageIcon icon;
    private static BasilioCertifiedHashmap<String, Image> loadedImages = new BasilioCertifiedHashmap<String, Image>();
    //custom hashmap usage, cache of loaded images

    public Piece(Type type, Color color) {
        this.type = type;
        this.color = color;
        this.icon = loadImage();
    }

    private ImageIcon loadImage() {
        String imagePath;
        if(!Chess.loiChess){
        imagePath = "resources/" + color.name().toLowerCase() + "_" + type.name().toLowerCase() + "_a" + ".png";
        } else {
        imagePath = "resources/" + color.name().toLowerCase() + "_" + type.name().toLowerCase() + "_bb" + ".png";
        }

        if (loadedImages.containsKey(imagePath)) {
            Image scaledImage = loadedImages.get(imagePath);
            return new ImageIcon(scaledImage);
        }

        ImageIcon icon = new ImageIcon(imagePath);
        Image scaledImage = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH); // Adjust size
        loadedImages.put(imagePath, scaledImage);
        return new ImageIcon(scaledImage);
    }

    public Type getType() { return type; }
    public Color getColor() { return color; }
    public ImageIcon getIcon() { return icon; }
}

class Board {

    //some uses of the custom implementation of data structures
    public HashMap<String, Piece> boardMap;
    public BasilioCertifiedStack<HashMap<String, Piece>> boardHistory = new BasilioCertifiedStack<HashMap<String, Piece>>();
    public BasilioCertifiedLinkedList<Piece> capturedPieces = new BasilioCertifiedLinkedList<Piece>();
    

    public Board() {
        boardMap = new HashMap<>();
        initializeBoard();
    }

    //initialize position and board istory
    private void initializeBoard() {
        
        for (int i = 0; i < 8; i++) {
            boardMap.put("6," + i, new Piece(Piece.Type.PAWN, Piece.Color.WHITE));
            boardMap.put("1," + i, new Piece(Piece.Type.PAWN, Piece.Color.BLACK));
        }

        String[] types = {"ROOK", "KNIGHT", "BISHOP", "QUEEN", "KING", "BISHOP", "KNIGHT", "ROOK"};
        for (int i = 0; i < types.length; i++) {
            boardMap.put("7," + i, new Piece(Piece.Type.valueOf(types[i]), Piece.Color.WHITE));
            boardMap.put("0," + i, new Piece(Piece.Type.valueOf(types[i]), Piece.Color.BLACK));
        }

        final HashMap<String, Piece> temp = new HashMap<String, Piece>(boardMap); //saves a state that cannot be altered
        boardHistory.push(temp);
    }

    public Piece getPiece(int row, int col) {
        return boardMap.get(row + "," + col);
    }

    public void movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        Piece piece = boardMap.remove(fromRow + "," + fromCol);
        boardMap.put(toRow + "," + toCol, piece);
    }

    //the boolean just returns true if may capture nangyari
    public boolean movePiece(int fromRow, int fromCol, int toRow, int toCol, boolean canCastle) {
        boolean captured = false;
        Piece piece = boardMap.remove(fromRow + "," + fromCol);
        if (boardMap.containsKey(toRow + "," + toCol)){
            capturedPieces.addatEnd(boardMap.get(toRow + "," + toCol));
            captured = true;
        } else {
            capturedPieces.addatEnd(null);
        }
        boardMap.put(toRow + "," + toCol, piece);
    

        if(canCastle){
            if (piece.getType() == Piece.Type.KING && Math.abs(toCol - fromCol) == 2 && fromRow == toRow) {
                int rookFromCol = (toCol > fromCol) ? 7 : 0; // Determine rook column (kingside/queenside)
                int rookToCol = toCol > fromCol ? toCol - 1 : toCol + 1; // New rook position
                if(boardMap.get(fromRow + "," + rookFromCol).getType() == Piece.Type.ROOK){
                Piece rook = boardMap.remove(fromRow + "," + rookFromCol);
                boardMap.put(fromRow + "," + rookToCol, rook);
                }
            }
         }
         return captured;
    }

    public boolean isEmpty(int row, int col) {
        return !boardMap.containsKey(row + "," + col);
    }

    public boolean isEnemyPiece(int row, int col, Piece.Color color) {
        Piece piece = getPiece(row, col);
        return piece != null && piece.getColor() != color;
    }

    public boolean undoMove(){
        if(boardHistory.size() > 1){
        boardHistory.pop();
        boardMap = new HashMap<String, Piece>(boardHistory.peek());
        return true;
        }
        return false;
    }

}

//the chess gui this is where the frames tiles and the like are
class GUI extends JFrame {
    private JButton[][] tiles;
    private JLabel turnLabel;
    private GameController gameController;

    public GUI(GameController controller) {
        this.gameController = controller;
        setTitle("Capchess");
        setSize(700, 750);
        ImageIcon icon = new ImageIcon("resources/black_pawn_a.png");
        Image img = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        setIconImage(img);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        initializeBoard();
        definePanels();
    }
    

    private void initializeBoard() {
        JPanel boardPanel = new JPanel(new GridLayout(8, 8));
        tiles = new JButton[8][8]; //every tile is a button

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JButton tile = new JButton();
                tile.setBackground((row + col) % 2 == 0 ? Color.WHITE : Color.GRAY);
                tile.setOpaque(true);
                tile.setBorderPainted(false);
                int finalRow = row, finalCol = col;

                tile.addActionListener(new ActionListener() {
                    
                    public void actionPerformed(ActionEvent e) {
                        gameController.handleTileClick(finalRow, finalCol);
                    }
                });

                tiles[row][col] = tile;
                boardPanel.add(tile);
            }
        }

        add(boardPanel, BorderLayout.CENTER);
    }

    private void definePanels() {
        turnLabel = new JLabel("Turn: White", SwingConstants.CENTER);
        turnLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(turnLabel, BorderLayout.NORTH);
        JPanel bottompanel = new JPanel(new GridLayout(1,2));
        JButton undoButton = new JButton("Undo");
        JButton checkCapturedButton = new JButton("Check Captured");
        bottompanel.add(undoButton);
        bottompanel.add(checkCapturedButton);
        add(bottompanel, BorderLayout.SOUTH);

        undoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gameController.undo();
            }
        });

        checkCapturedButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gameController.checkCaptured();
            }
        });

        
    }

    public void updateTile(int row, int col, Piece piece) {
        if (piece != null) {
            tiles[row][col].setIcon(piece.getIcon());
            tiles[row][col].setText(""); // Ensure no text overlap
        } else {
            tiles[row][col].setIcon(null);
            tiles[row][col].setText("");
        }
    }

    public void clearTile(int row, int col) {
        tiles[row][col].setIcon(null);
        tiles[row][col].setText("");
    }

    public void highlightTile(int row, int col, Color color) {
        tiles[row][col].setBackground(color);
    }

    public void resetTileColor(int row, int col) {
        tiles[row][col].setBackground((row + col) % 2 == 0 ? Color.WHITE : Color.GRAY);
    }

    public void updateLabel(String text) {
        turnLabel.setText(text);
    }


}

// GameController Class
class GameController {
    private Board board;
    private GUI gui;
    private boolean isWhiteTurn = true;
    private int selectedRow = -1, selectedCol = -1; //these are erroneous values to check if Piece isnt clicked
    private List<int[]> validMoves = new LinkedList<>();
    private boolean whiteKingMoved = false;
    private boolean blackKingMoved = false;
    private boolean kingInCheck = false;
    public boolean canCastle = false;
    public static boolean gameStarted = false;


    public GameController() {
        board = new Board();
        gui = new GUI(this);
        gui.setVisible(false);
        gui. setResizable(false);
        updateBoard();

    }

    public void handleTileClick(int row, int col) {

        if(!gameStarted) gameStarted = true;
        
        if (selectedRow == -1) { // First click: selecting a piece
            Piece piece = board.getPiece(row, col);
            if (piece != null && ((isWhiteTurn && piece.getColor() == Piece.Color.WHITE) ||
                                  (!isWhiteTurn && piece.getColor() == Piece.Color.BLACK))) {
                selectedRow = row;
                selectedCol = col;
                validMoves = getValidMoves(row, col, piece);
                highlightValidMoves();
            }
        } else { // Second click: attempting to move
            if (isValidMove(row, col)) {
                if(!isWhiteTurn){
                        if(!blackKingMoved){ //if king hasnt moved check if the moved piece is king, this is to invalidate castling once it was moved
                            blackKingMoved = board.getPiece(selectedRow, selectedCol).getType() == Piece.Type.KING;
                    }
                    } else {
                        if(!whiteKingMoved){ //if king hasnt moved check if the moved piece is king, this is to invalidate castling once it was moved
                            whiteKingMoved = board.getPiece(selectedRow, selectedCol).getType() == Piece.Type.KING;
                            }
                    }
                board.movePiece(selectedRow, selectedCol, row, col, canCastle);
                isWhiteTurn = !isWhiteTurn;
                gui.updateLabel("Turn: " + (isWhiteTurn ? "White" : "Black"));
                final HashMap<String, Piece> temp = new HashMap<String, Piece>(board.boardMap); //saves a state that cannot be altered
                board.boardHistory.push(temp);
            }
            resetHighlights();
            selectedRow = -1;
            selectedCol = -1;
            updateBoard();
        }
    }


    //this is called every time we need to change the board in anyway, usually undoing or doing a turn
    private void updateBoard() {

        String[] options = {"Queen", "Rook", "Bishop", "Knight"};

        for(int col = 0; col < 8; col++){
            Piece piece = board.getPiece(0, col);
            if (piece != null){
                if(piece.getColor() == Piece.Color.WHITE && piece.getType() == Piece.Type.PAWN){
                    board.boardMap.remove("0," + col);
                    String choice =  (String) JOptionPane.showInputDialog(gui,(piece.getColor() == Piece.Color.WHITE ? "White" : "Black") + " Pawn Promotion! Choose a piece:","Pawn Promotion",JOptionPane.PLAIN_MESSAGE,null,options,options[0]);
                    board.boardMap.put("0," + col, new Piece(getPieceTypeFromChoice(choice), Piece.Color.WHITE));
                }
            }
            piece = board.getPiece(7, col);
            if (piece != null){
                if(piece.getColor() == Piece.Color.BLACK && piece.getType() == Piece.Type.PAWN){
                    board.boardMap.remove("7," + col);
                    String choice =  (String) JOptionPane.showInputDialog(gui,(piece.getColor() == Piece.Color.WHITE ? "White" : "Black") + " Pawn Promotion! Choose a piece:","Pawn Promotion",JOptionPane.PLAIN_MESSAGE,null,options,options[0]);
                    board.boardMap.put("7," + col, new Piece(getPieceTypeFromChoice(choice), Piece.Color.BLACK));
                }
            }
        }

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPiece(row, col);
                if (piece != null) {
                    gui.updateTile(row, col, piece);
                } else {
                    gui.clearTile(row, col);
                }
                gui.resetTileColor(row, col);
            }
        }

        kingInCheck = false;
    
        // Check if either king is in check
        if (isKingInCheck(Piece.Color.WHITE)) {
            highlightKingInCheck(Piece.Color.WHITE);
        }
        else if (isKingInCheck(Piece.Color.BLACK)) {
            highlightKingInCheck(Piece.Color.BLACK);
        }
        else {
        checkStalemate(isWhiteTurn ? Piece.Color.WHITE : Piece.Color.BLACK);
        }

        canCastle = false;

    }
    
    private Piece.Type getPieceTypeFromChoice(String choice) {
        switch (choice) {
            case "Rook":
                return Piece.Type.ROOK;
            case "Bishop":
                return Piece.Type.BISHOP;
            case "Knight":
                return Piece.Type.KNIGHT;
            default:
                return Piece.Type.QUEEN; // Default to Queen if no valid choice
        }
    }
    

    //returns a list of valid moves
    private List<int[]> getValidMoves(int row, int col, Piece piece) {
        List<int[]> moves = new LinkedList<>();
        Piece.Type type = piece.getType();
        Piece.Color color = piece.getColor();

        switch (type) {
            case PAWN:
                moves.addAll(getPawnMoves(row, col, color));
                break;
            case ROOK:
                moves.addAll(getLinearMoves(row, col, color, 1, 0));
                moves.addAll(getLinearMoves(row, col, color, 0, 1));
                moves.addAll(getLinearMoves(row, col, color, -1, 0));
                moves.addAll(getLinearMoves(row, col, color, 0, -1));
                break;
            case KNIGHT:
                moves.addAll(getKnightMoves(row, col, color));
                break;
            case BISHOP:
                moves.addAll(getLinearMoves(row, col, color, 1, 1));
                moves.addAll(getLinearMoves(row, col, color, 1, -1));
                moves.addAll(getLinearMoves(row, col, color, -1, 1));
                moves.addAll(getLinearMoves(row, col, color, -1, -1));
                break;
            case QUEEN:
                moves.addAll(getLinearMoves(row, col, color, 1, 0));
                moves.addAll(getLinearMoves(row, col, color, 0, 1));
                moves.addAll(getLinearMoves(row, col, color, -1, 0));
                moves.addAll(getLinearMoves(row, col, color, 0, -1));
                moves.addAll(getLinearMoves(row, col, color, 1, 1));
                moves.addAll(getLinearMoves(row, col, color, 1, -1));
                moves.addAll(getLinearMoves(row, col, color, -1, 1));
                moves.addAll(getLinearMoves(row, col, color, -1, -1));
                break;
            case KING:
                moves.addAll(getKingMoves(row, col, color));
                break;
        }
        return moves;
    }

    private List<int[]> getPawnMoves(int row, int col, Piece.Color color) {
        List<int[]> moves = new LinkedList<>();
        int direction = color == Piece.Color.WHITE ? -1 : 1; //white moves upward while black moves downward

        if (board.isEmpty(row + direction, col)) {
            moves.add(new int[]{row + direction, col}); //add one above as a valid move

            //if its in its starting position 2 moves are valid
            if ((color == Piece.Color.WHITE && row == 6) || (color == Piece.Color.BLACK && row == 1)) {
                if (board.isEmpty(row + 2 * direction, col)) {
                    moves.add(new int[]{row + 2 * direction, col});
                }
            }
        }
        if (col > 0 && board.isEnemyPiece(row + direction, col - 1, color)) {
            moves.add(new int[]{row + direction, col - 1});
        }
        if (col < 7 && board.isEnemyPiece(row + direction, col + 1, color)) {
            moves.add(new int[]{row + direction, col + 1});
        }

        return moves;
    }

    private List<int[]> getKnightMoves(int row, int col, Piece.Color color) {
        List<int[]> moves = new LinkedList<>();

        int[][] knightOffsets = {{-2, 1}, {-2, -1}, {2, 1}, {2, -1}, {-1, 2}, {-1, -2}, {1, 2}, {1, -2}};//all possible knight offsets

        for (int[] offset : knightOffsets) {
            int newRow = row + offset[0];
            int newCol = col + offset[1];
            if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8 &&
                (board.isEmpty(newRow, newCol) || board.isEnemyPiece(newRow, newCol, color))) {
                moves.add(new int[]{newRow, newCol});
            }
        }

        return moves;
    }

    private List<int[]> getKingMoves(int row, int col, Piece.Color color) {
        List<int[]> moves = new LinkedList<>();
        int[][] kingOffsets = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

        for (int[] offset : kingOffsets) {
            int newRow = row + offset[0];
            int newCol = col + offset[1];
            if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8 &&
                (board.isEmpty(newRow, newCol) || board.isEnemyPiece(newRow, newCol, color))) {
                moves.add(new int[]{newRow, newCol});
            }
        }

        if (!kingInCheck){
        if (canCastle(color, row, col, true)) { // Kingside Castling
            moves.add(new int[]{row, col + 2});
            canCastle = true;
        }
        if (canCastle(color, row, col, false)) { // Queenside Castling
            moves.add(new int[]{row, col - 2});
            canCastle = true;
        }
         }

        return moves;
    }

    private List<int[]> getLinearMoves(int row, int col, Piece.Color color, int rowDir, int colDir) {
        List<int[]> moves = new LinkedList<>();
        int r = row + rowDir, c = col + colDir;

        while (r >= 0 && r < 8 && c >= 0 && c < 8) {
            if (board.isEmpty(r, c)) {
                moves.add(new int[]{r, c});
            } else if (board.isEnemyPiece(r, c, color)) {
                moves.add(new int[]{r, c});
                break;
            } else {
                break;
            }
            r += rowDir;
            c += colDir;
        }

        return moves;
    }

    //this function basically simulates the move to check if king would not be in check, or if the move is valid
    private boolean isValidMove(int row, int col) {
        boolean moveFound = false;

        for (int[] move : validMoves) {
            if (move[0] == row && move[1] == col) 
            moveFound = true;
        }

        if(!moveFound) return false; //if the simulated move is not valid, immediately return false

        Piece pieceToBeDisplaced = null; //originally just captured, made it to displaced to check castling
        boolean simulateOtherPieceDisplacement = false;
        //originally simulateCapture not really capture cause it also includes own peices xD
        boolean simulatedCastling = false;

        //handle castling validation
        int rookFromCol = -1;
        int rookToCol = -1;

        if(canCastle){
            if (board.getPiece(selectedRow, selectedCol).getType() == Piece.Type.KING && Math.abs(col - selectedCol) == 2 && row == selectedRow) {
                rookFromCol = col > selectedCol ? 7 : 0; // Determine rook column (kingside/queenside)
                rookToCol = col > selectedCol ? col - 1 : col + 1; // New rook position
                if(board.boardMap.get(selectedRow + "," + rookFromCol).getType() == Piece.Type.ROOK){
                pieceToBeDisplaced = board.getPiece(selectedRow, rookFromCol);
                simulatedCastling = true; //rook is displaced
                }
            }
        }

        //if the position it will move to has a piece in it
        if (board.boardMap.containsKey(row + "," + col)){
            pieceToBeDisplaced = board.getPiece(row, col);
            simulateOtherPieceDisplacement = true;
        }
    
        board.movePiece(selectedRow, selectedCol, row, col, canCastle); //in the original board, move it (in the hashmap only not visually)
        

        if (isKingInCheck(isWhiteTurn ? Piece.Color.WHITE : Piece.Color.BLACK)) {
            if (simulatedCastling){
                board.boardMap.put(row + "," + rookFromCol, pieceToBeDisplaced);
                board.boardMap.remove(row + "," + rookToCol);
            }
            board.movePiece(row, col, selectedRow, selectedCol); //back to original state since move is invalid
            if (simulateOtherPieceDisplacement)
                board.boardMap.put(row + "," + col, pieceToBeDisplaced);
            board.capturedPieces.removeatEnd();
            
            return false;
        }

        //puts back rook
        if (simulatedCastling){
            board.boardMap.put(row + "," + rookFromCol, pieceToBeDisplaced);
            board.boardMap.remove(row + "," + rookToCol);
        }

        board.movePiece(row, col, selectedRow, selectedCol); //back to original state to check rest of the valid moves

        if (simulateOtherPieceDisplacement) //put back simulated capture pieces
            board.boardMap.put(row + "," + col, pieceToBeDisplaced);
        board.capturedPieces.removeatEnd();
        return true;
    }

    private void highlightValidMoves() {
        for (int[] move : validMoves) {
            int row = move[0];
            int col = move[1];
            if(isValidMove(row, col)){
            Color originalColor = (row + col) % 2 == 0 ? Color.WHITE : Color.GRAY;
            Color highlightColor = new Color(255, 255, 153); // Light yellow for valid moves
            if (originalColor == Color.GRAY) {
                highlightColor = new Color(204, 204, 0); // Darker yellow for black tiles
            }
            gui.highlightTile(row, col, highlightColor);
        }
        }
    }

    private void resetHighlights() {
        for (int[] move : validMoves) {
            gui.resetTileColor(move[0], move[1]);
        }
    }

    private boolean isKingInCheck(Piece.Color kingColor) {
        int kingRow = -1, kingCol = -1;
    
        // Find the king's position
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPiece(row, col);
                if (piece != null && piece.getType() == Piece.Type.KING && piece.getColor() == kingColor) {
                    kingRow = row;
                    kingCol = col;
                    break;
                }
            }
        }
    
        // If the king's position is found, check if it's under attack
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPiece(row, col);
                if (piece != null && piece.getColor() != kingColor) { // Opponent's piece
                    List<int[]> moves = getValidMoves(row, col, piece);
                    for (int[] move : moves) {
                        if (move[0] == kingRow && move[1] == kingCol) {
                            return true; // King is under attack
                        }
                    }
                }
            }
        }
    
        return false; // No threats detected
    }

    private void highlightKingInCheck(Piece.Color kingColor) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPiece(row, col);
                if (piece != null && piece.getType() == Piece.Type.KING && piece.getColor() == kingColor) {
                    gui.highlightTile(row, col, Color.RED); //if king is check, check if its checkmate
                    kingInCheck = true;
                    checkCheckmate(row,col,kingColor, piece);
                }
            }
        }
    }

    private void checkCheckmate(int row, int col, Piece.Color kingColor, Piece king){
        selectedRow = row; selectedCol = col;
        validMoves = getValidMoves(row, col, king);
        for(int[] move : validMoves){
        if(isValidMove(move[0], move[1])) {System.out.println("Valid move at: " + king.getType() + " " + move[0] +"," + move[1] );selectedRow = -1; selectedCol = -1; return; }
        }


        for ( row = 0; row < 8; row++) {
            for ( col = 0; col < 8; col++) {
                Piece piece = board.getPiece(row, col);
                if (piece != null && piece.getType() != Piece.Type.KING && piece.getColor() == kingColor) {
                    selectedRow = row; selectedCol = col;
                    validMoves = getValidMoves(row, col, piece);
                    for (int[] move : validMoves) {
                        if(isValidMove(move[0], move[1])) {System.out.println("Valid move at: " + piece.getType() + " " + move[0] +"," + move[1] );selectedRow = -1; selectedCol = -1; return; }
                    }
                }
            }
        }

        System.out.println("CHECKMATE");
        gui.updateLabel("CHECKMATE!");

        int confirm = JOptionPane.showConfirmDialog(gui, "CHECKMATE" + (isWhiteTurn ? " BLACK WINS" : " WHITE WINS") + ", Reset Board?", "CHECKMATE" , JOptionPane.YES_NO_CANCEL_OPTION );
        if (confirm == JOptionPane.YES_OPTION){
            System.out.println("loading");
            board = new Board();
            gui.updateLabel("Turn: White");
            updateBoard();
            resetHighlights();
            isWhiteTurn = true;
            selectedCol = -1;
            selectedRow = -1;
            whiteKingMoved = false;
            blackKingMoved = false;

        }
    }

    


private void checkStalemate(Piece.Color kingColor){

    int kingRow = -1;
    int kingCol = -1;
    Piece king = null;
    int row;
    int col;


    //block of code to find king
    //(if i used this too much why dont i make it a function? good question.... (slightly diff implementation siya usually.. but barely))
    for ( row = 0; row < 8; row++) {
        for ( col = 0; col < 8; col++) {
            Piece piece = board.getPiece(row, col);
            if (piece != null && piece.getType() == Piece.Type.KING && piece.getColor() == kingColor) {
                king = piece;
                kingRow = row;
                kingCol = col;
                break;
            }
        }
    }

    selectedRow = kingRow; selectedCol = kingCol;
    validMoves = getValidMoves(kingRow, kingCol, king);
    for(int[] move : validMoves){
    if(isValidMove(move[0], move[1])) {selectedRow = -1; selectedCol = -1; return; }
    }


    for ( row = 0; row < 8; row++) {
        for ( col = 0; col < 8; col++) {
            Piece piece = board.getPiece(row, col);
            if (piece != null && piece.getType() != Piece.Type.KING && piece.getColor() == kingColor) {
                selectedRow = row; selectedCol = col;
                validMoves = getValidMoves(row, col, piece);
                for (int[] move : validMoves) {
                    if(isValidMove(move[0], move[1])) {selectedRow = -1; selectedCol = -1; return; }
                }
            }
        }
    }

    System.out.println("NO VALID MOVES DETECTED, STALEMATE");
    gui.updateLabel("STALEMATE!");
    JOptionPane.showMessageDialog(gui, "1/2 STALEMATE");

    //looked back at 113 for these
    int confirm = JOptionPane.showConfirmDialog(gui, "1/2 STALEMATE, Reset Board?", "Stalemate" , JOptionPane.YES_NO_CANCEL_OPTION );
    if (confirm == JOptionPane.YES_OPTION){
        board = new Board();
        gui.updateLabel("Turn: White");
        updateBoard();
        resetHighlights();
        isWhiteTurn = true;
        selectedCol = -1;
        selectedRow = -1;
        whiteKingMoved = false;
        blackKingMoved = false;

    }
}

private boolean canCastle(Piece.Color color, int kingRow, int kingCol, boolean isKingside) {
    int rookCol = isKingside ? 7 : 0;
    int step = isKingside ? 1 : -1; //in which direction the king is going to, 1 to the right, -1 to the left

    // Check if the rook and king haven't moved
    String rookKey = kingRow + "," + rookCol;
    if (!board.boardMap.containsKey(rookKey)) return false;
    Piece rook = board.getPiece(kingRow, rookCol);
    if (rook == null || rook.getType() != Piece.Type.ROOK || rook.getColor() != color) return false;
    if (color == Piece.Color.BLACK){if(blackKingMoved) return false;}
    else {if(whiteKingMoved) return false;}
   

    // Check for empty spaces between king and rook
    for (int col = kingCol + step; col != rookCol; col += step) {
        if (!board.isEmpty(kingRow, col)) return false;
    }

    return true;
}

    public void undo(){
        //handles undoing castling properly
        //rant: owemji, raming considerations sa chess :<
        boolean whiteKingRecentlyCastled = false;
        boolean blackKingRecentlyCastled = false;
        
        //theyve been displaced
        if(!(board.boardMap.containsKey("7,4"))){
            if(!(board.boardMap.containsKey("7,0")) || !(board.boardMap.containsKey("7,7")))
                if (board.boardMap.containsKey("7,2") || board.boardMap.containsKey("7,6"))
                    whiteKingRecentlyCastled = true;
        }
        if(!(board.boardMap.containsKey("0,4"))){
            if(!(board.boardMap.containsKey("0,0")) || !(board.boardMap.containsKey("0,7")))
                 if (board.boardMap.containsKey("0,2") || board.boardMap.containsKey("0,6")) 
                     blackKingRecentlyCastled = true;
        }

        if (board.undoMove()) {

            if(board.boardMap.containsKey("7,4")){
                if (board.getPiece(7, 4).getType() == Piece.Type.KING && whiteKingRecentlyCastled){if (board.boardMap.containsKey("7,0") || board.boardMap.containsKey("7,7"))whiteKingMoved = false;}
            }
            if(board.boardMap.containsKey("0,4")){
                if (board.getPiece(0, 4).getType() == Piece.Type.KING && blackKingRecentlyCastled){if (board.boardMap.containsKey("0,0") || board.boardMap.containsKey("0,7"))blackKingMoved = false;};
            }

            resetHighlights();
            isWhiteTurn = isWhiteTurn ? false : true;
            selectedCol = -1;
            selectedRow = -1;
            whiteKingMoved = false;
            blackKingMoved = false;
            gui.updateLabel("Turn: " + (isWhiteTurn ? "White" : "Black"));
            updateBoard();
            if(!board.capturedPieces.isEmpty())
            board.capturedPieces.removeatEnd();
        } else {
            if(!gameStarted){
                gameStarted = true;
                Chess.loiChess = true;
                board = new Board();
                updateBoard();
                isWhiteTurn = true;
                selectedCol = -1;
                selectedRow = -1;
            }
            JOptionPane.showMessageDialog(gui, "Cannot undo any further..");
        }
    }

    public void checkCaptured(){
        new CapturedPieces(board.capturedPieces);
    }

    public void booted(){
        gui.setVisible(true);
        gui.setLocation(Chess.locX, Chess.locY);
    }

}



// Main Class
public class Chess {

    public static int locX;
    public static int locY; //loc of the loadingscreen
    public static boolean booted;
    public static boolean loiChess = false;
   
    public static void main(String[] args) {
        GameController game = new GameController();
        Load load = new Load(); //load menu
        if(load.checkProgress()){
        locX = load.getX();
        locY = load.getY();

         load.dispose();
         }
        game.booted();
    }
}
