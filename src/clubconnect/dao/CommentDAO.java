package clubconnect.dao;

import clubconnect.db.DBManager;
import static clubconnect.db.DBManager.getConnection;
import clubconnect.models.Comment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO {

public static boolean addComment(Comment comment) {
    String sql = """
        INSERT INTO comments (event_id, club_id, user_id, content, parent_comment_id, status, created_at)
        VALUES (?, ?, ?, ?, ?, 'visible', NOW())
    """;

    try (Connection conn = DBManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, comment.getEventId());
        ps.setInt(2, comment.getClubId());
        ps.setInt(3, comment.getUserId());
        ps.setString(4, comment.getContent());

        // Correctly handle top-level (null) comments
        if (comment.getParentCommentId() == null) {
            ps.setNull(5, java.sql.Types.INTEGER);
        } else {
            ps.setInt(5, comment.getParentCommentId());
        }

        return ps.executeUpdate() > 0;

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}


    // ✅ Fetch all visible comments for an event or club
    public static List<Comment> getComments(int eventId, int clubId) {
        List<Comment> list = new ArrayList<>();
        String sql = """
            SELECT c.comment_id, c.event_id, c.club_id, c.user_id, c.content,
                   c.created_at, c.status, c.parent_comment_id, u.name AS user_name
            FROM comments c
            JOIN users u ON c.user_id = u.user_id
            WHERE (c.event_id = ? OR c.club_id = ?) AND c.status = 'visible'
            ORDER BY c.created_at ASC
        """;

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, eventId);
            ps.setInt(2, clubId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Comment c = new Comment();
                c.setCommentId(rs.getInt("comment_id"));
                c.setEventId(rs.getInt("event_id"));
                c.setClubId(rs.getInt("club_id"));
                c.setUserId(rs.getInt("user_id"));
                c.setContent(rs.getString("content"));
                c.setCreatedAt(rs.getTimestamp("created_at"));
                c.setStatus(rs.getString("status"));
                c.setUserName(rs.getString("user_name"));

                int parentId = rs.getInt("parent_comment_id");
                c.setParentCommentId(rs.wasNull() ? 0 : parentId);

                list.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Hide comment
    public static boolean hideComment(int commentId) {
        return updateStatus(commentId, "hidden");
    }

    // ✅ Delete comment (soft delete)
    public static boolean deleteComment(int commentId) {
        return updateStatus(commentId, "deleted");
    }

    //  Internal helper
    private static boolean updateStatus(int commentId, String newStatus) {
        String sql = "UPDATE comments SET status = ? WHERE comment_id = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, commentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
     // Update comment (edit)
    public static boolean updateComment(Comment comment) {
        try (Connection conn = DBManager.getConnection()) {
            String sql = "UPDATE comments SET content=? WHERE comment_id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, comment.getContent());
            stmt.setInt(2, comment.getCommentId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // Add like (one per user)
    public static boolean addLike(int commentId, int userId) {
        try (Connection conn = DBManager.getConnection()) {
            // Check if already liked
            String checkSQL = "SELECT COUNT(*) FROM comment_likes WHERE comment_id=? AND user_id=?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSQL);
            checkStmt.setInt(1, commentId);
            checkStmt.setInt(2, userId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) return false; // already liked

            String sql = "INSERT INTO comment_likes(comment_id, user_id) VALUES (?,?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, commentId);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
public static List<Comment> getCommentsForClub(int clubId) {
    List<Comment> comments = new ArrayList<>();
    String sql = """
        SELECT c.*, u.name as user_name,
               (SELECT COUNT(*) FROM comment_likes WHERE comment_id = c.comment_id) as likes_count
        FROM comments c 
        LEFT JOIN users u ON c.user_id = u.user_id
        WHERE c.club_id = ? 
        ORDER BY c.created_at ASC
    """;

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, clubId);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Comment c = new Comment();
            c.setCommentId(rs.getInt("comment_id"));
            c.setUserId(rs.getInt("user_id"));
            c.setUserName(rs.getString("user_name"));
            c.setContent(rs.getString("content"));
            c.setClubId(rs.getInt("club_id"));
            
            // Handle event_id
            int eventId = rs.getInt("event_id");
            c.setEventId(rs.wasNull() ? null : eventId);
            
            int parentId = rs.getInt("parent_comment_id");
            c.setParentCommentId(rs.wasNull() ? null : parentId);
            
            c.setCreatedAt(rs.getTimestamp("created_at"));
            c.setLikes(rs.getInt("likes_count")); // Use the calculated likes
            c.setStatus(rs.getString("status"));
            comments.add(c);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return comments;
}
    
   public static List<Comment> getAllCommentsForAdmin() {
    List<Comment> comments = new ArrayList<>();
    String sql = """
        SELECT c.*, cl.name as club_name, u.name as user_name,
               (SELECT COUNT(*) FROM comment_likes WHERE comment_id = c.comment_id) as likes_count
        FROM comments c 
        LEFT JOIN clubs cl ON c.club_id = cl.club_id 
        LEFT JOIN users u ON c.user_id = u.user_id
        ORDER BY c.created_at DESC
    """;

    System.out.println("Executing SQL: " + sql);
    
    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        ResultSet rs = stmt.executeQuery();
        System.out.println("Query executed successfully");

        while (rs.next()) {
            Comment c = new Comment();
            c.setCommentId(rs.getInt("comment_id"));
            c.setUserId(rs.getInt("user_id"));
            c.setContent(rs.getString("content"));
            c.setClubId(rs.getInt("club_id"));
            
            // Handle event_id - might be null
            int eventId = rs.getInt("event_id");
            c.setEventId(rs.wasNull() ? null : eventId);
            
            int parentId = rs.getInt("parent_comment_id");
            c.setParentCommentId(rs.wasNull() ? null : parentId);
            
            c.setCreatedAt(rs.getTimestamp("created_at"));
            
            // Get likes count from subquery instead of direct column
            c.setLikes(rs.getInt("likes_count"));
            
            c.setStatus(rs.getString("status"));
            
            // Set user name and club name
            c.setUserName(rs.getString("user_name"));
            c.setClubName(rs.getString("club_name"));
            
            System.out.println("Found comment: ID=" + c.getCommentId() + 
                             ", Content=" + c.getContent() + 
                             ", Club=" + c.getClubName() +
                             ", Likes=" + c.getLikes());
            comments.add(c);
        }

        System.out.println("Total comments found: " + comments.size());
        
    } catch (SQLException e) {
        System.err.println("Error in getAllCommentsForAdmin: " + e.getMessage());
        e.printStackTrace();
    }

    return comments;
}
    
    public static void testConnection() {
    System.out.println("=== TESTING DATABASE CONNECTION ===");
    try (Connection conn = getConnection()) {
        System.out.println("Database connection SUCCESSFUL");
        
        // Test if comments table exists and has data
        String testSql = "SELECT COUNT(*) as count FROM comments";
        PreparedStatement stmt = conn.prepareStatement(testSql);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            int count = rs.getInt("count");
            System.out.println("Total comments in database: " + count);
        }
        
    } catch (SQLException e) {
        System.err.println("Database connection FAILED: " + e.getMessage());
        e.printStackTrace();
    }
}
    
    public static boolean updateCommentStatus(int commentId, String status) {
    String sql = "UPDATE comments SET status = ? WHERE comment_id = ?";
    try (Connection conn = DBManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, status);
        ps.setInt(2, commentId);
        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
    public static List<Comment> getReplies(int parentCommentId) {
    List<Comment> replies = new ArrayList<>();
    String sql = "SELECT * FROM comments WHERE parent_comment_id = ? ORDER BY created_at ASC";
    
    try (Connection conn = DBManager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, parentCommentId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Comment c = new Comment();
            c.setCommentId(rs.getInt("comment_id"));
            c.setUserId(rs.getInt("user_id"));
            c.setClubId(rs.getInt("club_id"));
            c.setEventId(rs.getObject("event_id") != null ? rs.getInt("event_id") : null);
            c.setParentCommentId(rs.getObject("parent_comment_id") != null ? rs.getInt("parent_comment_id") : null);
            c.setContent(rs.getString("content"));
            c.setStatus(rs.getString("status"));
            c.setCreatedAt(rs.getTimestamp("created_at"));
            replies.add(c);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return replies;
}

    public static Comment getCommentById(int id) {
        Comment comment = null;
        String sql = "SELECT * FROM comments WHERE comment_id = ?";

        try (Connection conn = DBManager.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    comment = new Comment();
                    comment.setCommentId(rs.getInt("comment_id"));
                    comment.setUserId(rs.getInt("user_id"));
                    comment.setClubId(rs.getInt("club_id"));
                    comment.setEventId(rs.getObject("event_id") != null ? rs.getInt("event_id") : null);
                    comment.setParentCommentId(rs.getObject("parent_comment_id") != null ? rs.getInt("parent_comment_id") : null);
                    comment.setContent(rs.getString("content"));
                    comment.setStatus(rs.getString("status"));
                    comment.setLikes(rs.getInt("likes"));
                    comment.setCreatedAt(rs.getTimestamp("created_at"));
                 
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return comment;
    }

}
