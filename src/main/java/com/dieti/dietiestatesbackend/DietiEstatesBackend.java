package com.dieti.dietiestatesbackend;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.cloud.storage.*;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class DietiEstatesBackend {
    private static Connection myConnection;
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, new DaemonThreadFactory());
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$";
    private static final Pattern PASSWORD_COMPILED_PATTERN = Pattern.compile(PASSWORD_REGEX);
    private static final Logger logger = Logger.getLogger(DietiEstatesBackend.class.getName());

    @RestController
    class Controller {

        Logger logger = Logger.getLogger(getClass().getName());

        @PostMapping("/login")
        public ResponseEntity<Object> login(@RequestBody Map<String, String> body) {
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

        @PostMapping("/signup")
        public ResponseEntity<Object> signup(@RequestBody Map<String, String> body) {
            String email = body.get("email");
            // TODO verify email
            if (doesUserExist(email)) {
                return new ResponseEntity<>("Utente gia' registrato", HttpStatus.CONFLICT);
            }
            else {
                String password = body.get("password");
                String username = body.get("username");
                String name = body.get("name");
                String surname = body.get("surname");
                if (!isPasswordStrong(password)){
                    return new ResponseEntity<>("Password debole: deve contenere almeno 8 caratteri, di cui almeno una lettera maiuscola, una lettera minuscola, un numero e un carattere speciale (@ # $ % ^ & + =).", HttpStatus.BAD_REQUEST);
                }

                try {
                    createUser(email, password, username, name, surname);
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "Errore! {0}", e.getMessage());
                    return new ResponseEntity<>(getErrorMessageUserCreation(e), HttpStatus.BAD_REQUEST);
                }
            
                String accessToken = AccessTokenProvider.generateAccessToken(email);
                String refreshToken = RefreshTokenProvider.generateRefreshToken(email);
                return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
            }
        }

        @PostMapping("/authwithgoogle")
        public ResponseEntity<Object> authWithGoogle(@RequestBody Map<String, String> body) {
            try {
                GoogleIdToken.Payload payload = GoogleTokenValidator.validateToken(body.get("token"));
                String email = payload.getEmail();
                if (!doesUserExist(email)) {
                    if (body.containsKey("username")) {
                        String username = body.get("username");
                        String name = body.get("name");
                        String surname = body.get("surname");
                        try {
                            createUser(email, "", username, name, surname);
                        }
                        catch (SQLException e){
                            return new ResponseEntity<>(getErrorMessageUserCreation(e), HttpStatus.BAD_REQUEST);
                        }
                    }
                    else {
                        return new ResponseEntity<>("L'utente non esiste", HttpStatus.NOT_FOUND);
                    }
                }
                String accessToken = AccessTokenProvider.generateAccessToken(email);
                String refreshToken = RefreshTokenProvider.generateRefreshToken(email);
                return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
            } catch (IOException | GeneralSecurityException e) {
                return new ResponseEntity<>("Token google non valido", HttpStatusCode.valueOf(498));
            }
        }

        @PostMapping("/refresh")
        public ResponseEntity<Object> refreshAccessToken(@RequestBody Map<String, String> body) {
            String oldRefreshToken = body.get("refreshToken");
            String email = RefreshTokenProvider.getUsernameFromToken(oldRefreshToken);
            if (RefreshTokenProvider.isTokenOf(email, oldRefreshToken) && RefreshTokenProvider.validateToken(oldRefreshToken)) {
                String accessToken = AccessTokenProvider.generateAccessToken(email);
                String refreshToken = RefreshTokenProvider.generateRefreshToken(email);
                scheduler.schedule(()->RefreshTokenRepository.deleteUserToken(email, oldRefreshToken), 10, TimeUnit.SECONDS);
                return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
            }
            return new ResponseEntity<>("Refresh token non valido o scaduto", HttpStatusCode.valueOf(498));
        }

        @GetMapping("/listings/{keyword}")
        public ResponseEntity<Object> getListings(@PathVariable("keyword") String keyword,
        @RequestHeader(value = "Bearer", required = false) String accessToken) {
            if (accessToken == null || !AccessTokenProvider.validateToken(accessToken)) {
                return new ResponseEntity<>("Token non valido o scaduto", HttpStatusCode.valueOf(498));
            }
            return ResponseEntity.ok(Arrays.asList(new Listing(1,"Castello di Hogwarts",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor", "Napoli (NA)", 3500000f),
            new Listing(1,"Casa dello Hobbit", "Lorem ipsum", "Pioppaino (NA)", 1350000f)));
        }

        @GetMapping("/thumbnails/{id}")
        public ResponseEntity<Resource> getThumbnails(@PathVariable("id") long listingID) throws ResponseStatusException {
            Path path = Paths.get("/data/resources/listings/1/01.jpg");
            Resource resource = null;
            try {
                resource = new UrlResource(path.toUri());
            } catch (MalformedURLException e) {
                logger.log(Level.SEVERE, "URL malformato! {0}", e.getMessage());
            }
            return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
        }


        private boolean isPasswordStrong(String password){
            final Matcher matcher = PASSWORD_COMPILED_PATTERN.matcher(password);
            return matcher.matches();
        }
    
        private boolean doesUserExist(String email, String password) {
            email=email.toLowerCase();
            String query = "SELECT password FROM \"DietiEstates2025\".utente WHERE email = ?";
            try (PreparedStatement ps = myConnection.prepareStatement(query)) {
                ps.setString(1, email);
                
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.isBeforeFirst()) {
                        rs.next();
                        String storedPassword = rs.getString("password");
                        return passwordEncoder.matches(password, storedPassword);
                    } else {
                        return false;
                    }
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Errore del database! {0}", e.getMessage());
                return false;
            }
        }

        private boolean doesUserExist(String email) {
            email = email.toLowerCase();
            String query = "SELECT id FROM \"DietiEstates2025\".utente WHERE email = ?";
            
            try (PreparedStatement ps = myConnection.prepareStatement(query)) {
                ps.setString(1, email);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.isBeforeFirst();
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Errore del database! {0}", e.getMessage());
                return false;
            }
        }
        
    
        private String getErrorMessageUserCreation(SQLException e){
            String message = e.getMessage();
            if (message.contains("valid_email")) {
                return "Email non valida.";
            }
            else if (message.contains("unique_username")) {
                return "Username già esistente, scegline un altro.";
            }
            else {
                return "Errore sconosciuto";
            }
        }

        private void createUser(String email, String password, String username, String nome, String cognome) throws SQLException {
            String query = "INSERT INTO \"DietiEstates2025\".utente (email, password, username, nome, cognome) VALUES (?, ?, ?, ?, ?)";
            password = passwordEncoder.encode(password);
        
            try (PreparedStatement ps = myConnection.prepareStatement(query)) {
                ps.setString(1, email);
                ps.setString(2, password);
                ps.setString(3, username);
                ps.setString(4, nome);
                ps.setString(5, cognome);
        
                ps.executeUpdate();
            }
        }
        
    }

    private static void openConnection() throws SQLException {
        String url = "jdbc:postgresql://google/postgres?currentSchema=DietiEstates2025&socketFactory=com.google.cloud.sql.postgres.SocketFactory&cloudSqlInstance=third-oarlock-449614-m8:europe-west8:dietiestates2025";
        Properties info = new Properties();
        info.setProperty("user", "postgres");
        info.setProperty("password", System.getenv("DATABASE_CREDENTIALS"));
        myConnection = DriverManager.getConnection(url, info);
    }

    private static boolean attemptConnection() {
        try {
            openConnection();
            return true;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Connessione al database fallita! {0}", e.getMessage());
        }
        return false;
    }

    public static void main(String[] args) {
        attemptConnection();
        SpringApplication.run(DietiEstatesBackend.class, args);
    }
}
