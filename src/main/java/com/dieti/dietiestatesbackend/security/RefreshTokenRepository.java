package com.dieti.dietiestatesbackend.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class RefreshTokenRepository {
    private static final HashMap<String, ArrayList<String>> tokens = new HashMap<>();

    public static void save(String username, String newtoken) {
        if (tokens.containsKey(username)){
            tokens.get(username).add(newtoken);
        }
        else {
            tokens.put(username, new ArrayList<>(Arrays.asList(newtoken)));
        }
    }

    public static void deleteUserToken(String username, String removeToken) {
        if (tokens.containsKey(username)){
            tokens.get(username).remove(removeToken);
        }
    }

    public static ArrayList<String> getTokensByUserId(String username) {
        return tokens.get(username);
    }
}