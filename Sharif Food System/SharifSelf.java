import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Scanner;

public class SharifSelf {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        int nstudent = 0; // number of students
        int linecount = 1;  // input line counter
        int foodcounter = 0;  // number of foods that have a plan
        student[] students = new student[100]; // students
        date Crdate = new date(0);  // current date
        Food[] food = new Food[1000]; // foods
        String INPUT;

        while (true) {

            INPUT = scanner.nextLine();

            if (linecount == 1) {

                Crdate.setdate(INPUT);
                linecount += 1;
                continue;

            }

            if (linecount == 2) {

                String[] nstdParts = INPUT.split(" ");
                nstudent = Integer.parseInt(nstdParts[0]);

                for (int i = 0; i < nstudent; i++) {

                    students[i] = new student(scanner.nextLine());
                    linecount += 1;

                }
                continue;

            }

            if (INPUT.contains("next day")) {

                Crdate.setdate(Crdate.dateinint + 1);

                String[] Dateparts = Crdate.dateinstr.split("/");

                if(Integer.parseInt(Dateparts[2])==1){

                    for(int i = 0 ; i < nstudent ; i++){
                        students[i].resetnforget() ;
                    }
                }

            }

            if (INPUT.contains("add plan")) {

                while (true) {

                    String input = scanner.nextLine();

                    if (input.contains("end")) {
                        break;
                    }
                    food[foodcounter] = new Food(INPUT, input);
                    foodcounter += 1;

                }
            }

            if (INPUT.contains("reserve") & !(INPUT.contains("cancel"))) {

                String[] RParts = INPUT.split(" ");

                int Rid = Integer.parseInt(RParts[1]);
                String Rfood = RParts[3];
                String Rday = RParts[2];

                for (int i = 0; i < foodcounter; i++) {

                    if (food[i].name.equals(Rfood)) {

                        if (food[i].foodday.dateinstr.equals(Rday)) {

                            if (food[i].deadline.dateinint >= Crdate.dateinint) {

                                for (int j = 0; j < nstudent; j++) {

                                    if (students[j].id == Rid) {

                                        if (students[j].nofoodindate(food[i].foodday.dateinint)) {

                                            if (students[j].credit >= -20000) {

                                                students[j].reserve(Rfood, food[i].foodday.dateinint, food[i].cost);
                                                food[i].addcapacity();
                                                break;

                                            }
                                        }

                                    }
                                }
                            }
                        }
                    }
                }


            }

            if (INPUT.contains("deposit")) {
                String[] Parts = INPUT.split(" ");

                for (int i = 0; i < nstudent; i++) {

                    if (students[i].id == Integer.parseInt(Parts[1])) {

                        students[i].addcredit(Integer.parseInt(Parts[2]));

                    }
                }
            }

            if (INPUT.contains("cancel reserve")) {

                String[] CRParts = INPUT.split(" ");
                int r = 0;
                int RCid = Integer.parseInt(CRParts[2]);
                String RCfood = CRParts[4];
                String RCday = CRParts[3];


                for (int i = 0; i < nstudent; i++) {

                    if (students[i].id == RCid) {

                        if (students[i].havethisfood(RCfood, RCday)) {

                            r = 1;

                        }


                        for (int j = 0; j < foodcounter; j++) {

                            if (food[j].name.equals(RCfood)) {

                                if (food[i].foodday.dateinstr.equals(RCday)) {

                                    if (food[j].deadline.dateinint >= Crdate.dateinint) {

                                        if (r == 1) {

                                            students[i].cancelreserve(food[j].name, food[j].foodday.dateinint, food[j].cost);
                                            food[i].deccapacity();

                                        }

                                    }
                                }
                            }
                        }

                    }
                }
            }

            if (INPUT.contains("get food") & !(INPUT.contains(";"))) {

                String[] GFParts = INPUT.split(" ");
                String GFid = GFParts[2];
                String GFfood = GFParts[3];

                for (int j = 0; j < nstudent; j++) {

                    if (students[j].id == Integer.parseInt(GFid)) {

                        if (students[j].cangetthis(GFfood, Crdate.dateinstr))

                            students[j].getfood(GFfood, Crdate.dateinstr);

                    }
                }

            }

            if (INPUT.contains("get food") & (INPUT.contains(";"))) {

                String[] GFFParts = INPUT.split(" ");
                String[] IDF = GFFParts[2].split(";");

                String GFFid = IDF[0];
                String GFFday = IDF[1];
                String GFFfood = IDF[2];

                for (int j = 0; j < nstudent; j++) {

                    if (students[j].id == Integer.parseInt(GFFid)) {

                        if (students[j].cangetbyforget(GFFfood, Crdate.dateinstr) & GFFday.equals(Crdate.dateinstr)) {

                            students[j].getfood(GFFfood, Crdate.dateinstr);

                        }

                    }
                }

            }

            if (INPUT.contains("get forget code")) {

                String[] GFCParts = INPUT.split(" ");

                int GFCid = Integer.parseInt(GFCParts[3]);
                String GFCfood = GFCParts[4];


                for (int j = 0; j < nstudent; j++) {

                    if (students[j].id == GFCid) {

                        if (students[j].cangetforget(GFCfood, Crdate.dateinstr)) {

                            students[j].getforgetcode(GFCfood, Crdate.dateinstr);
                            break;

                        }

                    }
                }

            }

            if (INPUT.contains("sell in day")) {

                String[] sellP = INPUT.split(" ");
                int fidx = 0;
                int Sid = Integer.parseInt(sellP[3]);
                String Sfood = sellP[4];


                for (int j = 0; j < foodcounter; j++) {
                    if (food[j].name.equals(Sfood) & food[j].foodday.dateinint == Crdate.dateinint) {
                        fidx = j;

                    }
                }

                int su = 0;

                for (int i = 0; i < nstudent; i++) {

                    if (students[i].id == Sid) {

                        if(students[i].cangetthis(Sfood,Crdate.dateinstr)) {

                            if(food[fidx].havebuyer()){

                            int buyerid = food[fidx].buyerid();

                            for (int x = 0; x < nstudent; x++) {

                                if (students[x].id == buyerid) {

                                    if (students[x].credit >= -20000) {
                                        food[fidx].addseller(Sid);
                                        students[x].buy(Sfood, Crdate.dateinint, food[fidx].dayprice());
                                        students[i].sell(Sfood, Crdate.dateinint, food[fidx].dayprice());
                                        food[fidx].decnbuyer();
                                        food[fidx].decnseller();
                                        su = 1;
                                    }
                                    if (students[x].credit < -20000) {
                                        food[fidx].decnbuyer();
                                    }
                                    }
                                }
                            }
                            if (su == 0) {

                                food[fidx].addseller(Sid);
                                students[i].placeinselline(Sfood, Crdate.dateinint);

                        }

                        }
                    }



            }
        }

            if(INPUT.contains("buy in day")) {

                String[] buyP = INPUT.split(" ");

                int Bid = Integer.parseInt(buyP[3]);
                String Bfood = buyP[4];
                int fidx = 0 ;
                int bu = 0 ;

                for (int j = 0; j < foodcounter; j++) {
                    if (food[j].name.equals(Bfood) & food[j].foodday.dateinint == Crdate.dateinint) {
                            fidx = j ;
                            break;
                    }
                }

                for(int i = 0 ; i <nstudent ; i++){

                    if(students[i].id == Bid){

                        if(students[i].nofoodindate(Crdate.dateinint)){

                            if (food[fidx].haveseller()){
                                int sellerid = food[fidx].sellerid();
                                for (int x = 0 ; x < nstudent ; x++){
                                    if(students[x].id == sellerid){
                                    if(students[x].havethisfood(Bfood,Crdate.dateinstr)) {
                                            if (students[i].credit >= -20000) {
                                                students[i].buy(Bfood, Crdate.dateinint, food[fidx].dayprice());
                                                students[x].sell(Bfood, Crdate.dateinint, food[fidx].dayprice());
                                                food[fidx].decnseller();
                                                bu = 1 ;
                                            }
                                        }
                                    }
                                }
                            }
                            if(bu==0){
                                food[fidx].addnbuyer(Bid);
                            }
                        }
                    }
                }

            }

            if (INPUT.contains("closed")){
                scanner.close();
                break;

            }

        }
        
        //show output
        {
            int[] ids = new int[nstudent];
            int[] std = new int[nstudent];

            for (int j = 0; j < nstudent; j++) {
                ids[j] = students[j].id;
            }

            Arrays.sort(ids);
            
            for (int k = 0; k < nstudent; k++) {
                for (int j = 0; j < nstudent; j++) {
                    if (students[j].id == ids[k]) {
                        std[k] = j;
                    }
                }
            }
            for (int i : std) {
                System.out.println(students[i].id + " " + students[i].credit);


                int[] fds = new int[students[i].nfood]; 
                int[] fd = new int[students[i].nfood];

                for (int j2 = 0; j2 < students[i].nfood; j2++) {
                    fds[j2] = students[i].fooddate[j2].dateinint;
                }

                Arrays.sort(fds);

                for (int k2 = 0; k2 < students[i].nfood; k2++) {
                    for (int j3 = 0; j3 < students[i].nfood; j3++) {
                        if (students[i].fooddate[j3].dateinint == fds[k2]) {
                            fd[k2] = j3;
                        }
                    }
                }
                for (int j : fd) {
                    if (!(students[i].food[j].equals(""))) {
                        System.out.println(students[i].fooddate[j].dateinstr +
                                " " + students[i].food[j] + " " + students[i].gotfood[j] + " " + students[i].gotforgetcode[j]);
                    }
                }
            }

        }
        
    }
    
}

class date {

     int dateinint ;
     String dateinstr ;

      date(int date){
        this.dateinint = date;
        int2str(this.dateinint);
    }
     date(String date){
        String[] DateParts = date.split(" ");
        this.dateinstr = DateParts[0];
        str2int(this.dateinstr);
    }
      void setdate(int date){
        this.dateinint = date;
        int2str(this.dateinint);
    }
     void setdate(String date){
        String[] DateParts = date.split(" ");
        this.dateinstr = DateParts[0];
        str2int(this.dateinstr);
    }
    private void str2int (String dateinstr){

        String year = "^[0-9]{4}";
        Pattern yp = Pattern.compile(year); // year pattern
        Matcher m1 = yp.matcher(dateinstr); // m2 :
        while(m1.find()){
            this.dateinint =Integer.parseInt(m1.group())*360;
        }
        String month = "/[0-9]{1,2}/";
        Pattern temp = Pattern.compile(month); // month pattern
        Matcher m2 = temp.matcher(dateinstr); // m2 :
        while(m2.find()){
            Pattern y3 = Pattern.compile("[0-9]++"); // day pattern
            Matcher m3 = y3.matcher(m2.group()); // m3 :
            while (m3.find()){

                this.dateinint = this.dateinint + 30*(Integer.parseInt(m3.group())-1);
            }
        }
        String day = "[0-9]{1,2}$";
        Pattern dp = Pattern.compile(day); // year pattern
        Matcher m4 = dp.matcher(dateinstr); // m2 :
        while(m4.find()){

            this.dateinint = this.dateinint + Integer.parseInt(m4.group());
        }
    }
    private void int2str (int dateinint){

        int y = dateinint/360 ;
        int m = (dateinint-360*y)/30 + 1;
        int d = dateinint-360*y-30*(m-1);
        if( d==0 ) {
            d = 30;
            m = m-1 ;
        }
        this.dateinstr = y+"/"+m+"/"+d;
    }
}

class student {

     int id ;  //student ID
     int credit ;  // student's Money
     int nfood;  // number of reserved food
     int[] gotfood = new int[100];  // ==1 if student got ith food else 0
     int[] gotforgetcode = new int[100]; // ==1 if student got forget code ith food else 0
     private int[] insellline = new int[100];
     String[] food = new String[100];  //  student's food name reserves
     date[] fooddate = new date[100]; // student's food date
     private int ngetforget  ;

     student(String idstr){

        String[] idParts = idstr.split(" ");
        this.id = Integer.parseInt(idParts[0])  ;
        this.credit = 0 ;
        this.nfood = 0;
        this.ngetforget = 0 ;
        for(int k = 0 ; k<100 ; k++) {

            this.fooddate[k] = new date("");
            this.fooddate[k].setdate(0);
            this.food[k] = "";
            this.gotfood[k] = 0;
            this.gotforgetcode[k] = 0;
            this.insellline[k] = 0 ;

        }

    }

     boolean nofoodindate(int date){

        int c = 0 ;

        for(int i = 0 ; i < this.nfood ; i++){

            if(this.fooddate[i].dateinint == date){

                c=1;
                break;

            }
        }


        return (c==0);

    }

    boolean havethisfood(String fname , String fdate){

         int c = 0 ;

         for(int i = 0 ; i < this.nfood ; i++){

             if(this.food[i].equals(fname) & this.fooddate[i].dateinstr.equals(fdate)){

                     c=1 ;
                     break;

             }
         }


         return (c==1);

    }

     void reserve(String fname, int fdate ,int cost){

        this.food[nfood] = fname;
        this.fooddate[nfood] = new date(fdate);
        this.nfood += 1 ;
        this.credit=this.credit - cost;

    }

     void cancelreserve(String fname , int fdate ,int cost){

        for(int j = 0 ; j<this.nfood ;j++){

            if(fname.equals(this.food[j]) & fdate == this.fooddate[j].dateinint) {

                    this.food[j] ="";
                    this.fooddate[j].setdate("");
                    this.fooddate[j].setdate(0);
                    break;
            }
        }

        this.credit= this.credit + Math.floorDiv(cost+1,2) ;

    }

     void getfood(String fname,String fdate){

        for(int i = 0 ; i < this.nfood ; i++){

            if (this.food[i].equals(fname) & this.fooddate[i].dateinstr.equals(fdate)) {

                this.gotfood[i] = 1 ;

                break;
            }
        }

    }

    boolean cangetthis(String fname , String fdate){

        int c = 0 ;

        for(int i = 0 ; i < this.nfood ; i++){

            if (this.food[i].equals(fname) & this.fooddate[i].dateinstr.equals(fdate)) {

                if(this.gotfood[i] == 0 & this.insellline[i] == 0) {

                    c = 1;
                    break;
                }
            }
        }

        return (c==1) ;
    }

    boolean cangetforget(String fname , String fdate){

        int c = 0 ;

        for(int i = 0 ; i < this.nfood ; i++){

            if (this.food[i].equals(fname) & this.fooddate[i].dateinstr.equals(fdate) & this.ngetforget!=10 & this.gotforgetcode[i]==0) {

                if(this.gotfood[i] == 0 & this.insellline[i] == 0) {
                    this.ngetforget = this.ngetforget + 1;
                    c = 1;
                    break;
                }
            }
        }

        return (c==1) ;
    }

    boolean cangetbyforget(String fname , String fdate){

        int c = 0 ;

        for(int i = 0 ; i < this.nfood ; i++){

            if (this.food[i].equals(fname) & this.fooddate[i].dateinstr.equals(fdate) ) {

                if(this.gotfood[i] == 0 & (this.insellline[i] == 0) & (this.gotforgetcode[i]==1)) {

                    c = 1;
                    break;
                }
            }
        }

            return (c==1) ;
    }

     void getforgetcode(String fname , String fdate){

         for(int i = 0 ; i < this.nfood ; i++) {
             if(this.fooddate[i].dateinstr.equals(fdate) & this.food[i].equals(fname)) {
                 this.gotforgetcode[i] = 1;

                 break;
             }
         }
    }

    void  resetnforget(){

         this.ngetforget = 0 ;

    }

     void addcredit (int dep){

        this.credit = this.credit + dep ;

    }

     void sell(String fname , int fdate, int dayprice){

        for(int i = 0 ; i <this.nfood ; i++){

            if(fname.equals(this.food[i]) & fdate == this.fooddate[i].dateinint){

                this.credit = this.credit + dayprice ;
                this.food[i] ="";
                this.fooddate[i].setdate(0);
                this.fooddate[i].setdate("");
            }
        }

    }

     void buy(String fname , int fdate, int dayprice){

        this.credit = this.credit - dayprice ;
        this.food[this.nfood] =fname;
        this.fooddate[this.nfood].setdate(fdate);
        this.nfood = this.nfood + 1 ;

    }

    void placeinselline(String fname , int fdate){

         for(int i = 0 ; i <this.nfood ; i++){

            if(fname.equals(this.food[i]) & fdate == this.fooddate[i].dateinint){

                this.insellline[i]=1 ;
            }
        }
    }

}

class Food {

    String name;
    int cost;
    date deadline;
    date foodday;
    private int[] buyer= new int[100];
    private int[] seller = new int[100];
    private int nsbuyer ;
    private int nsseller ;
    private int nebuyer  ;
    private int neseller ;
    private int capacity = 0 ;

     Food(String IN, String in) {

        String[] INparts = IN.split(" ");
        String[] inparts = in.split(" ");

        this.name = inparts[0];
        this.cost = Integer.parseInt(inparts[1]);
        this.deadline = new date(inparts[2]);
        this.foodday = new date(INparts[2]);
        this.nebuyer = 0 ;
        this.nsbuyer = 0 ;
        this.nsseller = 0 ;
        this.neseller = 0 ;

    }

     boolean havebuyer(){

            return !((this.nsbuyer-this.nebuyer)>=0);
    }

      int buyerid(){

        return this.buyer[this.nsbuyer];

    }

     boolean haveseller(){

            return !((this.nsseller-this.neseller)>=0);

    }

    int sellerid(){

        return this.seller[this.nsseller];

    }


     void addcapacity(){

        this.capacity=this.capacity + 1 ;

    }

     void deccapacity(){

        this.capacity=this.capacity - 1 ;

    }

     void decnbuyer(){

        this.nsbuyer = this.nsbuyer + 1 ;

    }

     void decnseller(){

        this.nsseller = this.nsseller + 1 ;

    }

     void addnbuyer(int id){

        this.buyer[this.nebuyer] = id ;
        this.nebuyer = this.nebuyer + 1 ;

    }

     void addseller(int id){

        this.seller[this.neseller] = id ;
        this.neseller = this.neseller + 1 ;

    }

     int dayprice(){

        double dprc = this.cost * (1.2 +((double)this.nebuyer-(double)this.nsbuyer)/(double)this.capacity);
        return (int) Math.round(dprc);

    }
}
