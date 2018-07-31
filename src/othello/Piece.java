/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package othello;

import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author johnn
 */

//Extends button for custm button with my own code
public class Piece extends Button{
    
    //Statics to determine piece stae
    public static final int BLACK_TEAM = 1;
    public static final int WHITE_TEAM = 2;
    public static final int EMPTY = 0;
    public static final int HIGHLIGHT = -1;
    
    //holds where piece is
    private final int rowIndex;
    private final int columnIndex;
    
    //holds current type
    private int type;
    
    //holds background
    private AnchorPane backPane;
    
    //Animation States
    public static final int SHRINKING = 0;
    public static final int RISING = 1;
    public static final int FINISHED = 2;
    private String endType;
    
    //holds current animatin state
    private int animationState;

    //Constructor to get properties
    public Piece(int row, int column,int startType, AnchorPane back) {
        rowIndex = row;
        columnIndex = column;
        backPane = back;
        updatePiece(startType);
        animationState = FINISHED;
    }
    
    
    public final void updatePieceAnimated(int newType){
        //sets properties of the piece based on ste
        type = newType;
        setAnimationState(SHRINKING);
        switch(type){
            case 0:
                 endType = "-fx-background-color:#00CD66; -fx-border-radius: 200; -fx-background-radius: 200";
                 break;
            case 1:
                 endType = "-fx-background-color:#000000; -fx-border-radius: 200; -fx-background-radius: 200";
                 break;
            case 2:
                 endType = "-fx-background-color:#FFFFFF; -fx-border-radius: 200; -fx-background-radius: 200";
                 break;
            case -1:
                 endType = "-fx-background-color:#0000FF; -fx-border-radius: 200; -fx-background-radius: 200";
                 break;
        }
    }
    //updates propertis of th epiece
    public final void updatePiece(int newType){
        type = newType;
        switch(type){
            case 0:
                 super.setStyle("-fx-background-color: rgba(0, 205, 102, 0); -fx-border-radius: 200; -fx-background-radius: 200");
                 break;
            case 1:
                 super.setStyle("-fx-background-color:#000000; -fx-border-radius: 200; -fx-background-radius: 200");
                 break;
            case 2:
                 super.setStyle("-fx-background-color:#FFFFFF; -fx-border-radius: 200; -fx-background-radius: 200");
                 break;
            case -1:
                 super.setStyle("-fx-background-color:#0000FF; -fx-border-radius: 200; -fx-background-radius: 200");
                 break;
        }
    }

    //Getters of variables
    
    public int getType() {
        return type;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public int getAnimationState() {
        return animationState;
    }

    
    //Updates animatin stae
    public void setAnimationState(int animationState) {
        this.animationState = animationState;
    }

    public String getEndType() {
        return endType;
    }

    public AnchorPane getBackPane() {
        return backPane;
    }
    
    
    //prinsts type when printed instead of memory location
    @Override
    public String toString(){
        return type + "";
    }
    
    
}
