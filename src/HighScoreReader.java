//Adapted code from: //https://www.w3schools.com/java/java_files_read.asp
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner; // Import the Scanner class to read text files

public class HighScoreReader {
    public static void main(String[] args) {
        HighScoreReader.ReadHighScore();
    }
    public static HighScoresList[] ReadHighScore(){
        HighScoresList[] returnArray = new HighScoresList[27];
        for (int i = 0; i < 27; i++) {
            HighScoresList tempObject = new HighScoresList("AAA", 0, 0.0f);
            returnArray[i] = tempObject;
        }
        try {
            Path path = Paths.get("C:\\ProgramData\\StrataSnake\\highScoreTable.txt");
            if (!Files.exists(path)){
                HighScoreSaver.SaveHighScore(returnArray);
            }
            File myObj = new File(String.valueOf(path));
            Scanner myReader = new Scanner(myObj);
            int index = 0;
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] parts = data.split(",");
                returnArray[index].setInitials(String.valueOf(parts[0]));
                returnArray[index].setScore(Integer.parseInt(parts[1]));
                returnArray[index].setLevel(Float.parseFloat(parts[2]));
                index++;
            }
            return returnArray;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return returnArray;
        }
    }
}