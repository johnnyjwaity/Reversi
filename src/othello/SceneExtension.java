/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package othello;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author johnn
 */
public class SceneExtension extends Scene {
    
    public Stage myStage;
    public String lblText;
    
    
    
    
    public SceneExtension(Parent root, Stage stage) {
        //initializes the parent class with root
        super(root);
        myStage = stage;
        
        
        
    }
    
    public SceneExtension(Parent root, Stage stage, String lbl) {
        //inits parent with root
        super(root);
        myStage = stage;
        lblText = lbl;
        
        
        
    }
    
}
