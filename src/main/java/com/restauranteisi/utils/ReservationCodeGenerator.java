package com.restauranteisi.utils;

import java.security.SecureRandom;
import java.util.Random;

public class ReservationCodeGenerator {
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String ALPHANUM = UPPER + DIGITS;
    private static final Random RANDOM = new SecureRandom();

    public static String generateCode() {
        return generateCode(5);
    }

    public static String generateCode(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException();
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUM.charAt(RANDOM.nextInt(ALPHANUM.length())));
        }
        return sb.toString();
    }
}
