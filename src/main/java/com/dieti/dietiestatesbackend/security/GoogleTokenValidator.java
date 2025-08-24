package com.dieti.dietiestatesbackend.security;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import jakarta.annotation.PostConstruct;

@Component
public class GoogleTokenValidator {

    private static String ANDROID_ID;
    private static String WEB_ID;

    @Value("${google.client.android-id:}")
    private String androidClientId;

    @Value("${google.client.web-id:}")
    private String webClientId;

    @PostConstruct
    public void init() {
        ANDROID_ID = androidClientId;
        WEB_ID = webClientId;
    }

    public static GoogleIdToken.Payload validateToken(String idTokenString)
            throws GeneralSecurityException, IOException {
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