package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;
import java.io.File;

public interface IModel {
    void generateMaze(int width, int height);
    void moveCharacter(KeyCode movement);
    Maze getMaze();
    int getCharacterPositionRow();
    int getCharacterPositionColumn();
    void LoadMaze(File file);
    void solveMaze();
    void stopServers();
    void startServers();
    Solution getSolution();
    public java.lang.String getGenerate();
    public java.lang.String getSolve();
    void SaveMaze(File file);
    void IntialProperties();
    void SetPropertiesSolverServer(String s);
    void SetPropertiesGenerateServer(String s);
    void RemoveSol();
}
