package View;


import javafx.fxml.Initializable;
import javafx.scene.image.*;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class represents a window that opens and tells
 * the user that he has reached the end of the maze and managed to win the game.
 */
public class FinalController implements Initializable {
    public ImageView image;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        image.setImage(new Image(getClass().getResourceAsStream("/Images/Finish_pic.gif")));
    }
}


