import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Scanner;
public class regExUsage {

    public static void main(String[] args) {

        String MyPattern = "(((\\s++|\\p{Punct})[A-Z]((\\w)++)\\s++[A-Z]((\\w)++)\\s++[A-Z]((\\w)++)" +
                "\\s++[A-Z]((\\w)++)\\s++[A-Z]((\\w)++))" +
                "|((\\s++|\\p{Punct})[A-Z]((\\w)++)\\s++[A-Z]((\\w)++)\\s++[A-Z]((\\w)++)\\s++[A-Z]((\\w)++))" +
                "|((\\s++|\\p{Punct})[A-Z]((\\w)++)\\s++[A-Z]((\\w)++)\\s++[A-Z]((\\w)++))" +
                "|((\\s++|\\p{Punct})[A-Z]((\\w)++)\\s++[A-Z]((\\w)++)))";

        String Input ="";

        Scanner scanner = new Scanner(System.in);

        while(true) {

            Input =Input+ " " + scanner.nextLine(); // I also add a extra space at starting to make my pattern function

            if (Input.contains("EOS")){
                scanner.close();
                break;

            }
        }


        Input = Input.replace("EOS","");


        Pattern MainPattern = Pattern.compile(MyPattern);
        Matcher m1 = MainPattern.matcher(Input);   // m1 : matcher for Mypattern
        String output = Input ;

        while (m1.find()) {

            String startfixer = "\\w.++"; // to get rid of signs detected used Mypattern before fisrt word starting
            Pattern sfp = Pattern.compile(startfixer); // start fixer pattern
            String capitalletter = "[A-Z]|[0-9]"; // used to derive capital letters
            Pattern clp = Pattern.compile(capitalletter); // capital letter pattern

            Matcher m2 = sfp.matcher(m1.group()); // m2 : matcher used to delete extra signed detected

            String Detected = "";  // Detected in each loop is the phrase that should be abbriv.

            while(m2.find()) {
                Detected = m2.group();
            }

                Matcher m3 = clp.matcher(Detected);  // m3 : matcher for Capital Letters

                String subs = ""; // Detected replaced with subs , subs contains Capital Letters of Detected

                while (m3.find()) {

                    subs = subs + m3.group();  // each group is a capital letter
                }

                Input = Input.replace(Detected,subs);


        }
        output = Input ;
        // in below steps i remove extra space at starting



        output = output.replaceFirst(" ","");


        System.out.println(output);
    }

}
