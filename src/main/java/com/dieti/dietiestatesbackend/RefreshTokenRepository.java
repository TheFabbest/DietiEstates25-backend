package com.dieti.dietiestatesbackend;

import java.util.ArrayList;

class RefreshTokenRepository {
  private final static ArrayList<String> tokens = new ArrayList<>();

  static void save(String newtoken) {
    tokens.add(newtoken);
  }

  static void deleteByUserId(String username, String secretKey) {
    TokenHelper helper = new TokenHelper(secretKey);
    tokens.removeIf((String token) -> {
      return helper.getUsernameFromToken(token).equals(username);
    });
  }

  static String getTokenByUserId(String username, String secretKey) {
    TokenHelper helper = new TokenHelper(secretKey);
    for (String t : tokens) {
      if (helper.getUsernameFromToken(t).equals(username)) {
        return t;
      }
    }
    return null;
  }
}