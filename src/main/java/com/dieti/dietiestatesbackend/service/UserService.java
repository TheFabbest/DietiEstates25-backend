package com.dieti.dietiestatesbackend.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private static final Logger logger = Logger.getLogger(UserService.class.getName());
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$";
    private static final Pattern PASSWORD_COMPILED_PATTERN = Pattern.compile(PASSWORD_REGEX);
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    private final Connection myConnection;

    @Autowired
    public UserService(Connection myConnection) {
        this.myConnection = myConnection;
    }

    public boolean isPasswordStrong(String password) {
        final Matcher matcher = PASSWORD_COMPILED_PATTERN.matcher(password);
        return matcher.matches();
    }

    public boolean doesUserExist(String email, String password) {
        email = email.toLowerCase();
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

    public String getUsernameFromEmail(String email) {
        String query = "SELECT username FROM \"DietiEstates2025\".utente WHERE email = ?";
        try (PreparedStatement ps = myConnection.prepareStatement(query)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.isBeforeFirst()) {
                    rs.next();
                    return rs.getString("username");
                } else {
                    return "";
                }
            }
        } catch(SQLException e) {
            logger.log(Level.SEVERE, "Errore del database! {0}", e.getMessage());
            return "";
        }
    }

    public boolean doesUserExist(String email) {
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

    public String getErrorMessageUserCreation(SQLException e) {
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

    public void createUser(String email, String password, String username, String nome, String cognome) throws SQLException {
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