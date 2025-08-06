package com.dieti.dietiestatesbackend.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dieti.dietiestatesbackend.entities.Address;

@Service
public class AddressService {
    private static final Logger logger = Logger.getLogger(UserService.class.getName());

    private final Connection myConnection;

    @Autowired
    public AddressService(Connection myConnection) {
        this.myConnection = myConnection;
    }

    public Address getAddress(Long id) throws SQLException {
        String query = "SELECT id, country, province, city, street, street_number, building, latitude, longitude " +
                    "FROM dieti_estates.address WHERE id = ?";
        PreparedStatement ps = myConnection.prepareStatement(query);
        ps.setLong(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Address address = new Address();
            address.setId(rs.getLong("id"));
            address.setCountry(rs.getString("country"));
            address.setProvince(rs.getString("province"));
            address.setCity(rs.getString("city"));
            address.setStreet(rs.getString("street"));
            address.setStreet_number(rs.getString("street_number"));
            address.setBuilding(rs.getString("building"));
            address.setLatitude(rs.getBigDecimal("latitude"));
            address.setLongitude(rs.getBigDecimal("longitude"));
            return address;
        } else {
            return null;
        }
    }
}