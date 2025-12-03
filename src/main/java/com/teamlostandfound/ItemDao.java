package com.teamlostandfound;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ItemDao {

	public void addItem(String name, String category, String location, String date, String description, String status) throws SQLException {
		String sql = "INSERT INTO items(name, category, location, date, description, status) VALUES(?,?,?,?,?,?)";
		try (Connection connection = Database.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, name);
			ps.setString(2, category);
			ps.setString(3, location);
			ps.setString(4, date);
			ps.setString(5, description);
			ps.setString(6, status);
			ps.executeUpdate();
		}
	}

	public void updateItem(int id, String name, String category, String location, String date, String description, String status, String contactName, String contactPhone) throws SQLException {
		String sql = "UPDATE items SET name=?, category=?, location=?, date=?, description=?, status=?, contact_name=?, contact_phone=? WHERE id=?";
		try (Connection connection = Database.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, name);
			ps.setString(2, category);
			ps.setString(3, location);
			ps.setString(4, date);
			ps.setString(5, description);
			ps.setString(6, status);
			ps.setString(7, contactName);
			ps.setString(8, contactPhone);
			ps.setInt(9, id);
			ps.executeUpdate();
		}
	}

	public void deleteItem(int id) throws SQLException {
		String sql = "DELETE FROM items WHERE id=?";
		try (Connection connection = Database.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, id);
			ps.executeUpdate();
		}
	}

	public List<String> listItemNames() throws SQLException {
		String sql = "SELECT name FROM items ORDER BY id DESC";
		List<String> names = new ArrayList<>();
		try (Connection connection = Database.getConnection(); PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				names.add(rs.getString("name"));
			}
		}
		return names;
	}

	public List<Item> getAllItems() throws SQLException {
		String sql = "SELECT id, name, category, location, date, description, status, contact_name, contact_phone, visible FROM items ORDER BY id DESC";
		List<Item> items = new ArrayList<>();
		try (Connection connection = Database.getConnection(); 
			 PreparedStatement ps = connection.prepareStatement(sql); 
			 ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				int id = rs.getInt("id");
				String name = rs.getString("name");
				String category = rs.getString("category");
				String location = rs.getString("location");
				String date = rs.getString("date");
				String description = rs.getString("description");
				String status = rs.getString("status");
				String contactName = rs.getString("contact_name");
				String contactPhone = rs.getString("contact_phone");
				int visibleInt = rs.getInt("visible");
				boolean visible = visibleInt == 1;
				
				Item item = new Item(id, name, category, location, java.time.LocalDate.parse(date), description, status, contactName, contactPhone, visible);
				items.add(item);
			}
		}
		return items;
	}

	public void updateItemVisibility(int itemId, boolean visible) throws SQLException {
        String query = "UPDATE items SET visible = ? WHERE id = ?";
        try (Connection connection = Database.getConnection(); PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, visible ? 1 : 0);
            stmt.setInt(2, itemId);
            stmt.executeUpdate();
        }
    }
}


