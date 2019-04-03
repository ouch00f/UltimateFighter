package sample;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.sql.*;

public class Main extends Application {
    int ground = 300;

    // Character/object instantiation
    Squidward SQUIDWARD = new Squidward(675, ground);
    Shaggy SHAGGY = new Shaggy(25, ground);


    public Main() throws IOException {

    }
    @Override
    public void start(Stage primaryStage) {

        Group root = new Group();

        primaryStage.setTitle("");
        Scene scene = new Scene(root, 800, 450);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.setOnHiding( event -> {Runtime.getRuntime().exit(0);} );//Ends all processes of application on stage close
        root.getChildren().add(SHAGGY);
        root.getChildren().add(SQUIDWARD);


        //Key event filters
        scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {//key press controls
            this.keyPress(keyEvent.getCode());
        });

        scene.addEventFilter(KeyEvent.KEY_RELEASED, keyEvent -> {//key press controls
            this.keyRelease(keyEvent.getCode());
        });

        //All timing and motion tools
        Timer timer = new Timer(45, e -> {
            checkConditions(SHAGGY);
            checkConditions(SQUIDWARD);
        });
        timer.start();


    }

    public void keyPress(KeyCode keycode){//when keys are pressed

        switch(keycode){

                // SHAGGY CONTROLS
            case D:
                SHAGGY.runRight();
                break;
            case A:
                SHAGGY.runLeft();
                break;
            case C:
                SHAGGY.attack(SQUIDWARD);
                //SHAGGY.attack(SQUIDWARD, SHAGGY.getLayoutX(), SQUIDWARD.getLayoutX());
                break;
            case S:
                SHAGGY.jumpVariable = SHAGGY.jumpVariable/2;
                break;


                // SQUIDWARD CONTROLS
            case L:
                SQUIDWARD.runRight();
                break;
            case J:
                SQUIDWARD.runLeft();
                break;
            case P:
                SQUIDWARD.attack(SHAGGY);
                //SQUIDWARD.attack(SHAGGY,SQUIDWARD.getLayoutX(), SHAGGY.getLayoutX());
            case K:
                SQUIDWARD.jumpVariable = SQUIDWARD.jumpVariable/2;
        }
       /*  System.out.println("Shaggy Position(X):"+SHAGGY.getLayoutX());
        System.out.println("Shaggy Health:"+SHAGGY.getHealth());
*/
    }

    public void keyRelease(KeyCode keycode){//when keys are released

        switch (keycode){
            case W:
                SHAGGY.jump();
                break;
            case D:
                SHAGGY.standRight();
                break;
            case A:
                SHAGGY.standLeft();
                break;

            case C:
                // Upon attacking, this key release will set the image for shaggy position depending on where he faces.
                if (SHAGGY.isRunningLeft || !SHAGGY.getFacingRight()){
                    SHAGGY.standLeft();
                } else {
                    SHAGGY.standRight();
                }
                break;
            case I:
                SQUIDWARD.jump();
                break;
            case L:
                SQUIDWARD.standRight();
                break;
            case J:
                SQUIDWARD.standLeft();
                break;

            // Upon attacking, this key release will set the image for squidward position depending on where he faces.
            case P:
                if (SQUIDWARD.isRunningLeft() || !SQUIDWARD.getFacingRight()){
                    SQUIDWARD.standLeft();
                } else {
                    SQUIDWARD.standRight();
                }
                break;
        }
    }




    public void checkConditions(Character player){//continuously checks conditions on the tick of a timer
        System.out.println("The characters are touching: "+ SHAGGY.isTouching(SQUIDWARD));//////////////////////////////////////////////////////////////////////////////////////////this is spamming the sout fyi

        if(player.isRunningRight()){
            player.setLayoutX(player.getLayoutX()+player.getSpeed());
        }

        if(player.isRunningLeft()){
            player.setLayoutX(player.getLayoutX()-player.getSpeed());
        }

        if(player.getLayoutY() <= ground){//when the player is falling
            //player.jump();
            player.fall();

            //check to see if player has changed to hit ground
            if(player.getLayoutY() >= ground) {
                player.land();
            }
        }

        //boundaries of the stage and ensuring player does not exceed them
        if(player.getLayoutX()>750){
            player.setLayoutX(750);
        }
        if(player.getLayoutX()<0){
            player.setLayoutX(0);
        }

    }

    public static void main(String[] args) {
        launch(args);
        connect();
        createTable();
    }

    // final fields used for sql string syntax
    private static final String insertSQL = "INSERT INTO HighScores(dmgDealt) VALUES(?)";
    //private static final int newScore = 0;
    private static final String url = "jdbc:sqlite:/Users/rain/Desktop/FINAL/master.db";

    // Connection method, perhaps replaced with built in database tool(?)
    private static void connect() {
        Connection conn = null;
        try {

            //String url = "jdbc:sqlite:/Users/rain/Desktop/Squidward_VS_Shaggy/master.db";;

            conn = DriverManager.getConnection(url);

            System.out.println("Connection established.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // Creates a new table in the db
    private static void createTable(){
        String createT = "CREATE TABLE IF NOT EXISTS HighScores(dmgDealt int)";
        try (Connection conn = DriverManager.getConnection(url);
             Statement statement = conn.createStatement()) {
            statement.execute(createT);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    // insert method that currently does not work
    private static void insert(int score){
        try{
            Connection conn = DriverManager.getConnection(url);
            PreparedStatement statement = conn.prepareStatement(insertSQL);

            // tasting for parameter index, unsure what it is.
            statement.setInt('a', score);

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    private void newScore(int oldScore, int newScore){
        if (newScore > oldScore) {
            insert(newScore);
        }
    }

    // Suppose to select all row in the HighScore DB & print them out.
    private void selectAll(){
        String sql = "SELECT dmgDealt FROM HighScores";

        // not sure if SQL syntax is correct
        String minToMax = "SELECT MAX(dmgDealt) AS max FROM HighScores";

        // Might have conn = this.connect(); (?)**
        try(Connection conn = DriverManager.getConnection(url);
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql)){

            while (rs.next()){
                System.out.println(rs.getInt("dmgDealt"));
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

}