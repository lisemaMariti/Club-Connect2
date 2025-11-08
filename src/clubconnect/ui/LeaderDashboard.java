/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package clubconnect.ui;

import clubconnect.dao.AttendanceDAO;

import clubconnect.dao.BudgetDAO;
import clubconnect.models.User;
import clubconnect.models.Club;
import clubconnect.dao.ClubDAO;
import static clubconnect.dao.ClubDAO.getClubName;
import clubconnect.dao.EventDAO;
import clubconnect.dao.MembershipDAO;
import clubconnect.dao.NotificationDAO;
import clubconnect.dao.RoomDAO;
import clubconnect.dao.UserDAO;
import clubconnect.models.Attendance;
import clubconnect.models.BudgetRequest;
import clubconnect.models.Event;
import clubconnect.models.Membership;
import clubconnect.models.Notification;
import clubconnect.models.Room;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import clubconnect.models.Room;

import clubconnect.models.Room;

import clubconnect.models.Room;

import javax.swing.table.DefaultTableModel;

import javax.swing.table.DefaultTableModel;

import java.util.List;

import java.util.List;

import javax.swing.table.DefaultTableModel;

import javax.swing.JOptionPane;

import clubconnect.models.Club;

import clubconnect.models.Club;
import clubconnect.services.NotificationService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;

public class LeaderDashboard extends javax.swing.JFrame {
     private int clubId;
    private User user;
     private Club currentClub;
    private LeaderDiscussionPanel leaderDiscussionPanel;
    
  




    
    public LeaderDashboard(int ClubId) {
    this.clubId = ClubId;
   
    initComponents();   
    setLocationRelativeTo(null);
   
    
    
    

}
//     public LeaderDashboard(User user, Club club) {
//        this.user = user;
//        this.currentClub = club;  // Directly set the club
//        initComponents();   
//        setLocationRelativeTo(null);
//        setTitle("Leader Dashboard - " + user.getName() + " | " + club.getName());
//        
//        setupDiscussionBoard(); 
//          loadPendingMemberships();
//    populateComboBox();
//    populateEventTable();
//    loadBudgetRequests();
//    populateEventComboBox();
//    populateEventTable();
//    setupDiscussionBoard();
//        
//        System.out.println("Leader " + user.getName() + " managing: " + club.getName());
//        
//        
//        
//    }
    
    
    
private void setupDiscussionBoard() {
    System.out.println("=== DEBUG: Setting up discussion board ===");
    System.out.println("Current Club: " + (currentClub != null ? currentClub.getName() : "NULL"));
    
    if (currentClub != null) {
        try {
            System.out.println("DEBUG: Creating LeaderDiscussionPanel...");
            leaderDiscussionPanel = new LeaderDiscussionPanel(currentClub, user);
            jTabbedPane2.addTab("Discussion Board 🗨️", leaderDiscussionPanel);
            
            // Refresh the correct component
            jTabbedPane2.revalidate();
            jTabbedPane2.repaint();
            
            System.out.println("DEBUG: Leader discussion panel loaded successfully");
            
        } catch (Exception e) {
            System.err.println("DEBUG: Error creating panel: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading discussion board: " + e.getMessage());
        }
    } else {
        System.out.println("DEBUG: No club - showing placeholder");
        // Add a more informative placeholder panel
        JPanel placeholderPanel = new JPanel(new BorderLayout());
        JLabel message = new JLabel("<html><center><b>No Club Assigned</b><br>Discussion board unavailable<br><br>" +
                                   "You are not currently assigned as a leader of any club.<br>" +
                                   "Please contact the administrator.</center></html>", JLabel.CENTER);
        message.setForeground(Color.RED);
        placeholderPanel.add(message, BorderLayout.CENTER);
        jTabbedPane2.addTab("Discussion Board 🗨️", placeholderPanel);
        
        jTabbedPane2.revalidate();
        jTabbedPane2.repaint();
    }
}

private void populateComboBox() {
    comboRoom.removeAllItems();
    comboRoom.addItem("0 - Select event room"); 

    List<Room> rooms = RoomDAO.getAllRooms();
    for (Room room : rooms) {
        comboRoom.addItem(room.getRoomId() + " - " + room.getRoomName());
    }
}



private void populateEventTable() {
    DefaultTableModel model = (DefaultTableModel) tblEvents.getModel();
    model.setRowCount(0); // clear existing rows

    List<Event> events = EventDAO.getEventsByClubId(clubId);

    // Include the hidden ID as the first column
    for (Event e : events) {
        model.addRow(new Object[]{
            e.getEventId(),  // hidden ID
            e.getName(),
            e.getDescription(),
            e.getEventDate(),
            e.getRoomName()
        });
    }

    tblEvents.setModel(model);

    // Hide the ID column so user doesn't see it
    tblEvents.getColumnModel().getColumn(0).setMinWidth(0);
    tblEvents.getColumnModel().getColumn(0).setMaxWidth(0);
    tblEvents.getColumnModel().getColumn(0).setWidth(0);
}
// -----------------------------
// Load Budget Requests for this Leader
// -----------------------------
private void loadBudgetRequests() {
    DefaultTableModel model = new DefaultTableModel(
        new Object[]{"ID", "Amount", "Status", "Purpose"}, 0
    );

    // Get all requests from DAO
    List<BudgetRequest> requests = BudgetDAO.getAllBudgetRequests();

    for (BudgetRequest br : requests) {
        Club club = ClubDAO.getClubByLeaderId(user.getUserId());
        if (club != null && br.getEventId() == 0 && club.getClubId() == br.getClubId()) {
            model.addRow(new Object[]{
                br.getBudgetId(),
                "R " + String.format("%.2f", br.getAmount()),
                br.getStatus(),
                br.getPurpose()
            });
        }
    }

    tblBudgetRequests.setModel(model);
}

private void populateEventComboBox() {
    cmbEvents.removeAllItems();

    // ✅ Add a default prompt at the top
    cmbEvents.addItem("Choose Event");

    int leaderId = user.getUserId(); 
    List<Event> events = EventDAO.getEventsByLeaderId(leaderId);

    for (Event e : events) {
        cmbEvents.addItem(e.getEventId() + " - " + e.getName());
    }
}





    //  Constructor used when a leader logs in
    public LeaderDashboard(User user) {
        this.user = user;
        
   
    this.clubId = ClubDAO.getClubIdByLeader(user.getUserId());
    System.out.println("DEBUG: Found club ID: " + this.clubId);
    
    
    if (this.clubId > 0) {
        this.currentClub = ClubDAO.getClubById(this.clubId);
        System.out.println("DEBUG: Loaded club: " + (this.currentClub != null ? this.currentClub.getName() : "null"));
    } else {
        this.currentClub = null;
        System.out.println("DEBUG: No club ID found for user");
     
    }
        initComponents();
        setTitle("Leader Dashboard - " + user.getName());
        setLocationRelativeTo(null);
        loadPendingMemberships();
        populateComboBox();
        populateEventTable();
        loadBudgetRequests();
         populateEventComboBox();
            setupDiscussionBoard(); 
            
              if (this.currentClub != null) {
        this.clubId = currentClub.getClubId();
    } else {
        this.clubId = 0;
        
        
    }
              
              
                  if (currentClub != null) {
        setTitle("Leader Dashboard - " + user.getName() + " | " + currentClub.getName());
        jLabel2.setText("Club: " + currentClub.getName() + " (" + currentClub.getStatus() + ")");
        
        btnApproveMember.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        btnApproveMemberActionPerformed(e);
    }
});


        // 🧠 Check first before showing dashboard
        Club club = ClubDAO.getClubByLeaderId(user.getUserId());
        if (club == null) {
            int option = JOptionPane.showConfirmDialog(
                    null,
                    "You don’t have a club yet. Would you like to create one now?",
                    "Create Club",
                    JOptionPane.YES_NO_OPTION
            );

            if (option == JOptionPane.YES_OPTION) {
                // ✅ Open ClubForm before dashboard appears
                ClubForm clubForm = new ClubForm(user);
                clubForm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                clubForm.setVisible(true);

                // When ClubForm closes, reopen dashboard
                clubForm.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent e) {
                        Club createdClub = ClubDAO.getClubByLeaderId(user.getUserId());
                        if (createdClub != null) {
                            // now that the club exists, show the dashboard
                            jLabel2.setText("Club: " + createdClub.getName() + " (" + createdClub.getStatus() + ")");
                            setVisible(true);
                        } else {
                            JOptionPane.showMessageDialog(null, "No club created. Exiting.");
                            dispose();
                        }
                    }
                });
            } else {
                JOptionPane.showMessageDialog(null, "You must create a club to continue.");
                dispose();
            }
        } else {
            // ✅ Leader already has a club — show dashboard directly
            jLabel2.setText("Club: " + club.getName() + " (" + club.getStatus() + ")");
            setVisible(true);
        }
                  }
    }
    

  private void loadPendingMemberships() {
    List<Membership> pendingList = MembershipDAO.getPendingMembershipsByLeader(user.getUserId());
    String[] columnNames = {"Membership ID", "User ID", "Club ID", "Status", "Joined At"};
    DefaultTableModel model = new DefaultTableModel(columnNames, 0);

    for (Membership m : pendingList) {
        Object[] row = {
            m.getMembershipId(),
            m.getUserId(),
            m.getClubId(),
            m.getStatus(),
            m.getJoinedAt() != null ? m.getJoinedAt().toString() : ""
        };
        model.addRow(row);
    }

    tblMembers.setModel(model);
}
private void populateAttendanceTable(int eventId) {
    try {
        List<User> attendees = AttendanceDAO.getAttendeesForEvent(eventId);
        
        if (attendees.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No RSVPs found for this event.");
            return;
        }
        
        // Create simple dialog
        JDialog attendanceDialog = new JDialog(this, "Event RSVPs - Event ID: " + eventId, true);
        attendanceDialog.setLayout(new BorderLayout());
        attendanceDialog.setSize(400, 300);
        attendanceDialog.setLocationRelativeTo(this);
        
        // Create simple table
        String[] columns = {"Name", "RSVP Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        for (User u : attendees) {
            model.addRow(new Object[]{
                u.getName(),
//                u.getRsvpStatus() // This will show "Yes", "Maybe", or "No"
            });
        }
        
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        
        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> attendanceDialog.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnClose);
        
        attendanceDialog.add(scrollPane, BorderLayout.CENTER);
        attendanceDialog.add(buttonPanel, BorderLayout.SOUTH);
        attendanceDialog.setVisible(true);
        
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error loading RSVPs: " + e.getMessage());
    }
}

    // Default constructor (keep for design view)
    public LeaderDashboard() {
        initComponents();
                loadPendingMemberships();
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        btnEditClubDetails = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblMembers = new javax.swing.JTable();
        btnApproveMember = new javax.swing.JButton();
        btnRejectMember = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblEvents = new javax.swing.JTable();
        btnCreateEvent = new javax.swing.JButton();
        txtName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtDescription = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        dateChooserEventDate = new com.toedter.calendar.JDateChooser();
        btnMarkAttendance = new javax.swing.JButton();
        comboRoom = new javax.swing.JComboBox<>();
        requestBudgetCheckbox = new javax.swing.JCheckBox();
        btnFilterEventsbyCalender = new javax.swing.JButton();
        btnrefresh = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblBudgetRequests = new javax.swing.JTable();
        btnRequestBudget = new javax.swing.JButton();
        cmbEvents = new javax.swing.JComboBox<>();
        btnViewExpenses = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtAnnouncements = new javax.swing.JTextArea();
        btnSendNotification = new javax.swing.JButton();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        btnLogout = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel2.setText("Club info");

        btnEditClubDetails.setText("Edit Club");
        btnEditClubDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditClubDetailsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(178, 178, 178)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(169, 169, 169)
                        .addComponent(btnEditClubDetails)))
                .addContainerGap(533, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 280, Short.MAX_VALUE)
                .addComponent(btnEditClubDetails)
                .addGap(31, 31, 31))
        );

        jTabbedPane1.addTab("My Club", jPanel1);

        tblMembers.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane4.setViewportView(tblMembers);

        btnApproveMember.setText("Approve");
        btnApproveMember.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApproveMemberActionPerformed(evt);
            }
        });

        btnRejectMember.setText("Reject");
        btnRejectMember.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRejectMemberActionPerformed(evt);
            }
        });

        jLabel1.setText("pending member requests");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addComponent(btnApproveMember)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnRejectMember)
                .addGap(75, 75, 75))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(130, 130, 130)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(506, Short.MAX_VALUE))
            .addComponent(jScrollPane4)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnApproveMember)
                    .addComponent(btnRejectMember))
                .addContainerGap(106, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Membership", jPanel2);

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
        jScrollPane3.setViewportView(tblEvents);

        btnCreateEvent.setText("Create Event");
        btnCreateEvent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateEventActionPerformed(evt);
            }
        });

        jLabel3.setText("Name");

        jLabel4.setText("Description");

        txtDescription.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDescriptionActionPerformed(evt);
            }
        });

        jLabel5.setText("EventDate");

        btnMarkAttendance.setText("Attendance");
        btnMarkAttendance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMarkAttendanceActionPerformed(evt);
            }
        });

        comboRoom.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select event room" }));
        comboRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboRoomActionPerformed(evt);
            }
        });

        requestBudgetCheckbox.setText("Request Budget");

        btnFilterEventsbyCalender.setText("filter");
        btnFilterEventsbyCalender.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterEventsbyCalenderActionPerformed(evt);
            }
        });

        btnrefresh.setText("Refresh Events");
        btnrefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnrefreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(requestBudgetCheckbox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnCreateEvent, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnMarkAttendance, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnFilterEventsbyCalender)
                        .addGap(29, 29, 29)
                        .addComponent(btnrefresh)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtName, javax.swing.GroupLayout.DEFAULT_SIZE, 329, Short.MAX_VALUE)
                            .addComponent(txtDescription)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addComponent(jLabel4))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(31, 31, 31)
                                .addComponent(jLabel5))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(28, 28, 28)
                                .addComponent(jLabel3))
                            .addComponent(dateChooserEventDate, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(comboRoom, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 447, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDescription, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dateChooserEventDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(comboRoom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCreateEvent)
                    .addComponent(btnMarkAttendance)
                    .addComponent(requestBudgetCheckbox)
                    .addComponent(btnFilterEventsbyCalender)
                    .addComponent(btnrefresh))
                .addGap(115, 115, 115))
        );

        jTabbedPane1.addTab("Events", jPanel3);

        tblBudgetRequests.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tblBudgetRequests);

        btnRequestBudget.setText("Request budget");
        btnRequestBudget.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRequestBudgetActionPerformed(evt);
            }
        });

        btnViewExpenses.setText("View Expenses");
        btnViewExpenses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewExpensesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(cmbEvents, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnRequestBudget)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnViewExpenses)
                .addGap(47, 47, 47))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 616, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbEvents, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRequestBudget)
                    .addComponent(btnViewExpenses))
                .addContainerGap(180, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Budget", jPanel4);

        txtAnnouncements.setColumns(20);
        txtAnnouncements.setRows(5);
        jScrollPane1.setViewportView(txtAnnouncements);

        btnSendNotification.setText("Send to Members");
        btnSendNotification.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendNotificationActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addComponent(btnSendNotification)))
                .addContainerGap(548, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSendNotification)
                .addContainerGap(187, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Notifications", jPanel5);
        jTabbedPane1.addTab("DiscussionBoard", jTabbedPane2);

        btnLogout.setText("Logout");
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnLogout)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(btnLogout)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnEditClubDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditClubDetailsActionPerformed
                                                     
    if (currentClub == null) {
        JOptionPane.showMessageDialog(this, "No club found to edit.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Create input fields
    JTextField txtClubName = new JTextField(currentClub.getName(), 20);
    JTextArea txtDescription = new JTextArea(currentClub.getDescription(), 4, 20);
    txtDescription.setLineWrap(true);
    txtDescription.setWrapStyleWord(true);
    JScrollPane scrollPane = new JScrollPane(txtDescription);

    // Create panel with form layout
    JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
    panel.add(new JLabel("Club Name:"));
    panel.add(txtClubName);
    panel.add(new JLabel("Description:"));
    panel.add(scrollPane);

    int result = JOptionPane.showConfirmDialog(this, panel, 
        "Edit Club Details", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {
        String newName = txtClubName.getText().trim();
        String newDescription = txtDescription.getText().trim();

        if (newName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Club name cannot be empty.", 
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Update club object
        currentClub.setName(newName);
        currentClub.setDescription(newDescription);

        // Save to database
        boolean success = ClubDAO.updateClubProfile(currentClub);
        
        if (success) {
            JOptionPane.showMessageDialog(this, "Club details updated successfully!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Update UI
            jLabel2.setText("Club: " + newName + " (" + currentClub.getStatus() + ")");
            setTitle("Leader Dashboard - " + user.getName() + " | " + newName);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update club details.", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    }//GEN-LAST:event_btnEditClubDetailsActionPerformed

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
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

    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnApproveMemberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApproveMemberActionPerformed
                                                      
    int selectedRow = tblMembers.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this,
                "Please select a member to approve.",
                "No Member Selected",
                JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Get membership ID from the table
    int membershipId = (int) tblMembers.getValueAt(selectedRow, 0);

    int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to approve this membership?",
            "Confirm Approval",
            JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) {
        boolean success = MembershipDAO.updateMembershipStatus(membershipId, "Active");

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Membership approved successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            // Refresh table after approval
            loadPendingMemberships();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to approve membership. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    }//GEN-LAST:event_btnApproveMemberActionPerformed

    private void btnRejectMemberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRejectMemberActionPerformed
         int selectedRow = tblMembers.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this,
                "Please select a member to reject.",
                "No Member Selected",
                JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Get membership ID from the table (make sure column 0 is membership_id)
    int membershipId = (int) tblMembers.getValueAt(selectedRow, 0);

    int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to reject this membership?",
            "Confirm Rejection",
            JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) {
        boolean success = MembershipDAO.updateMembershipStatus(membershipId, "Rejected");

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Membership rejected successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            // Refresh the pending members
            loadPendingMemberships();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to reject membership. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    }//GEN-LAST:event_btnRejectMemberActionPerformed

    private void btnCreateEventActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateEventActionPerformed
try {
    RoomDAO.releaseExpiredRooms(); // release expired rooms

    // gather form data
    String name = txtName.getText().trim();
    String description = txtDescription.getText().trim();
    java.util.Date eventDate = dateChooserEventDate.getDate();

    if (name.isEmpty() || eventDate == null) {
        JOptionPane.showMessageDialog(this, "Please fill in all required fields.",
                                      "Validation Error", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // validate room selection
    String selectedRoom = (String) comboRoom.getSelectedItem();
    if (selectedRoom == null || selectedRoom.startsWith("0 -")) {
        JOptionPane.showMessageDialog(this, "Please select a room.",
                                      "Validation Error", JOptionPane.WARNING_MESSAGE);
        return;
    }

    int roomId = Integer.parseInt(selectedRoom.split(" - ")[0].trim());

    // check room availability
    if (!RoomDAO.isRoomAvailable(roomId, eventDate)) {
        JOptionPane.showMessageDialog(this, "Selected room is already booked. Please choose another room.",
                                      "Validation Error", JOptionPane.WARNING_MESSAGE);
        return;
    }

    Event event = new Event(0, this.clubId, name, description, eventDate, roomId);
    int eventId = EventDAO.createEvent(event, this.clubId);

    if (eventId > 0) {
        RoomDAO.markRoomAsBooked(roomId);
        JOptionPane.showMessageDialog(this, "Event created and room booked successfully!");
         populateEventTable();

        if (requestBudgetCheckbox.isSelected()) {
            BudgetRequestDialog dialog = new BudgetRequestDialog(this, this.clubId, eventId);
            dialog.setVisible(true);
            BudgetRequest br = dialog.getBudgetRequest();
            if (br != null && BudgetDAO.submitBudgetRequest(br)) {
                JOptionPane.showMessageDialog(this, "Budget request submitted successfully!");
            }
        }

        // clear form
        txtName.setText("");
        txtDescription.setText("");
        dateChooserEventDate.setDate(null);
        comboRoom.setSelectedIndex(0);
        requestBudgetCheckbox.setSelected(false);

    } else {
        JOptionPane.showMessageDialog(this, "Failed to create event.", "Error", JOptionPane.ERROR_MESSAGE);
    }

} catch (NumberFormatException nfe) {
    JOptionPane.showMessageDialog(this, "Invalid room selection.", "Validation Error", JOptionPane.WARNING_MESSAGE);
} catch (Exception e) {
    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
    e.printStackTrace();
}
    }//GEN-LAST:event_btnCreateEventActionPerformed

    private void txtDescriptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDescriptionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDescriptionActionPerformed

    private void comboRoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboRoomActionPerformed
 String selected = (String) comboRoom.getSelectedItem();
if (selected != null && !selected.startsWith("0 -")) { // skip placeholder
    // Parse room ID and name from string "id - name"
    int roomId = Integer.parseInt(selected.split(" - ")[0]);
    String roomName = selected.split(" - ")[1];
    System.out.println("User selected room: " + roomName + " (ID: " + roomId + ")");
}
       // TODO add your handling code here:
    }//GEN-LAST:event_comboRoomActionPerformed


    private void btnMarkAttendanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMarkAttendanceActionPerformed
       try {
        int selectedRow = tblEvents.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an event first.");
            return;
        }

        // Convert to int safely
        int eventId = Integer.parseInt(tblEvents.getModel().getValueAt(selectedRow, 0).toString());

        // Populate attendance table for selected event
        populateAttendanceTable(eventId);

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Invalid event ID.");
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
    }

    }//GEN-LAST:event_btnMarkAttendanceActionPerformed

    private void btnRequestBudgetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRequestBudgetActionPerformed
   String description = JOptionPane.showInputDialog(this, "Enter budget description:");
    if (description == null || description.trim().isEmpty()) return;

    String amountStr = JOptionPane.showInputDialog(this, "Enter requested amount:");
    if (amountStr == null || amountStr.trim().isEmpty()) return;

    try {
        double amount = Double.parseDouble(amountStr);
        Club club = ClubDAO.getClubByLeaderId(user.getUserId());

        if (club == null) {
            JOptionPane.showMessageDialog(this, "No club found for your account.");
            return;
        }

        //  Get selected event ID from combo box
        int eventId = 0; // Default to no event
        if (cmbEvents.getSelectedItem() != null) {
            String selected = cmbEvents.getSelectedItem().toString(); 
            // Assuming item format: "eventId - eventName"
            eventId = Integer.parseInt(selected.split(" - ")[0]);
        }

        //  Create budget request
        BudgetRequest budgetRequest = new BudgetRequest(
            club.getClubId(),
            eventId,   
            amount,
            description
        );

        // ✅Submit and notify user
        if (BudgetDAO.submitBudgetRequest(budgetRequest)) {
            JOptionPane.showMessageDialog(this, 
                "Budget request submitted for approval.");
        } else {
            JOptionPane.showMessageDialog(this, 
                "Error submitting budget request.");
             loadBudgetRequests();
        }

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, 
            "Invalid amount entered. Please enter a valid number.");
    }

    }//GEN-LAST:event_btnRequestBudgetActionPerformed

    private void btnFilterEventsbyCalenderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterEventsbyCalenderActionPerformed
                                                         
    try {
        // Fetch all events for this club
        List<Event> events = EventDAO.getAllEvents(this.clubId);

        // Create the dialog
        JDialog calendarDialog = new JDialog(this, "Event Calendar", true);
        calendarDialog.setSize(800, 600);
        calendarDialog.setLocationRelativeTo(this);

        // Create tabbed pane for Today, Week, Month
        JTabbedPane tabbedPane = new JTabbedPane();

        // TODAY tab
        JPanel todayPanel = new JPanel(new BorderLayout());
        LocalDate today = LocalDate.now();
        DefaultListModel<String> todayListModel = new DefaultListModel<>();
        for (Event e : events) {
            LocalDate eventDate = e.getEventDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (eventDate.equals(today)) {
                todayListModel.addElement(e.getName() + " (" + e.getRoomId() + ")");
            }
        }
        JList<String> todayList = new JList<>(todayListModel);
        todayPanel.add(new JScrollPane(todayList), BorderLayout.CENTER);
        tabbedPane.addTab("Today", todayPanel);

        // WEEK tab
        JPanel weekPanel = new JPanel(new BorderLayout());
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);
        DefaultListModel<String> weekListModel = new DefaultListModel<>();
        for (Event e : events) {
            LocalDate eventDate = e.getEventDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (!eventDate.isBefore(startOfWeek) && !eventDate.isAfter(endOfWeek)) {
                weekListModel.addElement(eventDate + " - " + e.getName() + " (" + e.getRoomId() + ")");
            }
        }
        JList<String> weekList = new JList<>(weekListModel);
        weekPanel.add(new JScrollPane(weekList), BorderLayout.CENTER);
        tabbedPane.addTab("Week", weekPanel);

        // MONTH tab
        JPanel monthPanel = new JPanel(new BorderLayout());
        YearMonth currentMonth = YearMonth.now();
        DefaultListModel<String> monthListModel = new DefaultListModel<>();
        for (Event e : events) {
            LocalDate eventDate = e.getEventDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            YearMonth eventMonth = YearMonth.from(eventDate);
            if (eventMonth.equals(currentMonth)) {
                monthListModel.addElement(eventDate + " - " + e.getName() + " (" + e.getRoomId() + ")");
            }
        }
        JList<String> monthList = new JList<>(monthListModel);
        monthPanel.add(new JScrollPane(monthList), BorderLayout.CENTER);
        tabbedPane.addTab("Month", monthPanel);

        calendarDialog.add(tabbedPane);
        calendarDialog.setVisible(true);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error loading calendar: " + e.getMessage(),
                                      "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }


    }//GEN-LAST:event_btnFilterEventsbyCalenderActionPerformed

    private void btnViewExpensesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewExpensesActionPerformed
       
    int selectedBudgetId = 1; // get from selection in your budget table/list
    BudgetExpenseDialog dialog = new BudgetExpenseDialog(this, selectedBudgetId);
    dialog.setVisible(true);


    }//GEN-LAST:event_btnViewExpensesActionPerformed

    private void btnSendNotificationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendNotificationActionPerformed

        try {
            // 1️⃣ Ensure user is logged in
            if (user == null) {
                JOptionPane.showMessageDialog(this, "User not logged in.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2️⃣ Ensure clubId is valid
            if (clubId <= 0) {
                JOptionPane.showMessageDialog(this, "Club ID not set.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 3️⃣ Get message
            String message = txtAnnouncements.getText().trim();
            if (message.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a message.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 4️⃣ Fetch all active members of this club
            List<User> members = UserDAO.getUsersByClubId(clubId);

            if (members.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No active members found to notify.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // 5️⃣ Send notification to each member
            int successCount = 0;
            for (User member : members) {
                boolean sent = NotificationService.notifyUser(
                    member.getUserId(),
                    member.getEmail(),
                    member.getName(),
                    clubId,
                    getClubName(clubId), // implement this to get club name by ID
                    message
                );
                if (sent) successCount++;
            }

            // 6️⃣ Show result
            JOptionPane.showMessageDialog(this,
                String.format("Notification sent to %d/%d members!", successCount, members.size()),
                "Success", JOptionPane.INFORMATION_MESSAGE);

            // 7️⃣ Clear input
            txtAnnouncements.setText("");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error sending notifications: " + e.getMessage(),
                "Exception", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSendNotificationActionPerformed

    private void btnrefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnrefreshActionPerformed
        // TODO add your handling code here:
         populateEventTable();
    }//GEN-LAST:event_btnrefreshActionPerformed


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
            java.util.logging.Logger.getLogger(LeaderDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LeaderDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LeaderDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LeaderDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LeaderDashboard().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnApproveMember;
    private javax.swing.JButton btnCreateEvent;
    private javax.swing.JButton btnEditClubDetails;
    private javax.swing.JButton btnFilterEventsbyCalender;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnMarkAttendance;
    private javax.swing.JButton btnRejectMember;
    private javax.swing.JButton btnRequestBudget;
    private javax.swing.JButton btnSendNotification;
    private javax.swing.JButton btnViewExpenses;
    private javax.swing.JButton btnrefresh;
    private javax.swing.JComboBox<String> cmbEvents;
    private javax.swing.JComboBox<String> comboRoom;
    private com.toedter.calendar.JDateChooser dateChooserEventDate;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JCheckBox requestBudgetCheckbox;
    private javax.swing.JTable tblBudgetRequests;
    private javax.swing.JTable tblEvents;
    private javax.swing.JTable tblMembers;
    private javax.swing.JTextArea txtAnnouncements;
    private javax.swing.JTextField txtDescription;
    private javax.swing.JTextField txtName;
    // End of variables declaration//GEN-END:variables
}
