package clubconnect.models;

import java.util.Date;

public class Comment {
    private int commentId;
    private Integer eventId; 
    private int clubId;  
    private int userId;
    private String content;
    private Date createdAt;
    private String status; 
    private String userName;
    private String clubName; // ADD THIS FIELD
    private Integer parentCommentId;
    private int likes;

    public Comment() {}

    // getters & setters
    public int getCommentId() { return commentId; }
    public void setCommentId(int commentId) { this.commentId = commentId; }

    public Integer getEventId() { return eventId; }
    public void setEventId(Integer eventId) { this.eventId = eventId; }

    public int getClubId() { return clubId; }
    public void setClubId(int clubId) { this.clubId = clubId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    // ADD THIS GETTER AND SETTER
    public String getClubName() { return clubName; }
    public void setClubName(String clubName) { this.clubName = clubName; }

    public Integer getParentCommentId() { return parentCommentId; }
    public void setParentCommentId(Integer parentCommentId) { this.parentCommentId = parentCommentId; }

    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }

    @Override
    public String toString() {
        return "Comment{" +
                "commentId=" + commentId +
                ", eventId=" + eventId +
                ", clubId=" + clubId +
                ", userId=" + userId +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                ", status='" + status + '\'' +
                ", userName='" + userName + '\'' +
                ", clubName='" + clubName + '\'' +
                ", parentCommentId=" + parentCommentId +
                ", likes=" + likes +
                '}';
    }
}