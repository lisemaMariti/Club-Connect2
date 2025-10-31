/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package clubconnect.ui;

import clubconnect.models.User;
import clubconnect.models.Club;
import clubconnect.dao.ClubDAO;
import clubconnect.dao.EventDAO;
import clubconnect.dao.MembershipDAO;
import clubconnect.dao.NotificationDAO;
import clubconnect.dao.RoomDAO;
import clubconnect.models.Event;
import clubconnect.models.Membership;
import clubconnect.models.Notification;
import clubconnect.models.Room;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class LeaderDashboard extends javax.swing.JFrame {
     private int clubId;
    private User user;
    
  




    
    public LeaderDashboard(int ClubId) {
    this.clubId = ClubId;
    initComponents();   
    setLocationRelativeTo(null);

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

    for (Event e : events) {
        model.addRow(new Object[]{      
            e.getName(),           
            e.getDescription(),   
            e.getEventDate(),      
            e.getRoomName()        
        });
    }
}


    // ✅ Constructor used when a leader logs in
    public LeaderDashboard(User user) {
        this.user = user;
        
        this.clubId = ClubDAO.getClubIdByLeader(user.getUserId());
        initComponents();
        setTitle("Leader Dashboard - " + user.getName());
        setLocationRelativeTo(null);
        loadPendingMemberships();
        populateComboBox();
        populateEventTable();
        
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
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblBudgetRequests = new javax.swing.JTable();
        btnRequestBudget = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtAnnouncements = new javax.swing.JTextArea();
        btnSendNotification = new javax.swing.JButton();
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
                .addContainerGap(186, Short.MAX_VALUE))
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
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 414, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 27, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addComponent(btnApproveMember)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnRejectMember)
                .addGap(75, 75, 75))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(130, 130, 130)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        comboRoom.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select event room" }));
        comboRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboRoomActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnCreateEvent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(txtName, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txtDescription, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE))
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
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(65, 65, 65)
                        .addComponent(btnMarkAttendance, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
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
                        .addComponent(comboRoom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(23, 23, 23)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCreateEvent)
                    .addComponent(btnMarkAttendance))
                .addGap(109, 109, 109))
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

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 414, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnRequestBudget)
                .addGap(154, 154, 154))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(169, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnRequestBudget)
                .addGap(11, 11, 11))
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
                .addContainerGap(201, Short.MAX_VALUE))
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
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 427, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(187, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnLogout)
                .addContainerGap())
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
        SwingUtilities.invokeLater(() -> new ClubForm().setVisible(true));

    }//GEN-LAST:event_btnEditClubDetailsActionPerformed

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
  int confirm = JOptionPane.showConfirmDialog(
        this, 
        "Are you sure you want to log out?", 
        "Confirm Logout", 
        JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) {
        this.dispose(); // close current dashboard instance
        new clubconnect.ui.LoginForm().setVisible(true); // open login screen
    };
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
        // Gather data from form fields
        String name = txtName.getText().trim();
        String description = txtDescription.getText().trim();
        java.util.Date eventDate = dateChooserEventDate.getDate();

        // Basic validation
        if (name.isEmpty() || eventDate == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.", 
                                          "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate room selection
        String selectedRoom = (String) comboRoom.getSelectedItem();
        if (selectedRoom == null || selectedRoom.startsWith("0 -")) {
            JOptionPane.showMessageDialog(this, "Please select a room.", 
                                          "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Parse room ID
        String[] parts = selectedRoom.split(" - ", 2);
        int roomId = Integer.parseInt(parts[0].trim());

        // Use clubId from the dashboard instance
        int clubId = this.clubId;

        // Create Event object
        Event event = new Event(0, clubId, name, description, eventDate, roomId);

        // Save to database
        boolean success = EventDAO.createEvent(event, clubId);

        if (success) {
            JOptionPane.showMessageDialog(this, "Event created successfully!");
            // Clear form fields
            txtName.setText("");
            txtDescription.setText("");
            dateChooserEventDate.setDate(null);
            comboRoom.setSelectedIndex(0);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to create event.", 
                                          "Error", JOptionPane.ERROR_MESSAGE);
        }

    } catch (NumberFormatException nfe) {
        JOptionPane.showMessageDialog(this, "Invalid room selection.", 
                                      "Validation Error", JOptionPane.WARNING_MESSAGE);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), 
                                      "Exception", JOptionPane.ERROR_MESSAGE);
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

    private void btnSendNotificationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendNotificationActionPerformed
        try {
        // Make sure a user is logged in
        if (user == null) {
            JOptionPane.showMessageDialog(this, "User not logged in.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Make sure clubId is set (leader's club)
        if (clubId <= 0) {
            JOptionPane.showMessageDialog(this, "Club ID not set.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get the message text
        String message = txtAnnouncements.getText().trim();
        if (message.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a message.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Create notification with userId and clubId
        Notification notification = new Notification(user.getUserId(), message, clubId);

        // Insert into DB
        boolean success = NotificationDAO.createNotification(notification);

        if (success) {
            JOptionPane.showMessageDialog(this, "Notification sent!");
            txtAnnouncements.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to send notification.", "Error", JOptionPane.ERROR_MESSAGE);
        }

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_btnSendNotificationActionPerformed

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
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnMarkAttendance;
    private javax.swing.JButton btnRejectMember;
    private javax.swing.JButton btnRequestBudget;
    private javax.swing.JButton btnSendNotification;
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
    private javax.swing.JTable tblBudgetRequests;
    private javax.swing.JTable tblEvents;
    private javax.swing.JTable tblMembers;
    private javax.swing.JTextArea txtAnnouncements;
    private javax.swing.JTextField txtDescription;
    private javax.swing.JTextField txtName;
    // End of variables declaration//GEN-END:variables
}
