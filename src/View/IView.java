package View;

import algorithms.mazeGenerators.Maze;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import java.io.FileNotFoundException;

public interface IView {
    void displayMaze(Maze maze);
    public void LoadOldMaze()throws FileNotFoundException;
    public void Help(ActionEvent actionEvent);
    public void About(ActionEvent actionEvent);
    public void ResizeEvent(Scene scene);
    public void KeyPressed(KeyEvent keyEvent);
    public void solveMaze();
    public void generateMaze();
    public void SaveMaze();
    public void SelectBFS();
    public void SelectBEFS();
    public void SelectDFS();
    public void SelectSimpleGenerator();
    public void SelectMyMazeGenerator();
    public void Exit();

}
