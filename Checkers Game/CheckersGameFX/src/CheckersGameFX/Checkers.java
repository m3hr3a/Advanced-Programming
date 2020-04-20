package CheckersGameFX;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import java.util.ArrayList;
import javafx.scene.image.Image;

public class Checkers extends Application {
    static int TileSize = 50;
    private static int Dim = 8 ;
    private static Group tileGroup = new Group();
    private static Group pieceGroup = new Group();
    private static Group forceDest = new Group();
    private static Group scoreBoard = new Group();
    static Group errors = new Group();
    private static Group signature = new Group();
    private static Pane root = new Pane();
    private static GameBoard gameBoard;

    private Parent createContent(){
        Player[] players = new Player[2];
        designSP(players);
        root.setPrefSize((Dim + 5) * TileSize,Dim * TileSize);
        return root;
    }
    private static void errorShow(int errorCode, String color){
        switch (errorCode){
            case 0 : {
                Text error01 = new Text();
                Text error02 = new Text();
                textSet(error01, "Game Ended!",(Dim + 0.3)*TileSize,
                        TileSize * 6.5, Color.BLANCHEDALMOND, 15);
                textSet(error02, color + " won!",(Dim + 0.3)*TileSize,
                        TileSize * 7, color.equals("Pink") ? Color.LIGHTPINK : Color.LIGHTSKYBLUE, 15);
                errors.getChildren().addAll(error01,error02);
                break;
            }
            case 3 : {
                Text error3l1 = new Text();
                Text error3l2 = new Text();
                textSet(error3l1, "Notice!",(Dim + 0.3)*TileSize,
                        TileSize * 6.5, Color.BLANCHEDALMOND, 15);
                if(color.equals("Pink"))
                    textSet(error3l2, "Choose a " + color + " piece!",(Dim + 0.3)*TileSize,
                            TileSize * 7, Color.LIGHTPINK, 15);
                else
                    textSet(error3l2, "Choose a " + color + " piece!",(Dim + 0.3)*TileSize,
                            TileSize * 7, Color.LIGHTSKYBLUE, 15);
                errors.getChildren().addAll(error3l1,error3l2);
                break;
            }
            case 4 : {
                Text error4l1 = new Text();
                Text error4l2 = new Text();
                textSet(error4l1, "You cannot move to a ",(Dim + 0.3)*TileSize,
                        TileSize * 6.5, Color.BLANCHEDALMOND, 15);
                textSet(error4l2, "tile with a piece!",(Dim + 0.3)*TileSize,
                        TileSize * 7, Color.BLANCHEDALMOND, 15);
                errors.getChildren().addAll(error4l1,error4l2);
                break;
            }
            case 5 : {
                Text error5 = new Text();
                textSet(error5, "You need to take force jump!",(Dim + 0.3)*TileSize,
                        TileSize * 6.5, Color.BLANCHEDALMOND, 15);
                errors.getChildren().add(error5);
                break;
            }
            case 6 : {
                Text error6 = new Text();
                textSet(error6, "Not a valid movement!",(Dim + 0.3)*TileSize,
                        TileSize * 6.5, Color.BLANCHEDALMOND, 15);
                errors.getChildren().add(error6);
                break;
            }
        }
    }
    private static void disableForceDest(GameBoard gameBoard){
        for(int r = 0; r < 8; r++){
            for(int c = 0; c < 8; c++){
                gameBoard.GB[r][c].tileGraphic(r, c, 0, false, 0);
            }
        }   
    }
    static void mover(int a, int b, Player[] players, GameBoard gameBoard, Group pieceGroup) {
        int s = 0;
        int c1 = (a - 1) % 8;
        int r1 = (a - 1) / 8;
        int c2 = (b - 1) % 8;
        int r2 = (b - 1) / 8;
        boolean diag = (c2 - c1) == 1 | (c1 - c2) == 1;
        String color;

        if (players[0].getTurn())
            color = "Pink";
        else
            color = "Blue";

        if (!players[gameBoard.GB[r1][c1].piece.getPlayerId()].getTurn())
            errorShow(3, color);
        else {
            if (gameBoard.GB[r2][c2].havePiece()) {
                if(players[0].getScore().equals("12"))
                    errorShow(0,"Pink");
                else {
                    if (players[1].getScore().equals("12"))
                        errorShow(0, "Blue");
                    else
                        errorShow(4, color);
                }
            }
            else {
                if (gameBoard.GB[r1][c1].piece.getPlayerId() == 0) {
                    if (players[0].getScore().equals("12"))
                        errorShow(0, color);
                    else {
                            gameBoard.CheckForceJump(players);
                        if (gameBoard.nFJPink != 0) {
                            for (int i = 0; i < gameBoard.nFJPink; i++) {
                                if (Integer.toString(a).equals(gameBoard.pinkFJLoc1.get(i)) &
                                        Integer.toString(b).equals(gameBoard.pinkFJLoc2.get(i))) {
                                    disableForceDest(gameBoard);
                                    gameBoard.move(r1, c1, r2, c2, 0, gameBoard.GB[r1][c1].piece.king,
                                            players, gameBoard, pieceGroup);
                                    gameBoard.kill((r1 + r2) / 2, (c1 + c2) / 2, pieceGroup);
                                    if (r2 == 7 &  gameBoard.GB[r2][c2].piece.king == 0 ) {
                                        gameBoard.GB[r2][c2].piece.crown();
                                        gameBoard.GB[r2][c2].piece.pieceGraphics(r2, c2);
                                        if(players[0].getScore().equals("12"))
                                            turnChanger(players, 0);
                                    }
                                    players[0].addScore();
                                    if (players[0].getScore().equals("12")) {
                                        errorShow(0, color);
                                    }
                                    Checkers.designSB(players);
                                    gameBoard.CheckForceJump(players);
                                    if (!gameBoard.pinkFJLoc1.contains(Integer.toString(b))) {
                                        if (!players[0].getScore().equals("12"))
                                            turnChanger(players, 0);
                                    }
                                    gameBoard.CheckForceJump(players);
                                    s++;
                                    break;
                                }
                            }
                            if (s == 0)
                                errorShow(5, "Pink");
                        } else {
                            if (diag) {
                                if (r2 - r1 == 1) {
                                    gameBoard.move(r1, c1, r2, c2, 0, gameBoard.GB[r1][c1].piece.king,
                                            players, gameBoard, pieceGroup);
                                    if (!players[0].getScore().equals("12"))
                                        turnChanger(players, 0);
                                    gameBoard.CheckForceJump(players);
                                    s++;

                                    if (r2 == 7) {
                                        gameBoard.GB[r2][c2].piece.crown();
                                        gameBoard.GB[r2][c2].piece.pieceGraphics(r2, c2);
                                    }
                                } else if (gameBoard.GB[r1][c1].piece.king == 1)
                                    if (r1 - r2 == 1) {
                                        gameBoard.move(r1, c1, r2, c2, 0, gameBoard.GB[r1][c1].piece.king,
                                                players, gameBoard, pieceGroup);
                                        if (!players[0].getScore().equals("12")) {
                                            turnChanger(players, 0);
                                        }
                                        s++;
                                        gameBoard.CheckForceJump(players);
                                    }

                            } else
                                if (!players[0].getScore().equals("12"))
                                errorShow(6, color);
                        }
                    }
                }else {
                    if (players[1].getScore().equals("12"))
                        errorShow(0, color);
                    else {
                        gameBoard.CheckForceJump(players);
                        if (gameBoard.nFJBlue != 0) {
                            for (int i = 0; i < gameBoard.nFJBlue; i++) {
                                if (Integer.toString(a).equals(gameBoard.blueFJLoc1.get(i)) &
                                        Integer.toString(b).equals(gameBoard.blueFJLoc2.get(i))) {
                                    disableForceDest(gameBoard);
                                    gameBoard.move(r1, c1, r2, c2, 1, gameBoard.GB[r1][c1].piece.king,
                                            players, gameBoard, pieceGroup);
                                    gameBoard.kill((r1 + r2) / 2, (c1 + c2) / 2, pieceGroup);
                                    if (r2 == 0 & gameBoard.GB[r2][c2].piece.king == 0) {
                                        gameBoard.GB[r2][c2].piece.crown();
                                        gameBoard.GB[r2][c2].piece.pieceGraphics(r2, c2);
                                        if (!players[1].getScore().equals("12"))
                                            turnChanger(players, 1);
                                    }
                                    players[1].addScore();
                                    if (players[1].getScore().equals("12")) {
                                        errorShow(0, color);
                                    }
                                    Checkers.designSB(players);
                                    gameBoard.CheckForceJump(players);
                                    if (!gameBoard.blueFJLoc1.contains(Integer.toString(b))) {
                                        if (!players[1].getScore().equals("12"))
                                            turnChanger(players, 1);
                                    }
                                    gameBoard.CheckForceJump(players);
                                    s++;
                                    break;
                                }
                            }
                            if (s == 0)
                                errorShow(5, "Blue");
                        } else {
                            if (diag) {
                                if (r1 - r2 == 1) {
                                    gameBoard.move(r1, c1, r2, c2, 1, gameBoard.GB[r1][c1].piece.king,
                                            players, gameBoard, pieceGroup);
                                    if (!players[1].getScore().equals("12"))
                                        turnChanger(players, 1);
                                    gameBoard.CheckForceJump(players);
                                    s++;
                                    if (r2 == 0) {
                                        gameBoard.GB[r2][c2].piece.crown();
                                        gameBoard.GB[r2][c2].piece.pieceGraphics(r2, c2);
                                    }
                                } else if (gameBoard.GB[r1][c1].piece.king == 1)
                                    if (r2 - r1 == 1) {
                                        gameBoard.move(r1, c1, r2, c2, 1, gameBoard.GB[r1][c1].piece.king,
                                                players, gameBoard, pieceGroup);
                                        if (!players[1].getScore().equals("12")) {
                                            turnChanger(players, 1);
                                        }
                                        s++;
                                        gameBoard.CheckForceJump(players);
                                    }
                            } else
                            if (!players[1].getScore().equals("12"))
                                errorShow(6, color);
                        }
                    }
                }
            }
        }
        if (s == 0) {
            gameBoard.GB[r1][c1].piece.pieceGraphics(r1, c1);
        }
    }
    private static void textSet(Text tName, String text, double x, double y, Color fColor, int fSize){
        tName.setText(text);
        tName.setFont(Font.font("Kristen ITC", FontWeight.BOLD, FontPosture.REGULAR, fSize));
        tName.setX(x);
        tName.setY(y);
        tName.setFill(fColor);
        tName.setStrokeWidth(0);

    }
    private static Rectangle createBG(int h, int y, Color fColor, Color sColor){
        Rectangle BG = new Rectangle(5 * TileSize, h * TileSize);
        BG.relocate(Dim * TileSize, y * TileSize);
        BG.setFill(fColor);
        BG.setStroke(sColor);
        BG.setStrokeWidth(2);
        return BG;
    }
    private static void designSB(Player[] players){
        scoreBoard.getChildren().clear();
        Rectangle newGameBtn = new Rectangle((2) * TileSize,0.5 * TileSize);
        newGameBtn.setFill(Color.BLANCHEDALMOND);
        newGameBtn.setStroke(Color.BLACK);
        newGameBtn.setStrokeWidth(2);
        newGameBtn.relocate(8.3 * TileSize, 7.35 * TileSize);
        newGameBtn.setOnMouseEntered(e1 -> newGameBtn.setFill(Color.SKYBLUE));
        newGameBtn.setOnMouseExited(e2 -> newGameBtn.setFill(Color.BLANCHEDALMOND));
        Text newGame = new Text();
        textSet(newGame, "New Game", 8.55 * TileSize, 7.7 * TileSize, Color.BLACK, 14);
        newGame.setOnMouseEntered(e3 -> newGameBtn.setFill(Color.SKYBLUE));
        newGame.setOnMouseExited(e4 -> newGameBtn.setFill(Color.BLANCHEDALMOND));
        newGameBtn.setOnMouseClicked(e1 -> newGame(players));
        newGame.setOnMouseClicked(e2 -> newGame(players));

        Rectangle BG1 = createBG(3, 0, Color.DIMGRAY, Color.BLANCHEDALMOND);
        Rectangle BG2 = createBG(3, 3, Color.BLANCHEDALMOND, Color.DIMGRAY);
        Rectangle BG3 = createBG(2, 6, Color.DIMGRAY, Color.BLANCHEDALMOND);

        Text p0name = new Text();
        Text p1name = new Text();
        Text p0score = new Text();
        Text p1score = new Text();
        Text p0time = new Text();
        Text p1time = new Text();
        Text end0 = new Text();
        Text end1 = new Text();
        Text turnText = new Text();

        textSet(p0name, players[0].getName() + " {", (Dim + 0.7) * TileSize, 0.8 * TileSize,
                Color.LIGHTPINK, 24);
        textSet(p0score, "Score : { "+players[0].getScore() + " }", (Dim + 1) * TileSize,
                (0.8 + 0.6) * TileSize, Color.LIGHTPINK, 20);
        textSet(p0time, "Time { "+players[0].getTime() + " }", (Dim + 1) * TileSize,
                (0.8 + 1.2) * TileSize, Color.LIGHTPINK, 20);
        textSet(end0, "}", (Dim + 0.7) * TileSize, (0.8 + 1.8) * TileSize,
                Color.LIGHTPINK, 24);
        textSet(p1name, players[1].getName() + " {", (Dim + 0.7) * TileSize, 3.8 * TileSize,
                Color.STEELBLUE, 24);
        textSet(p1score, "Score : { " + players[1].getScore() + " }", (Dim + 1) * TileSize,
                (3.8 + 0.6) * TileSize, Color.STEELBLUE, 20);
        textSet(p1time, "Time : { " + players[1].getTime() + " }", (Dim + 1) * TileSize,
                (3.8 + 1.2) * TileSize, Color.STEELBLUE, 20);
        textSet(end1, "}", (Dim + 0.7) * TileSize, (3.8 + 1.8) * TileSize,
                Color.STEELBLUE, 24);

        Circle turnTeller = new Circle(0.20 * TileSize);
        if (players[1].getTurn()) {
            textSet(turnText, "Turn", (Dim + 0.25) * TileSize, 3.65 * TileSize,
                    Color.BLACK, 6);
            turnTeller.relocate((Dim + 0.2) * TileSize, TileSize * 3.4);
            turnTeller.setFill(Color.STEELBLUE);
        }
        else {
            textSet(turnText, "Turn", (Dim + 0.25) * TileSize, 0.65 * TileSize,
                    Color.BLACK, 6);
            turnTeller.relocate((Dim + 0.2) * TileSize, TileSize * 0.4);
            turnTeller.setFill(Color.LIGHTPINK);
        }
        turnTeller.setStroke(Color.BLACK);
        turnTeller.setStrokeWidth(0.6);

        scoreBoard.getChildren().addAll(BG1, BG2, BG3, p0name, p0score, p0time,
                p1name, p1score, p1time, end0, end1, turnTeller, turnText, newGameBtn, newGame);
    }
    private static void designSig(){
        Text sign = new Text();
        textSet(sign,"© Created by Mehrsa Pourya", (Dim + 2.65) * TileSize,
                (7.9) * TileSize, Color.BLANCHEDALMOND, 8);
        signature.getChildren().add(sign);
    }
    private static void designSP(Player[] players){
        designSig();
        pieceGroup.getChildren().clear();
        for (int i = 0; i < 2; i++)
            players[i] = new Player();
        gameBoard = new GameBoard();
        for (int r1 = 0; r1 < Dim; r1++) {
            for (int c1 = 0; c1 < Dim; c1++) {
                gameBoard.GB[r1][c1].tileGraphic(r1, c1,0,false,0);
                tileGroup.getChildren().add(gameBoard.GB[r1][c1]);
                if (gameBoard.GB[r1][c1].havePiece()) {
                    gameBoard.GB[r1][c1].piece.pieceGraphics(r1, c1);
                    pieceGroup.getChildren().add(gameBoard.GB[r1][c1].piece);
                    final int row = r1;
                    final int col = c1;
                    gameBoard.GB[r1][c1].piece.setOnMouseReleased(event -> {
                        int r2 = (int) (gameBoard.GB[row][col].piece.getLayoutY() / 50 + 0.5);
                        int c2 = (int) (gameBoard.GB[row][col].piece.getLayoutX() / 50 + 0.5);
                        int b = r2 * 8 + c2 + 1 ;
                        errors.getChildren().clear();
                        try {
                            Checkers.mover(gameBoard.GB[row][col].piece.getLocation(), b, players, gameBoard, pieceGroup);
                        } catch (Exception e){
                            gameBoard.GB[row][col].piece.pieceGraphics(row, col);
                            errorShow(6,"");
                        }

                    });
                }
            }
        }
        root.getChildren().clear();
        Rectangle bG = new Rectangle((Dim + 5) * TileSize,Dim * TileSize);
        bG.setFill(Color.LIGHTBLUE);
        Text welcome = new Text();
        textSet(welcome, "Welcome to \"Checkers\" ! :)", 3 * TileSize , 2.2 * TileSize ,
                Color.PURPLE, 30);
        Rectangle startBtn = new Rectangle((2) * TileSize,0.5 * TileSize);
        startBtn.setFill(Color.MEDIUMAQUAMARINE);
        startBtn.setStroke(Color.PURPLE);
        startBtn.setStrokeWidth(2);
        startBtn.relocate(5.7 * TileSize, 5.6 * TileSize);
        startBtn.setOnMouseEntered(event -> startBtn.setFill(Color.BLANCHEDALMOND));
        startBtn.setOnMouseExited(event -> startBtn.setFill(Color.MEDIUMAQUAMARINE));
        Text start = new Text();
        textSet(start, "Start", 6.3 * TileSize, 5.95 * TileSize, Color.PURPLE, 14);
        start.setOnMouseEntered(e3 -> startBtn.setFill(Color.BLANCHEDALMOND));
        start.setOnMouseExited(e4 -> startBtn.setFill(Color.MEDIUMAQUAMARINE));
        Text fPN = new Text();
        textSet(fPN, "Enter first Player's Name : ", 4.9 * TileSize, 3.05 * TileSize, Color.PURPLE, 14);
        Text sPN = new Text();
        textSet(sPN, "Enter second Player Name : ", 4.9 * TileSize, 4.15 * TileSize, Color.PURPLE, 14);
        TextField PN1 = new TextField();
        PN1.relocate( 5.2 * TileSize, 3.25 * TileSize);
        TextField PN2 = new TextField();
        PN2.relocate( 5.2 * TileSize, 4.35 * TileSize);
        CheckBox random = new CheckBox();
        random.relocate(5.3 * TileSize, 5 * TileSize);
        random.setText("Random Starter Player");
        random.setSelected(false);
        random.setOnMouseClicked(event -> random.setSelected(true));
        root.getChildren().addAll(bG, welcome, startBtn, start, fPN, PN1, sPN, PN2, random, signature);
        startBtn.setOnMouseClicked(e1 -> startGame(players, PN1, PN2, random));
        start.setOnMouseClicked(e2 -> startGame(players, PN1, PN2, random));
    }
    private static void startGame(Player[] players, TextField PN1, TextField PN2, CheckBox random){
        root.getChildren().clear();
        if(!random.isSelected()) {
            players[0].setName(PN2.getText());
            players[1].setName(PN1.getText());
            players[0].setTurn(false);
            players[1].setTurn(true);
            players[1].setTime(System.currentTimeMillis());
            designSB(players);
        }
        else  {
            int ID = Math.random() > 0.5 ? 1 : 0;
            players[ID].setName(PN2.getText());
            players[1 - ID].setName(PN1.getText());
            players[0].setTurn(false);
            players[1].setTurn(true);
            players[1].setTime(System.currentTimeMillis());
            designSB(players);
        }
        root.getChildren().addAll(tileGroup, forceDest, pieceGroup, scoreBoard, errors, signature);
    }
    private static void newGame(Player[] players){
        designSP(players);
    }
    private static void turnChanger(Player[] players, int ID){
        long CrTime = System.currentTimeMillis();
        players[ID].addTime(CrTime);
        players[1-ID].setTime(CrTime);
        players[1-ID].changeTurn();
        players[ID].changeTurn();
        designSB(players);
    }
    @Override
    public void start(Stage primaryStage) throws Exception{
        Scene scene = new Scene(createContent());
        primaryStage.getIcons().add(new Image(("file:Checkers.jpg")));
        primaryStage.setTitle("Checkers");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}

class Player{
    private String name;
    private int score;
    private boolean turn;
    private long time = 0 ;
    private long sTime;

    void setName(String pName){
        this.name = pName;
    }
    void setTurn(boolean turn){
        this.turn = turn;
    }
    void changeTurn(){
        this.turn = !this.turn;
    }
    String getTime(){
        return (this.time/60000) + " : " + ((this.time - (60000 * (this.time/60000)))/1000) + "."
                + ((this.time - (60000 * (this.time/60000)) - ((this.time - (60000 * (this.time/60000)))
                /1000) * 1000 ) /100) ;
    }
    String getName(){
        return this.name;
    }
    String getScore(){
        return Integer.toString(score);
    }
    boolean getTurn(){
        return this.turn;
    }
    void addScore(){
        this.score++;
    }
    void setTime(long time){
        this.sTime = time;
    }
    void addTime(long time){
        this.time += (time - this.sTime);
    }
}

class GameBoard{
    Tile[][] GB = new Tile[8][8];
    int nFJPink , nFJBlue;
    ArrayList<String> pinkFJLoc1 =new ArrayList<>();
    ArrayList<String> blueFJLoc1 =new ArrayList<>();
    ArrayList<String> pinkFJLoc2 =new ArrayList<>();
    ArrayList<String> blueFJLoc2 =new ArrayList<>();

    GameBoard(){
        this.nFJPink = 0 ;
        this.nFJBlue = 0 ;
        for(int row = 0 ; row < 8 ; row++){
            for(int col = 0 ; col < 8 ; col++) {
                this.GB[row][col] = new Tile();
                if ((row % 2 == 0) ^ (col % 2 == 0))
                    if (row < 3) {
                        this.GB[row][col].tile(1, row * 8 + col + 1);
                        GB[row][col].setPiece(0,0);
                    }
                    else if (row > 4) {
                        this.GB[row][col].tile(1, row * 8 + col + 1);
                        this.GB[row][col].setPiece(1,0);
                    }
                    else
                        this.GB[row][col].tile(1, row * 8 + col + 1);
                else
                    this.GB[row][col].tile(0, row * 8 + col + 1);
            }
        }
    }
    void move(int r1, int c1, int r2, int c2 ,int id, int king,Player[] players, GameBoard gameBoard,Group pieceGroup){
        pieceGroup.getChildren().remove(this.GB[r1][c1].piece);
        this.GB[r1][c1].piece = null;
        this.GB[r2][c2].setPiece(id,king);
        GB[r2][c2].piece.setOnMouseReleased(event -> {
            int r3 = (int) (GB[r2][c2].piece.getLayoutY() / 50 + 0.5);
            int c3 = (int) (GB[r2][c2].piece.getLayoutX()  / 50 + 0.5);
            int b = r3 * 8 + c3 + 1 ;
            Checkers.errors.getChildren().clear();
            Checkers.mover(r2*8+c2+1, b, players, gameBoard, pieceGroup);
        });
        this.GB[r2][c2].piece.pieceGraphics(r2,c2);
        pieceGroup.getChildren().add(this.GB[r2][c2].piece);
    }
    void kill(int r, int c,Group pieceGroup){
        pieceGroup.getChildren().remove(this.GB[r][c].piece);
        this.GB[r][c].piece = null;
    }
    private void addPinkFJ(int r1 , int c1 , int r2 , int c2){
        this.nFJPink ++;
        this.pinkFJLoc1.add(Integer.toString(this.GB[r1][c1].piece.getLocation()));
        this.pinkFJLoc2.add(Integer.toString(this.GB[r2][c2].getLocation()));
    }
    private void addBlueFJ(int r1 , int c1 , int r2 , int c2){
        this.nFJBlue ++;
        this.blueFJLoc1.add(Integer.toString(this.GB[r1][c1].piece.getLocation()));
        this.blueFJLoc2.add(Integer.toString(this.GB[r2][c2].getLocation()));
    }
    void CheckForceJump(Player[] players){
        this.pinkFJLoc1.clear();
        this.pinkFJLoc2.clear();
        this.blueFJLoc1.clear();
        this.blueFJLoc2.clear();
        this.nFJPink = 0 ;
        this.nFJBlue = 0 ;

        for(int r = 0 ; r < 8 ; r++){
            for (int c = 0 ; c < 8 ; c++){
                if(this.GB[r][c].havePiece()){
                    if (this.GB[r][c].piece.getPlayerId()==0){
                        if(r < 6 & c > 1){
                            if(this.GB[r + 1][c - 1].havePiece() & !this.GB[r + 2][c - 2].havePiece()){
                                if(this.GB[r + 1][c -1].piece.getPlayerId()==1){
                                    this.addPinkFJ(r,c,r+2,c-2);
                                    this.GB[r+2][c-2].tileGraphic(r+2,c-2,1,players[0].getTurn(),0);
                                }
                            }
                        }
                        if(r<6 & c<6 ){
                            if(this.GB[r + 1][c + 1].havePiece() & !this.GB[r + 2][c + 2].havePiece()){
                                if(this.GB[r + 1][c +1].piece.getPlayerId()==1){
                                    this.addPinkFJ(r,c,r+2,c+2);
                                    this.GB[r+2][c+2].tileGraphic(r+2,c+2,1,players[0].getTurn(),0);
                                }
                            }
                        }
                        if (this.GB[r][c].piece.king == 1){
                            if(r>1 & c>1 ){
                                if(this.GB[r - 1][c - 1].havePiece() & !this.GB[r - 2][c - 2].havePiece()){
                                    if(this.GB[r - 1][c -1].piece.getPlayerId()==1){
                                        this.addPinkFJ(r,c,r-2,c-2);
                                        this.GB[r-2][c-2].tileGraphic(r-2,c-2,1,
                                                players[0].getTurn(),0);
                                    }
                                }
                            }
                            if(r>1 & c<6 ){
                                if(this.GB[r - 1][c + 1].havePiece() & !this.GB[r - 2][c + 2].havePiece()){
                                    if(this.GB[r - 1][c +1].piece.getPlayerId()==1){
                                        this.addPinkFJ(r,c,r-2,c+2);
                                        this.GB[r-2][c+2].tileGraphic(r-2,c+2,1,
                                                players[0].getTurn(),0);
                                    }
                                }
                            }
                        }
                    }
                    if (this.GB[r][c].piece.getPlayerId()==1) {
                        if(r>1 & c>1 ){
                            if(this.GB[r - 1][c - 1].havePiece() & !this.GB[r - 2][c - 2].havePiece()){
                                if(this.GB[r - 1][c -1].piece.getPlayerId()==0){
                                    this.addBlueFJ(r,c,r-2,c-2);
                                    this.GB[r-2][c-2].tileGraphic(r-2,c-2,1,players[1].getTurn(),1);
                                }
                            }
                        }
                        if(r>1 & c<6 ){
                            if(this.GB[r - 1][c + 1].havePiece() & !this.GB[r - 2][c + 2].havePiece()){
                                if(this.GB[r - 1][c +1].piece.getPlayerId()==0){
                                    this.addBlueFJ(r,c,r-2,c+2);
                                    this.GB[r-2][c+2].tileGraphic(r-2,c+2,1,players[1].getTurn(),1);
                                }
                            }
                        }
                        if (this.GB[r][c].piece.king == 1){
                            if(r < 6 & c > 1){
                                if(this.GB[r + 1][c - 1].havePiece() & !this.GB[r + 2][c - 2].havePiece()){
                                    if(this.GB[r + 1][c -1].piece.getPlayerId()==0){
                                        this.addBlueFJ(r,c,r+2,c-2);
                                        this.GB[r+2][c-2].tileGraphic(r+2,c-2,1,
                                                players[1].getTurn(),1);
                                    }
                                }
                            }
                            if(r<6 & c<6 ){
                                if(this.GB[r + 1][c + 1].havePiece() & !this.GB[r + 2][c + 2].havePiece()){
                                    if(this.GB[r + 1][c +1].piece.getPlayerId()==0){
                                        this.addBlueFJ(r,c,r+2,c+2);
                                        this.GB[r+2][c+2].tileGraphic(r+2,c+2,1,
                                                players[1].getTurn(),1);
                                    }
                                }
                            }
                        }
                    }


                }
            }
        }

    }
}

class Tile extends StackPane{
    Piece piece = new Piece();
    private int color ;
    private int location;

    void tile(int color, int location){
        this.piece = null;
        this.color = color ;
        this.location = location;
    }
    void setPiece(int id, int king){
        this.piece = new Piece();
        this.piece.piece(id,king,location);
    }
    boolean havePiece(){
        return  this.piece != null ;
    }
    int getLocation(){
        return this.location;
    }
    void tileGraphic(int x, int y,int forceDest,boolean turn,int id){

        relocate(y * Checkers.TileSize, x * Checkers.TileSize);
        Rectangle tile = new Rectangle();
        tile.setWidth(Checkers.TileSize);
        tile.setHeight(Checkers.TileSize);
        tile.setFill(this.color == 1 ? Color.DIMGRAY : Color.BLANCHEDALMOND);
        getChildren().add(tile);
        
        if(forceDest == 1 & turn){
            Circle bg = new Circle( Checkers.TileSize * 0.30) ;
            if(id == 0)
                bg.setFill(Color.LIGHTPINK);
            else
                bg.setFill(Color.STEELBLUE);
            Circle fg = new Circle( Checkers.TileSize * 0.259) ;
            fg.setFill(Color.GRAY);
            getChildren().addAll(bg,fg);
        }
    }
}

class Piece extends StackPane{
    private int playerId;
    private int location;
    int king;
    private double mouseX, mouseY;

    void piece(int id, int king,int location){
        this.playerId = id;
        this.king = king;
        this.location = location;
    }
    int getPlayerId(){
        return this.playerId;
    }
    int getLocation(){
        return this.location;
    }
    void crown(){
        this.king = 1;
    }
    private void setEllipse(Ellipse ellipse){
        ellipse.setStroke(Color.BLACK);
        ellipse.setStrokeWidth( Checkers.TileSize * 0.03);
        ellipse.setTranslateX((Checkers.TileSize - Checkers.TileSize * 0.3125 * 2) / 2 );
    }
    void pieceGraphics(int x, int y){
        relocate(y * Checkers.TileSize, x *  Checkers.TileSize);

        Ellipse bg = new Ellipse( Checkers.TileSize * 0.3125, Checkers.TileSize * 0.26) ;
        if(this.playerId == 1)
            bg.setFill(Color.MIDNIGHTBLUE);
        else
            bg.setFill(Color.INDIGO);
        setEllipse(bg);
        bg.setTranslateY(( Checkers.TileSize -  Checkers.TileSize * 0.26 * 2) / 2 *  Checkers.TileSize * 0.025);
        Ellipse ellipse = new Ellipse(Checkers.TileSize * 0.3125 ,  Checkers.TileSize * 0.26) ;
        ellipse.setFill(this.getPlayerId() == 0 ? Color.LIGHTPINK : Color.STEELBLUE);
        ellipse.setTranslateY((Checkers.TileSize -  Checkers.TileSize * 0.26 * 2) / 2 );
        setEllipse(ellipse);
        Ellipse crown = new Ellipse(Checkers.TileSize * 0.21 ,  Checkers.TileSize * 0.15) ;
        crown.setFill(Color.LIGHTGOLDENRODYELLOW);
        setEllipse(crown);
        crown.setTranslateY((Checkers.TileSize -  Checkers.TileSize * 0.28 * 2) / 2 );
        if(this.king == 1)
            getChildren().addAll(bg,ellipse,crown);
        else
            getChildren().addAll(bg , ellipse);

        setOnMousePressed(event -> {
            mouseX = event.getSceneX();
            mouseY = event.getSceneY();
            ellipse.setFill(this.getPlayerId() == 0 ? Color.DEEPPINK : Color.DEEPSKYBLUE);
        });
        setOnMouseDragged(event -> relocate(event.getSceneX() - mouseX + y*50,
                event.getSceneY() - mouseY + x*50));
    }
}



