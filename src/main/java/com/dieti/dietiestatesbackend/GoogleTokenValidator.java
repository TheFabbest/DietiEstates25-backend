package com.dieti.dietiestatesbackend;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

class GoogleTokenValidator {
    static GoogleIdToken.Payload validateToken(String idTokenString)
            throws GeneralSecurityException, IOException {
        final String ANDROID_ID = "68500182941-q8cp0sg6nvpq4tpr3ct30invplj34ets.apps.googleusercontent.com";
        final String WEB_ID = "68500182941-19rccqu4iigg9mcj062rf3t9blgjg5h5.apps.googleusercontent.com";
        List<String> audience = Arrays.asList(WEB_ID, ANDROID_ID);

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                new GsonFactory())
                .setAudience(audience)
                .build();

        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            return idToken.getPayload();
        } else {
            throw new SecurityException("Invalid ID token.");
        }
    }
}