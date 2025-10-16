package com.teamlostandfound;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserDao {

	public boolean authenticateUser(String username, String password) throws SQLException {
		String sql = "SELECT password_hash FROM users WHERE username = ?";
		try (Connection connection = Database.getConnection(); 
			 PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, username);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					String storedHash = rs.getString("password_hash");
					String inputHash = hashPassword(password);
					return storedHash.equals(inputHash);
				}
			}
		}
		return false;
	}

	public void createDefaultAdmin() throws SQLException {
		// Check if admin already exists
		String checkSql = "SELECT COUNT(*) FROM users WHERE username = 'admin'";
		try (Connection connection = Database.getConnection(); 
			 PreparedStatement ps = connection.prepareStatement(checkSql);
			 ResultSet rs = ps.executeQuery()) {
			if (rs.next() && rs.getInt(1) > 0) {
				return; // Admin already exists
			}
		}

		// Create default admin user
		String insertSql = "INSERT INTO users(username, password_hash, role) VALUES(?, ?, ?)";
		try (Connection connection = Database.getConnection(); 
			 PreparedStatement ps = connection.prepareStatement(insertSql)) {
			ps.setString(1, "admin");
			ps.setString(2, hashPassword("admin123"));
			ps.setString(3, "ADMIN");
			ps.executeUpdate();
		}
	}

	private String hashPassword(String password) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hash = md.digest(password.getBytes());
			StringBuilder hexString = new StringBuilder();
			for (byte b : hash) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1) {
					hexString.append('0');
				}
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("SHA-256 not available", e);
		}
	}
}
