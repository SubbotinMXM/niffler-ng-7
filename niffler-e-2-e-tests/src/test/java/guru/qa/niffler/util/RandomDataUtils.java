package guru.qa.niffler.util;

import com.github.javafaker.Faker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomDataUtils {

    private static final Faker faker = new Faker();

    private static final Random random = new Random();

    public static String randomUsername(){
        String name = faker.name().username();
        String randomInt = String.valueOf(random.nextInt(1, 1000));
        return name + " " + randomInt;
    }

    public static String randomName(){
        return faker.name().firstName();
    }

    public static String randomSurname(){
        return faker.name().lastName();
    }

    public static String randomCategoryName(){
        return faker.harryPotter().spell();
    }

    public static String randomSentence(int wordsCount) {
        List<String> words = new ArrayList<>();

        for (int i = 0; i < wordsCount; i++) {
            words.add(faker.elderScrolls().race());
        }
        return String.join(" ", words) + ".";
    }

    public static String randomPassword(int minLength, int maxLength) {
        return faker.internet().password(minLength, maxLength);
    }
}
