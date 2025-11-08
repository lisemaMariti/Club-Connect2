package clubconnect.models;

import java.util.ArrayList;
import java.util.List;

public class User {
    private int userId;
    private String name;
    private String email;
    private String passwordHash;
    private String role;
    private boolean isActive;
    private int clubId; // optional, if needed for a primary club
    private List<Club> clubs = new ArrayList<>(); // stores all associated clubs

    // --- Constructors ---
    public User() {}

    // Full constructor without clubs
    public User(int userId, String name, String email, String passwordHash, String role, boolean isActive) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.isActive = isActive;
    }

    // --- Getters & Setters ---
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean isActive) { this.isActive = isActive; }

    public int getClubId() { return clubId; }
    public void setClubId(int clubId) { this.clubId = clubId; }

    public List<Club> getClubs() { return clubs; }
    public void setClubs(List<Club> clubs) { this.clubs = clubs; }

    // --- Helper Methods ---
    public boolean isAdminOrLeader() {
        return "ADMIN".equalsIgnoreCase(this.role) || "LEADER".equalsIgnoreCase(this.role);
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(this.role);
    }

    public boolean isActiveUser() {
        return this.isActive;
    }

    @Override
    public String toString() {
        return name + " (" + role + ")";
    }
}
