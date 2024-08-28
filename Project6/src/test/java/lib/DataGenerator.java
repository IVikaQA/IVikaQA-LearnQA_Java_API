package lib;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DataGenerator {
    // Метод, который возвращает случайный email в формате строки.
    public static String getRandomEmail() {
        // Создание строки с текущей датой и временем в формате "yyyyMMddHHmmss".
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
        // Возвращает строку email, состоящую из "learnqa", текущего времени и домена "@example.com".
        return "learnqa" + timestamp + "@example.com";
    }

    public static String getRandomEmailWithRandomCount() {
        // Создание строки с текущей датой и временем в формате "yyyyMMddHHmmss".
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        // Генерация случайного числа (например, от 1000 до 9999) и преобразование его в строку.
        Random random = new Random();
        int randomNumber = random.nextInt(9000) + 1000; // Генерирует число от 1000 до 9999
        String randomNumberString = Integer.toString(randomNumber);

        // Возвращает строку email, состоящую из "learnqa", текущего времени, случайного числа и домена "@example.com".
        return "learnqa" + timestamp + randomNumberString + "@example.com";
    }

    //Метод возвращает дефолтные данные для того,чтобы дальше создать нового пользователя
    public static Map<String,String > getRegistrationData() {
        Map<String, String> data = new HashMap<>();
        data.put("email", DataGenerator.getRandomEmailWithRandomCount());
        data.put("password", "123");
        data.put("username", "learnqa");
        data.put("firstName", "learnqa");
        data.put("lastName", "learnqa");

        return data;
    }


    /*
    Метод возвращает конкретные данные.
    Нужен, когда нужно проверить,что создавать пользователя с теме же данными НЕ ПОЛУЧИТСЯ!
     */
    public static Map<String,String> getRegistrationData(Map<String,String> nonDefaultValues){
        /*
        1)Берем данные для создания пользователя
         */
        Map<String,String> defaultValues = DataGenerator.getRegistrationData();
        Map<String,String> userData = new HashMap<>();
        String[] keys = {"email","password","username","firstName","lastName"};
        for (String key: keys){
            if (nonDefaultValues.containsKey(key)){
                userData.put(key,nonDefaultValues.get(key));
            } else {
                userData.put(key,defaultValues.get(key));
            }
        }
        return userData;
    }
}
