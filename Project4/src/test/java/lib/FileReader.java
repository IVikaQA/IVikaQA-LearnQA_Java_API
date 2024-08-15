package lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileReader {

    private static String filePath = "src/test/resources/user_agents.txt";

    public static List<String> readUserAgents() throws IOException {
        List<String> userAgents = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new java.io.FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                userAgents.add(line.trim());
            }
        }
        return userAgents;
    }
}
