package com.example.helloworld;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

class Listing{
  public String name;
  public String description;
  public String location;
  public float price;
  public Listing(String name, String desc, String location, float price){
    this.name=name;
    description=desc;
    this.location=location;
    this.price=price;
  }
}

class AuthResponse {
    private final String accessToken;
    private final String refreshToken;
    
    public AuthResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
    
    public String getAccessToken() {
        return accessToken;
    }
    
    public String getRefreshToken() {
        return refreshToken;
    }
}

class TokenHelper {
  private final String secretKey;
  public TokenHelper(String secretKey){
    this.secretKey = secretKey;
  }

  public boolean validateToken(String token, String supposedUsername) {
    final String username = getUsernameFromToken(token);
    return (username.equals(supposedUsername) && !isTokenExpired(token));
  }

  // TODO check safety
  public boolean validateToken(String token) {
    return !isTokenExpired(token);
  }
    
  private boolean isTokenExpired(String token) {
    final Date expiration = getExpirationDateFromToken(token);
    return expiration.before(new Date());
  }
  
  public String getUsernameFromToken(String token) {
    return getClaimFromToken(token, Claims::getSubject);
  }
  
  public Date getExpirationDateFromToken(String token) {
    return getClaimFromToken(token, Claims::getExpiration);
  }
  
  public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = getAllClaimsFromToken(token);
    return claimsResolver.apply(claims);
  }
  
  private Claims getAllClaimsFromToken(String token) {
    return Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .getBody();
  }
}

class RefreshTokenRepository {
  private final static ArrayList<String> tokens = new ArrayList<>();
  protected static void save(String newtoken) {
    tokens.add(newtoken);
  }

  protected static void deleteByUserId (String username, String secretKey) {
    TokenHelper helper = new TokenHelper(secretKey);
    tokens.removeIf((String token) -> {return helper.getUsernameFromToken(token).equals(username);});
  }

  protected static String getTokenByUserId(String username, String secretKey) {
    TokenHelper helper = new TokenHelper(secretKey);
    for (String t: tokens){
      if (helper.getUsernameFromToken(t).equals(username)){
        return t;
      }
    }
    return null;
  }
}

@Component
class AccessTokenProvider {

    @Value("${jwt.secret}")
    private static final String SECRET_KEY = "UjRoSEFoSVNtQzVUcGJaZVRJMmgxaVhlSm81THhHajVob0M4SWFsaUJ6YnNvZzZ1WklSNkxTUnhaUjJ6UEMzVQ==";

    @Value("${jwt.access.expiration}")
    private final static Long ACCESS_TOKEN_DURATION_MS = 3600000l; // 1 hour
    
    public static String generateAccessToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + ACCESS_TOKEN_DURATION_MS);
        
        Map<String, Object> claims = new HashMap<>();
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    public static boolean validateToken(String token){
      TokenHelper th = new TokenHelper(SECRET_KEY);
      return th.validateToken(token);
    }
}

@Component
class RefreshTokenProvider {
    
    @Value("${jwt.secret}")
    private static final String SECRET_KEY = "UjRoSEFoSVNtQzVUcGJaZVRJMmgxaVhlSm81THhHajVob0M4SWFsaUJ6YnNvZzZ1WklSNkxTUnhaUjJ6UEMzVQ==";

    @Value("${jwt.refresh.expiration}")
    private static final Long REFRESH_TOKEN_DURATION_MS = 604800000l; // 7 days
    
    protected static String generateRefreshToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + REFRESH_TOKEN_DURATION_MS);
        
        Map<String, Object> claims = new HashMap<>();

        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
        
        RefreshTokenRepository.deleteByUserId(username, SECRET_KEY);
        RefreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    protected static boolean validateToken(String token){
      TokenHelper th = new TokenHelper(SECRET_KEY);
      return th.validateToken(token);
    }

    protected static String getUsernameFromToken(String token){
      TokenHelper helper = new TokenHelper(SECRET_KEY);
      return helper.getUsernameFromToken(token);
    }
}

@SpringBootApplication
public class HelloworldApplication {

  @RestController
  class HelloworldController {

    @RequestMapping(value="/login/{user}/{password}", method = RequestMethod.GET)
    ResponseEntity<?> login(@PathVariable("user") String user, @PathVariable("password") String password){
      if (user.equalsIgnoreCase("fab") && password.equals("fab"))
      {
        String accessToken = AccessTokenProvider.generateAccessToken(user);
        String refreshToken = RefreshTokenProvider.generateRefreshToken(user);
        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
      }
      else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).contentType(MediaType.TEXT_PLAIN).body("Credenziali non valide");
      }
    }
    
    @RequestMapping(value="/signupcredentials/{user}/{password}", method = RequestMethod.POST)
    ResponseEntity<?> signupCredentials(@PathVariable("user") String user, @PathVariable("password") String password){
      // TODO verify email and check for existence, create user.
      String accessToken = AccessTokenProvider.generateAccessToken(user);
      String refreshToken = RefreshTokenProvider.generateRefreshToken(user);
      return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }

    @RequestMapping(value="/refresh", method = RequestMethod.POST)
    ResponseEntity<?> refreshAccessToken(@RequestHeader("Authorization") String authorizationHeader){
      String oldRefreshToken = authorizationHeader.replace("Bearer ", "");
      String user = RefreshTokenProvider.getUsernameFromToken(oldRefreshToken);
      String accessToken = AccessTokenProvider.generateAccessToken(user);
      return ResponseEntity.ok(new AuthResponse(accessToken, oldRefreshToken));
    }

    @RequestMapping(value="/listings/{keyword}", method = RequestMethod.GET)
    List<Listing> getListings(@PathVariable("keyword") String keyword, @RequestHeader("Authorization") String authorizationHeader){
      String accessToken = authorizationHeader.replace("Bearer ", "");
      if (!AccessTokenProvider.validateToken(accessToken)) {
        return Arrays.asList();
      }
      return Arrays.asList(new Listing("Castello di Hogwarts", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor", "Napoli (NA)", 3500000f),
                            new Listing("Casa dello Hobbit", "Lorem ipsum", "Pioppaino (NA)", 1350000f));
    }

    @GetMapping("/thumbnails/{filename}")
    public ResponseEntity<Resource> getThumbnails(@PathVariable("filename") String filename) throws Exception {
        // Path path = Paths.get("./thumbnails/"+filename+".jpg");
        // Resource resource = new UrlResource(path.toUri());
        Resource resource = new UrlResource("https://upload.wikimedia.org/wikipedia/commons/thumb/c/cd/Hogwarts_(29353868725).jpg/1200px-Hogwarts_(29353868725).jpg");
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }
  }

  public static void main(String[] args) {
    SpringApplication.run(HelloworldApplication.class, args);
  }
}

