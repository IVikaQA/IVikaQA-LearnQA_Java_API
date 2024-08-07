import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class PasswordReader {
    public static void main(String[] args) {
        String path = "src/test/resources/pass.txt";
        File file = new File(path);
        String absolutePath = file.getAbsolutePath();
        System.out.println(absolutePath);

        try (BufferedReader reader = new BufferedReader(new FileReader(absolutePath))) {
            String password;
            while ((password = reader.readLine()) != null) {
                System.out.println("Пароль: " + password);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}