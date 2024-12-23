package guru.qa.niffler.util;

import com.github.javafaker.Faker;

import java.util.Random;

public class DataUtil {

    private static final Faker faker = new Faker();

    private static final Random random = new Random();

    public static String generateRandomUsername(){
        String name = faker.name().name();
        String randomInt = String.valueOf(random.nextInt(1, 1000));
        return name + " " + randomInt;
    }
}
