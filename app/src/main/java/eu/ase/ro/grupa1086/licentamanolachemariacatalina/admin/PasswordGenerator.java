package eu.ase.ro.grupa1086.licentamanolachemariacatalina.admin;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;

public class PasswordGenerator {

    private static final int MIN_CODE = 33;
    private static final int MAX_CODE = 126;

    private static String characters = "abcdefghijklmnopqrstuwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!#=?/&%";

    static Random random;
    public static String process(int length) {
        StringBuilder builder = new StringBuilder();

        random = new Random();


        for (int i = 0; i < length; i++) {
            int ran = random.nextInt(length);
            builder.append(characters.toCharArray()[ran]);
//            builder.append((char) ThreadLocalRandom.current().nextInt(MIN_CODE, MAX_CODE + 1));
        }

        return builder.toString();
    }

}
