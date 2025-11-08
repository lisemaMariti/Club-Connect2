package clubconnect.ui;

import clubconnect.dao.CommentDAO;
import clubconnect.dao.EventDAO;
import clubconnect.models.Club;
import clubconnect.models.Comment;
import clubconnect.models.Event;
import clubconnect.models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class LeaderDiscussionPanel extends JPanel {
    private Club club;
    private User leader;
    private JTable commentsTable;
    private DefaultTableModel tableModel;
    private JButton btnRefresh, btnHideComment, btnDeleteComment, btnRestoreComment, btnViewDetails, btnNewPost, btnReply;
    private JComboBox<String> statusFilterCombo;
    private JComboBox<Event> eventComboBox;
    private JLabel statsLabel;
    private JTextArea newPostArea;
    private JPanel postPanel;
    private boolean postPanelVisible = false;
    private Integer replyingToCommentId = null;

    public LeaderDiscussionPanel(Club club, User leader) {
        this.club = club;
        this.leader = leader;
        initComponents();
        loadClubComments();

        commentsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateButtonStates();
        });
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 5, 5, 5);

        JPanel topPanel = createTopPanel();
        gbc.gridx = 0; gbc.gridy = 0; gbc.weighty = 0.15;
        add(topPanel, gbc);

        JScrollPane tableScroll = createTableScroll();
        gbc.gridx = 0; gbc.gridy = 1; gbc.weighty = 0.85;
        add(tableScroll, gbc);

        postPanel = createPostPanel();
        gbc.gridx = 0; gbc.gridy = 2; gbc.weighty = 0.0;
        add(postPanel, gbc);
        postPanel.setVisible(false);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.add(new JLabel("🏢 Club: " + club.getName() + " | 📝 Discussion Board"));
        panel.add(infoPanel, BorderLayout.NORTH);

        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Status filter
        controlsPanel.add(new JLabel("Status:"));
        statusFilterCombo = new JComboBox<>(new String[]{"All", "Visible", "Hidden", "Deleted"});
        controlsPanel.add(statusFilterCombo);

        // Event filter
        controlsPanel.add(new JLabel("Event:"));
        eventComboBox = new JComboBox<>();
        loadLeaderEvents();
        controlsPanel.add(eventComboBox);

        // Buttons
        btnRefresh = new JButton("🔄 Refresh");
        btnHideComment = new JButton("👁️ Hide");
        btnDeleteComment = new JButton("🗑️ Delete");
        btnRestoreComment = new JButton("↩️ Restore");
        btnViewDetails = new JButton("📋 Details");
        btnNewPost = new JButton("💬 Create New Post");
        btnReply = new JButton("💬 Reply");

        controlsPanel.add(btnRefresh);
        controlsPanel.add(btnHideComment);
        controlsPanel.add(btnDeleteComment);
        controlsPanel.add(btnRestoreComment);
        controlsPanel.add(btnViewDetails);
        controlsPanel.add(btnReply);
        controlsPanel.add(btnNewPost);

        panel.add(controlsPanel, BorderLayout.CENTER);

        statsLabel = new JLabel("Loading statistics...");
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.add(statsLabel);
        panel.add(statsPanel, BorderLayout.SOUTH);

        setupEventListeners();
        return panel;
    }

    private void loadLeaderEvents() {
        List<Event> events = EventDAO.getEventsByLeaderId(leader.getUserId());
        eventComboBox.removeAllItems();
        eventComboBox.addItem(null); // "All events" option
        for (Event e : events) {
            eventComboBox.addItem(e);
        }
    }

    private JScrollPane createTableScroll() {
        String[] columns = {"ID", "User", "Club", "Content", "Date", "Status", "Likes", "Type"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
            public Class<?> getColumnClass(int col) { return col == 0 || col == 6 ? Integer.class : String.class; }
        };

        commentsTable = new JTable(tableModel);
        commentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        commentsTable.setRowHeight(25);
        commentsTable.getTableHeader().setReorderingAllowed(false);

        return new JScrollPane(commentsTable);
    }

    private JPanel createPostPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("💬 CREATE NEW POST"));
        panel.setBackground(new Color(240, 248, 255));

        newPostArea = new JTextArea(4, 50);
        newPostArea.setLineWrap(true);
        newPostArea.setWrapStyleWord(true);
        newPostArea.setFont(new Font("Arial", Font.PLAIN, 14));

        JScrollPane scroll = new JScrollPane(newPostArea);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSubmit = new JButton("📤 Submit");
        JButton btnClear = new JButton("🗑️ Clear");
        JButton btnCancel = new JButton("❌ Cancel");

        btnPanel.add(btnClear);
        btnPanel.add(btnCancel);
        btnPanel.add(btnSubmit);
        panel.add(btnPanel, BorderLayout.SOUTH);

        // Listeners
        btnSubmit.addActionListener(e -> createNewPostOrReply());
        btnClear.addActionListener(e -> newPostArea.setText(""));
        btnCancel.addActionListener(e -> togglePostPanel());

        return panel;
    }

    private void setupEventListeners() {
        btnRefresh.addActionListener(e -> refreshComments());
        btnHideComment.addActionListener(e -> hideSelectedComment());
        btnDeleteComment.addActionListener(e -> deleteSelectedComment());
        btnRestoreComment.addActionListener(e -> restoreSelectedComment());
        btnViewDetails.addActionListener(e -> viewCommentDetails());
        btnNewPost.addActionListener(e -> togglePostPanel());
        btnReply.addActionListener(e -> startReplyToSelectedComment());

        statusFilterCombo.addActionListener(e -> applyFilters());
        eventComboBox.addActionListener(e -> applyFilters());
    }

    private void togglePostPanel() {
        postPanelVisible = !postPanelVisible;
        postPanel.setVisible(postPanelVisible);
        if (!postPanelVisible) {
            newPostArea.setText("");
            replyingToCommentId = null;
        } else {
            newPostArea.requestFocus();
        }
        revalidate();
        repaint();
    }

    private void createNewPostOrReply() {
        String content = newPostArea.getText().trim();
        if (content.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter some content."); return; }
        if (content.length() > 500) { JOptionPane.showMessageDialog(this, "Max 500 chars."); return; }

        Comment comment = new Comment();
        comment.setClubId(club.getClubId());
        comment.setUserId(leader.getUserId());
        comment.setContent(content);
        Event selectedEvent = (Event) eventComboBox.getSelectedItem();
        if (selectedEvent != null) comment.setEventId(selectedEvent.getEventId());
        comment.setParentCommentId(replyingToCommentId);

        if (CommentDAO.addComment(comment)) {
            JOptionPane.showMessageDialog(this, "Post submitted!");
            newPostArea.setText("");
            togglePostPanel();
            refreshComments();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to submit post.");
        }
    }

    public void loadClubComments() {
        List<Comment> comments = CommentDAO.getCommentsForClub(club.getClubId());
        // Apply filters
        comments = comments.stream()
                .filter(c -> {
                    String status = statusFilterCombo.getSelectedItem().toString().toLowerCase();
                    return status.equals("all") || c.getStatus().equalsIgnoreCase(status);
                })
                .filter(c -> {
                    Event selected = (Event) eventComboBox.getSelectedItem();
                    return selected == null || (c.getEventId() != null && c.getEventId().equals(selected.getEventId()));
                })
                .collect(Collectors.toList());

        displayComments(comments);
        updateStatistics(comments);
    }

    private void displayComments(List<Comment> comments) {
        tableModel.setRowCount(0);
        for (Comment c : comments) addCommentRow(c, 0);
        updateButtonStates();
    }

    private void addCommentRow(Comment comment, int indent) {
        String user = comment.getUserName() != null ? comment.getUserName() : "User " + comment.getUserId();
        if (comment.getUserId() == leader.getUserId()) user = "👑 " + user + " (You)";
        String type = comment.getEventId() != null ? "Event Comment" : "Club Discussion";
        Object[] row = {
                comment.getCommentId(),
                user,
                comment.getClubName() != null ? comment.getClubName() : club.getName(),
                " ".repeat(indent * 4) + comment.getContent(),
                comment.getCreatedAt(),
                comment.getStatus(),
                comment.getLikes(),
                type
        };
        tableModel.addRow(row);

        List<Comment> replies = CommentDAO.getReplies(comment.getCommentId());
        for (Comment r : replies) addCommentRow(r, indent + 1);
    }

    private void updateStatistics(List<Comment> comments) {
        long total = comments.size();
        long visible = comments.stream().filter(c -> "visible".equalsIgnoreCase(c.getStatus())).count();
        long hidden = comments.stream().filter(c -> "hidden".equalsIgnoreCase(c.getStatus())).count();
        long deleted = comments.stream().filter(c -> "deleted".equalsIgnoreCase(c.getStatus())).count();
        long leaderPosts = comments.stream().filter(c -> c.getUserId() == leader.getUserId()).count();

        statsLabel.setText(String.format("📊 Total: %d | 👁️ Visible: %d | 🚫 Hidden: %d | 🗑️ Deleted: %d | 👑 Your Posts: %d",
                total, visible, hidden, deleted, leaderPosts));
    }

    private void applyFilters() { loadClubComments(); }
    private void refreshComments() { loadClubComments(); }

    private void hideSelectedComment() { updateSelectedCommentStatus("hidden"); }
    private void deleteSelectedComment() { updateSelectedCommentStatus("deleted"); }
    private void restoreSelectedComment() { updateSelectedCommentStatus("visible"); }

    private void updateSelectedCommentStatus(String status) {
        int row = commentsTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a comment."); return; }
        int id = (Integer) tableModel.getValueAt(row, 0);
        if (CommentDAO.updateCommentStatus(id, status)) refreshComments();
        else JOptionPane.showMessageDialog(this, "Failed to update comment.");
    }

    private void viewCommentDetails() {
        int row = commentsTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a comment."); return; }
        int id = (Integer) tableModel.getValueAt(row, 0);
        Comment c = CommentDAO.getCommentById(id);
        if (c == null) return;

        String details = String.format("ID: %d\nUser: %s\nType: %s\nStatus: %s\nLikes: %d\nDate: %s\nContent:\n%s",
                c.getCommentId(),
                c.getUserName(),
                c.getEventId() != null ? "Event Comment" : "Club Discussion",
                c.getStatus(),
                c.getLikes(),
                c.getCreatedAt(),
                c.getContent()
        );
        JOptionPane.showMessageDialog(this, details, "Comment Details", JOptionPane.INFORMATION_MESSAGE);
    }

    public void startReplyToSelectedComment() {
        int row = commentsTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a comment to reply."); return; }
        replyingToCommentId = (Integer) tableModel.getValueAt(row, 0);
        togglePostPanel();
        newPostArea.setText("Replying to comment #" + replyingToCommentId + "...\n");
        newPostArea.requestFocus();
    }

    private void updateButtonStates() {
        int row = commentsTable.getSelectedRow();
        if (row == -1) {
            btnHideComment.setEnabled(false); btnDeleteComment.setEnabled(false);
            btnRestoreComment.setEnabled(false); btnViewDetails.setEnabled(false);
            btnReply.setEnabled(false); return;
        }
        String status = (String) tableModel.getValueAt(row, 5);
        btnHideComment.setEnabled("visible".equalsIgnoreCase(status));
        btnDeleteComment.setEnabled(true);
        btnRestoreComment.setEnabled(!"visible".equalsIgnoreCase(status));
        btnViewDetails.setEnabled(true);
        btnReply.setEnabled(true);
    }
}
