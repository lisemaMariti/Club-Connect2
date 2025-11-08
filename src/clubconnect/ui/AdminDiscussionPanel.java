package clubconnect.ui;

import clubconnect.dao.CommentDAO;
import clubconnect.models.Club;
import clubconnect.models.Comment;
import clubconnect.models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AdminDiscussionPanel extends JPanel {
    private Club club;
    private User admin;
    private JTable commentsTable;
    private DefaultTableModel tableModel;
    private JButton btnRefresh;
    private JButton btnHideComment;
    private JButton btnDeleteComment;
    private JButton btnViewAll;
    private JComboBox<String> clubFilterCombo;
    private List<Club> allClubs;

    public AdminDiscussionPanel(Club club, User admin) {
        this.club = club;
        this.admin = admin;
        initComponents();
        loadAllClubs();
        
        // Load data immediately after UI is built
        SwingUtilities.invokeLater(() -> {
            loadAllComments();
        });
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel with controls
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        btnRefresh = new JButton("Refresh");
        btnHideComment = new JButton("Hide Comment");
        btnDeleteComment = new JButton("Delete Comment");
        btnViewAll = new JButton("View All Comments");
        
        clubFilterCombo = new JComboBox<>();
        clubFilterCombo.addItem("All Clubs");
        
        topPanel.add(new JLabel("Filter by Club:"));
        topPanel.add(clubFilterCombo);
        topPanel.add(btnRefresh);
        topPanel.add(btnHideComment);
        topPanel.add(btnDeleteComment);
        topPanel.add(btnViewAll);

        add(topPanel, BorderLayout.NORTH);

        // Table for comments
        String[] columnNames = {"Comment ID", "Club", "User ID", "Content", "Date", "Status", "Likes"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        commentsTable = new JTable(tableModel);
        commentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        commentsTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        commentsTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        commentsTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        commentsTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        commentsTable.getColumnModel().getColumn(3).setPreferredWidth(300);
        commentsTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        commentsTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        commentsTable.getColumnModel().getColumn(6).setPreferredWidth(60);

        JScrollPane scrollPane = new JScrollPane(commentsTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        add(scrollPane, BorderLayout.CENTER);

        // Add action listeners
        setupEventListeners();
    }

    private void setupEventListeners() {
        btnRefresh.addActionListener(e -> refreshComments());
        btnHideComment.addActionListener(e -> hideSelectedComment());
        btnDeleteComment.addActionListener(e -> deleteSelectedComment());
        btnViewAll.addActionListener(e -> loadAllComments());
        clubFilterCombo.addActionListener(e -> filterCommentsByClub());
    }

    private void loadAllClubs() {
        try {
            allClubs = clubconnect.dao.ClubDAO.getAllClubs();
            for (Club c : allClubs) {
                clubFilterCombo.addItem(c.getName() + " (ID: " + c.getClubId() + ")");
            }
            System.out.println("Loaded " + allClubs.size() + " clubs for filter");
        } catch (Exception e) {
            System.err.println("Error loading clubs: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void loadAllComments() {
        System.out.println("=== LOADING ALL COMMENTS ===");
        try {
            List<Comment> allComments = CommentDAO.getAllCommentsForAdmin();
            System.out.println("Retrieved " + allComments.size() + " comments from database");
            displayComments(allComments);
        } catch (Exception e) {
            System.err.println("Error loading comments: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading comments: " + e.getMessage());
        }
    }

    public void loadCommentsForClub(int clubId) {
        System.out.println("Loading comments for club ID: " + clubId);
        try {
            List<Comment> clubComments = CommentDAO.getCommentsForClub(clubId);
            System.out.println("Retrieved " + clubComments.size() + " comments for club " + clubId);
            displayComments(clubComments);
        } catch (Exception e) {
            System.err.println("Error loading club comments: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void displayComments(List<Comment> comments) {
        System.out.println("Displaying " + comments.size() + " comments in table");
        
        tableModel.setRowCount(0); // Clear existing rows

        if (comments.isEmpty()) {
            System.out.println("No comments to display");
            // Add a placeholder row
            tableModel.addRow(new Object[]{"", "No comments found", "", "", "", "", ""});
        } else {
            for (Comment comment : comments) {
                String clubName = comment.getClubName() != null ? comment.getClubName() : "Club " + comment.getClubId();
                
                Object[] row = {
                    comment.getCommentId(),
                    clubName,
                    comment.getUserId(),
                    comment.getContent(),
                    comment.getCreatedAt(),
                    comment.getStatus(),
                    comment.getLikes()
                };
                tableModel.addRow(row);
            }
            System.out.println("Added " + comments.size() + " rows to table");
        }
        
        // Force UI refresh
        tableModel.fireTableDataChanged();
        revalidate();
        repaint();
    }

    private void refreshComments() {
        System.out.println("Manual refresh triggered");
        loadAllComments();
    }

    private void hideSelectedComment() {
        int selectedRow = commentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a comment to hide.");
            return;
        }

        int commentId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to hide this comment?",
                "Confirm Hide",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = CommentDAO.hideComment(commentId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Comment hidden successfully!");
                refreshComments();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to hide comment.");
            }
        }
    }

    private void deleteSelectedComment() {
        int selectedRow = commentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a comment to delete.");
            return;
        }

        int commentId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this comment?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = CommentDAO.deleteComment(commentId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Comment deleted successfully!");
                refreshComments();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete comment.");
            }
        }
    }

    private void filterCommentsByClub() {
        String selected = (String) clubFilterCombo.getSelectedItem();
        if (selected == null) return;

        System.out.println("Filter selected: " + selected);

        if (selected.equals("All Clubs")) {
            loadAllComments();
        } else {
            try {
                int clubId = Integer.parseInt(selected.split("ID: ")[1].replace(")", ""));
                loadCommentsForClub(clubId);
            } catch (Exception e) {
                System.err.println("Error parsing club ID from filter: " + e.getMessage());
                JOptionPane.showMessageDialog(this, "Error filtering comments: " + e.getMessage());
            }
        }
    }

    // Public method to be called from AdminDashboard
    public void loadCommentsForClub(Integer clubId) {
        if (clubId == null) {
            loadAllComments();
        } else {
            loadCommentsForClub(clubId.intValue());
        }
    }
}