package com.dieti.dietiestatesbackend.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RandomPasswordGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE_CHARACTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "@#$%^&+=!";

    public static String generateRandom() {
        Random rand=new Random();
        List<Character> characters = new ArrayList<>();
        int randIndex;
        for (int i = 0; i < 4; i++) {
            randIndex=rand.nextInt(CHARACTERS.length()); 
            characters.add(CHARACTERS.charAt(randIndex));
            randIndex=rand.nextInt(LOWERCASE_CHARACTERS.length()); 
            characters.add(LOWERCASE_CHARACTERS.charAt(randIndex));   
        }
        randIndex=rand.nextInt(DIGITS.length()); 
        characters.add(DIGITS.charAt(randIndex));
        randIndex=rand.nextInt(SPECIAL_CHARACTERS.length()); 
        characters.add(SPECIAL_CHARACTERS.charAt(randIndex));
        return shuffleString(characters);
    }

    private static String shuffleString(List<Character> input) {
        Collections.shuffle(input);
        String str = input.stream()
                  .map(e->e.toString())
                  .collect(Collectors.joining());
        return str;
    }
}
