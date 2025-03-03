package com.example.helloworld;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javafx.util.Pair;

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

class TokenHelper {
  private String secretKey;
  public TokenHelper(String secretKey){
    this.secretKey = secretKey;
  }

  public boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
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
  private ArrayList<String> static tokens = {};
  protected static void Save(String newtoken) {
    tokens.Add(newtoken);
  }

  protected static void deleteByUserId (String username, String secretKey) {
    TokenHelper helper = new TokenHelper(secretKey);
    tokens.removeIf((String token) -> {return helper.getUsernameFromToken(token) == username;});
  }
}

@Component
class AccessTokenProvider {

    @Value("${jwt.secret}")
    private static String secretKey = "R4hHAhISmC5TpbZeTI2h1iXeJo5LxGj5hoC8IaliBzbsog6uZIR6LSRxZR2zPC3U";

    @Value("${jwt.access.expiration}")
    private static Long accessTokenDurationMs = 3600000l; // 1 hour
    
    public static String generateAccessToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenDurationMs);
        
        Map<String, Object> claims = new HashMap<>();
        
        // claims.put("roles", userDetails.getAuthorities());
        // claims.put("userId", user.getId());
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }
}

@Component
class RefreshTokenProvider {
    
    @Value("${jwt.secret}")
    private static String secretKey = "R4hHAhISmC5TpbZeTI2h1iXeJo5LxGj5hoC8IaliBzbsog6uZIR6LSRxZR2zPC3U";

    @Value("${jwt.refresh.expiration}")
    private static Long refreshTokenDurationMs = 604800000l; // 7 days
    
    public static String generateRefreshToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenDurationMs);
        
        Map<String, Object> claims = new HashMap<>();
        
        // claims.put("roles", userDetails.getAuthorities());
        // claims.put("userId", user.getId());

        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
        
        RefreshTokenRepository.deleteByUserId(username, secretKey);
        
        return RefreshTokenRepository.save(refreshToken);
    }
    
    private static String generateSecureRandomString() {
        byte[] randomBytes = new byte[64];
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}

@SpringBootApplication
public class HelloworldApplication {

  @RestController
  class HelloworldController {

    @RequestMapping(value="/login/{user}/{password}", method = RequestMethod.GET)
    Pair<String,String> login(@PathVariable("user") String user, @PathVariable("password") String password){
      if (user.equalsIgnoreCase("fab") && password.equals("fab"))
      {
        return new Pair<String, String> (AccessTokenProvider.generateAccessToken(user), RefreshTokenProvider.generateRefreshToken(user));
      }
      else {
        return new Pair<String, String> ("","");
      }
    }

    @RequestMapping(value="/listings/{keyword}", method = RequestMethod.GET)
    List<Listing> getListings(@PathVariable("keyword") String keyword){
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

