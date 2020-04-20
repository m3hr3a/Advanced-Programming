import java.util.Scanner;
import java.util.ArrayList;

public class Checkers {
    public static void main(String[] args) {

        Player[] players = new Player[2];
        for (int i = 0 ; i < 2; i++)
            players[i] = new Player();

        System.out.println("Welcome to Checkers! ;)");
        getPlayerNames(players);

        GameBoard gameBoard = new GameBoard();
        
        UI ui = new UI();
        show(ui,players,gameBoard);
        long StartTime = System.currentTimeMillis();
        players[1].setTime(StartTime);
        Scanner scanner = new Scanner(System.in);
        int start, end;
        while (!players[0].getScore().equals("12") & !players[1].getScore().equals("12")) {
            try {
                System.out.print("Start Index : ");
                start = scanner.nextInt();
                System.out.print("End Index : ");
                end = scanner.nextInt();
                Move(start, end, players, gameBoard, ui);

            } catch (Exception e){
                errorShow(1,0,"",gameBoard);
                scanner.next();
            }
        }
        if(players[0].getScore().equals("12"))
            System.out.println("Game Ended, Blue Won!");
        if(players[1].getScore().equals("12"))
            System.out.println("Game Ended, Red Won!");

    }
    private static void getPlayerNames(Player[] players){
        System.out.println("Enter First Player's Name :");
        Scanner scanner = new Scanner(System.in);
        players[1].setName(scanner.nextLine());
        players[0].setTurn(false);
        players[1].setTurn(true);
        System.out.println("Enter Second Player's Name :");
        players[0].setName(scanner.nextLine());
    }
    private static void show(UI ui,Player[] players, GameBoard gameBoard){
        ui.displayScoreBoard(players);
        gameBoard.checkForceJump();
        if (players[0].getTurn() & gameBoard.nForceBlue!=0 ){
            errorShow(5, 0, "Blue", gameBoard);
        }
        if (players[1].getTurn() & gameBoard.nForceRed!=0 ){
            errorShow(5, 0, "Red", gameBoard);
        }
        ui.displayBoard(gameBoard);
        System.out.println("Input Your Movement's Start And End Tile's Index : ");
    }
    private static void errorShow(int errorCode, int a, String color, GameBoard gameBoard){
        switch (errorCode){
            case 1 : {
                System.out.println("Tile's Number should be between 1 and 64");
                break;
            }
            case 2 : {
                System.out.println("No Piece in Place, " + a + " Please Choose a Tile With a " + color + " piece.");
                break;
            }
            case 3 : {
                System.out.println("You cannot move opponent piece, Please Choose a Tile With a " + color + " piece.");
                break;
            }
            case 4 : {
                System.out.println("You cannot move to a tile with a piece!");
                break;
            }
            case 5 : {
                System.out.print("You need to take force jump, notice place(s) : ");
                if(color.equals("Blue")) {
                    for (int j = 0; j < gameBoard.nForceBlue; j++)
                        if (j == 0)
                            System.out.print(" " + gameBoard.blueFJPlace1.get(j) + " to "
                                    + gameBoard.blueFJPlace2.get(j));
                        else
                            System.out.print(", " + gameBoard.blueFJPlace1.get(j) + " to "
                                    + gameBoard.blueFJPlace2.get(j));
                    System.out.println();
                }
                else {
                    for (int j = 0; j < gameBoard.nForceRed; j++)
                        if (j == 0)
                            System.out.print(" " + gameBoard.redFJPlace1.get(j) + " to "
                                    + gameBoard.redFJPlace2.get(j));
                        else
                            System.out.print(", " + gameBoard.redFJPlace1.get(j) + " to "
                                    + gameBoard.redFJPlace2.get(j));
                    System.out.println();
                }
                break;
            }
            case 6 : {
                System.out.println("Not a valid movement!");
                break;
            }
        }
    }
    private static void changeTurner(Player[] players, int ID){
        long CrTime = System.currentTimeMillis();
        players[ID].addTime(CrTime);
        players[1-ID].setTime(CrTime);
        players[1-ID].changeTurn();
        players[ID].changeTurn();
    }
    private static void Move(int a, int b, Player[] players, GameBoard gameBoard, UI ui){
        int c1 = (a - 1) % 8;
        int r1 = (a - 1) / 8;
        int c2 = (b - 1) % 8;
        int r2 = (b - 1) / 8;
        boolean diag = (c2 - c1) == 1 | (c1 - c2) == 1;
        String color;
        if(players[0].getTurn())
            color = "Blue";
        else
            color = "Red";
        if (!gameBoard.GB[r1][c1].havePiece())
            errorShow(2,a,color,gameBoard);
        else if(! players[gameBoard.GB[r1][c1].piece.getPlayerID()].getTurn())
            errorShow(3,a,color,gameBoard);
        else if(gameBoard.GB[r2][c2].havePiece())
            errorShow(4,a,color,gameBoard);
        else if(gameBoard.GB[r1][c1].piece.getPlayerID() == 0) {
            gameBoard.checkForceJump();
            if(gameBoard.nForceBlue !=0){
                int k = 1 ;
                for (int i = 0; i < gameBoard.nForceBlue; i++)
                    if (Integer.toString(a).equals(gameBoard.blueFJPlace1.get(i)) &
                            Integer.toString(b).equals(gameBoard.blueFJPlace2.get(i))) {
                        gameBoard.move(r1, c1, r2, c2, 0, gameBoard.GB[r1][c1].piece.king);
                        gameBoard.kill((r1+r2)/2 , (c1+c2)/2);
                        if(r2 == 7 &  gameBoard.GB[r2][c2].piece.king == 0) {
                            changeTurner(players, 0);
                            gameBoard.GB[r2][c2].piece.crown();
                        }
                        players[0].addScore();
                        gameBoard.checkForceJump();
                        if(!gameBoard.blueFJPlace1.contains(Integer.toString(b))){
                            changeTurner(players, 0);
                        }
                        gameBoard.checkForceJump();
                        show(ui,players,gameBoard);
                        break;
                    }
                    else {
                        if(k==1) {
                            errorShow(5,a,color,gameBoard);
                            k++;
                        }
                    }
            }
            else if (diag) {
                if(r2 - r1 == 1) {
                    gameBoard.move(r1, c1, r2, c2, 0, gameBoard.GB[r1][c1].piece.king);
                    if(r2 == 7 & gameBoard.GB[r2][c2].piece.king == 0) {
                        gameBoard.GB[r2][c2].piece.crown();
                        changeTurner(players,0);
                    }
                }
                else if(gameBoard.GB[r1][c1].piece.king == 1)
                    if (r1 - r2 == 1)
                        gameBoard.move(r1, c1, r2, c2, 0, gameBoard.GB[r1][c1].piece.king);
                changeTurner(players, 0);
                gameBoard.checkForceJump();
                show(ui,players,gameBoard);
            }
            else
                errorShow(6,a,color,gameBoard);
        }
        else {
            gameBoard.checkForceJump();
            if (gameBoard.nForceRed != 0) {
                int k = 1;
                for (int i = 0; i < gameBoard.nForceRed; i++ )
                    if (Integer.toString(a).equals(gameBoard.redFJPlace1.get(i)) &
                            Integer.toString(b).equals(gameBoard.redFJPlace2.get(i))) {
                        gameBoard.move(r1, c1, r2, c2, 1, gameBoard.GB[r1][c1].piece.king);
                        gameBoard.kill((r1+r2)/2 , (c1+c2)/2);
                        if(r2 == 0 & gameBoard.GB[r2][c2].piece.king == 0) {
                            gameBoard.GB[r2][c2].piece.crown();
                            changeTurner(players, 1);
                        }
                            players[1].addScore();
                            gameBoard.checkForceJump();
                        if(!gameBoard.redFJPlace1.contains(Integer.toString(b))) {
                            changeTurner(players, 1);
                        }
                        gameBoard.checkForceJump();
                        show(ui,players,gameBoard);
                        break;
                    }
                    else {
                        if(k==1) {
                            errorShow(5,a,color,gameBoard);
                            k++;
                        }
                    }
            }
            else  {
                if (diag) {
                    if(r1 - r2 == 1) {
                        gameBoard.move(r1, c1, r2, c2, 1, gameBoard.GB[r1][c1].piece.king);
                        if(r2 == 0)
                            gameBoard.GB[r2][c2].piece.crown();
                    }
                    else if(gameBoard.GB[r1][c1].piece.king == 1)
                        if (r2 - r1 == 1)
                            gameBoard.move(r1, c1, r2, c2, 1, gameBoard.GB[r1][c1].piece.king);
                    gameBoard.checkForceJump();
                    changeTurner(players, 1);
                    show(ui,players,gameBoard);
                }
                else
                    errorShow(6,a,color,gameBoard);
            }

        }
    }
}

class UI {

    void displayScoreBoard(Player[] players){
        String FGCode;                                  // ForeGround Color Code
        String BGCode;                                  // BackGround Color Code
        String WCode = (char) 27 + "[30m";              // White FG Color Code
        String CancelCode = (char) 27 + "[0m";          // Cancel other ANSI's Code

        System.out.print(WCode + 
                        "+---------+-----------------------------" +
                        "+-------------------+-------------------+");
        System.out.println();
        System.out.println("| Number  | Name                        | " +
                "Time(ms)          | Score             |");

        for (int i = 0; i < 2; i++){
            if (i==0)
                FGCode = (char) 27 + "[34m";
            else
                FGCode = (char) 27 + "[31m";
            if (players[i].getTurn())
                BGCode = (char) 27 + "[103m";
            else
                BGCode = "";
            System.out.print(WCode + "| " + BGCode + FGCode + (i+1) +
                    BGCode + WCode + "       | " + BGCode + FGCode + players[i].getName());

            for (int j = 0; j < 28 - players[i].getName().length(); j++ )
                System.out.print(" ");

            System.out.print(BGCode + WCode +"| " + BGCode + FGCode);
            System.out.print(players[i].getTime());

            for (int j = 0; j < 18 - players[i].getTime().length(); j++ )
                System.out.print(" ");

            System.out.print(BGCode + WCode +"| " + BGCode + FGCode);
            System.out.print(players[i].getScore());

            for (int j = 0; j < 17 - players[i].getScore().length(); j++ )
                System.out.print(" ");

            System.out.print(WCode +" |" + CancelCode);
            System.out.println();
        }
        System.out.print(WCode +
                "+---------+-----------------------------" +
                "+-------------------+-------------------+"
                + CancelCode);
        System.out.println();
    }
    void displayBoard(GameBoard gameBoard){
        int[][] GameBoard = gameBoard.getMatrix();
        int row , col ;
        int Width = 10;
        int Height = 4;
        String wCode = (char) 27 + "[30m";
        String CancelCode = (char) 27 + "[0m";
        for(int r = 0 ; r < 33 ; r++) {

            if((r % Height) == 0)
                System.out.print(wCode +
                        "+---------+---------+---------+---------" +
                        "+---------+---------+---------+---------+" +
                        CancelCode);
            else{
                for(int c = 0 ; c < 81 ; c++) {
                    if((c % Width) == 0)
                        System.out.print(wCode + "|"  + CancelCode);
                    else {
                        row = r / Height;
                        col = c / Width;
                        String num = Integer.toString(row * 8 + col + 1);
                        if((row * 8 + col + 1) < 10) {
                            num = "0" + num ;
                        }
                        if((c % Width) == Width - 2 & (r % Height) == Height - 1) {

                            if (GameBoard[row][col] == 0)
                                System.out.print((char) 27 + "[40m" + (char) 27 + "[97m" + num + CancelCode);
                            else
                                System.out.print((char) 27 + "[107m" + wCode + num + CancelCode);
                        }
                        else if((((r % Height) == Height - 1) & ((c % Width) != Width - 1)) | ((r % Height) != Height - 1)){
                            if (GameBoard[row][col] == 0)
                                System.out.print((char) 27 + "[40m" + " " + (char) 27 + "[0m");
                            if (GameBoard[row][col] == 1)
                                System.out.print((char) 27 + "[107m" + " " + (char) 27 + "[0m");
                            boolean circle = ((r % Height) == 1 | (r % Height) == Height - 1)
                                    & (c % Width) < Width-3 & (c % Width) > 3  |
                                    ((r % Height) == 2 | (r % Height) == Height - 2 )
                                            &  (c % Width) < Width - 2 & (c % Width) > 2  ;
                            boolean crown = (c % Width) < Width - 5 & (c % Width) > 5  |
                                    (c % Width) < Width - 4 & (c % Width) > 4  ;
                            if (GameBoard[row][col] == 2) {
                                if(circle)
                                    System.out.print((char) 27 + "[107m" + (char) 27 + "[34m" + "©"
                                            + (char) 27 + "[0m");
                                else
                                    System.out.print((char) 27 + "[107m" + (char) 27 + "[96m" + " "
                                            + (char) 27 + "[0m");
                            }
                            if (GameBoard[row][col] == 3) {
                                if(circle)
                                    System.out.print((char) 27 + "[107m" + (char) 27 + "[31m" + "©"
                                            + (char) 27 + "[0m");
                                else
                                    System.out.print((char) 27 + "[107m" + (char) 27 + "[91m" + " "
                                            + (char) 27 + "[0m");
                            }
                            if (GameBoard[row][col] == 4) {
                                if(circle)
                                    if (crown)
                                        System.out.print((char) 27 + "[107m" + (char) 27 + "[33m" + "©"
                                                + (char) 27 + "[0m");
                                    else
                                        System.out.print((char) 27 + "[107m" + (char) 27 + "[34m" + "©"
                                                + (char) 27 + "[0m");
                                else
                                    System.out.print((char) 27 + "[107m" + (char) 27 + "[91m" + " "
                                            + (char) 27 + "[0m");
                            }
                            if (GameBoard[row][col] == 5) {
                                if(circle)
                                    if (crown)
                                        System.out.print((char) 27 + "[107m" + (char) 27 + "[33m" + "©"
                                                + (char) 27 + "[0m");
                                    else
                                        System.out.print((char) 27 + "[107m" + (char) 27 + "[31m" + "©"
                                                + (char) 27 + "[0m");
                                else
                                    System.out.print((char) 27 + "[107m" + (char) 27 + "[91m" + " "
                                            + (char) 27 + "[0m");
                            }
                        }
                    }
                }
            }
            System.out.println();
        }
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
        return Long.toString(this.time);
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
    private int[][] GBMatrix = new int [8][8];
    Tile[][] GB = new Tile[8][8];
    int nForceBlue , nForceRed;
    ArrayList<String> blueFJPlace1 =new ArrayList<>();
    ArrayList<String> redFJPlace1 =new ArrayList<>();
    ArrayList<String> blueFJPlace2 =new ArrayList<>();
    ArrayList<String> redFJPlace2 =new ArrayList<>();

    GameBoard(){
        this.nForceBlue = 0 ;
        this.nForceRed = 0 ;
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
    int[][] getMatrix(){
        for(int row = 0 ; row < 8 ; row++) {
            for (int col = 0; col < 8; col++) {
                if(this.GB[row][col].havePiece())
                    if(this.GB[row][col].piece.getPlayerID() == 0)
                        if(this.GB[row][col].piece.king == 0)
                            this.GBMatrix[row][col] = 2;
                        else
                            this.GBMatrix[row][col] = 4;
                    else
                    if (this.GB[row][col].piece.king == 0)
                        this.GBMatrix[row][col] = 3;
                    else
                        this.GBMatrix[row][col] = 5;
                else
                    this.GBMatrix[row][col] = this.GB[row][col].getColor();
            }
        }
        return this.GBMatrix;
    }
    void move(int r1, int c1, int r2, int c2 ,int id, int king){
        this.GB[r1][c1].piece = null;
        this.GB[r2][c2].setPiece(id,king);
    }
    void kill(int r, int c){
        this.GB[r][c].piece = null;
    }
    private void addBlueFJ(int r1 , int c1 , int r2 , int c2){
        this.nForceBlue ++;
        this.blueFJPlace1.add(Integer.toString(this.GB[r1][c1].piece.getLocation()));
        this.blueFJPlace2.add(Integer.toString(this.GB[r2][c2].getLocation()));
    }
    private void addRedFJ(int r1 , int c1 , int r2 , int c2){
        this.nForceRed ++;
        this.redFJPlace1.add(Integer.toString(this.GB[r1][c1].piece.getLocation()));
        this.redFJPlace2.add(Integer.toString(this.GB[r2][c2].getLocation()));
    }
    void checkForceJump(){
        this.blueFJPlace1.clear();
        this.blueFJPlace2.clear();
        this.redFJPlace1.clear();
        this.redFJPlace2.clear();
        this.nForceBlue = 0 ;
        this.nForceRed = 0 ;

        for(int r = 0 ; r < 8 ; r++){
            for (int c = 0 ; c < 8 ; c++){
                if(this.GB[r][c].havePiece()){
                    if (this.GB[r][c].piece.getPlayerID()==0){
                        if(r < 6 & c > 1){
                            if(this.GB[r + 1][c - 1].havePiece() & !this.GB[r + 2][c - 2].havePiece()){
                                if(this.GB[r + 1][c -1].piece.getPlayerID()==1){
                                    this.addBlueFJ(r,c,r + 2,c - 2);
                                }
                            }
                        }
                        if(r<6 & c<6 ){
                            if(this.GB[r + 1][c + 1].havePiece() & !this.GB[r + 2][c + 2].havePiece()){
                                if(this.GB[r + 1][c +1].piece.getPlayerID()==1){
                                    this.addBlueFJ(r,c,r + 2,c + 2);
                                }
                            }
                        }
                        if (this.GB[r][c].piece.king == 1){
                            if(r>1 & c>1 ){
                                if(this.GB[r - 1][c - 1].havePiece() & !this.GB[r - 2][c - 2].havePiece()){
                                    if(this.GB[r - 1][c -1].piece.getPlayerID()==1){
                                        this.addBlueFJ(r,c,r - 2,c - 2);
                                    }
                                }
                            }
                            if(r>1 & c<6 ){
                                if(this.GB[r - 1][c + 1].havePiece() & !this.GB[r - 2][c + 2].havePiece()){
                                    if(this.GB[r - 1][c +1].piece.getPlayerID()==1){
                                        this.addBlueFJ(r,c,r - 2,c + 2);
                                    }
                                }
                            }
                        }
                    }
                    if (this.GB[r][c].piece.getPlayerID()==1) {
                        if(r>1 & c>1 ){
                            if(this.GB[r - 1][c - 1].havePiece() & !this.GB[r - 2][c - 2].havePiece()){
                                if(this.GB[r - 1][c -1].piece.getPlayerID()==0){
                                    this.addRedFJ(r,c,r - 2,c - 2);
                                }
                            }
                        }
                        if(r>1 & c<6 ){
                            if(this.GB[r - 1][c + 1].havePiece() & !this.GB[r - 2][c + 2].havePiece()){
                                if(this.GB[r - 1][c +1].piece.getPlayerID()==0){
                                    this.addRedFJ(r,c,r - 2,c + 2);
                                }
                            }
                        }
                        if (this.GB[r][c].piece.king == 1){
                            if(r < 6 & c > 1){
                                if(this.GB[r + 1][c - 1].havePiece() & !this.GB[r + 2][c - 2].havePiece()){
                                    if(this.GB[r + 1][c -1].piece.getPlayerID()==0){
                                        this.addRedFJ(r,c,r + 2,c - 2);
                                    }
                                }
                            }
                            if(r<6 & c<6 ){
                                if(this.GB[r + 1][c + 1].havePiece() & !this.GB[r + 2][c + 2].havePiece()){
                                    if(this.GB[r + 1][c +1].piece.getPlayerID()==0){
                                        this.addRedFJ(r,c,r + 2,c + 2);
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

class Piece{
    private int playerID;
    private int location;
    int king;
    void piece(int id, int king,int location){
        this.playerID = id;
        this.king = king;
        this.location = location;
    }
    int getPlayerID(){
        return this.playerID;
    }
    int getLocation(){
        return this.location;
    }
    void crown(){
        this.king = 1;
    }
}

class Tile{
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
    int getColor(){
        return this.color;
    }
    int getLocation(){
        return this.location;
    }
}