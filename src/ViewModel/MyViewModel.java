package ViewModel;

import Model.IModel;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.beans.property.*;
import javafx.scene.input.KeyCode;
import java.io.File;
import java.util.Observable;
import java.util.Observer;


public class MyViewModel extends Observable implements Observer {

    private IModel model;
    public IntegerProperty PlayerPositionRow;
    public IntegerProperty PlayerPositionColumn;
    public StringProperty rows;
    public StringProperty columns;

    public MyViewModel(IModel model){
        this.model = model;
        PlayerPositionColumn = new SimpleIntegerProperty();
        PlayerPositionRow = new SimpleIntegerProperty();
        rows = new SimpleStringProperty();
        columns = new SimpleStringProperty();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o==model){
            setChanged();
            notifyObservers(arg);
        }
    }

    public void generateMaze(){
        model.generateMaze(Integer.parseInt(rows.get()),Integer.parseInt(columns.get()));
    }
    public void solveMaze() {model.solveMaze();}
    public void moveCharacter(KeyCode movement){
        model.moveCharacter(movement);
    }
    public Maze getMaze() {
        return model.getMaze();
    }
    public int getCharacterPositionRow() {
        return model.getCharacterPositionRow();
    }
    public int getCharacterPositionColumn() { return model.getCharacterPositionColumn(); }
    public String getGenerateString() {return model.getGenerate();}
    public String getSolveString() {return model.getSolve();}
    public Solution getSolution() {
        return model.getSolution();
    }
    public void LoadMaze(File file) {
        model.LoadMaze(file);
    }
    public void SaveMaze(File file) { model.SaveMaze(file); }
    public void StopServers() {
        model.stopServers();
    }
    public void IntialProperties() {
        model.IntialProperties();
    }
    public void SetPropSolve(String s) {
        model.SetPropertiesSolverServer(s);
    }
    public void SetPropGenerate(String s) {
        model.SetPropertiesGenerateServer(s);
    }
    public void RemoveSol() {
        model.RemoveSol();
    }
    public String  getRows() {
        return rows.get();
    }
    public void setRows(String rows) {
        this.rows.set(rows);
    }
    public String  getColumns() {
        return columns.get();
    }
    public void setColumns(String columns) {
        this.columns.set(columns);
    }
}
