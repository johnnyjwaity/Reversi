/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package othello;


import java.io.IOException;
import javafx.scene.paint.Color;
import java.net.URL;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.converter.LocalTimeStringConverter;

/**
 *
 * @author johnn
 */
public class FXMLDocumentController implements Initializable {
    
    private static final int GRID_PANE_ROWS = 8;
    private static final int GRID_PANE_COLUMNS = 8;
    
    private Piece[][] boardArray = new Piece[GRID_PANE_ROWS][GRID_PANE_COLUMNS];
    
    private int currentTurn = 1;
    
    @FXML
    private AnchorPane Pane, optionPane;
    
    @FXML
    private ChoiceBox<String> playMode;
    
    private GridPane board;
    private Label blkText, whtText;

    public FXMLDocumentController() {
        chipSpinner.start();
        setupView();
    }
    private void setupView(){
        
    }
    
    private List<Piece> chipsToAnimate = new ArrayList<>();
    private LocalTime lastRun = LocalTime.now();
    
    private int cpuDifficulty = 0;
    private boolean cpuPlaying = true;
    private boolean waitingForCPUToMakeAMove = false;
    
    AnimationTimer chipSpinner = new AnimationTimer() {
        @Override
        public void handle(long now) {
            //LocalTime.now().toSecondOfDay() - lastRun.toSecondOfDay() >= 0.1
            if(true){
                lastRun = LocalTime.now();
                //Makes sure cpu makes move after current chiops are done animating
                if(chipsToAnimate.isEmpty() && cpuPlaying && waitingForCPUToMakeAMove){
                    //Runs CPU Function to make its move
                    waitingForCPUToMakeAMove = false;
                    cpuMove(findAllValidMoves(false));
                    findAllValidMoves(true);
                }
                //List is filled with pieces that are done animating
                List<Piece> chipsToRemove = new ArrayList<>();
                for(Piece chip : chipsToAnimate){
                    //Gets state from each chip and animates accordingly
                    if(chip.getAnimationState() == Piece.SHRINKING){
                        //Decreases Chip Width
                        chip.setPrefWidth(chip.getWidth() - 2);
                        //chip.setPrefHeight(chip.getHeight() - 10);
                        //Makes sure chip doesnt get to 0 becuase of graphic glitch
                        if(chip.getWidth() <= 2){
                            chip.setStyle(chip.getEndType());
                            chip.setAnimationState(Piece.RISING);
                        }
                    }
                    else if(chip.getAnimationState() == Piece.RISING){
                        //Increases chip size
                        chip.setPrefWidth(chip.getWidth() + 2);
                       // chip.setPrefHeight(chip.getHeight() + 10);
                        if(chip.getWidth() >= 45){
                            //sets it to finished if its get back to original width
                            chip.setAnimationState(Piece.FINISHED);
                        }
                    }
                    else{
                        //If finished removes chip from animated
                        chipsToRemove.add(chip);
                    }
                    //sets chip position in the square based on its wifth
                    chip.setLayoutX((50 - chip.getWidth())/2);
               
                }
                //Removes finished chips
                for(Piece chip : chipsToRemove){
                    chipsToAnimate.remove(chip);
                }
                //Clears the remove list so its not removed multiple times
                chipsToRemove.clear();
                
            }
           
        }
    };
    
    //Ran when play buttin is clicked
    @FXML
    private void play(){
        //Gets the stage its on for resizing
        SceneExtension scene = (SceneExtension) Pane.getScene();
        Stage myStage = scene.myStage;
        //Gets rid of the options for game
        optionPane.setOpacity(0);
        //sets variable based on play settings
        switch(playMode.getSelectionModel().getSelectedIndex()){
            case 0:
                cpuPlaying = false;
                break;
            case 1:
                cpuPlaying = true;
                cpuDifficulty = 0;
                waitingForCPUToMakeAMove = false;
                break;
            case 2:
                cpuPlaying = true;
                cpuDifficulty = 1;
                waitingForCPUToMakeAMove = false;
                break;
            case 3:
                cpuPlaying = true;
                cpuDifficulty = 2;
                waitingForCPUToMakeAMove = false;
                break;
        }
        //gets the Pane and sets width and height
        System.out.println("Play");
        Pane.setStyle("-fx-background-color:#00CD66");
        Pane.setPrefWidth(500);
        Pane.setPrefHeight(500);
        board = new GridPane();
        board.setGridLinesVisible(true);
//        board.setPrefWidth((GRID_PANE_COLUMNS)*50);
//        board.setPrefHeight((GRID_PANE_ROWS)*50);
        //Resizes window based on colums and rows chosen
        myStage.setMinWidth(400);
        myStage.setWidth((GRID_PANE_COLUMNS*50) + 17);
        myStage.setHeight((GRID_PANE_ROWS*50) + 100);
        //Creats the board for each row and column
        for(int r = 0; r<GRID_PANE_ROWS; r++){
            for(int c = 0; c<GRID_PANE_COLUMNS; c++){
                //Creates a background square for each piece
                AnchorPane backPane = new AnchorPane();
                backPane.setPrefHeight(50);
                backPane.setPrefWidth(50);
                backPane.setStyle("-fx-background-color: rgba(0, 205, 102, 0)");
                
                //Creates the pieces whic are an extension of button for tiles
                Piece btn = new Piece(r, c, Piece.EMPTY, backPane);
                //Adds button to the array
                boardArray[r][c] = btn;
                //Updates its style to empty
                btn.updatePiece(Piece.EMPTY);
                //btn.setStyle("-fx-border-radius: 200; -fx-background-radius: 200");
                EventHandler<ActionEvent> click = new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        //Gets Piece
                        Piece clickedPiece = (Piece) event.getSource();
                        //Checks if clicked piece is valid to put a piece in the square called in a specific order becuase it would malfunction if the amount of tiles check was called earlier becuase it would flip bad tiles
                        if(clickedPiece.getType() == Piece.EMPTY && sharesBorder(clickedPiece) && turn(clickedPiece, true) > 0){
                            //Updates Piece to the correct color un animated to simulate placing a tile and not flipping it uses ternary opertaor to reduce lines of code and if statements
                            clickedPiece.updatePiece((currentTurn%2 == 0) ? Piece.WHITE_TEAM : Piece.BLACK_TEAM);
                            //Checks to see if there is a winner
                            int winner = checkWinner();
                            //Means that there was a winner or a tie
                             if(winner != 0){
                                 Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                //Displays an alert of the winner of a tie
                                 switch(winner){
                                    case 1:
                                        alert.setHeaderText("Black Wins");
                                        break;
                                    case 2:
                                        alert.setHeaderText("White Wins");
                                        break;
                                    case 3:
                                        alert.setHeaderText("Tie");
                                        break;
                                }
                                 //Shows alert
                                alert.showAndWait();

                            }
                             //Increases turn
                            currentTurn++;
                            if(cpuPlaying){
                                //Tells the cpu to wait till done animating
                                waitingForCPUToMakeAMove = true;
                            }
                            else{
                                //Shows valid moves for next player
                                Map<Piece, Integer> map = findAllValidMoves(true);
                                //Rns if there are no valid moves
                                if(map.isEmpty() && winner == 0){
                                    //Shows no move alert
                                    Alert noMovesAlert = new Alert(Alert.AlertType.INFORMATION);
                                    noMovesAlert.setHeaderText("No Moves Avaible For " + ((currentTurn%2 == 0) ? "White" : "Black") + " Your Move Has Been Forfeited");
                                    noMovesAlert.showAndWait();
                                    currentTurn++;
                                    findAllValidMoves(true);
                                }
                            }
                            
                        }
                        
                    }
                };
                //Sets tile info
                btn.setOnAction(click);
                btn.setPrefHeight(45);
                btn.setPrefWidth(45);
                btn.setMinWidth(0);
                btn.setLayoutX(2.5);
                btn.setLayoutY(2.5);
                backPane.getChildren().add(btn);
                board.add(backPane, c, r);
            }
        }
        //Creates intial 4 tiles
        boardArray[(GRID_PANE_ROWS/2) -1][(GRID_PANE_COLUMNS/2) -1].updatePiece(Piece.BLACK_TEAM);
        boardArray[GRID_PANE_ROWS/2][GRID_PANE_COLUMNS/2].updatePiece(Piece.BLACK_TEAM);
        boardArray[GRID_PANE_ROWS/2][(GRID_PANE_COLUMNS/2) -1].updatePiece(Piece.WHITE_TEAM);
        boardArray[(GRID_PANE_ROWS/2) -1][GRID_PANE_COLUMNS/2].updatePiece(Piece.WHITE_TEAM);
        
        Font pieceFont = new Font("system", 50);
        
        //Font k = new Font
        //adds board to view
        //Crweates bottom UI
        Pane.getChildren().add(board);
        ImageView blk = new ImageView("resources/Black_Circle.png");
        blk.setFitWidth(50);
        blk.setFitHeight(50);
        blk.setLayoutX(10);
        blk.setLayoutY(7 + (GRID_PANE_ROWS * 50));
        
        blkText = new Label("2");
        blkText.setLayoutX(blk.getLayoutX() + blk.getFitWidth() + 5);
        blkText.setLayoutY(blk.getLayoutY() - 10);
        blkText.setFont(pieceFont);
        blkText.setAlignment(Pos.TOP_LEFT);
        blkText.setTextFill(Color.WHITE);
        
        ImageView wht = new ImageView("resources/White_Circle.png");
        wht.setFitWidth(50);
        wht.setFitHeight(50);
        wht.setLayoutX(150);
        wht.setLayoutY(7 + (GRID_PANE_ROWS * 50));
        
        whtText = new Label("2");
        whtText.setLayoutX(wht.getLayoutX() + blk.getFitWidth() + 5);
        whtText.setLayoutY(wht.getLayoutY() - 10);
        whtText.setFont(pieceFont);
        whtText.setAlignment(Pos.TOP_LEFT);
        whtText.setTextFill(Color.WHITE);
        //Button to start new game
        Button restartButton = new Button("New Game");
        EventHandler<ActionEvent> click2 = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                restart();
            }
        };
        restartButton.setOnAction(click2);
        restartButton.setLayoutX(whtText.getLayoutX() + whtText.getWidth() + 100);
        restartButton.setLayoutY(whtText.getLayoutY() + 20);
        
        Pane.getChildren().add(restartButton);
        Pane.getChildren().add(blk);
        Pane.getChildren().add(blkText);
        Pane.getChildren().add(wht);
        Pane.getChildren().add(whtText);
        findAllValidMoves(true);
        
        //displayMessageDialog(0);
        
    }
    //Runs when cpu needs to make move
    private void cpuMove(Map<Piece, Integer> possibleMoves){
        //Stores move for max changes and least changes
        Map.Entry<Piece, Integer> max = null;
        Map.Entry<Piece, Integer> min = null;
        //Goses through map and finds all the mins an maxes
        for (Map.Entry<Piece, Integer> entry : possibleMoves.entrySet()) {
            Piece key = entry.getKey();
            Integer value = entry.getValue();
            if(max == null){
                max = entry;
            }
            if(min == null){
                min = entry;
            }
            
            if(value > max.getValue()){
                max = entry;
            }
            
            if(value < min.getValue()){
                min = entry;
            }
            
        }
        //Returns to player moves if there are no valid moves
        if(max == null || min == null){
            currentTurn++;
            return;
        }
        //Makes move based on dificulty
        switch(cpuDifficulty){
            case 0: 
                //Easy uses min
                turn(min.getKey(), true);
                min.getKey().updatePiece((currentTurn%2 == 0) ? Piece.WHITE_TEAM : Piece.BLACK_TEAM);
                break;
            case 1:
                //Medium uses random
                int r = (int) Math.random() * possibleMoves.size();
                List<Piece> pieces = new ArrayList<>();
                pieces.addAll(possibleMoves.keySet());
                turn(pieces.get(r), true);
                pieces.get(r).updatePiece((currentTurn%2 == 0) ? Piece.WHITE_TEAM : Piece.BLACK_TEAM);
                break;
            case 2:
                //Hard uses max
                turn(max.getKey(), true);
                max.getKey().updatePiece((currentTurn%2 == 0) ? Piece.WHITE_TEAM : Piece.BLACK_TEAM);
                break;
        }
        //Checks if winning move has been made
        int winner = checkWinner();
        if(winner != 0){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                
             switch(winner){
                case 1:
                    alert.setHeaderText("Black Wins");
                    break;
                case 2:
                    alert.setHeaderText("White Wins");
                    break;
                case 3:
                    alert.setHeaderText("Tie");
                    break;
            }
            alert.showAndWait();
            
        }
        currentTurn++;
    }
    // old code that was going to make a seprete scrren to display winner and loser
    private void displayMessageDialog(String winner){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLMessage.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL); 
            stage.setTitle(winner);
            stage.setScene(new SceneExtension(root, stage, winner));  
            stage.show();
            
            
            
          }
            catch(Exception e){
                System.out.println(e);
          }
    }
    //Run to make a turn boolean contains the turn is real or just a scan for possible moves
    private int turn(Piece selectedPiece, boolean flip){
        //Each one scans board for moves in each direction of piece
        int amountOfOppFipped = 0;
        amountOfOppFipped += scanBoard(selectedPiece, 0, 1, false, true, 0, 1, flip);
        amountOfOppFipped += scanBoard(selectedPiece, 0, -1, false, true, 0, -1, flip);
        amountOfOppFipped += scanBoard(selectedPiece, 1, 0, true, false, 1, 0, flip);
        amountOfOppFipped += scanBoard(selectedPiece, -1, 0, true, false, -1, 0, flip);
        amountOfOppFipped += scanBoard(selectedPiece, 1, 1, true, true, 1, 1, flip);
        amountOfOppFipped += scanBoard(selectedPiece, -1, -1, true, true, -1, -1, flip);
        amountOfOppFipped += scanBoard(selectedPiece, -1, 1, true, true, -1, 1, flip);
        amountOfOppFipped += scanBoard(selectedPiece, 1, -1, true, true, 1, -1, flip);
        return amountOfOppFipped;
    }
    //Scan board takes in parameters to move along row column or diagnol to find amount switched 
    private int scanBoard(Piece selectedPiece, int rowOffset, int columnOffset, boolean useIndexRow, boolean useIndexColumn, int rowIndexMultiplier, int columnIndexMultiplier, boolean flip){
        //Finds if rows or colums are larger
        int max = (GRID_PANE_ROWS > GRID_PANE_COLUMNS) ? GRID_PANE_ROWS : GRID_PANE_COLUMNS;
        //Holds all tiles that can be potentiall switched if a same piece is found
        List<Piece> potentialSwitches = new ArrayList<>();
        for(int i = 0; i < max; i++){
            //Finds the row an column of the board to check that piece 
            int column = selectedPiece.getColumnIndex() + columnOffset + (((useIndexColumn) ? i : 0) * columnIndexMultiplier);
            int row = selectedPiece.getRowIndex() + rowOffset + (((useIndexRow) ? i : 0) * rowIndexMultiplier);
            //Checks if space is valid on the board and if it isnt breaks the loop
            if(column >= GRID_PANE_COLUMNS || column < 0 || row >= GRID_PANE_ROWS || row < 0){
                break;
            }
            //Adds it to potential switches if it isnt a same piece
            if(boardArray[row][column].getType() != ((currentTurn%2 == 0) ? Piece.WHITE_TEAM : Piece.BLACK_TEAM)){
               potentialSwitches.add(boardArray[row][column]);
            }
            else{
                //If it is a smae piece it means it is a valid turn and proceeds to flip and returns amount of flips
                return switchPieces(potentialSwitches, flip);
            }
        }
        //returns 0 is it wasnt a valid move
        return 0;
    }
    
    
    
    //Method flips pieces
    private int switchPieces(List<Piece> piecesToSwitch, boolean flip){
        //Holds the opposite piece color based on turn
        int opp = (currentTurn%2 == 0) ? Piece.BLACK_TEAM : Piece.WHITE_TEAM;
        //Holds amount of other pieces color switched
        int amountOfOppSwitched = 0;
        //Scans array for opposites
        for(Piece piece : piecesToSwitch){
            //is opposite the add a point to switched
            if(piece.getType() == opp){
                amountOfOppSwitched ++;
            }
            else if(piece.getType() == Piece.EMPTY){
                //If an empty piece is found its not a valid move and returns 0 switched
                return 0;
            }
        }
        //If 0 switched returns 0 EXTRA CHECK
        if(amountOfOppSwitched == 0){
            return 0;
        }
        else if(!flip){
            //If pieced do not need to flipped it returns amount of opposite potentially flipped
            return amountOfOppSwitched;
        }
        //Flips the pieced in the array 
        for(Piece piece : piecesToSwitch){
            //Flips animated to simulate changin side of piece
            piece.updatePieceAnimated(currentTurn%2 ==0 ? Piece.WHITE_TEAM : Piece.BLACK_TEAM);
            //adds it to teh anmation loop
            chipsToAnimate.add(piece);
        }
        //Returns amount flipped
        return amountOfOppSwitched;
    }
    //Returns true if shares a border with another piece
    private boolean sharesBorder (Piece piece){
        boolean sharesBorder = false;
        List<Integer[]> borders = new ArrayList<>();
        //Gets colum and row
        int column = piece.getColumnIndex();
        int row = piece.getRowIndex();
        Integer[] borderCoord = new Integer[2];
        
        //Following code checks all borders
        
        //TopLeft
        borderCoord[0] = row-1;
        borderCoord[1] = column-1;
        
        borders.add(borderCoord);
        
        //Top Center
        borderCoord = new Integer[2];
        borderCoord[0] = row-1;
        borderCoord[1] = column;
        borders.add(borderCoord);
        //Top Right
        borderCoord = new Integer[2];
        borderCoord[0] = row-1;
        borderCoord[1] = column+1;
        borders.add(borderCoord);
        //Left
        borderCoord = new Integer[2];
        borderCoord[0] = row;
        borderCoord[1] = column-1;
        borders.add(borderCoord);
        //Right
        borderCoord = new Integer[2];
        borderCoord[0] = row;
        borderCoord[1] = column+1;
        borders.add(borderCoord);
        //Bottom Left
        borderCoord = new Integer[2];
        borderCoord[0] = row+1;
        borderCoord[1] = column-1;
        borders.add(borderCoord);
        //Bottom Center
        borderCoord = new Integer[2];
        borderCoord[0] = row+1;
        borderCoord[1] = column;
        borders.add(borderCoord);
        //Bottom Right
        borderCoord = new Integer[2];
        borderCoord[0] = row-1;
        borderCoord[1] = column+1;
        borders.add(borderCoord);
        
        //Checks all boundaris to see if it contains another piece
        for(Integer[] coord : borders){
            if(coord[0] < 0 || coord[0] > GRID_PANE_ROWS-1 || coord[1] < 0 || coord[1] > GRID_PANE_COLUMNS-1){
                //Goes to next index if current boundary is invalid
                continue;
            }
            
            if(boardArray[coord[0]][coord[1]].getType() != Piece.EMPTY){
                //If found boundary breaks the loop
                sharesBorder = true;
                break;
            }
        }

        
        //returns true or false based on loop
        return sharesBorder;
    }
    
    //Code runs to check winner
    private int checkWinner(){
        int[] pieceCount = new int[2];
        boolean winner = true;
        //scans entire board to see if its full and it also counts how mnay each team has
        for(int r = 0; r<GRID_PANE_ROWS; r++){
            for(int c = 0; c<GRID_PANE_COLUMNS; c++){
                if(boardArray[r][c].getType() == Piece.EMPTY){
                    winner = false;
                }
                else{
                    pieceCount[(boardArray[r][c].getType() == Piece.BLACK_TEAM) ? 0 : 1] +=1;
                }
                
                
            }
        }
        //Sets labels on the bottom based on chip amount
        blkText.setText("" + pieceCount[0]);
        whtText.setText("" + pieceCount[1]);
        //returns 0 if there is no winner
        if(!winner){
            return 0;
        }
        //returns 3 for tie
        if(pieceCount[0] == pieceCount[1]){
            return 3;
        }
        //returns winner based on which is greater
        return (pieceCount[0] > pieceCount[1]) ? 1 : 2;
        
    }
    //returns a map of valid moves
    private Map<Piece, Integer> findAllValidMoves(boolean highlight){
        
        Map<Piece, Integer> validPieces = new HashMap<>();
        //Goes through entire board
        for(int r = 0; r < GRID_PANE_ROWS; r++){
            for(int c = 0; c< GRID_PANE_COLUMNS; c++){
                //gets piece for current position
                Piece piece = boardArray[r][c];
                //sets style of background to normal
                piece.getBackPane().setStyle("-fx-background-color: rgba(0, 205, 102, 0)");
                //checks if shares boarder is valid and if piece is empty
                if(piece.getType() == Piece.EMPTY && sharesBorder(piece)){
                    //Gets amount of flips for current move
                    int amountOfMoves = turn(piece, false);
                    //checks if greater than 0
                    if(amountOfMoves > 0){
                        //puts the piece int o possinle
                        validPieces.put(piece, amountOfMoves);
                        //highlights background if highlight is ture
                        if(highlight){
                            piece.getBackPane().setStyle("-fx-background-color: rgba(76, 166, 255, 0.5)");
                        }
                       else{
                            piece.getBackPane().setStyle("-fx-background-color: rgba(0, 205, 102, 0)");
                        }
                    }
                }
                    
            }
        }
        //returns map
        
        return validPieces;
    }
    //Restarts the game
    private void restart(){
        try{
            //Creates a new instance of the fxml document
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL); 
            stage.setTitle("Game");
            stage.setScene(new SceneExtension(root, stage));  
            stage.show();
            
            //closes the current stage after new one is created
            Stage currentStage = ((SceneExtension) Pane.getScene()).myStage;
            currentStage.close();
            
          }
            catch(Exception e){
                System.out.println(e);
          }
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        playMode.getItems().addAll("2 Player", "CPU Easy", "CPU Medium", "CPU Hard");
        playMode.getSelectionModel().select(0);
    }    
    
}
