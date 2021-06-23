import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Random;

enum Position {
    Start,
    SCP3,
    KEPLER,
    MANAGEMENT,
    MENSA
}


public class MovementGenerator {
    private static int filecount = 100;
    private static String directory = "D:\\Routes_output\\";
    private static int wait_time = 80;
    private static int starting_interval = 10;
    private static int intermediate_interval = 20;
    private static StringBuilder result;
    private static String returnRoute;

    public static void main(String[] args){
        filecount = 100;
        directory = "D:\\Routes_output\\";

        for(int i = 0; i<filecount;i++){
            File gpsFile = CreateFile(String.valueOf(i), directory);
            Position currPos = Position.Start;
            System.out.println("writing " + gpsFile.getAbsolutePath());

            result = new StringBuilder();
            result.append("Route name\n");
            result.append("City one\n");
            result.append("City two\n");
            result.append("false\n");
            result.append("10\n");

            // combine X paths
            Random r = new Random();
            for(int j = 0; j<5;j++){
                if(j==0){
                    result.append(r.nextInt(starting_interval)+" ");
                }else{
                    result.append(r.nextInt(intermediate_interval)+wait_time+ " ");
                }
                currPos = appendNextRandomDestination(currPos);
            }

            // add return Path
            result.append(r.nextInt(intermediate_interval)+wait_time+ " ");
            appendReturnDestination(currPos);
            writeToFile(result.toString(), gpsFile);
        }
    }

    public static File CreateFile(String name, String directory) {
        File myObj = null;
        try {
            myObj = new File(directory + name + ".gps");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return myObj;
    }

    public static Position appendReturnDestination(Position currPos){
        File f = null;
        String source = null;
        switch (currPos.ordinal()){
            case 0: System.out.println("Already at Destination - not allowed"); break;
            case 1: source = "SCP3"; break;
            case 2: source = "Kepler"; break;
            case 3: source = "Management"; break;
            case 4: source = "Mensa"; break;
        }
        f = new File("Routes/Return/"+ source + "-" + returnRoute + ".gps");
        result.append(readContent(f));
        return Position.Start;
    }

    public static Position appendNextRandomDestination(Position currPos){
        File f = null;
        File[] files;
        switch (currPos.ordinal()){
            case 0: f = new File("Routes/Start"); break;
            case 1: f = new File("Routes/SCP3"); break;
            case 2: f = new File("Routes/Kepler"); break;
            case 3: f = new File("Routes/Management"); break;
            case 4: f = new File("Routes/Mensa"); break;
        }
        files = f.listFiles();
        Random rand = new Random();
        File file = files[rand.nextInt(files.length)];
        result.append(readContent(file));
        int posStart = file.getName().indexOf('-');
        int posEnd = file.getName().indexOf('.');
        Position value = Position.valueOf(file.getName().substring(posStart+1, posEnd).toUpperCase(Locale.ROOT));
        if(currPos.ordinal() == 0){
            returnRoute =  file.getName().split("\\-")[0];
            System.out.println("RR "+ returnRoute);
        }
        System.out.println(value);
        return value;
    }

    public static String readContent(File file){
        String content = "";
        try {
            content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    public static void writeToFile(String content, File file){
        try {
            FileWriter myWriter = new FileWriter(file.getAbsolutePath());
            myWriter.append(content);
            myWriter.close();
            System.out.println("Successfully wrote to the file " + file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
