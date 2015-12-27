/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crashcourseclient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 *
 * @author johanwendt
 */
public class CrashCourseClient extends Application implements Constants{
    private Scene scene;
    private static Group root = new Group();
    
    private DataInputStream fromServer;
    private DataOutputStream toServer;
    
    private HashMap<Integer, ImageView> objectsOnScreen = new HashMap<>(); 
    private HashMap<Integer, Image> imageMap = new HashMap<>(); 
    private HashMap<Integer, Rectangle> lifeMeters = new HashMap<>(); 

    private AudioHandler audiohandler;
    
    private boolean enteredIP = false;
    
    private String IP;

    @Override
    public void start(Stage primaryStage) {
        loadImages();
        setTheStage(primaryStage);
        
        MissingTextPrompt(primaryStage);
        //promptForIP();
        createEventHandling();
        connectToServer();
        
        audiohandler = new AudioHandler();
    }
    
    private void setTheStage(Stage primaryStage) {
        scene = new Scene(root, GAME_WIDTH, GAME_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Crash Course");
        
        primaryStage.setOnCloseRequest(c -> {
            System.exit(0);
        });
        
        primaryStage.show();
        
        
    }
    private void removeFromScreen(Node toRemove) {
        root.getChildren().remove(toRemove);
    }
    private void addToScreen(Node toAdd) {
        root.getChildren().add(toAdd);
    }
    private void createEventHandling() {
        scene.setOnKeyPressed(e -> {
            try {
                if(e.getCode().equals(KeyCode.UP)) {
                    toServer.write(PLAYER_INPUT_UP_PRESSED);
                }
                if(e.getCode().equals(KeyCode.RIGHT)) {
                    toServer.write(PLAYER_INPUT_RIGHT_PRESSED);
                }
                if(e.getCode().equals(KeyCode.DOWN)) {
                    toServer.write(PLAYER_INPUT_DOWN_PRESSED);
                }
                if(e.getCode().equals(KeyCode.LEFT)) {
                    toServer.write(PLAYER_INPUT_LEFT_PRESSED);
                }
                if(e.getCode().equals(KeyCode.R)) {
             //       reStart();
                }
            }
            catch (IOException ex) {
                Logger.getLogger(CrashCourseClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        scene.setOnKeyReleased(e -> {
            try {
                if(e.getCode().equals(KeyCode.UP)) {
                    toServer.write(PLAYER_INPUT_UP_RELEASED);
                }
                if(e.getCode().equals(KeyCode.RIGHT)) {
                    toServer.write(PLAYER_INPUT_RIGHT_RELEASED);
                } 
                if(e.getCode().equals(KeyCode.DOWN)) {
                    toServer.write(PLAYER_INPUT_DOWN_RELEASED);
                }
                if(e.getCode().equals(KeyCode.LEFT)) {
                    toServer.write(PLAYER_INPUT_LEFT_RELEASED);
                }
            }
            catch (IOException ex) {
                Logger.getLogger(CrashCourseClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
 
    private void reStart() {
      //  ObjectHandler.resetAllObjects();
    //    trackBuilder.buildStandardTrack();
    //    createPlayers();
      //  setPlayerStartControls();
    }
    private void createPlayers() {
        //CrashCourse crashCourse, VisibleObjects deatils, Players playerDetails, int xLocation, int yLocation, int startSpeed
    //    playerOne = new Player(VisibleObjects.PLAYER_ONE, Players.PLAYER_ONE);
     //   playerTwo = new Player(VisibleObjects.PLAYER_TWO, Players.PLAYER_TWO);
    }
    /**
    private void setPlayerStartControls() {
        playerOne.setControls(KeyCode.UP, KeyCode.RIGHT, KeyCode.LEFT, KeyCode.DOWN);
        playerTwo.setControls(KeyCode.W, KeyCode.D, KeyCode.A, KeyCode.S);
    }
    * */
    private void connectToServer() {
        try {
            Socket tempSocket = new Socket(IP, SOCKET);
            
            DataInputStream fromServerTemp = new DataInputStream(tempSocket.getInputStream());
            
            int newSocket = fromServerTemp.readInt();
            
            Socket socket = new Socket(IP, newSocket);
            
            fromServer = new DataInputStream(socket.getInputStream());
            
            toServer = new DataOutputStream(socket.getOutputStream());
            
        } 
        catch (IOException ex) {
            ex.printStackTrace();
        }
        
    
    
        new Thread(() -> {
            while(true) {
                try {
                    int takeAction = fromServer.readByte();
                    
                   // System.out.println("takeAction: " + takeAction);
                    if(takeAction == ACTION_CREATE_NEW) {
                        loadNewObjects();
                    }
                    else if(takeAction == ACTION_DESTROY_OLD) {
                        destroyOldObject();
                    }
                    else if(takeAction == ACTION_SET_POSITION) {
                        setPostion();
                    }
                    else if(takeAction == ACTION_CHANGE_APPEARANCE) {
                        changeAppearance();
                    }
                    else if(takeAction == ACTION_PLAY_SOUND) {
                        playSound();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(CrashCourseClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
        
    }
    private void loadImages() {
        Image playerOne1 = new Image(getClass().getResourceAsStream("player-one1.png"), SIZE_PLAYER_ONE_WIDTH, SIZE_PLAYER_ONE_HEIGHT, true, false);
        Image playerOne2 = new Image(getClass().getResourceAsStream("player-one2.png"), SIZE_PLAYER_ONE_WIDTH, SIZE_PLAYER_ONE_HEIGHT, true, false);
        Image playerOne3 = new Image(getClass().getResourceAsStream("player-one3.png"), SIZE_PLAYER_ONE_WIDTH, SIZE_PLAYER_ONE_HEIGHT, true, false);
        Image playerOne4 = new Image(getClass().getResourceAsStream("player-one4.png"), SIZE_PLAYER_ONE_WIDTH, SIZE_PLAYER_ONE_HEIGHT, true, false);
        Image playerOne5 = new Image(getClass().getResourceAsStream("player-one5.png"), SIZE_PLAYER_ONE_WIDTH, SIZE_PLAYER_ONE_HEIGHT, true, false);
        Image playerOne6 = new Image(getClass().getResourceAsStream("player-one6.png"), SIZE_PLAYER_ONE_WIDTH, SIZE_PLAYER_ONE_HEIGHT, true, false);
        Image playerOne7 = new Image(getClass().getResourceAsStream("player-one7.png"), SIZE_PLAYER_ONE_WIDTH, SIZE_PLAYER_ONE_HEIGHT, true, false);

        imageMap.put(IMAGE_PLAYER_ONE_ONE, playerOne1);
        imageMap.put(IMAGE_PLAYER_ONE_TWO, playerOne2);
        imageMap.put(IMAGE_PLAYER_ONE_THREE, playerOne3);
        imageMap.put(IMAGE_PLAYER_ONE_FOUR, playerOne4);
        imageMap.put(IMAGE_PLAYER_ONE_FIVE, playerOne5);
        imageMap.put(IMAGE_PLAYER_ONE_SIX, playerOne5);
        imageMap.put(IMAGE_PLAYER_ONE_SEVEN, playerOne6);
        
        Image playerTwo1 = new Image(getClass().getResourceAsStream("player-two1.png"), SIZE_PLAYER_TWO_WIDTH, SIZE_PLAYER_TWO_HEIGHT, true, false);
        Image playerTwo2 = new Image(getClass().getResourceAsStream("player-two2.png"), SIZE_PLAYER_TWO_WIDTH, SIZE_PLAYER_TWO_HEIGHT, true, false);
        Image playerTwo3 = new Image(getClass().getResourceAsStream("player-two3.png"), SIZE_PLAYER_TWO_WIDTH, SIZE_PLAYER_TWO_HEIGHT, true, false);
        Image playerTwo4 = new Image(getClass().getResourceAsStream("player-two4.png"), SIZE_PLAYER_TWO_WIDTH, SIZE_PLAYER_TWO_HEIGHT, true, false);
        Image playerTwo5 = new Image(getClass().getResourceAsStream("player-two5.png"), SIZE_PLAYER_TWO_WIDTH, SIZE_PLAYER_TWO_HEIGHT, true, false);
        Image playerTwo6 = new Image(getClass().getResourceAsStream("player-two6.png"), SIZE_PLAYER_TWO_WIDTH, SIZE_PLAYER_TWO_HEIGHT, true, false);
        Image playerTwo7 = new Image(getClass().getResourceAsStream("player-two7.png"), SIZE_PLAYER_TWO_WIDTH, SIZE_PLAYER_TWO_HEIGHT, true, false);

        imageMap.put(IMAGE_PLAYER_TWO_ONE, playerTwo1);
        imageMap.put(IMAGE_PLAYER_TWO_TWO, playerTwo2);
        imageMap.put(IMAGE_PLAYER_TWO_THREE, playerTwo3);
        imageMap.put(IMAGE_PLAYER_TWO_FOUR, playerTwo4);
        imageMap.put(IMAGE_PLAYER_TWO_FIVE, playerTwo5);
        imageMap.put(IMAGE_PLAYER_TWO_SIX, playerTwo6);
        imageMap.put(IMAGE_PLAYER_TWO_SEVEN, playerTwo7);
        
        Image hinder = new Image(getClass().getResourceAsStream("hinder.png"), SIZE_SMALL_HINDER_WIDTH, SIZE_SMALL_HINDER_HEIGHT, true, false);
        Image Hhinder = new Image(getClass().getResourceAsStream("horizontal-hinders.png"), SIZE_HORIZONTAL_FULLSCREEN_HINDER_WIDTH, SIZE_HORIZONTAL_FULLSCREEN_HINDER_HEIGHT, false, false);
        Image Vhinder = new Image(getClass().getResourceAsStream("vertical-hinders.png"), SIZE_VERTICAL_FULLSCREEN_HINDER_WIDTH, SIZE_VERTICAL_FULLSCREEN_HINDER_HEIGHT, false, false);
        Image makeFasterBonus = new Image(getClass().getResourceAsStream("fuelpump.png"), SIZE_MAKE_FASTER_BONUS_WIDTH, SIZE_MAKE_FASTER_BONUS_HEIGHT, true, false);
        Image bomb = new Image(getClass().getResourceAsStream("bomb.png"), SIZE_BOMB_WIDTH, SIZE_BOMB_HEIGHT, true, false);
        Image explosion = new Image(getClass().getResourceAsStream("explosion4.gif"), SIZE_EXPLOSION_WIDTH, SIZE_EXPLOSION_HEIGHT, true, false);
        Image crater = new Image(getClass().getResourceAsStream("crater.png"), SIZE_CRATER_WIDTH, SIZE_CRATER_HEIGHT, true, false);
        Image lifeMeter = new Image(getClass().getResourceAsStream("lifeMeter.png"), SIZE_LIFE_METER_WIDTH, SIZE_LIFE_METER_HEIGHT, true, false);
        Image backGroundImage = new Image(getClass().getResourceAsStream("background.png"), GAME_WIDTH, GAME_HEIGHT, true, false);
        ImageView backGround = new ImageView(backGroundImage);
        addToScreen(backGround);
        
        
        imageMap.put(IMAGE_HINDER, hinder);
        imageMap.put(IMAGE_HORIZONTAL_FULLSCREN_HINDER, Hhinder);
        imageMap.put(IMAGE_VERTICAL_FULLSCREN_HINDER, Vhinder);
        imageMap.put(IMAGE_MAKE_FASTER_BONUS, makeFasterBonus);
        imageMap.put(IMAGE_BOMB, bomb);
        imageMap.put(IMAGE_EXPLOSION, explosion);
        imageMap.put(IMAGE_CRATER, crater);
        imageMap.put(IMAGE_LIFE_METER, lifeMeter);
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    private void loadNewObjects() {
        try {

            int objectNumber = readFourBytesAsInt();
            int xPosition = readFourBytesAsInt();
            int yPosition = readFourBytesAsInt();
            int rotation = readFourBytesAsInt();
            int imageNumber = fromServer.readByte();

            ImageView onScreen = new ImageView(imageMap.get(imageNumber));
            if(imageNumber == IMAGE_EXPLOSION) {
                onScreen.setBlendMode(BlendMode.COLOR_DODGE);
            }
            onScreen.toFront();

            Platform.runLater( () -> {
                addToScreen(onScreen);
            });

            objectsOnScreen.put(objectNumber, onScreen);
            
            onScreen.setTranslateX(xPosition);
            onScreen.setTranslateY(yPosition);
            onScreen.setRotate(rotation);
            
            
            if(imageNumber == IMAGE_LIFE_METER) {
                int lifeFactor = fromServer.readByte();
                int meterXLocation = readFourBytesAsInt();
                int meterYLocation = readFourBytesAsInt();
                int meterWidth = fromServer.readByte();
                int meterHeight = fromServer.readByte();;

                Rectangle lifeLeft = new Rectangle(meterXLocation, meterYLocation, meterWidth, meterHeight);
                lifeMeters.put(objectNumber, lifeLeft);
                lifeLeft.setFill(Color.GREEN);
                if(lifeFactor < 40) {
                    lifeLeft.setFill(Color.YELLOW);
                }
                if(lifeFactor < 20) {
                    lifeLeft.setFill(Color.RED);
                }
                Platform.runLater(() -> {
                    addToScreen(lifeLeft);
                    onScreen.toFront();
                });
            }
        }
        catch (IOException ex) {
            Logger.getLogger(CrashCourseClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void destroyOldObject() {
        int objectNumber = readFourBytesAsInt();
        ImageView toRemove = objectsOnScreen.get(objectNumber);

        Platform.runLater( () -> {
            removeFromScreen(toRemove);
        });

        objectsOnScreen.remove(toRemove);
        if(lifeMeters.keySet().contains(objectNumber)) {
            Platform.runLater( () -> {
                removeFromScreen(lifeMeters.get(objectNumber));
                lifeMeters.remove(objectNumber);
            });
        }
    }

    private void setPostion() {
        int objectNumber = readFourBytesAsInt();
        int xPosition = readFourBytesAsInt();
        int yPosition = readFourBytesAsInt();
        int rotation = readFourBytesAsInt();

        ImageView toMove = objectsOnScreen.get(objectNumber);
        toMove.setTranslateX(xPosition);
        toMove.setTranslateY(yPosition);
        toMove.setRotate(rotation);
    }
    private void changeAppearance() {
        try {
            int objectNumber = readFourBytesAsInt();
            int imageNumber = fromServer.readByte();
            ImageView changeAppearance = objectsOnScreen.get(objectNumber);
            changeAppearance.setImage(imageMap.get(imageNumber));
        } catch (IOException ex) {
            Logger.getLogger(CrashCourseClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void playSound() {
        try {
            int audioNumber = fromServer.readByte();
            int volume = fromServer.readByte();
            System.out.println("audionumber: " + audioNumber);
            System.out.println("volume: " + volume);
            audiohandler.playSound(audioNumber, volume);
        } catch (IOException ex) {
            Logger.getLogger(CrashCourseClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private int byteArrayToInt(byte[] b) {
        return   b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }
    private int readFourBytesAsInt() {
        byte[] input = new byte[4];
        try {
            fromServer.read(input, 0, 4);
        } catch (IOException ex) {
            Logger.getLogger(CrashCourseClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return byteArrayToInt(input);
    }

    private void setEnteredIP() {
        enteredIP = true;
    }
    
    private void MissingTextPrompt(Window owner) {
      final Stage dialog = new Stage();

      dialog.setTitle("Enter host IP");
      dialog.initOwner(owner);
      dialog.initStyle(StageStyle.UTILITY);
      dialog.initModality(Modality.WINDOW_MODAL);
      dialog.setX(owner.getX() + owner.getWidth() / 2);
      dialog.setY(owner.getY() + owner.getHeight() / 2);

      final TextField textField = new TextField();
      final Button submitButton = new Button("Submit");
      submitButton.setDefaultButton(true);
      submitButton.setOnAction(e -> {
          dialog.close();
      });
      textField.setMinHeight(TextField.USE_PREF_SIZE);

      final VBox layout = new VBox(10);
      layout.setAlignment(Pos.CENTER_RIGHT);
      layout.setStyle("-fx-background-color: azure; -fx-padding: 10;");
      layout.getChildren().setAll(
        textField, 
        submitButton
      );

      dialog.setScene(new Scene(layout));
      dialog.showAndWait();

      IP = textField.getText();
    }
}