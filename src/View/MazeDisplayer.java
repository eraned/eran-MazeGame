package View;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * This class is responsible for all graphical representation of the maze within the game,
 * any update in the graphics of the maze is implemented by this class.
 */
public class MazeDisplayer extends Canvas {

    private Maze maze;
    public int PlayerPositionRow;
    public int PlayerPositionCol;
    private int goalPositionRow;
    private  int getGoalPositionCol;
    private Solution solution;

    public void SetMaze(Maze maze) {
        this.maze = maze;
        redraw();
    }

    public void SetPlayerPosition(int row, int column) {
        PlayerPositionRow = row;
        PlayerPositionCol = column;
        redraw();
    }
    public void SetGoalPosition(int rowIndex, int columnIndex) {
        goalPositionRow=rowIndex;
        getGoalPositionCol=columnIndex;
        redraw();
    }
    public int getPlayerPositionRow() {
        return PlayerPositionRow;
    }
    public int getPlayerPositionCol() {
        return PlayerPositionCol;
    }


    public void redraw() {
        if (maze != null) {
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            double cellHeight = canvasHeight / maze.Struct[0].length;
            double cellWidth = canvasWidth / maze.Struct.length;

            try {
                Image wallImage = new Image(new FileInputStream(ImageFileNameWall.get()));
                Image characterImage = new Image(new FileInputStream(ImageFileNameCharacter.get()));
                Image solImage= new Image(new FileInputStream(ImageFileNameSolution.get()));
                Image goalImage= new Image(new FileInputStream(ImageFileNameGoal.get()));
                GraphicsContext gc = getGraphicsContext2D(); //This class is used to issue draw calls to a Canvas using a buffer.
                gc.clearRect(0, 0, getWidth(), getHeight()); //Intersects the current clip with the specified rectangle.

                for (int i = 0; i < maze.Struct.length; i++) {
                    for (int j = 0; j < maze.Struct[i].length; j++) {
                        if (maze.Struct[i][j] == 1) {
                            gc.drawImage(wallImage,i * cellWidth, j* cellHeight, cellWidth, cellHeight); //if its 1 then put the pic of a wall
                        }
                        if (maze.Struct[i][j] == 2) {
                            gc.drawImage(solImage,i * cellWidth, j* cellHeight, cellWidth, cellHeight); //if its 2 then put the pic of a soll
                        }
                        if (maze.getGoalPosition().getRowIndex()==i && maze.getGoalPosition().getColIndex()==j) {
                            gc.drawImage(goalImage,i * cellWidth, j* cellHeight,  cellWidth, cellHeight);
                        }
                    }
                }
                gc.drawImage(characterImage, PlayerPositionRow *cellWidth , PlayerPositionCol * cellHeight,  cellWidth, cellHeight);;
            } catch (FileNotFoundException e) {
                //e.printStackTrace();
            }
        }
    }

    private StringProperty ImageFileNameWall = new SimpleStringProperty();
    private StringProperty ImageFileNameCharacter = new SimpleStringProperty();
    private StringProperty ImageFileNameSolution = new SimpleStringProperty();
    private StringProperty ImageFileNameGoal = new SimpleStringProperty();

    public String getImageFileNameWall() {
        return ImageFileNameWall.get();
    }
    public void setImageFileNameWall(String imageFileNameWall) {
        this.ImageFileNameWall.set(imageFileNameWall);
    }
    public String getImageFileNameGoal() {
        return ImageFileNameGoal.get();
    }
    public void setImageFileNameGoal(String imageFileNameGoal) {
        this.ImageFileNameGoal.set(imageFileNameGoal);
    }
    public String getImageFileNameCharacter() {
        return ImageFileNameCharacter.get();
    }
    public void setImageFileNameCharacter(String imageFileNameCharacter) {this.ImageFileNameCharacter.set(imageFileNameCharacter); }
    public String getImageFileNameSolution() {
        return ImageFileNameSolution.get();
    }
    public void setImageFileNameSolution(String imageFileNameSolution) {this.ImageFileNameSolution.set(imageFileNameSolution);}


    public void setSolution(Solution solution) {
        this.solution = solution;
        if (this.solution == null)
            System.out.println("No Solution");
        else {
            String s = (this.solution.getSolutionPath()).toString();
            String[] arr = s.split("[\\,\\[\\]\\{\\}\\s]+");
            for (int i = 1; i < arr.length - 1; i += 2) {
                String row = arr[i];
                String col = arr[++i];
                if ((Integer.parseInt(row) != maze.getStartPosition().getRowIndex() || Integer.parseInt(col) != maze.getStartPosition().getColIndex()) && (Integer.parseInt(row) != maze.getGoalPosition().getRowIndex() && Integer.parseInt(col) != maze.getGoalPosition().getColIndex()))
                    maze.Struct[Integer.parseInt(row)][Integer.parseInt(col)] = 2;
            }
        }
        redraw();
    }

    public double getCellHeight() {
        double canvasHeight = getHeight();
        return canvasHeight / maze.Struct[0].length;
    }

    public double getCellWidth() {
        double canvasWidth = getWidth();
       return canvasWidth / maze.Struct.length;
    }

    public void RemoveSolCharacter() {
        maze.Struct[PlayerPositionRow][PlayerPositionCol]=0;
        redraw();
    }
}
