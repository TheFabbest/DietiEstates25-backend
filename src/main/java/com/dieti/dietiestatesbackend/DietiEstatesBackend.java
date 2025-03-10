package com.dieti.dietiestatesbackend;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class DietiEstatesBackend {
  private static Connection myConnection;
  private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
  ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, new DaemonThreadFactory());

  @RestController
  class Controller {

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
      String email = body.get("email");
      String password = body.get("password");
      if (doesUserExist(email, password)) {
        String accessToken = AccessTokenProvider.generateAccessToken(email);
        String refreshToken = RefreshTokenProvider.generateRefreshToken(email);
        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
      } else {
        return new ResponseEntity<>("Credenziali non valide", HttpStatus.UNAUTHORIZED);
      }
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ResponseEntity<?> signup(@RequestBody Map<String, String> body) {
      String email = body.get("email");
      // TODO verify email
      if (doesUserExist(email)) {
        return new ResponseEntity<>("Utente gia' registrato", HttpStatusCode.valueOf(409));
      }
      else {
        String password = body.get("password");
        createUser(email, password, "prova", "prova", "prova");
        // TODO create user
        String accessToken = AccessTokenProvider.generateAccessToken(email);
        String refreshToken = RefreshTokenProvider.generateRefreshToken(email);
        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
      }
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
        return new ResponseEntity<>("Token google non valido", HttpStatusCode.valueOf(498));
      }
    }

    @RequestMapping(value = "/refresh", method = RequestMethod.POST)
    public ResponseEntity<?> refreshAccessToken(@RequestBody Map<String, String> body) {
      String oldRefreshToken = body.get("refreshToken");
      String email = RefreshTokenProvider.getUsernameFromToken(oldRefreshToken);
      if (RefreshTokenProvider.isTokenOf(email, oldRefreshToken) && RefreshTokenProvider.validateToken(oldRefreshToken)) {
        String accessToken = AccessTokenProvider.generateAccessToken(email);
        String refreshToken = RefreshTokenProvider.generateRefreshToken(email);
        scheduler.schedule(()->{RefreshTokenRepository.deleteUserToken(email, oldRefreshToken);}, 10, TimeUnit.SECONDS);
        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
      }
      return new ResponseEntity<>("Refresh token non valido o scaduto", HttpStatusCode.valueOf(498));
    }

    @RequestMapping(value = "/listings/{keyword}", method = RequestMethod.GET)
    public ResponseEntity<?> getListings(@PathVariable("keyword") String keyword,
        @RequestHeader("Authorization") String authorizationHeader) {
      String accessToken = authorizationHeader.replace("Bearer ", "");
      if (!AccessTokenProvider.validateToken(accessToken)) {
        return new ResponseEntity<>("Token non valido o scaduto", HttpStatusCode.valueOf(498));
      }
      return ResponseEntity.ok(Arrays.asList(new Listing("Castello di Hogwarts",
          "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor", "Napoli (NA)", 3500000f),
          new Listing("Casa dello Hobbit", "Lorem ipsum", "Pioppaino (NA)", 1350000f)));
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


  private boolean doesUserExist(String email, String password) {
		email=email.toLowerCase();
		try
		{
			String query = "SELECT password FROM \"DietiEstates2025\".utente WHERE email = ?";
			PreparedStatement ps = myConnection.prepareStatement(query);
			ps.setString(1, email);
			
			ResultSet rs = ps.executeQuery();
			boolean hasResults = rs.isBeforeFirst();
      if (hasResults) {
        rs.next();
        String storedPassword = rs.getString("password");
        return passwordEncoder.matches(password, storedPassword);
      }
      else {
        return false;
      }
		}
		catch (SQLException e)
		{
      System.err.println("uncaught SQL exception");
		}
		return false;
	}

  private boolean doesUserExist(String email) {
		email=email.toLowerCase();
		try
		{
			String query = "SELECT * FROM \"DietiEstates2025\".utente WHERE email = ?";
			PreparedStatement ps = myConnection.prepareStatement(query);
			ps.setString(1, email);
			
			ResultSet rs = ps.executeQuery();
			boolean hasResults = rs.isBeforeFirst();
			return hasResults;
		}
		catch (SQLException e)
		{
      System.err.println("uncaught SQL exception");
		}
		return false;
	}

  private void createUser(String email, String password, String username, String nome, String cognome) {
    password = passwordEncoder.encode(password);
    String unformattedQuery = "INSERT INTO \"DietiEstates2025\".utente (email, password, username, nome, cognome) VALUES ('%s','%s','%s','%s','%s')";
    String query = String.format(unformattedQuery, email, password, username, nome, cognome);
    try
		{
      Statement st = myConnection.createStatement();
      st.executeUpdate(query);
    }
		catch (SQLException e)
		{
      System.err.println("non e' stato possibile creare l'utente");
		}
  }

  private static void openConnection() throws ClassNotFoundException, SQLException {
    Class.forName("org.postgresql.Driver");
    String url = "jdbc:postgresql://34.154.28.76:5432/postgres?currentSchema=DietiEstates2025";
    myConnection = DriverManager.getConnection(url, "postgres", System.getenv("DATABASE_CREDENTIALS"));
  }

  private static boolean attemptConnection() {
    try {
      openConnection();
      return true;
    } catch (ClassNotFoundException e) {
      System.err.println("Driver non trovato");
    } catch (SQLException e) {
      System.err.println("Connessione fallita");
    }
    return false;
  }

  public static void main(String[] args) {
    attemptConnection();
    SpringApplication.run(DietiEstatesBackend.class, args);
  }
}
