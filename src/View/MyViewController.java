package View;

import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

public class MyViewController implements Observer, IView, Initializable {

    @FXML
    private MyViewModel myViewModel;
    public MazeDisplayer mazeDisplayer;
    public javafx.scene.control.TextField RowsNum_TxtField;
    public javafx.scene.control.TextField ColNum_TxtField;
    public javafx.scene.control.Button GenerateMaze_Button;
    public javafx.scene.control.Button SolveMaze_Button;
    public javafx.scene.control.Label Rows_Label;
    public javafx.scene.control.Label Col_Label;
    public javafx.scene.control.RadioMenuItem Bfs_Button;
    public javafx.scene.control.RadioMenuItem Dfs_Button;
    public javafx.scene.control.RadioMenuItem Befs_Button;
    public javafx.scene.control.RadioMenuItem MyMaze_Button;
    public javafx.scene.control.RadioMenuItem Simple_Button;
    public javafx.scene.control.Button RemoveSol_Button;
    public javafx.scene.control.Button Mute_Button;

    boolean mute = false;
    public IntegerProperty PlayerRow = new SimpleIntegerProperty();
    public IntegerProperty PlayerCol = new SimpleIntegerProperty();
    public int getPlayerRow() {
        return PlayerRow.get();
    }
    public IntegerProperty playerRowProperty() {
        return PlayerRow;
    }
    public int getPlayerCol() {
        return PlayerCol.get();
    }
    public IntegerProperty playerColProperty() {
        return PlayerCol;
    }

    public void SetMyViewModel(MyViewModel myViewModel) {
        this.myViewModel = myViewModel;
        myViewModel.rows.bind(RowsNum_TxtField.textProperty());
        myViewModel.columns.bind(ColNum_TxtField.textProperty());
    }

    /**
     *
     *
     *
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        if (o == myViewModel) {
            switch ((String) arg) {
                case "maze": {
                    displayMaze(myViewModel.getMaze());
                    GenerateMaze_Button.setDisable(false); //disable the generate maze button
                    break;
                }
                case "move": {
                    if (myViewModel.getMaze().Struct[myViewModel.getCharacterPositionRow()][myViewModel.getCharacterPositionColumn()]==2)
                        mazeDisplayer.RemoveSolCharacter();
                    mazeDisplayer.SetPlayerPosition(myViewModel.getCharacterPositionRow(), myViewModel.getCharacterPositionColumn());

                    if(myViewModel.getCharacterPositionRow()==myViewModel.getMaze().getGoalPosition().getRowIndex() && myViewModel.getCharacterPositionColumn()==myViewModel.getMaze().getGoalPosition().getColIndex()) {
                        Final();
                    }
                    if (myViewModel.getMaze().Struct[myViewModel.getCharacterPositionRow()][myViewModel.getCharacterPositionColumn()]==2)
                        mazeDisplayer.RemoveSolCharacter();

                    break;
                }
                case "solve": {
                    displaySolution(myViewModel.getSolution());
                    SolveMaze_Button.setDisable(false);
                    break;
                }
                case "load": {
                    displayMaze(myViewModel.getMaze());
                    GenerateMaze_Button.setDisable(false);
                    break;
                }
                case "prop": {
                    MazeGenerationProperties(myViewModel.getGenerateString());
                    SolveAlgoProperties(myViewModel.getSolveString());
                    break;
                }
                case "RemoveSol":
                {
                    mazeDisplayer.SetMaze(myViewModel.getMaze());
                    mazeDisplayer.requestFocus();
                    break;
                }

            }

        }
    }

    /**
     * This method allows to change the type of the creation of the maze,
     * creating the maze is possible by two different algorithms
     * one in a random way and the other according to prim algorithm.
     * @param generateString
     */
    private void MazeGenerationProperties(String generateString) {
        switch (generateString) {
            case "MyMazeGenerator":
            {
                MyMaze_Button.setSelected(true);
                Simple_Button.setSelected(false);
                break;
            }
            case "SimpleMazeGenerator":
            {
                MyMaze_Button.setSelected(false);
                Simple_Button.setSelected(true);
                break;
            }
        }
    }

    /**
     * When the user uses the game to solve the maze he can use 3 different type of algorithms,
     * this method allows him to choose the algorithm he wants to use to solve the maze.
     * @param solveString
     */
    private void SolveAlgoProperties(String solveString) {
        switch (solveString) {
            case "BreadthFirstSearch":
            {
                Bfs_Button.setSelected(true);
                Befs_Button.setSelected(false);
                Dfs_Button.setSelected(false);
                break;
            }
            case "DepthFirstSearch":
            {
                Bfs_Button.setSelected(false);
                Befs_Button.setSelected(false);
                Dfs_Button.setSelected(true);
                break;
            }
            case "BestFirstSearch":
            {
                Bfs_Button.setSelected(false);
                Befs_Button.setSelected(true);
                Dfs_Button.setSelected(false);
                break;
            }
        }


    }

    @Override
    public void displayMaze(Maze maze) {
        mazeDisplayer.SetMaze(maze);
        mazeDisplayer.SetPlayerPosition(maze.getStartPosition().getRowIndex(), maze.getStartPosition().getColIndex());
        mazeDisplayer.SetGoalPosition(maze.getGoalPosition().getRowIndex(), maze.getGoalPosition().getColIndex());
    }

    public void displaySolution(Solution sol) {
        mazeDisplayer.setSolution(sol);
    }
    @Override
    public void generateMaze() {
        GenerateMaze_Button.setDisable(true);
        myViewModel.generateMaze();
        SolveMaze_Button.setDisable(false);
    }

    @Override
    public void solveMaze() {
        myViewModel.solveMaze();
        RemoveSol_Button.setDisable(false);
    }

    @Override
    public void KeyPressed(KeyEvent keyEvent) {
        myViewModel.moveCharacter(keyEvent.getCode());
        keyEvent.consume();
    }

    /**
     *
     * @param scene
     */
    @Override
    public void ResizeEvent(Scene scene) {
        long width = 0;
        long height = 0;
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                mazeDisplayer.setWidth(scene.getWidth());
                mazeDisplayer.redraw();
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                mazeDisplayer.setHeight(scene.getHeight());
                mazeDisplayer.redraw();
            }
        });
    }

    /**
     * This method allows the user to read about the game developers.
     * @param actionEvent
     */
    @Override
    public void About(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("About");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("About.fxml").openStream());
            Scene scene = new Scene(root, 673, 500);
            scene.getStylesheets().add(getClass().getResource("AboutStyle.css").toExternalForm());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method allows the user who did not understand the rules of the game to enter the Help
     *  button and get a brief explanation of the rules and how to play.
     * @param actionEvent
     */
    @Override
    public void Help(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("Help");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("Help.fxml").openStream());
            Scene scene = new Scene(root, 734, 530);
            scene.getStylesheets().add(getClass().getResource("HelpStyle.css").toExternalForm());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (Exception e) {

        }
    }

    /**
     * This method is activated when the user manages to solve the labyrinth and receives positive feedback
     * in order to know that he has won.
     */
    MediaPlayer m;
    Media media;
    public void Final() {
        try {
            Stage stage = new Stage();
            stage.setTitle("Finished!");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("Final.fxml").openStream());
            Scene scene = new Scene(root, 600, 400);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
            m.stop();
            media=new Media(getClass().getResource("/Images/Finish_song.mp3").toString());
            m = new MediaPlayer(media);
            m.play();
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    m.setMute(true);
                    m.stop();
                    media=new Media(getClass().getResource("/Images/Gaming_Song.mp3").toString());
                    m = new MediaPlayer(media);
                    if(!mute)
                    m.play();
                }
            });
        } catch (Exception e) {

        }
    }

    /**
     * This method allows the user to load a certain maze that he previously saved and continue to play it.
     * @throws FileNotFoundException
     */
    @Override
    public void LoadOldMaze() throws FileNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        FileChooser.ExtensionFilter fileExtensions = new FileChooser.ExtensionFilter("Maze Files (.Maze)", ".Maze");
        File file = fileChooser.showOpenDialog((Stage) RowsNum_TxtField.getScene().getWindow());
        if(file!=null)
            myViewModel.LoadMaze(file);
        SolveMaze_Button.setDisable(false);

    }

    /**
     * This method allows the user to stop playing the maze and save it to a specific place on the computer for later use.
     */
    @Override
    public void SaveMaze() {
        FileChooser fc = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Maze Files (.Maze)", ".Maze");
        fc.getExtensionFilters().add(extFilter);
        File file = fc.showSaveDialog((Stage) RowsNum_TxtField.getScene().getWindow());
        if(file!=null)
            myViewModel.SaveMaze(file);
    }
    @Override
    public void Exit()
    {
            myViewModel.StopServers();
    }


    public void IntialProperties()
    {
        myViewModel.IntialProperties();
    }

    @Override
    public void SelectBFS()
    {
        SolveAlgoProperties("BreadthFirstSearch");
        myViewModel.SetPropSolve("BreadthFirstSearch");
    }
    @Override
    public void SelectBEFS()
    {
        SolveAlgoProperties("BestFirstSearch");
        myViewModel.SetPropSolve("BestFirstSearch");
    }
    @Override
    public void SelectDFS()
    {
        SolveAlgoProperties("DepthFirstSearch");
        myViewModel.SetPropSolve("DepthFirstSearch");
    }
    @Override
    public void SelectSimpleGenerator()
    {
        MazeGenerationProperties("SimpleMazeGenerator");
        myViewModel.SetPropGenerate("SimpleMazeGenerator");
    }
    @Override
    public void SelectMyMazeGenerator()
    {
        MazeGenerationProperties("MyMazeGenerator");
        myViewModel.SetPropGenerate("MyMazeGenerator");
    }
    public void RemoveSol()
    {
        myViewModel.RemoveSol();
        RemoveSol_Button.setDisable(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        media=new Media(getClass().getResource("/Images/Gaming_Song.mp3").toString());
        m = new MediaPlayer(media);
        m.play();
        m.setCycleCount(MediaPlayer.INDEFINITE);
    }

    /**
     *
     * @param movement
     */
    public void Mouse(MouseEvent movement) {
       if (myViewModel.getMaze() == null)
            return;
        System.out.println(movement.getY());

        int mouseX = (int) ((movement.getX() - 185) / (mazeDisplayer.getCellWidth()));
        int mouseY = (int) ((movement.getY() -25) / (mazeDisplayer.getCellHeight()));
        if (Math.abs(myViewModel.getCharacterPositionRow() - mouseX) < 2 || (Math.abs(myViewModel.getCharacterPositionColumn() - mouseY) < 2)) {
            if (mouseY <myViewModel.getCharacterPositionColumn()) {
                myViewModel.moveCharacter(KeyCode.UP);
            }
            if (mouseY > myViewModel.getCharacterPositionColumn()) {
                myViewModel.moveCharacter(KeyCode.DOWN);
            }
            if (mouseX < myViewModel.getCharacterPositionRow()) {

                myViewModel.moveCharacter(KeyCode.LEFT);
            }
            if (mouseX > myViewModel.getCharacterPositionRow()) {
                myViewModel.moveCharacter(KeyCode.RIGHT);
            }
        }
        mazeDisplayer.requestFocus();

    }

    /**
     * This method allows the user to mute the music or activate it according to the user's wishes.
     */
    public void Mute()
    {
        if(!mute){
            m.setMute(true);
            mute=true;
            Mute_Button.setText("Music On");
        }
        else
        {
            m.setMute(false);
            mute=false;
            m.play();
            Mute_Button.setText("Mute Music");
        }

    }
}
