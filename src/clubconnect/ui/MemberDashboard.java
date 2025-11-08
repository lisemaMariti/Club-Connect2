/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package clubconnect.ui;

import clubconnect.dao.AttendanceDAO;
import clubconnect.dao.ClubDAO;
import clubconnect.dao.CommentDAO;
import clubconnect.dao.EventDAO;
import clubconnect.dao.MembershipDAO;
import clubconnect.dao.NotificationDAO;
import clubconnect.dao.UserDAO;
import clubconnect.models.Club;
import clubconnect.models.Comment;
import clubconnect.models.Event;
import clubconnect.models.Notification;
import clubconnect.models.User;
import clubconnect.utils.PasswordUtil;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JComboBox;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author User
 */
public class MemberDashboard extends javax.swing.JFrame {
     private User user;
     private Club currentClub;
      private JPanel mainContentPanel;
      private JPanel dashboardContainer; 
      private javax.swing.JPanel dashboardContentPanel;



public MemberDashboard(User user) {
    this.user = user;
    this.currentClub = null;

    initComponents();
    setTitle("Member Dashboard - " + user.getName());
    setLocationRelativeTo(null);
       loadClubList();
       populateEventTableForMember();
        loadNotifications();
       populateRSVPStatuses();
        populateEvents();
        
  eventsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                eventsTableMouseClicked(evt);
            }
        });

       
btnViewDiscussion.addActionListener(e -> {
    int selectedRow = eventsTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select an event first.");
        return;
    }

    int eventId = (int) eventsTable.getModel().getValueAt(selectedRow, 0); // hidden event_id column
    Event event = EventDAO.getEventById(eventId); // you need a method to fetch one Event by ID
    if (event == null) {
        JOptionPane.showMessageDialog(this, "Event not found.");
        return;
    }

    openDiscussion(event); // <-- call it here
});


       
       btnApply.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent evt) {
        btnApplyActionPerformed(evt);
    }
});
}
    /**
     * Creates new form MemberDashboard
     */
    public MemberDashboard() {
        initComponents();
        loadClubList();
         populateEvents();
        
   

       
    }
    
    
    
    
    private void loadCommentsForEvent(int eventId) {
   pnComments.removeAll(); // clear existing comments

    List<Comment> comments = CommentDAO.getComments(eventId, currentClub.getClubId());

    for (Comment comment : comments) {
        if (comment.getParentCommentId() == null) {
            // Top-level comment panel
            JPanel commentPanel = new JPanel();
            commentPanel.setLayout(new BoxLayout(commentPanel, BoxLayout.Y_AXIS));
            commentPanel.add(new JLabel(comment.getUserName() + ": " + comment.getContent()));

            // Add child replies
            addReplies(commentPanel, comment.getCommentId(), comments);

            pnComments.add(commentPanel);
        }
    }

    pnComments.revalidate();
    pnComments.repaint();
}

    
    private void addReplies(JPanel parentPanel, int parentId, List<Comment> allComments) {
    for (Comment reply : allComments) {
        // Check if the comment is a reply to parentId
        if (reply.getParentCommentId() != null && reply.getParentCommentId() == parentId) {
            JPanel replyPanel = new JPanel();
            replyPanel.setLayout(new BoxLayout(replyPanel, BoxLayout.Y_AXIS));
            replyPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 5)); // indent
            replyPanel.add(new JLabel(reply.getUserName() + ": " + reply.getContent()));

            parentPanel.add(replyPanel);

            // Recursively add nested replies
            addReplies(replyPanel, reply.getCommentId(), allComments);
        }
    }
}

private void loadClubList() {
  
    List<Club> clubs = ClubDAO.getApprovedClubs(); 

    String[] columnNames = {"Club ID", "Name", "Description", "Leader"};
    DefaultTableModel model = new DefaultTableModel(columnNames, 0);

    for (Club c : clubs) {
        Object[] row = {
            c.getClubId(),
            c.getName(),
            c.getDescription(),
            
            c.getLeaderName() != null ? c.getLeaderName() : "N/A"
        };
        model.addRow(row);
    }

    clubList.setModel(model);
}

    
private void populateEventTableForMember() {
    if (user == null) return;

    DefaultTableModel model = new DefaultTableModel();
    // Add event_id column first (we can hide it in the JTable)
    model.setColumnIdentifiers(new Object[]{"Event ID", "Name", "Description", "Date", "Room"});

    List<Event> events = EventDAO.getEventsForMember(user.getUserId());

    for (Event e : events) {
        model.addRow(new Object[]{
            e.getEventId(), // store ID here
            e.getName(),
            e.getDescription(),
            e.getEventDate(),
            e.getRoomName()
        });
    }

    tblEvents.setModel(model);

    // Hide the event_id column visually
    tblEvents.getColumnModel().getColumn(0).setMinWidth(0);
    tblEvents.getColumnModel().getColumn(0).setMaxWidth(0);
    tblEvents.getColumnModel().getColumn(0).setWidth(0);
}

private void populateEvents() {
    if (user == null) return;

    DefaultTableModel model = new DefaultTableModel();
    // Add event_id and club_id columns (hidden later)
    model.setColumnIdentifiers(new Object[]{"Event ID", "Name", "Description", "Date", "Room", "Club ID"});

    List<Event> events = EventDAO.getEventsForMember(user.getUserId());

    for (Event e : events) {
        model.addRow(new Object[]{
            e.getEventId(),
            e.getName(),
            e.getDescription(),
            e.getEventDate(),
            e.getRoomName(),
            e.getClubId()  // store club ID for later
        });
    }

    eventsTable.setModel(model);

    // Hide event_id and club_id columns
    eventsTable.getColumnModel().getColumn(0).setMinWidth(0);
    eventsTable.getColumnModel().getColumn(0).setMaxWidth(0);
    eventsTable.getColumnModel().getColumn(0).setWidth(0);

    eventsTable.getColumnModel().getColumn(5).setMinWidth(0);
    eventsTable.getColumnModel().getColumn(5).setMaxWidth(0);
    eventsTable.getColumnModel().getColumn(5).setWidth(0);
}




private void loadNotifications() {
    txtAnnouncements.setText(""); 

    List<Notification> notifications = NotificationDAO.getNotificationsForMember(user.getUserId());

    for (Notification n : notifications) {
        String line = "[" + n.getSentAt() + "] Club ID " + n.getClubId() + ": " + n.getMessage() + "\n";
        txtAnnouncements.append(line);
    }
}

 private void populateRSVPStatuses() {
        cmbRSVPStatuses.addItem("Yes");
        cmbRSVPStatuses.addItem("No");
        cmbRSVPStatuses.addItem("Maybe");
     
    }
 
 private void openDiscussion(Event event) {
    if (event == null) return;
    if (currentClub == null) currentClub = new Club();
    currentClub.setClubId(event.getClubId());

    EventDiscussionPanel discussionPanel = new EventDiscussionPanel(event.getEventId(), currentClub, user);

    JFrame frame = new JFrame("Discussion - " + event.getName());
    frame.setContentPane(discussionPanel);
    frame.setSize(600, 500);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
}

 private void loadCommentsForEvent2(int eventId) {
    pnComments.removeAll(); // clear old comments
    List<Comment> comments = CommentDAO.getComments(eventId, currentClub.getClubId());

    // Build a tree: parentId -> list of replies
    Map<Integer, List<Comment>> repliesMap = new HashMap<>();
    List<Comment> topLevel = new ArrayList<>();

    for (Comment c : comments) {
        if ("visible".equalsIgnoreCase(c.getStatus())) {
            Integer parentId = c.getParentCommentId() == 0 ? null : c.getParentCommentId();
            if (parentId == null) {
                topLevel.add(c);
            } else {
                repliesMap.computeIfAbsent(parentId, k -> new ArrayList<>()).add(c);
            }
        }
    }

    // Recursive function to display comments with indentation
    for (Comment c : topLevel) {
        JPanel panel = createCommentPanel(c, repliesMap, 0);
        pnComments.add(panel);
    }

    pnComments.revalidate();
    pnComments.repaint();
}

private JPanel createCommentPanel(Comment comment, Map<Integer, List<Comment>> repliesMap, int indent) {
    JPanel commentPanel = new JPanel();
    commentPanel.setLayout(new BoxLayout(commentPanel, BoxLayout.Y_AXIS));
    commentPanel.setBorder(BorderFactory.createEmptyBorder(5, indent * 20, 5, 5));

    JLabel lblContent = new JLabel("<html><b>" + comment.getUserName() + ":</b> " + comment.getContent() + "</html>");
    commentPanel.add(lblContent);

    // Replies
    List<Comment> replies = repliesMap.get(comment.getCommentId());
    if (replies != null) {
        for (Comment reply : replies) {
            JPanel replyPanel = createCommentPanel(reply, repliesMap, indent + 1);
            commentPanel.add(replyPanel);
        }
    }

    return commentPanel;
}




    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        clubList = new javax.swing.JTable();
        btnApply = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblEvents = new javax.swing.JTable();
        btnSubmitRSVP = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        cmbRSVPStatuses = new javax.swing.JComboBox<>();
        btnCheckin = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtAnnouncements = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        btnUpdateProfile = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtCurrentPassword = new javax.swing.JTextField();
        lblNewPassword = new javax.swing.JLabel();
        txtNewPassword = new javax.swing.JPasswordField();
        txtConfirmPassword = new javax.swing.JPasswordField();
        jLabel9 = new javax.swing.JLabel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        pnComments = new java.awt.Panel();
        jScrollPane5 = new javax.swing.JScrollPane();
        eventsTable = new javax.swing.JTable();
        btnViewDiscussion = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        clubList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(clubList);

        btnApply.setText("Apply");
        btnApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApplyActionPerformed(evt);
            }
        });

        jLabel1.setText("all active clubs");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnApply)
                        .addGap(195, 195, 195))))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(200, 200, 200)
                .addComponent(jLabel1)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnApply)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Browse Clubs", jPanel1);

        tblEvents.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Name", "Description", "Date", "Room"
            }
        ));
        jScrollPane2.setViewportView(tblEvents);

        btnSubmitRSVP.setText("Submit RSVP");
        btnSubmitRSVP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubmitRSVPActionPerformed(evt);
            }
        });

        jButton4.setText("Cancel RSVP");

        jLabel2.setText("RSVPed events");

        btnCheckin.setText("Check-in");
        btnCheckin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCheckinActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 424, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(195, 195, 195)
                        .addComponent(jLabel2))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(cmbRSVPStatuses, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnSubmitRSVP)
                        .addGap(18, 18, 18)
                        .addComponent(jButton4)
                        .addGap(18, 18, 18)
                        .addComponent(btnCheckin)))
                .addContainerGap(140, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbRSVPStatuses, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSubmitRSVP)
                    .addComponent(jButton4)
                    .addComponent(btnCheckin))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("My Events", jPanel2);

        txtAnnouncements.setColumns(20);
        txtAnnouncements.setRows(5);
        jScrollPane3.setViewportView(txtAnnouncements);

        jLabel3.setText("club announcements");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 424, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(135, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(171, 171, 171))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(79, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Announcements", jPanel3);

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jScrollPane4.setViewportView(jTextArea2);

        jLabel4.setText("Feedback");

        jButton5.setText("submit");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(184, Short.MAX_VALUE)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 308, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(92, 92, 92))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(202, 202, 202)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(198, 198, 198)
                        .addComponent(jButton5)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(66, 66, 66)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton5)
                .addContainerGap(76, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Feedback", jPanel4);

        jLabel5.setText("Personal info");

        btnUpdateProfile.setText("Update");
        btnUpdateProfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateProfileActionPerformed(evt);
            }
        });

        jLabel6.setText("Name");

        jLabel7.setText("Email");

        jLabel8.setText("Current Password");

        lblNewPassword.setText("New Password");

        jLabel9.setText("Comfirm Password");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(198, 198, 198)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnUpdateProfile)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8)
                            .addComponent(lblNewPassword)
                            .addComponent(jLabel9))
                        .addGap(36, 36, 36)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtConfirmPassword, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
                            .addComponent(txtEmail)
                            .addComponent(txtName)
                            .addComponent(txtCurrentPassword)
                            .addComponent(txtNewPassword))))
                .addContainerGap(230, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel5)
                .addGap(41, 41, 41)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(txtCurrentPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblNewPassword)
                    .addComponent(txtNewPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtConfirmPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                .addComponent(btnUpdateProfile)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Profile", jPanel5);

        eventsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        eventsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                eventsTableMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(eventsTable);

        btnViewDiscussion.setText("View Disscussion");
        btnViewDiscussion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewDiscussionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnCommentsLayout = new javax.swing.GroupLayout(pnComments);
        pnComments.setLayout(pnCommentsLayout);
        pnCommentsLayout.setHorizontalGroup(
            pnCommentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 584, Short.MAX_VALUE)
            .addGroup(pnCommentsLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(btnViewDiscussion)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnCommentsLayout.setVerticalGroup(
            pnCommentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnCommentsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnViewDiscussion)
                .addGap(154, 154, 154))
        );

        jTabbedPane2.addTab("tab1", pnComments);

        jTabbedPane1.addTab("Comments", jTabbedPane2);

        jButton1.setText("Logout");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jButton1))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 396, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
 int confirm = JOptionPane.showConfirmDialog(
        this, 
        "Are you sure you want to log out?", 
        "Confirm Logout", 
        JOptionPane.YES_NO_OPTION
);

if (confirm == JOptionPane.YES_OPTION) {
    // Clear the session
    UserDAO.logout(); 

    // Close current dashboard
    this.dispose();

    // Open login screen
    new clubconnect.ui.LoginForm().setVisible(true);
}

    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnUpdateProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateProfileActionPerformed
        try {
            // ✅ 1. Get input from text fields
            String newName = txtName.getText().trim();
            String newEmail = txtEmail.getText().trim();
            String newPassword = new String(txtNewPassword.getPassword()).trim();

            // ✅ 2. Validate inputs
            if (newName.isEmpty() || newEmail.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name and Email cannot be empty.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            User updatedUser = new User();
            updatedUser.setUserId(user.getUserId());
            updatedUser.setName(newName);
            updatedUser.setEmail(newEmail);

            boolean updatePassword = false;

            if (!newPassword.isEmpty()) {
                updatedUser.setPasswordHash(PasswordUtil.hashPassword(newPassword));
                updatePassword = true;
            }

            //  Call DAO to update
            boolean success = UserDAO.updateProfile(updatedUser, updatePassword);

            //  Display result
            if (success) {
                JOptionPane.showMessageDialog(this, "Profile updated successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                user.setName(newName);
                user.setEmail(newEmail);

                // clear password field
                txtNewPassword.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update profile. Try again later.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating profile: " + e.getMessage(),
                "Exception", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnUpdateProfileActionPerformed

    private void btnCheckinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCheckinActionPerformed
        int selectedRow = tblEvents.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an event first.");
            return;
        }

        int eventId = (int) tblEvents.getModel().getValueAt(selectedRow, 0);
        int userId = user.getUserId();

        // Ask for confirmation
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to check in for this event?",
            "Confirm Check-In",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = AttendanceDAO.markAttendance(eventId, userId, true);
            if (success) {
                JOptionPane.showMessageDialog(this, "Checked in successfully!");
                //            refreshAttendanceTable(eventId); // update table to show new status
            } else {
                JOptionPane.showMessageDialog(this, "Failed to check in. Please try again.");
            }
        }
    }//GEN-LAST:event_btnCheckinActionPerformed

    private void btnSubmitRSVPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubmitRSVPActionPerformed

        int selectedRow = tblEvents.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an event first.");
            return;
        }

        int eventId = (int) tblEvents.getModel().getValueAt(selectedRow, 0);
        int userId = user.getUserId();
        String status = cmbRSVPStatuses.getSelectedItem().toString();

        // ✅ Automatically handle capacity logic
        try {
            int capacity = EventDAO.getEventCapacity(eventId);
            int confirmedCount = EventDAO.getConfirmedRSVPCount(eventId);

            if (status.equals("Yes") && confirmedCount >= capacity) {
                status = "Waitlist";
                JOptionPane.showMessageDialog(this,
                    "Event is full. You've been placed on the waitlist.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error checking event capacity: " + e.getMessage());
            return;
        }

        Integer waitlistPosition = null;
        if (status.equals("Waitlist")) {
            try {
                waitlistPosition = EventDAO.getNextWaitlistPosition(eventId);
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error calculating waitlist position: " + e.getMessage());
                return;
            }
        }

        int rsvpId = -1;
        try {
            rsvpId = EventDAO.addRSVPAndGetId(eventId, userId, status, waitlistPosition);
            if (rsvpId == -1) {
                JOptionPane.showMessageDialog(this, "Failed to submit RSVP. You may have already RSVPed.");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error submitting RSVP: " + e.getMessage());
            return;
        }

        Club club = ClubDAO.getClubByEventId(eventId);
        if (club != null) {
            String eventName = tblEvents.getModel().getValueAt(selectedRow, 1).toString();
            String message = status.equals("Waitlist")
            ? "You are on the waitlist for event: " + eventName
            : "Your RSVP for event: " + eventName + " is confirmed!";

            boolean notificationCreated = NotificationDAO.createRSVPNotification(userId, club.getClubId(), rsvpId, message);
            if (!notificationCreated) {
                System.err.println("Failed to create RSVP notification");
            }

            if (status.equals("Waitlist")) {
                NotificationDAO.createWaitlistNotification(rsvpId);
            }
        }

        JOptionPane.showMessageDialog(this, "RSVP submitted successfully!");
        populateEventTableForMember();

    }//GEN-LAST:event_btnSubmitRSVPActionPerformed

    private void btnApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApplyActionPerformed

        int selectedRow = clubList.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a club to apply for.",
                "No Club Selected",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int clubId = (int) clubList.getValueAt(selectedRow, 0);

        if (user == null) {
            JOptionPane.showMessageDialog(this,
                "You must be logged in to apply for a club.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        int userId = user.getUserId();

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to apply for this club?",
            "Confirm Application",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = MembershipDAO.requestMembership(userId, clubId);

            if (success) {
                JOptionPane.showMessageDialog(this,
                    "Membership request submitted successfully!\nPlease wait for approval.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to submit membership request. You may have already applied.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }

    }//GEN-LAST:event_btnApplyActionPerformed

    private void btnViewDiscussionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewDiscussionActionPerformed
                                                  
    // Check if a row is selected
    int selectedRow = eventsTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select an event first.");
        return;
    }

    // Get event_id and club_id from hidden columns
    int eventId = (int) eventsTable.getValueAt(selectedRow, 0);
    int clubId = (int) eventsTable.getValueAt(selectedRow, 5);

    // Fetch the Club object (replace with your DAO or method)
    Club selectedClub = ClubDAO.getClubById(clubId);

    // Current user object
    User currentUser = this.user; // assuming 'user' is your logged-in user

    // Create the discussion panel
    EventDiscussionPanel discussionPanel = new EventDiscussionPanel(eventId, selectedClub, currentUser);

    // Add discussionPanel to your dashboard (assuming a container panel exists)
    mainContentPanel.removeAll();  
    mainContentPanel.setLayout(new BorderLayout());
    mainContentPanel.add(discussionPanel, BorderLayout.CENTER);
    mainContentPanel.revalidate();
    mainContentPanel.repaint();


    }//GEN-LAST:event_btnViewDiscussionActionPerformed

    private void eventsTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_eventsTableMouseClicked
    int selectedRow = eventsTable.getSelectedRow();
        if (selectedRow >= 0) {
            int eventId = (int) eventsTable.getValueAt(selectedRow, 0); // hidden Event ID
            int clubId = (int) eventsTable.getValueAt(selectedRow, 5);  // hidden Club ID

            Club myClub = ClubDAO.getClubById(clubId);

            // Clear previous content and add discussion panel
            mainContentPanel.removeAll();
            EventDiscussionPanel discussionPanel = new EventDiscussionPanel(eventId, myClub, user);
            mainContentPanel.add(discussionPanel, BorderLayout.CENTER);

            mainContentPanel.revalidate();
            mainContentPanel.repaint();
        }
    }//GEN-LAST:event_eventsTableMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MemberDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MemberDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MemberDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MemberDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MemberDashboard().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnApply;
    private javax.swing.JButton btnCheckin;
    private javax.swing.JButton btnSubmitRSVP;
    private javax.swing.JButton btnUpdateProfile;
    private javax.swing.JButton btnViewDiscussion;
    private javax.swing.JTable clubList;
    private javax.swing.JComboBox<String> cmbRSVPStatuses;
    private javax.swing.JTable eventsTable;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JLabel lblNewPassword;
    private java.awt.Panel pnComments;
    private javax.swing.JTable tblEvents;
    private javax.swing.JTextArea txtAnnouncements;
    private javax.swing.JPasswordField txtConfirmPassword;
    private javax.swing.JTextField txtCurrentPassword;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtName;
    private javax.swing.JPasswordField txtNewPassword;
    // End of variables declaration//GEN-END:variables
}
