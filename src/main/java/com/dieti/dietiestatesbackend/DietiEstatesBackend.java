package com.dieti.dietiestatesbackend;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

@SpringBootApplication
public class DietiEstatesBackend {

  @RestController
  class Controller {

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
      String email = body.get("email");
      String password = body.get("password");
      if (email.equalsIgnoreCase("fab") && password.equals("fab")) {
        String accessToken = AccessTokenProvider.generateAccessToken(email);
        String refreshToken = RefreshTokenProvider.generateRefreshToken(email);
        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
      } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).contentType(MediaType.TEXT_PLAIN)
            .body("Credenziali non valide");
      }
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ResponseEntity<?> signup(@RequestBody Map<String, String> body) {
      String email = body.get("email");
      String password = body.get("password");
      // TODO verify email and check for existence, create user.
      String accessToken = AccessTokenProvider.generateAccessToken(email);
      String refreshToken = RefreshTokenProvider.generateRefreshToken(email);
      return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }
    
    @RequestMapping(value = "/authwithgoogle", method = RequestMethod.POST)
    public ResponseEntity<?> authWithGoogle(@RequestBody Map<String, String> body) {
      try {
        GoogleIdToken.Payload payload = GoogleTokenValidator.validateToken(body.get("token"));
        
        // TODO create user if needed
        String email = payload.getEmail();
        String accessToken = AccessTokenProvider.generateAccessToken(email);
        String refreshToken = RefreshTokenProvider.generateRefreshToken(email);
        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
      } catch (IOException | GeneralSecurityException e) {
          System.err.println("Token validation failed: " + e.getMessage());
          return ResponseEntity.status(498)
            .body("Token Google non valido.");
      }
    }

    @RequestMapping(value = "/refresh", method = RequestMethod.POST)
    public ResponseEntity<?> refreshAccessToken(@RequestBody Map<String, String> body) {
      String oldRefreshToken = body.get("refreshToken");
      String user = RefreshTokenProvider.getUsernameFromToken(oldRefreshToken);
      if (RefreshTokenProvider.isTokenOf(user,oldRefreshToken)){
        String accessToken = AccessTokenProvider.generateAccessToken(user);
        return ResponseEntity.ok(new AuthResponse(accessToken, oldRefreshToken));
      }
      return ResponseEntity.status(498)
      .body("Il refresh token non appartiene a questo utente.");
    }

    @RequestMapping(value = "/listings/{keyword}", method = RequestMethod.GET)
    public List<Listing> getListings(@PathVariable("keyword") String keyword,
        @RequestHeader("Authorization") String authorizationHeader) {
      String accessToken = authorizationHeader.replace("Bearer ", "");
      if (!AccessTokenProvider.validateToken(accessToken)) {
        return Arrays.asList();
      }
      return Arrays.asList(new Listing("Castello di Hogwarts",
          "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor", "Napoli (NA)", 3500000f),
          new Listing("Casa dello Hobbit", "Lorem ipsum", "Pioppaino (NA)", 1350000f));
    }

    @GetMapping("/thumbnails/{filename}")
    public ResponseEntity<Resource> getThumbnails(@PathVariable("filename") String filename) throws Exception {
      // Path path = Paths.get("./thumbnails/"+filename+".jpg");
      // Resource resource = new UrlResource(path.toUri());
      Resource resource = new UrlResource(
          "https://upload.wikimedia.org/wikipedia/commons/thumb/c/cd/Hogwarts_(29353868725).jpg/1200px-Hogwarts_(29353868725).jpg");
      return ResponseEntity.ok()
          .contentType(MediaType.IMAGE_JPEG)
          .body(resource);
    }
  }

  public static void main(String[] args) {
    SpringApplication.run(DietiEstatesBackend.class, args);
  }
}
