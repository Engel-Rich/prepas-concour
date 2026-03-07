package com.mutrix.prepa.cors;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class NumberGenerator {

    public static String generateRandomSixDigitInt() {
        // Minimum value is 100000
        StringBuilder str = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            str.append(random.nextInt(10)); // Append a random digit from 0-9
        }
        return str.toString();

    }
}
