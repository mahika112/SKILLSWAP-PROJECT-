package service;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.User;



public class UserService {

    public boolean registerUser(User user, Connection conn) {
        String sql = "INSERT INTO users (name, email, password, skills, desired_skill) VALUES (?, ?, ?, ?,?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getSkills());
            stmt.setString(5, user.getDesiredSkill());


            int rows = stmt.executeUpdate();

            // Set generated user ID back to the user object (optional but useful)
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                }
            }

            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error in registerUser: " + e.getMessage());
            return false;
        }
    }

    // Login user
    public User loginUser(String email, String password) {
        String query = "SELECT * FROM users WHERE email = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String skills = rs.getString("skills");
                String desiredSkill = rs.getString("desiredSkill");

                return new User(id, name, email, password, skills, desiredSkill);
            }

        } catch (SQLException e) {
            System.out.println("Error in loginUser: " + e.getMessage());
        }

        return null; // login failed
    }

    // Get all other users except the current user
    public List<User> getAllOtherUsers(int currentUserId, Connection conn) {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users WHERE id != ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, currentUserId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String password = rs.getString("password"); // optional
                String skills = rs.getString("skills");
                String desiredSkill = rs.getString("desired_Skill");



                User user = new User(id, name, email, password, skills, desiredSkill);
                users.add(user);
            }

        } catch (SQLException e) {
            System.out.println("Error in getAllOtherUsers: " + e.getMessage());
        }

        return users;
    }
    //MATCHING USER
    public List<User> getMatchingUsers(User currentUser, Connection conn) {
        List<User> matchedUsers = new ArrayList<>();
        String query = "SELECT * FROM users WHERE FIND_IN_SET(?, skills) > 0 AND desired_skill = ? AND id != ?";
    
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, currentUser.getDesiredSkill()); // skill they want
            stmt.setString(2, currentUser.getSkills());       // skill they have
            stmt.setInt(3, currentUser.getId());              // don't match with self
    
            ResultSet rs = stmt.executeQuery();
    
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String password = rs.getString("password");
                String skills = rs.getString("skills");
                String desiredSkill = rs.getString("desired_skill");
    
                User matched = new User(id, name, email, password, skills, desiredSkill);
                matchedUsers.add(matched);
            }
    
        } catch (SQLException e) {
            System.out.println("Error in getMatchingUsers: " + e.getMessage());
        }
    
        return matchedUsers;
    }
    public List<String> findSkillSwapMatches(Connection conn) {
        List<String> matches = new ArrayList<>();
        List<User> users = getAllUsers(conn); // method to fetch all users
    
        for (User u1 : users) {
            for (User u2 : users) {
                if (u1.getId() == u2.getId()) continue; // Skip same user
    
                // Check if u1's skill is u2's desired skill and vice versa
                if (u1.getSkills().toLowerCase().contains(u2.getDesiredSkill().toLowerCase()) &&
                    u2.getSkills().toLowerCase().contains(u1.getDesiredSkill().toLowerCase())) {
    
                    String matchInfo = u1.getName() + " and " + u2.getName() +
                            " | " + u1.getSkills() + " -- " + u1.getDesiredSkill();
                    matches.add(matchInfo);
                }
            }
        }
    
        return matches;
    }
    
    public List<User> getAllUsers(Connection conn) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
    
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
    
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String password = rs.getString("password");
                String skills = rs.getString("skills");
                String desiredSkill = rs.getString("desired_skill");
    
                users.add(new User(id, name, email, password, skills, desiredSkill));
            }
        } catch (SQLException e) {
            System.out.println("Error in getAllUsers: " + e.getMessage());
        }
    
        return users;
    }
    
    
}
