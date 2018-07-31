/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package othello;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

/**
 *
 * @author johnn
 */
public class MessageController implements Initializable{

    SceneExtension scene;
    
    @FXML
    Label winnerLabel;

    
    
    public void setText(String text){
        winnerLabel.setText(text);
    }
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        scene =  (SceneExtension) winnerLabel.getScene();
        
    }
    
}
