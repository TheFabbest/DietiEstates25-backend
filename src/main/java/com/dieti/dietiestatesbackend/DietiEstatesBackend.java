package com.dieti.dietiestatesbackend;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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
  private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, new DaemonThreadFactory());
  private final static String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$";
  private final static Pattern PASSWORD_COMPILED_PATTERN = Pattern.compile(PASSWORD_REGEX);

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
        return new ResponseEntity<>("Utente gia' registrato", HttpStatus.CONFLICT);
      }
      else {
        String password = body.get("password");
        if (!isPasswordStrong(password)){
          return new ResponseEntity<>("Password debole: deve contenere almeno 8 caratteri, di cui almeno una lettera maiuscola, una lettera minuscola, un numero e un carattere speciale (@ # $ % ^ & + =).", HttpStatus.BAD_REQUEST);
        }
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
        String email = payload.getEmail();
        if (doesUserExist(email)) {
          createUser(email, "", "prova", "prova", "prova");
        }
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

  private boolean isPasswordStrong(String password){
    final Matcher matcher = PASSWORD_COMPILED_PATTERN.matcher(password);
    return matcher.matches();
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
      System.err.println("non e' stato possibile creare l'utente: "+e);
    }
  }
private static boolean isSSLEnabled(Connection connection) throws SQLException {
    DatabaseMetaData metadata = connection.getMetaData();
    String url = metadata.getURL();
    return url.contains("ssl=true") || url.contains("sslmode=");
}
  private static void openConnection() throws ClassNotFoundException, SQLException {
    Class.forName("org.postgresql.Driver");
    String url = "jdbc:postgresql:///postgres?currentSchema=DietiEstates2025&sslmode=verify-ca&sslfactory=org.postgresql.ssl.DefaultJavaSSLFactory&socketFactory=com.google.cloud.sql.postgres.SocketFactory&cloudSqlInstance=third-oarlock-449614-m8:europe-west8:dietiestates2025";
    Properties info = new Properties();
    info.setProperty("user", "postgres");
    info.setProperty("password", System.getenv("DATABASE_CREDENTIALS"));
    info.setProperty("_serverSslCertificate", "-----BEGIN CERTIFICATE-----\n" + //
            "MIIDfzCCAmegAwIBAgIBADANBgkqhkiG9w0BAQsFADB3MS0wKwYDVQQuEyQ4Mjg3\n" + //
            "ODcxZi1hYzg0LTQyZWQtYjI4MS04NTk3YjZjYzdiZDYxIzAhBgNVBAMTGkdvb2ds\n" + //
            "ZSBDbG91ZCBTUUwgU2VydmVyIENBMRQwEgYDVQQKEwtHb29nbGUsIEluYzELMAkG\n" + //
            "A1UEBhMCVVMwHhcNMjUwMzA5MTAzMjU5WhcNMzUwMzA3MTAzMzU5WjB3MS0wKwYD\n" + //
            "VQQuEyQ4Mjg3ODcxZi1hYzg0LTQyZWQtYjI4MS04NTk3YjZjYzdiZDYxIzAhBgNV\n" + //
            "BAMTGkdvb2dsZSBDbG91ZCBTUUwgU2VydmVyIENBMRQwEgYDVQQKEwtHb29nbGUs\n" + //
            "IEluYzELMAkGA1UEBhMCVVMwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIB\n" + //
            "AQDToLWu+92VNJVqEPxfvugrEc5xG7KndIJWhaKYKQe7/QyfjvEAq5IPYlnfEQEq\n" + //
            "LpEeubF3rJ5kQbqUpO8dFMXlokidtI50vvPuG5UtKgr4E2Tg5krMjXAZPiYQyaPs\n" + //
            "RHIH5E/sDWFmpGNzhNihptARF2VX+Du0sWjPSUBqYCdMz2nx2F7KJWOXI3K8+TOi\n" + //
            "hSxriDF5uwncJoDdwi8+zbzLJivP6Eq8CS07yQoEyMlAMwVSGhU2OWlxbYXfXMo6\n" + //
            "tMkaGJo3w2bJFUjGn4jlULIdHN+mpHRxi2veJhuIN8FShtu+stDzlKaLzxtIBtYN\n" + //
            "Qla2you4Hnw+70eVpgp4sbiBAgMBAAGjFjAUMBIGA1UdEwEB/wQIMAYBAf8CAQAw\n" + //
            "DQYJKoZIhvcNAQELBQADggEBACy1IVagk2EWYo7lpqhgyP0CtuDbpe5TPrK3M65y\n" + //
            "bS+ZYFPbcqrXZx7bFfvD8uj1xTqyQCeVFCC1gXAz1rQclgsRwuBiylIfTI2R5FcB\n" + //
            "xS6Rj2QC+RKvNBPhVp7fReW5KFT3zMfL6odpT+tB2l2bHpSxXbXR8OWzIhm5XS6G\n" + //
            "3p9STh5BCeGLuacTiBCwMwQzuGq4s/4B4uUA7qEPceRq3yCvUnPZJBm2s4R3Ley9\n" + //
            "hJHHGTSHG1OfKbVjclK6AxZfsSetR/NSTxIZnK/qfF7wwL8ivJdZODQV8FMZsUni\n" + //
            "YHveOfQu/Ua76tbpfh5JCgqgaduorUF9LN4R1IDFL4VtlYY=\n" + //
            "-----END CERTIFICATE-----");
    info.setProperty("_clientSslCertificate", "-----BEGIN CERTIFICATE-----\n" + //
            "MIIDaDCCAlCgAwIBAgIETEuGWTANBgkqhkiG9w0BAQsFADB/MS0wKwYDVQQuEyQ1\n" + //
            "NjU4Y2VmMy05NzEwLTQyM2MtODg5Yy05MmMzZTZkYjdmMzkxKzApBgNVBAMTIkdv\n" + //
            "b2dsZSBDbG91ZCBTUUwgQ2xpZW50IENBIGJhY2tlbmQxFDASBgNVBAoTC0dvb2ds\n" + //
            "ZSwgSW5jMQswCQYDVQQGEwJVUzAeFw0yNTAzMTIxNzE3NDJaFw0zNTAzMTAxNzE4\n" + //
            "NDJaMDUxEDAOBgNVBAMTB2JhY2tlbmQxFDASBgNVBAoTC0dvb2dsZSwgSW5jMQsw\n" + //
            "CQYDVQQGEwJVUzCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAMfL/eXm\n" + //
            "C1Tuwpe6BWVVmLtMmV5zXKJJvBLqiWIW+BvKuyrUgzgQ6udHTSl7xTTOkMBx6+/m\n" + //
            "H9VRH8kYZX6vv3CdcEaEhXfcnIWFHZgFrrmeBh0BaJdx+vWKHV/W4VpQGR5jatEW\n" + //
            "dL3/qSIpB5nHOf7JtKwRbwF5RerZNjdHRxhYuw2ikEVD5CQuemGCdcL4SVZA1vVj\n" + //
            "qIzeLAVMYFrkeNjJ3qNW4+hQn9VIVe0O2mZ9W1oaUaVxi1o2/gE1GP2Fk9OLn5Z2\n" + //
            "i/6PjJQPLvMn8d6LOcXKIhdUcIpmTgaW302p08CAKVlQhUvPPF1HMtVD8NAkxsk4\n" + //
            "b6tNuELDT9oaVWkCAwEAAaM2MDQwCQYDVR0TBAIwADAnBgNVHREEIDAegRxmYWJy\n" + //
            "aXppb2FwdXp6bzIwMDNAZ21haWwuY29tMA0GCSqGSIb3DQEBCwUAA4IBAQBOV8pJ\n" + //
            "EEUEmZ+gk7d7EXr+5x8FHou6XpXYlYC9h16SoGrez7R+iklhuNIS8DuzyY1ziGs5\n" + //
            "WFhL5kBOS3V+USOOKtxqh5JEcpb0EEDQW/zNfQhPLZGzOdYIFgFcdstYPM2kahJ2\n" + //
            "9XiXMxp/sXsnmecMRev6RqylvPZjHboW8CYl3SffiZXA9TV6jlQW3unM+qszREhT\n" + //
            "rLb5fouQ8BF/Qz+xL1H5zMze3WfAO0mJoM9kq1BhK+GapugxxdAKbM7iwCAg8qpJ\n" + //
            "e+F9pZ0noqRXCGYbKvUwpGyObbm91J/Dkaacud+LfuiEw8vCRUpA6bElakr9yMX7\n" + //
            "ZpARQsG4Ksy3hP8V\n" + //
            "-----END CERTIFICATE-----");
    info.setProperty("_clientSslKey", "-----BEGIN RSA PRIVATE KEY-----\n" + //
            "MIIEowIBAAKCAQEAx8v95eYLVO7Cl7oFZVWYu0yZXnNcokm8EuqJYhb4G8q7KtSD\n" + //
            "OBDq50dNKXvFNM6QwHHr7+Yf1VEfyRhlfq+/cJ1wRoSFd9ychYUdmAWuuZ4GHQFo\n" + //
            "l3H69YodX9bhWlAZHmNq0RZ0vf+pIikHmcc5/sm0rBFvAXlF6tk2N0dHGFi7DaKQ\n" + //
            "RUPkJC56YYJ1wvhJVkDW9WOojN4sBUxgWuR42Mneo1bj6FCf1UhV7Q7aZn1bWhpR\n" + //
            "pXGLWjb+ATUY/YWT04uflnaL/o+MlA8u8yfx3os5xcoiF1RwimZOBpbfTanTwIAp\n" + //
            "WVCFS888XUcy1UPw0CTGyThvq024QsNP2hpVaQIDAQABAoIBABMFEC8LYWcc8ZOz\n" + //
            "EYzDg24fzSEzgeKF0BchkkwvBpXSt1L3obmRIWOR+JOrOitG7NBn0pTnBynDTBop\n" + //
            "2I5Lrxb1OI57UsoFy4q0Zjb7d4DTvR4+wj+vNJDyVyMxWAjvw2xj/MkEcRRImx0V\n" + //
            "jdPYUOTjO8VTmt8niyiKXqxlfIGh1FMgrfLEn1XbprYxj+LfrVSreld/pV3ZoPIL\n" + //
            "u6MvGh6JnXckWAJYXUd0iLzPK/dzmyxIyNesddCWdNPkwrWq8UxRTwrOxjiOJ9MP\n" + //
            "3kngz1824Xk35zNqE4S7ScPQEFQMihSBlSjUMYdfce6niLAUCK8kO9begzikCb0G\n" + //
            "YItcGZ8CgYEA7gYkF69HqjXvIu1oodhDrw9tF11/WLZDaf/UlgRd3wKh+9WrvOY9\n" + //
            "/5LMFpxTMzrhdN+HvLf3KRrFh4UAcQyB+1p8PhGHyMlHvzJie7TaXcEXB6JAaN5B\n" + //
            "Fioygy25ybhAW2UpupdUUYr94OfVcW1gaOOce4kqJNkvf/8BVv5tFOcCgYEA1uLI\n" + //
            "YDGYJ80z1fOwiHWNgpDRbDbv/crXgl8cjxicQQqkN34Qnx5X0tc2Hs2KIM78qllW\n" + //
            "XDNoLBv3M2GgS7xXb1VJz7Bd+25GI3XjqCB9ALjusy88z1VukqWt/V5hHewl7kxf\n" + //
            "0o3tQFkC7Tm8RYkKnHs1Zo2862Q6HtzLJaShqS8CgYA1jWonbgdIs3PHPvTwnPC1\n" + //
            "fVoFDMMvGWrXeXjB+9+G4lfBilRmXsMKMmP6nldVymSZYAVxH07bj6trjzAHP1Ir\n" + //
            "VDNp35XWRfr/eH2R6aHLwDrHO+kT7xVjwEC3JN2NFghcx8j4sz2ETypJpL9wErTD\n" + //
            "RJqbbMPvoHHAcYZCpWGzdwKBgEiMPDYtyD73yp5bLaiEd4gzOGEekxvOFcBQOCYf\n" + //
            "mvFilFqf1yDBb8khG4Z+wjmdaDRjAQMT4AG0V1sjhKjNJ3/Q/aB+3Nv8OPXT4U5r\n" + //
            "2Yga54vbUN5262PbR8iYypT9mzi+1EMT45ur8Y8BxQx83fZCqp67D6MxLWYqCPPm\n" + //
            "hyXFAoGBAOzPdW+dDtIIWjiELlZodj4Oy6WNRNBTlLIofaaCW/F0pmtkNpC5C7b1\n" + //
            "uvsaRg/dqxOnf2pJnu3Sc4ID7RRrnrkrmSviaJ5YaX/tELroxAuKIGYy7gKyWQ7L\n" + //
            "3VKOu1f+NXCMd54LK7Mi3dZ309FukdCXzBrQpsqBtMJYTQHmWdau\n" + //
            "-----END RSA PRIVATE KEY-----");
    //url = "jdbc:postgresql://34.154.28.76:5432/postgres?currentSchema=DietiEstates2025";
    myConnection = DriverManager.getConnection(url, info);
    if(isSSLEnabled(myConnection)){
      System.err.println("SSL enabled.");
    }
    else {
      System.err.println("SSL not enabled.");
    }
  }

  private static boolean attemptConnection() {
    try {
      openConnection();
      return true;
    } catch (ClassNotFoundException e) {
      System.err.println("Driver non trovato");
    } catch (SQLException e) {
      System.err.println("Connessione fallita" + e.toString());
    }
    return false;
  }

  public static void main(String[] args) {
    attemptConnection();
    SpringApplication.run(DietiEstatesBackend.class, args);
  }
}
