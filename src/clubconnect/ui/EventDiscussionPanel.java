package clubconnect.ui;

import clubconnect.models.Comment;
import clubconnect.models.Club;
import clubconnect.models.User;
import clubconnect.dao.CommentDAO;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class EventDiscussionPanel extends JPanel {

    private final JPanel pnComments;
    private final JTextField txtComment;
    private final JButton btnPost;
    private final JButton btnRefresh;

    private final int currentEventId;
    private final Club currentClub;
    private final User currentUser;

    private final Color[] replyColors = new Color[]{
            new Color(245, 245, 245),
            new Color(230, 250, 255),
            new Color(220, 240, 255),
            new Color(210, 235, 255),
            new Color(200, 230, 255)
    };

    public EventDiscussionPanel(int eventId, Club club, User user) {
        this.currentEventId = eventId;
        this.currentClub = club;
        this.currentUser = user;

        setLayout(new BorderLayout(10, 10));

        // Comments panel
        pnComments = new JPanel();
        pnComments.setLayout(new BoxLayout(pnComments, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(pnComments);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel for new comment
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        txtComment = new JTextField();
        btnPost = new JButton("Post");
        btnRefresh = new JButton("Refresh");

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(btnRefresh);
        btnPanel.add(btnPost);

        bottomPanel.add(txtComment, BorderLayout.CENTER);
        bottomPanel.add(btnPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        // Action listeners
        btnPost.addActionListener(e -> postNewComment(null));
        btnRefresh.addActionListener(e -> loadCommentsForEvent(currentEventId));

        loadCommentsForEvent(currentEventId);
    }

    private void postNewComment(Integer parentId) {
        try {
            String content = txtComment.getText().trim();
            if (content.isEmpty()) return;

            Comment comment = new Comment();
            comment.setUserId(currentUser.getUserId());
            comment.setUserName(currentUser.getName());
            comment.setContent(content);
            comment.setEventId(currentEventId);
            comment.setClubId(currentClub.getClubId());
            comment.setParentCommentId(parentId != null && parentId != 0 ? parentId : null);
            comment.setCreatedAt(new Date());
            comment.setStatus("visible");
            comment.setLikes(0);

            if (CommentDAO.addComment(comment)) {
                txtComment.setText("");
                loadCommentsForEvent(currentEventId);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to post comment.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error posting comment:\n" + e.getMessage());
        }
    }

    public void loadCommentsForEvent(int eventId) {
        try {
            pnComments.removeAll();

            List<Comment> comments = CommentDAO.getComments(eventId, currentClub.getClubId());
            if (comments == null || comments.isEmpty()) {
                pnComments.add(new JLabel("No comments yet."));
                pnComments.revalidate();
                pnComments.repaint();
                return;
            }

            // Map parent comment ID -> replies
            Map<Integer, List<Comment>> repliesMap = comments.stream()
                    .filter(c -> c.getParentCommentId() != null && c.getParentCommentId() != 0)
                    .collect(Collectors.groupingBy(Comment::getParentCommentId));

            // Top-level comments
            comments.stream()
                    .filter(c -> c.getParentCommentId() == null || c.getParentCommentId() == 0)
                    .forEach(c -> pnComments.add(createCommentPanel(c, repliesMap, 0)));

            pnComments.revalidate();
            pnComments.repaint();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading comments:\n" + e.getMessage());
        }
    }

    private JPanel createCommentPanel(Comment comment, Map<Integer, List<Comment>> repliesMap, int indent) {
        try {
            JPanel commentPanel = new JPanel();
            commentPanel.setLayout(new BoxLayout(commentPanel, BoxLayout.Y_AXIS));
            commentPanel.setBorder(BorderFactory.createEmptyBorder(5, indent * 30, 5, 5));

            Color bgColor = replyColors[Math.min(indent, replyColors.length - 1)];

            // Header with username and timestamp
            JPanel header = new JPanel(new BorderLayout());
            header.setBackground(bgColor);
            JLabel lblUser = new JLabel(comment.getUserName());
            lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
            JLabel lblDate = new JLabel(new SimpleDateFormat("MMM dd, yyyy HH:mm").format(comment.getCreatedAt()));
            lblDate.setForeground(Color.GRAY);
            lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            header.add(lblUser, BorderLayout.WEST);
            header.add(lblDate, BorderLayout.EAST);

            // Comment content
            JTextPane txtContent = new JTextPane();
            txtContent.setEditable(false);
            txtContent.setBackground(bgColor);
            txtContent.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            txtContent.setFont(new Font("Segoe UI", indent > 0 ? Font.ITALIC : Font.PLAIN, 12));
            highlightMentions(txtContent, comment.getContent());

            // Action buttons panel
            JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            actionPanel.setBackground(bgColor);

            // Reply button
            JButton btnReply = new JButton("Reply");
            btnReply.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            btnReply.addActionListener(e -> {
                String replyText = JOptionPane.showInputDialog(this, "Write your reply:");
                if (replyText != null && !replyText.isBlank()) {
                    txtComment.setText(replyText);
                    postNewComment(comment.getCommentId());
                }
            });
            actionPanel.add(btnReply);

            boolean isOwner = currentUser.getUserId() == comment.getUserId();
            boolean isAdmin = "ADMIN".equalsIgnoreCase(currentUser.getRole());
            boolean isLeader = "LEADER".equalsIgnoreCase(currentUser.getRole()) && currentUser.getClubId() == comment.getClubId();

            // Edit button for owner
            if (isOwner) {
                JButton btnEdit = new JButton("Edit");
                btnEdit.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                btnEdit.addActionListener(e -> {
                    String newText = JOptionPane.showInputDialog(this, "Edit your comment:", comment.getContent());
                    if (newText != null && !newText.isBlank()) {
                        comment.setContent(newText);
                        CommentDAO.updateComment(comment);
                        loadCommentsForEvent(currentEventId);
                    }
                });
                actionPanel.add(btnEdit);
            }

            // Delete button
            if (isOwner || isAdmin || isLeader) {
                JButton btnDelete = new JButton("Delete");
                btnDelete.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                btnDelete.addActionListener(e -> {
                    int choice = JOptionPane.showConfirmDialog(this, "Delete this comment?", "Confirm", JOptionPane.YES_NO_OPTION);
                    if (choice == JOptionPane.YES_OPTION) {
                        CommentDAO.deleteComment(comment.getCommentId());
                        loadCommentsForEvent(currentEventId);
                    }
                });
                actionPanel.add(btnDelete);
            }

            // Like button
            JButton btnLike = new JButton("Like (" + comment.getLikes() + ")");
            btnLike.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            btnLike.addActionListener(e -> {
                comment.setLikes(comment.getLikes() + 1);
                CommentDAO.addLike(comment.getCommentId(), comment.getLikes());
                btnLike.setText("Like (" + comment.getLikes() + ")");
            });
            actionPanel.add(btnLike);

            // Combine header, content, actions
            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.add(header, BorderLayout.NORTH);
            contentPanel.add(txtContent, BorderLayout.CENTER);
            contentPanel.add(actionPanel, BorderLayout.SOUTH);
            contentPanel.setBackground(bgColor);

            commentPanel.add(contentPanel);

            // Replies recursively
            List<Comment> replies = repliesMap.get(comment.getCommentId());
            if (replies != null && !replies.isEmpty()) {
                JPanel repliesPanel = new JPanel();
                repliesPanel.setLayout(new BoxLayout(repliesPanel, BoxLayout.Y_AXIS));
                repliesPanel.setBackground(bgColor);
                replies.forEach(reply -> repliesPanel.add(createCommentPanel(reply, repliesMap, indent + 1)));

                commentPanel.add(repliesPanel);

                // Toggle button for replies
                JButton btnToggleReplies = new JButton("Show/Hide Replies (" + replies.size() + ")");
                btnToggleReplies.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                btnToggleReplies.addActionListener(e -> {
                    boolean visible = repliesPanel.isVisible();
                    repliesPanel.setVisible(!visible);
                    commentPanel.revalidate();
                });
                JPanel togglePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
                togglePanel.setBackground(bgColor);
                togglePanel.add(btnToggleReplies);
                commentPanel.add(togglePanel);
            }

            return commentPanel;

        } catch (Exception e) {
            e.printStackTrace();
            return new JPanel();
        }
    }

    // Highlight @username mentions
    private void highlightMentions(JTextPane textPane, String content) {
        StyledDocument doc = textPane.getStyledDocument();
        SimpleAttributeSet normal = new SimpleAttributeSet();
        StyleConstants.setForeground(normal, Color.BLACK);

        SimpleAttributeSet mention = new SimpleAttributeSet();
        StyleConstants.setForeground(mention, Color.BLUE);
        StyleConstants.setBold(mention, true);

        try {
            doc.remove(0, doc.getLength());
            Pattern pattern = Pattern.compile("@\\w+");
            Matcher matcher = pattern.matcher(content);

            int lastEnd = 0;
            while (matcher.find()) {
                doc.insertString(doc.getLength(), content.substring(lastEnd, matcher.start()), normal);
                doc.insertString(doc.getLength(), matcher.group(), mention);
                lastEnd = matcher.end();
            }
            if (lastEnd < content.length()) {
                doc.insertString(doc.getLength(), content.substring(lastEnd), normal);
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}
