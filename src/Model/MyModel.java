package Model;

import Client.Client;
import IO.MyDecompressorInputStream;
import Server.Server;
import View.Main;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;
import Client.IClientStrategy;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MyModel extends Observable implements IModel {

    private ExecutorService threadPool = Executors.newCachedThreadPool();
    Server GenerateServer;
    Server SolverServer;
    private int PlayerPositionRow;
    private int PlayerPositionCol;
    private int goalPositionRow;
    private int goalPositionColumn;
    private int rows;
    private int columns;
    private Maze maze;
    private Solution s;
    private java.lang.String generate;
    private java.lang.String solve;

    //constructor
    public MyModel() {
        GenerateServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        SolverServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
    }


    public void startServers() {
        GenerateServer.start();
        SolverServer.start();
    }

    public void stopServers() {
        GenerateServer.stop();
        SolverServer.stop();
        Main.ExitSystem();
    }

    /**
     * method that create new maze according to the inpute that the user insert in the textbox
     * and then press on the generatemaze button conect to server that generate mazes and display it on the grid.
     * @param width
     * @param height
     */
    @Override
    public void generateMaze(int width, int height) {
        s=null;
        threadPool.execute(() -> {
            try {
                Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                    public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                        try {
                            ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                            ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                            toServer.flush();
                            int[] mazeDimensions = new int[]{width, height};
                            toServer.writeObject(mazeDimensions);
                            toServer.flush();
                            byte[] compressedMaze = (byte[]) fromServer.readObject();
                            InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                            byte[] decompressedMaze = new byte[100000];
                            is.read(decompressedMaze);
                            Maze mymaze = new Maze(decompressedMaze);
                            maze = mymaze;
                            PlayerPositionRow = maze.getStartPosition().getRowIndex();
                            PlayerPositionCol = maze.getStartPosition().getColIndex();
                            goalPositionRow=maze.getGoalPosition().getRowIndex();
                            goalPositionColumn=maze.getGoalPosition().getColIndex();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                client.communicateWithServer();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                setChanged();
                notifyObservers("maze");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * When the user reaches the point where he can not solve the maze
     * he can use our solution server that based on 3 algorithms to solve a maze
     * and they will show him on the maze the path to the end point
     */
    @Override
    public void solveMaze() {
        threadPool.execute(() -> {
            maze.setStartPotision(new Position(PlayerPositionRow,PlayerPositionCol));
            try {
                Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                    public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                        try {
                            ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                            ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                            toServer.flush();
                            toServer.writeObject(maze);
                            toServer.flush();
                            s = (Solution) fromServer.readObject();
                        } catch (Exception var10) {
                            var10.printStackTrace();
                        }

                    }
                });
                client.communicateWithServer();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            setChanged();
            notifyObservers("solve");
        });
    }

    /**
     * This method represents the movement of the player on the labyrinth,
     * each button on the keyboard should be translated into a change of the player on the structure of the maze
     * @param movement
     */

    @Override
    public void moveCharacter(KeyCode movement) {
        switch (movement) {
            case LEFT:
                if (PlayerPositionRow != 0) {
                    if (maze.Struct[PlayerPositionRow - 1][PlayerPositionCol] != 1)
                        PlayerPositionRow--;
                }
                break;
            case NUMPAD9:
                if ((PlayerPositionRow != 0)&&(PlayerPositionCol != maze.Struct[0].length - 1)) {
                    if (maze.Struct[PlayerPositionRow + 1][PlayerPositionCol -1] != 1)
                        PlayerPositionRow++;
                    PlayerPositionCol--;
                }
                break;
            case NUMPAD3:
                if ((PlayerPositionRow != maze.Struct.length - 1)&&(PlayerPositionCol != maze.Struct[0].length - 1)) {
                    if (maze.Struct[PlayerPositionRow + 1][PlayerPositionCol +1] != 1)
                        PlayerPositionRow++;
                    PlayerPositionCol++;
                }
                break;
            case NUMPAD7:
                if ((PlayerPositionRow != 0)&&(PlayerPositionCol != 0)) {
                    if (maze.Struct[PlayerPositionRow - 1][PlayerPositionCol -1] != 1)
                        PlayerPositionRow--;
                    PlayerPositionCol--;
                }
                break;
            case NUMPAD1:
                if ((PlayerPositionCol != 0)&&(PlayerPositionRow != maze.Struct.length - 1)) {
                    if (maze.Struct[PlayerPositionRow - 1][PlayerPositionCol +1] != 1)
                        PlayerPositionRow--;
                    PlayerPositionCol++;
                }
                break;
            case NUMPAD4:
                if (PlayerPositionRow != 0) {
                    if (maze.Struct[PlayerPositionRow - 1][PlayerPositionCol] != 1)
                        PlayerPositionRow--;
                }
                break;
            case RIGHT:
                if (PlayerPositionRow != maze.Struct.length - 1) {
                    if (maze.Struct[PlayerPositionRow + 1][PlayerPositionCol] != 1)
                        PlayerPositionRow++;
                }
                break;
            case NUMPAD6:
                if (PlayerPositionRow != maze.Struct.length - 1) {
                    if (maze.Struct[PlayerPositionRow + 1][PlayerPositionCol] != 1)
                        PlayerPositionRow++;
                }
                break;
            case DOWN:
                if (PlayerPositionCol != maze.Struct[0].length - 1) {
                    if (maze.Struct[PlayerPositionRow][PlayerPositionCol + 1] != 1)
                        PlayerPositionCol++;
                }
                break;
            case NUMPAD2:
                if (PlayerPositionCol != maze.Struct[0].length - 1) {
                    if (maze.Struct[PlayerPositionRow][PlayerPositionCol + 1] != 1)
                        PlayerPositionCol++;
                }
                break;
            case UP:
                if (PlayerPositionCol != 0) {
                    if (maze.Struct[PlayerPositionRow][PlayerPositionCol - 1] != 1)
                        PlayerPositionCol--;
                }
                break;
            case NUMPAD8:
                if (PlayerPositionCol != 0) {
                    if (maze.Struct[PlayerPositionRow][PlayerPositionCol - 1] != 1)
                        PlayerPositionCol--;
                }
                break;
        }
        setChanged();
        notifyObservers("move");
    }


    @Override
    public int getCharacterPositionRow() {
        return PlayerPositionRow;
    }

    @Override
    public int getCharacterPositionColumn() {
        return PlayerPositionCol;
    }

    /**
     * This method represents the user's option to load a maze that started in the past
     * and wants to continue trying to solve it. The class receives a maze file type and loads the maze on the grid
     * @param file
     */
    public void LoadMaze(File file) {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
            Maze m = (Maze) in.readObject();
            PlayerPositionRow = (int) in.readObject();
            PlayerPositionCol = (int) in.readObject();
            maze = new Maze(m.Struct);
            maze.setStartPotision( new Position(PlayerPositionRow, PlayerPositionCol));
            maze.setGoalPosition(new Position(m.getGoalPosition().getRowIndex(), m.getGoalPosition().getColIndex()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        setChanged();
        notifyObservers("load");
    }

    /**
     * This method represents the user's ability to save the maze that he currently plays in a specific place on the computer to be selected.
     * @param file
     */
    public void SaveMaze(File file) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(maze);
            out.writeObject(PlayerPositionRow);
            out.writeObject(PlayerPositionCol);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method defines the initial settings of the game, you can change the settings but initially must provide initial settings.
     */
    public void IntialProperties() {
        Properties prop = new Properties();
        try (FileInputStream input = new FileInputStream("./config.properties")) {
            prop.load(input);
            generate = prop.getProperty("generate");
            solve = prop.getProperty("algorithm");
            setChanged();
            notifyObservers("prop");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * This method allows you to change the solver server settings according to the user selection.
     * @param s
     */
    public void SetPropertiesSolverServer(java.lang.String s) {
        java.lang.String thred = ThredProperties();
        Properties prop = new Properties();

        try (FileOutputStream out = new FileOutputStream("./config.properties")) {

            switch (s) {
                case "BreadthFirstSearch": {
                    prop.setProperty("algorithm", "BreadthFirstSearch");
                    solve="BreadthFirstSearch";
                    prop.setProperty("generate", generate);
                    prop.setProperty("threadNum", thred);
                    prop.store(out, null);
                    break;
                }
                case "DepthFirstSearch": {
                    prop.setProperty("algorithm", "DepthFirstSearch");
                    solve="DepthFirstSearch";
                    prop.setProperty("generate", generate);
                    prop.setProperty("threadNum", thred);
                    prop.store(out, null);
                    break;
                }
                case "BestFirstSearch": {
                    prop.setProperty("algorithm", "BestFirstSearch");
                    solve="BestFirstSearch";
                    prop.setProperty("generate", generate);
                    prop.setProperty("threadNum", thred);
                    prop.store(out, null);
                    break;
                }
            }
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * This method allows you to change the generate server settings according to the user selection.
     * @param s
     */
    public void SetPropertiesGenerateServer(java.lang.String s) {
        java.lang.String thred = ThredProperties();
        Properties prop = new Properties();

        try (FileOutputStream out = new FileOutputStream("./config.properties")) {

            switch (s) {
                case "SimpleMazeGenerator": {
                    System.out.println("ggg");
                    prop.setProperty("algorithm", solve);
                    prop.setProperty("generate", "SimpleMazeGenerator");
                    generate="SimpleMazeGenerator";
                    prop.setProperty("threadNum", thred);
                    prop.store(out, null);
                    break;
                }
                case "MyMazeGenerator": {
                    prop.setProperty("algorithm", solve);
                    prop.setProperty("generate", "MyMazeGenerator");
                    generate="MyMazeGenerator";
                    prop.setProperty("threadNum", thred);
                    prop.store(out, null);
                    break;
                }
            }
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private java.lang.String ThredProperties() {
        java.lang.String s=null;
        Properties prop = new Properties();

        try (FileInputStream input = new FileInputStream("./config.properties")) {
            prop.load(input);
            s=prop.getProperty("threadNum");
            input.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    /**
     * This method allows after the user has decided to use the server to solve the maze to remove the track shown on the maze
     */
    public void RemoveSol()
    {
        for (int i = 0; i <maze.Struct.length ; i++) {
            for (int j = 0; j <maze.Struct[0].length ; j++) {
                if (maze.Struct[i][j]== 2)
                    maze.Struct[i][j]= 0;
            }
        }
        setChanged();
        notifyObservers("RemoveSol");
    }

    @Override
    public Maze getMaze() {
        return maze;
    }
    public Solution getSolution() {
        return s;
    }
    public java.lang.String getGenerate() {
        return generate;
    }
    public java.lang.String getSolve() {
        return solve;
    }
}