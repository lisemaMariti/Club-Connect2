/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package clubconnect.ui;

import clubconnect.dao.AttendanceDAO;
import clubconnect.dao.BudgetDAO;
import clubconnect.dao.ClubDAO;
import static clubconnect.dao.ClubDAO.approveClub;
import static clubconnect.dao.ClubDAO.deactivateClub;
import clubconnect.dao.EventDAO;
import clubconnect.dao.ExpenseDAO;
import clubconnect.dao.MembershipDAO;
import clubconnect.dao.NotificationDAO;
import clubconnect.dao.UserDAO;
import static clubconnect.dao.UserDAO.assignUserRole;
import static clubconnect.dao.UserDAO.deactivateUser;
import static clubconnect.dao.UserDAO.deleteUser;
import clubconnect.models.BudgetRequest;
import clubconnect.models.Club;
import clubconnect.models.Event;
import clubconnect.models.Expense;
import clubconnect.models.Notification;
import clubconnect.models.User;
import clubconnect.utils.CSVExporter;
import clubconnect.utils.PDFExporter;
import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;



/**
 *
 * @author User
 */
public class AdminDashboard extends javax.swing.JFrame {
 private User user;
 private Club currentClub;
  private User loggedInUser;
     private List<Club> allClubs = new ArrayList<>();
     private AdminDiscussionPanel adminDiscussionPanel;

 
   public AdminDashboard(User user) {
          this.loggedInUser = user;
        initComponents();
        setTitle("Admin Dashboard - " + user.getName());
        setLocationRelativeTo(null);
         this.loggedInUser = user;
        loadClubs();
        loadUsers();
        loadBudgetRequests();
        loadBudgetComboForExpenses();
           loadAttendanceChart();
    loadFinancialChart();
    
adminDiscussionPanel = new AdminDiscussionPanel(null, user);
jTabbedPane3.addTab("Discussion Board 🗨️", adminDiscussionPanel);

// Force the tabbed pane to refresh
jTabbedPane3.revalidate();
jTabbedPane3.repaint();

// Debug: Check if tab is added
System.out.println("Number of tabs: " + jTabbedPane3.getTabCount());


btnExportMembers.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnExportMembersActionPerformed(evt);
    }
});







 
        btnExportAttendance.addActionListener(e -> {
    JFileChooser chooser = new JFileChooser();
    int option = chooser.showSaveDialog(this);
    if(option == JFileChooser.APPROVE_OPTION){
        File file = chooser.getSelectedFile();
        // Convert DAO data to List<Map<String, Object>> for PDFExporter
        Map<String, Integer> data = AttendanceDAO.getAttendanceCounts();
        List<Map<String, Object>> pdfData = new ArrayList<>();
        for(Map.Entry<String, Integer> entry : data.entrySet()){
            Map<String,Object> row = new LinkedHashMap<>();
            row.put("Event Name", entry.getKey());
            row.put("Attendees", entry.getValue());
            pdfData.add(row);
        }
        PDFExporter.exportAttendanceReport(file, pdfData);
    }
});

        
        
        
        txtSearchClub.getDocument().addDocumentListener(new DocumentListener() {
    @Override
    public void insertUpdate(DocumentEvent e) { searchAndUpdate(); }
    @Override
    public void removeUpdate(DocumentEvent e) { searchAndUpdate(); }
    @Override
    public void changedUpdate(DocumentEvent e) { searchAndUpdate(); }
});
        

        
        
         txtSearch.getDocument().addDocumentListener(new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            handleSearch();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            handleSearch();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            handleSearch();
        }

        private void handleSearch() {
            String keyword = txtSearch.getText().trim();
            // Call your search method, e.g.
            loadSearchedUsers(keyword);
        }
    });
        
    }
private List<Club> filterClubs(String keyword) {
    if (keyword == null || keyword.isEmpty()) return new ArrayList<>(allClubs);

    final String kw = keyword.toLowerCase(); // make a final copy
    return allClubs.stream()
        .filter(c -> c.getName().toLowerCase().contains(kw) ||
                     c.getDescription().toLowerCase().contains(kw))
        .collect(Collectors.toList());
}

private void searchAndUpdate() {
    String keyword = txtSearchClub.getText().trim();
    List<Club> filtered = filterClubs(keyword);
    updateClubTable(filtered);
}


private void loadClubs() {
    allClubs = ClubDAO.getAllClubs(); // fetch clubs with memberCount and upcomingEvents
    System.out.println("Loaded clubs: " + allClubs.size());
for (Club c : allClubs) {
    System.out.println(c.getName() + " | Members: " + c.getMemberCount() + " | Events: " + c.getUpcomingEvents());
}


    String[] columnNames = {"Club ID", "Name", "Description", "Status", "Leader ID", "Members", "Upcoming Events"};

    DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 1 || column == 2; // only Name and Description editable
        }
    };

    for (Club c : allClubs) {
        model.addRow(new Object[]{
            c.getClubId(),
            c.getName(),
            c.getDescription(),
            c.getStatus(),
            c.getLeaderId(),
            c.getMemberCount(),
            c.getUpcomingEvents()
        });
    }

    clublist.setModel(model);

    // Enable sorting by clicking column headers
    clublist.setAutoCreateRowSorter(true);
}


   
private void loadUsers() {
    List<User> users = UserDAO.loadUsers(); 

    // Filter for non-admin/non-leader users
    if (!loggedInUser.isAdminOrLeader()) {
        users = users.stream()
                     .filter(u -> u.getUserId() == loggedInUser.getUserId())
                     .toList();
    }

    String[] columnNames = {"User ID", "Name", "Email", "Role", "Active", "Clubs"};
    DefaultTableModel model = new DefaultTableModel(columnNames, 0);

    for (User u : users) {
        String clubNames = u.getClubs().stream()
                            .map(c -> c.getName() + " [" + c.getStatus() + "]")
                            .reduce((a, b) -> a + ", " + b)
                            .orElse("");

        Object[] row = {
            u.getUserId(),
            u.getName(),
            u.getEmail(),
            u.getRole(),
            u.isActive() ? "Yes" : "No",
            clubNames
        };
        model.addRow(row);
    }

    UsersList.setModel(model);
    UsersList.repaint();
}




private void loadSearchedUsers(String keyword) {
    List<User> users = UserDAO.searchUsers(keyword); 
    String[] columnNames = {"User ID", "Name", "Email", "Role", "Active", "Clubs"};
    DefaultTableModel model = new DefaultTableModel(columnNames, 0);

    for (User u : users) {
        String clubNames = u.getClubs().stream()
                            .map(c -> c.getName() + " [" + c.getStatus() + "]")
                            .reduce((a, b) -> a + ", " + b)
                            .orElse("N/A");

        Object[] row = {
            u.getUserId(),
            u.getName(),
            u.getEmail(),
            u.getRole(),
            u.isActive() ? "Yes" : "No",
            clubNames
        };
        model.addRow(row);
    }

    UsersList.setModel(model);
    UsersList.repaint();
}

// ----------------------
// Load all budget requests
// ----------------------
private void loadBudgetRequests() {
    DefaultTableModel model = new DefaultTableModel(
        new Object[]{"Budget ID", "Club ID", "Amount", "Status", "Purpose"}, 0
    );

    List<BudgetRequest> list = BudgetDAO.getAllBudgetRequests();
    for (BudgetRequest b : list) {
        model.addRow(new Object[]{
            b.getBudgetId(),
            b.getClubId(),
            "R " + String.format("%.2f", b.getAmount()),
            b.getStatus(),
            b.getPurpose()
        });
    }

    tblBudgetRequests.setModel(model);
}
private void sendBudgetNotification(int budgetId, String status) {
    try {
        // Get club info for this budget
        BudgetRequest request = BudgetDAO.getBudgetById(budgetId);
        if (request == null) return;

        Club club = ClubDAO.getClubById(request.getClubId());
        if (club == null) return;

        String message = String.format(
            "Your budget request for '%s' has been %s by the Admin.",
            request.getPurpose(),
            status.toLowerCase()
        );

        Notification notification = new Notification(club.getLeaderId(), message, club.getClubId());
        NotificationDAO.createNotification(notification);

    } catch (Exception e) {
        System.err.println("Failed to send budget notification: " + e.getMessage());
    }
}
private void updateClubTable(List<Club> clubs) {
    String[] columnNames = {"Club ID", "Name", "Description", "Status", "Leader ID", "Members", "Upcoming Events"};

    DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 1 || column == 2; // only Name & Description editable
        }
    };

    for (Club c : clubs) {
        model.addRow(new Object[]{
            c.getClubId(),
            c.getName(),
            c.getDescription(),
            c.getStatus(),
            c.getLeaderId(),
            c.getMemberCount(),
            c.getUpcomingEvents()
        });
    }

    clublist.setModel(model);

    // Make table sortable
    clublist.setAutoCreateRowSorter(true);
}

// Load expenses into your  tblExpenses
private void loadExpenses(int budgetId) {
    DefaultTableModel model = (DefaultTableModel) tblExpenses.getModel();
    model.setRowCount(0); // clear existing rows

    List<Expense> expenses = ExpenseDAO.getExpensesByBudget(budgetId);

    if (expenses.isEmpty()) {
        // Add a single placeholder row
        model.addRow(new Object[]{"N/A", "No expenses logged", "N/A", "N/A"});
    } else {
        for (Expense exp : expenses) {
            model.addRow(new Object[]{
                exp.getExpenseId(),
                exp.getDescription(),
                exp.getAmount(),
                exp.getExpenseDate()
            });
        }
    }
}

private void loadBudgetComboForExpenses() {
    cmbBudgets.removeAllItems();
    List<BudgetRequest> budgets = BudgetDAO.getAllBudgetRequests();

    // Populate combo box
    for (BudgetRequest b : budgets) {
        cmbBudgets.addItem(b.getBudgetId() + " - " + b.getPurpose());
    }

    // Action listener to load expenses whenever selection changes
    cmbBudgets.addActionListener(e -> {
        String selected = (String) cmbBudgets.getSelectedItem();
        if (selected != null && !selected.startsWith("0 -")) {
            int budgetId = Integer.parseInt(selected.split(" - ")[0].trim());
            loadExpenses(budgetId); // your method that fills the JTable
        } else {
            // Optionally clear table if placeholder selected
            clearExpensesTable();
        }
    });
}

// Example method to clear the table
private void clearExpensesTable() {
    DefaultTableModel model = (DefaultTableModel) tblExpenses.getModel();
    model.setRowCount(0);
}

//private void populateAdminEvents() {
//    if (currentClub == null) return;
//
//    DefaultTableModel model = new DefaultTableModel();
//    model.setColumnIdentifiers(new Object[]{"Event ID", "Name", "Description", "Date", "Room"});
//
//    List<Event> events = EventDAO.getAllEvents(currentClub.getClubId());
//
//    for (Event e : events) {
//        model.addRow(new Object[]{
//            e.getEventId(),
//            e.getName(),
//            e.getDescription(),
//            e.getEventDate(),
//            e.getRoomName()
//        });
//    }
//
//    tblEvents.setModel(model);
//
//    // hide event_id column
//    tblEvents.getColumnModel().getColumn(0).setMinWidth(0);
//    tblEvents.getColumnModel().getColumn(0).setMaxWidth(0);
//    tblEvents.getColumnModel().getColumn(0).setWidth(0);
//
//
//}


private void loadAttendanceChart() {
    Map<String, Integer> attendanceData = AttendanceDAO.getAttendanceCounts();
    System.out.println("Attendance Map: " + attendanceData); // debug

    panelAttendanceChart.removeAll();
    BarChartPanel chart = new BarChartPanel(attendanceData);
    panelAttendanceChart.setLayout(new BorderLayout());
    panelAttendanceChart.add(chart, BorderLayout.CENTER);
    panelAttendanceChart.revalidate();
    panelAttendanceChart.repaint();
}


// -------------------------
// Prepare data for pie chart
// -------------------------
private Map<String, Double> preparePieData(List<Map<String, Object>> financialData) {
    Map<String, Double> pieData = new LinkedHashMap<>();
    for (Map<String, Object> row : financialData) {
        String clubName = (String) row.get("club_name");
        double approved = ((Number) row.get("approved_total")).doubleValue();
        double pending = ((Number) row.get("pending_total")).doubleValue();
        double rejected = ((Number) row.get("rejected_total")).doubleValue();

        pieData.put(clubName + " Approved", approved);
        pieData.put(clubName + " Pending", pending);
        pieData.put(clubName + " Rejected", rejected);
    }
    return pieData;
}

// -------------------------
// Load Financial Pie Chart
// -------------------------
private void loadFinancialChart() {
    List<Map<String, Object>> financialData = BudgetDAO.getFinancialSummary();
    Map<String, Double> chartData = preparePieData(financialData);

    budgetPanel.removeAll();                 
    PieChartPanel chart = new PieChartPanel(chartData);
    budgetPanel.setLayout(new BorderLayout());
    budgetPanel.add(chart, BorderLayout.CENTER);
    budgetPanel.revalidate();
    budgetPanel.repaint();
}










 /**
     * 
     * 
     * Creates new form AdminDashboard
     */
    public AdminDashboard() {
        
        initComponents();
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
        clublist = new javax.swing.JTable();
        btnApproveClub = new javax.swing.JButton();
        btnUpdateClub = new javax.swing.JButton();
        btnDeactivateClub = new javax.swing.JButton();
        txtSearchClub = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        btnExportMembers = new javax.swing.JButton();
        btnClubApproval = new javax.swing.JButton();
        btnRejectClub = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblBudgetRequests = new javax.swing.JTable();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        btnApproveBudgetRequest = new javax.swing.JButton();
        btnRejectBudgetRequest = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        UsersList = new javax.swing.JTable();
        btnDeactivate = new javax.swing.JButton();
        btnRemoveUser = new javax.swing.JButton();
        cmbRoles = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        btnAssignRole = new javax.swing.JButton();
        btnChangeRole = new javax.swing.JButton();
        btnDeactivateUser = new javax.swing.JButton();
        btnDeleteduser = new javax.swing.JButton();
        comboboxRoles = new javax.swing.JComboBox<>();
        panelAttendanceChart = new javax.swing.JPanel();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        btnExportAttendance = new javax.swing.JButton();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblExpenses = new javax.swing.JTable();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        jTabbedPane4 = new javax.swing.JTabbedPane();
        budgetPanel = new java.awt.Panel();
        jButton10 = new javax.swing.JButton();
        cmbBudgets = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        clublist.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ClubID", "Name", "Description", "Status", "Leader"
            }
        ));
        jScrollPane1.setViewportView(clublist);

        btnApproveClub.setText("Approve Club");
        btnApproveClub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApproveClubActionPerformed(evt);
            }
        });

        btnUpdateClub.setText("Edit Club");
        btnUpdateClub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateClubActionPerformed(evt);
            }
        });

        btnDeactivateClub.setText("Deactivate Club");
        btnDeactivateClub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeactivateClubActionPerformed(evt);
            }
        });

        txtSearchClub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchClubActionPerformed(evt);
            }
        });

        jLabel2.setText("Search Club");

        btnExportMembers.setText("ExportMembers");
        btnExportMembers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportMembersActionPerformed(evt);
            }
        });

        btnClubApproval.setText("Aprrove Club");
        btnClubApproval.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClubApprovalActionPerformed(evt);
            }
        });

        btnRejectClub.setText("Reject club");
        btnRejectClub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRejectClubActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnApproveClub)
                .addGap(109, 109, 109)
                .addComponent(btnUpdateClub)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnDeactivateClub)
                .addGap(54, 54, 54))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(btnExportMembers, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 529, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(28, 28, 28)
                .addComponent(txtSearchClub, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(88, 88, 88))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1)
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(btnClubApproval)
                .addGap(28, 28, 28)
                .addComponent(btnRejectClub)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearchClub, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(btnExportMembers))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnClubApproval)
                    .addComponent(btnRejectClub))
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnApproveClub)
                    .addComponent(btnUpdateClub)
                    .addComponent(btnDeactivateClub))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Clubs", jPanel1);

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

        jButton4.setText("Approve");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("Reject");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        btnApproveBudgetRequest.setText("Approve Request");
        btnApproveBudgetRequest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApproveBudgetRequestActionPerformed(evt);
            }
        });

        btnRejectBudgetRequest.setText("Reject Request");
        btnRejectBudgetRequest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRejectBudgetRequestActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(115, 115, 115)
                .addComponent(jButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton5)
                .addGap(154, 154, 154))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(67, 67, 67)
                        .addComponent(btnApproveBudgetRequest)
                        .addGap(47, 47, 47)
                        .addComponent(btnRejectBudgetRequest))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1049, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnApproveBudgetRequest)
                    .addComponent(btnRejectBudgetRequest))
                .addGap(40, 40, 40)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton4)
                    .addComponent(jButton5))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Budgets", jPanel2);

        UsersList.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(UsersList);

        btnDeactivate.setText("Activate/Activate A/C");
        btnDeactivate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeactivateActionPerformed(evt);
            }
        });

        btnRemoveUser.setText("Remove Member");
        btnRemoveUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveUserActionPerformed(evt);
            }
        });

        cmbRoles.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Admin", "Member", "Leader" }));
        cmbRoles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbRolesActionPerformed(evt);
            }
        });

        jLabel1.setText("Search Users");

        txtSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchActionPerformed(evt);
            }
        });

        btnAssignRole.setText("Promote/demote Member");
        btnAssignRole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAssignRoleActionPerformed(evt);
            }
        });

        btnChangeRole.setText("Promote/Demote User");
        btnChangeRole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChangeRoleActionPerformed(evt);
            }
        });

        btnDeactivateUser.setText("Deactivate User Account");
        btnDeactivateUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeactivateUserActionPerformed(evt);
            }
        });

        btnDeleteduser.setText("Remove User");
        btnDeleteduser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteduserActionPerformed(evt);
            }
        });

        comboboxRoles.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Member", "Leader", "Admin" }));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(cmbRoles, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(btnAssignRole)
                                .addGap(18, 18, 18)
                                .addComponent(btnDeactivate)
                                .addGap(18, 18, 18)
                                .addComponent(btnRemoveUser)
                                .addGap(0, 577, Short.MAX_VALUE))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane3)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addComponent(btnChangeRole, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(btnDeactivateUser, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                                    .addComponent(btnDeleteduser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(comboboxRoles, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(26, 26, 26)
                        .addComponent(btnChangeRole)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnDeactivateUser)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnDeleteduser)
                        .addGap(18, 18, 18)
                        .addComponent(comboboxRoles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(85, 85, 85)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDeactivate)
                    .addComponent(btnRemoveUser)
                    .addComponent(btnAssignRole))
                .addGap(18, 18, 18)
                .addComponent(cmbRoles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Users", jPanel3);

        jButton8.setText("Generate Attendance PDF");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setText("Generate Financial PDF");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        btnExportAttendance.setText("Export event Attendance");

        javax.swing.GroupLayout panelAttendanceChartLayout = new javax.swing.GroupLayout(panelAttendanceChart);
        panelAttendanceChart.setLayout(panelAttendanceChartLayout);
        panelAttendanceChartLayout.setHorizontalGroup(
            panelAttendanceChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAttendanceChartLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jButton8)
                .addGap(151, 151, 151)
                .addComponent(btnExportAttendance)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 341, Short.MAX_VALUE)
                .addComponent(jButton9)
                .addGap(74, 74, 74))
        );
        panelAttendanceChartLayout.setVerticalGroup(
            panelAttendanceChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAttendanceChartLayout.createSequentialGroup()
                .addGap(261, 261, 261)
                .addGroup(panelAttendanceChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton8)
                    .addComponent(jButton9)
                    .addComponent(btnExportAttendance))
                .addContainerGap(56, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Attendance Reports", panelAttendanceChart);

        tblExpenses.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "Descrption", "Amount", "Date"
            }
        ));
        jScrollPane4.setViewportView(tblExpenses);

        jTabbedPane2.addTab("Manage expenses", jScrollPane4);

        jTabbedPane1.addTab("Expenses", jTabbedPane2);
        jTabbedPane1.addTab("Notifications", jTabbedPane3);

        javax.swing.GroupLayout budgetPanelLayout = new javax.swing.GroupLayout(budgetPanel);
        budgetPanel.setLayout(budgetPanelLayout);
        budgetPanelLayout.setHorizontalGroup(
            budgetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1061, Short.MAX_VALUE)
        );
        budgetPanelLayout.setVerticalGroup(
            budgetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 305, Short.MAX_VALUE)
        );

        jTabbedPane4.addTab("Budget Magement", budgetPanel);

        jTabbedPane1.addTab("Financial Report", jTabbedPane4);

        jButton10.setText("Logout");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        cmbBudgets.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(cmbBudgets, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton10))
                    .addComponent(jTabbedPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                .addComponent(cmbBudgets, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(241, 241, 241)
                .addComponent(jButton10)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnApproveClubActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApproveClubActionPerformed
     int selectedRow = clublist.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a club first.", "Warning", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // since club_id is in column 0 of your table
    int clubId = (int) clublist.getValueAt(selectedRow, 0);

    boolean success = approveClub(clubId);

    if (success) {
        JOptionPane.showMessageDialog(this, 
            "Club approved successfully!", 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
        loadClubs(); // refresh the UI
    } else {
        JOptionPane.showMessageDialog(this, 
            "Failed to approve club.", 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_btnApproveClubActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
       int selectedRow = tblBudgetRequests.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a budget request to approve.");
        return;
    }

    int budgetId = (int) tblBudgetRequests.getValueAt(selectedRow, 0);

    int confirm = JOptionPane.showConfirmDialog(
        this,
        "Approve this budget request?",
        "Confirm Approval",
        JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) {
        boolean updated = BudgetDAO.updateBudgetStatus(budgetId, "Approved");

        if (updated) {
            sendBudgetNotification(budgetId, "Approved");
            JOptionPane.showMessageDialog(this, "Budget request approved successfully!");
            loadBudgetRequests();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update status.");
        }
    }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void btnRemoveUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveUserActionPerformed
           int selectedRow = UsersList.getSelectedRow();
    
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(null, "Please select a user to delete.");
        return;
    }

    // Get user ID from table (assuming first column is user ID)
    int userId = (int) UsersList.getValueAt(selectedRow, 0);

    // Ask for confirmation
    int confirm = JOptionPane.showConfirmDialog(
        null, 
        "Are you sure you want to delete this user?", 
        "Confirm Delete", 
        JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) {
        boolean deleted = UserDAO.deleteUser(userId);
        if (deleted) {
            JOptionPane.showMessageDialog(null, "User deleted successfully!");
            // Refresh table
            loadUsers();
        } else {
            JOptionPane.showMessageDialog(null, "Failed to delete user.");
        }
    }
    }//GEN-LAST:event_btnRemoveUserActionPerformed

    private void btnDeactivateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeactivateActionPerformed
        int selectedRow = UsersList.getSelectedRow();
    
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a user to deactivate.");
        return;
    }

    // Get user ID from table (assuming first column is user ID)
    int userId = (int) UsersList.getValueAt(selectedRow, 0);

    // Ask for confirmation
    int confirm = JOptionPane.showConfirmDialog(
        this, 
        "Are you sure you want to deactivate this user?", 
        "Confirm Deactivate", 
        JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) {
        boolean success = UserDAO.deactivateUser(userId); // You need to implement this in DAO
        if (success) {
            JOptionPane.showMessageDialog(this, "User deactivated successfully!");
            loadUsers(); // Refresh table
        } else {
            JOptionPane.showMessageDialog(this, "Failed to deactivate user.");
        }
    }
    }//GEN-LAST:event_btnDeactivateActionPerformed

    private void btnAssignRoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAssignRoleActionPerformed
        int selectedRow = UsersList.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a user to assign a role.");
        return;
    }

    int userId = (int) UsersList.getValueAt(selectedRow, 0); 
    String selectedRole = cmbRoles.getSelectedItem().toString();

    // Confirm action
    int confirm = JOptionPane.showConfirmDialog(
        this,
        "Are you sure you want to assign the role '" + selectedRole + "' to this user?",
        "Confirm Role Assignment",
        JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) {
        boolean success = UserDAO.assignUserRole(userId, selectedRole);
        if (success) {
            JOptionPane.showMessageDialog(this, "Role assigned successfully!");
            loadUsers(); // Refresh the table to show updated role
        } else {
            JOptionPane.showMessageDialog(this, "Failed to assign role.");
        }
    }

    }//GEN-LAST:event_btnAssignRoleActionPerformed

    private void cmbRolesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbRolesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbRolesActionPerformed

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
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

    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
         try {
            List<Map<String, Object>> attendanceData = MembershipDAO.getAttendanceSummary();
            if (attendanceData.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No attendance records found.");
                return;
            }

            File outFile = new File("attendance_report.pdf");
            PDFExporter.exportAttendanceReport(outFile, attendanceData);

            JOptionPane.showMessageDialog(this,
                    "Attendance report generated successfully!\n" + outFile.getAbsolutePath());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating report: " + e.getMessage());
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
                try {
            List<Map<String, Object>> financialData = BudgetDAO.getFinancialSummary();
            if (financialData.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No financial data found.");
                return;
            }

            File outFile = new File("financial_report.pdf");
            PDFExporter.exportFinancialReport(outFile, financialData);

            JOptionPane.showMessageDialog(this,
                    "Financial report generated successfully!\n" + outFile.getAbsolutePath());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating report: " + e.getMessage());
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
          int selectedRow = tblBudgetRequests.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a budget request to reject.");
        return;
    }

    int budgetId = (int) tblBudgetRequests.getValueAt(selectedRow, 0);

    int confirm = JOptionPane.showConfirmDialog(
        this,
        "Reject this budget request?",
        "Confirm Rejection",
        JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) {
        boolean updated = BudgetDAO.updateBudgetStatus(budgetId, "Rejected");

        if (updated) {
            sendBudgetNotification(budgetId, "Rejected");
            JOptionPane.showMessageDialog(this, "Budget request rejected successfully!");
            loadBudgetRequests();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update status.");
        }
    }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void btnDeactivateClubActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeactivateClubActionPerformed
        int selectedRow = clublist.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a club to deactivate.", "No Selection", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Assuming the first column in your JTable contains the club ID
    int clubId = (int) clublist.getValueAt(selectedRow, 0);
    String clubName = clublist.getValueAt(selectedRow, 1).toString();

    int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to deactivate (reject) the club \"" + clubName + "\"?",
            "Confirm Deactivation",
            JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
        boolean success = ClubDAO.deactivateClub(clubId);
        if (success) {
            JOptionPane.showMessageDialog(this, "Club \"" + clubName + "\" has been deactivated successfully.");
            // Optionally refresh your table data here
            loadClubs();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to deactivate the club. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    }//GEN-LAST:event_btnDeactivateClubActionPerformed

    private void btnUpdateClubActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateClubActionPerformed
        // TODO add your handling code here:
          int selectedRow = clublist.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a club to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Read the edited data from the table
    int clubId = Integer.parseInt(clublist.getValueAt(selectedRow, 0).toString());
    String newName = clublist.getValueAt(selectedRow, 1).toString();
    String newDescription = clublist.getValueAt(selectedRow, 2).toString();

    if (newName.trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Club name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Create updated Club object
    Club updatedClub = new Club(clubId, newName, newDescription, "", 0);

    // Save changes in DB
    boolean success = ClubDAO.updateClubProfile(updatedClub);
    if (success) {
        JOptionPane.showMessageDialog(this, "Club updated successfully!");
        loadClubs(); // refresh table
    } else {
        JOptionPane.showMessageDialog(this, "Failed to update club.", "Error", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_btnUpdateClubActionPerformed

    private void txtSearchClubActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchClubActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchClubActionPerformed

    private void btnExportMembersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportMembersActionPerformed
     try {
        System.out.println("Export button clicked");

        String folderPath = "exports";
        File folder = new File(folderPath);
        if (!folder.exists()) folder.mkdirs();

        String filePath = new File(folder, "all_members.csv").getAbsolutePath();
        System.out.println("Target file path: " + filePath);

        List<String[]> memberData = MembershipDAO.getAllMembersForCSV();
        System.out.println("Rows to export:");
        for (String[] row : memberData) {
            System.out.println(java.util.Arrays.toString(row));
        }

        System.out.println("Members found: " + memberData.size());

        if (memberData.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No members found in any club.");
            return;
        }

        String[] headers = {"Club Name", "User ID", "Name", "Email", "Status", "Joined At"};
        CSVExporter.export(filePath, memberData, headers);

        JOptionPane.showMessageDialog(this, "CSV exported successfully to:\n" + filePath);

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error exporting CSV: " + e.getMessage());
    
    
    
    
    
    }    }//GEN-LAST:event_btnExportMembersActionPerformed

    private void btnApproveBudgetRequestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApproveBudgetRequestActionPerformed
           // Step 1️⃣: Check if a row is selected
    int selectedRow = tblBudgetRequests.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a budget request to approve.",
                                      "No Selection", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Step 2️⃣: Get budget ID from the table
    int budgetId = (int) tblBudgetRequests.getValueAt(selectedRow, 0); // assuming ID is column 0

    // Step 3️⃣: Confirm approval
    int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to approve this budget request?",
            "Confirm Approval",
            JOptionPane.YES_NO_OPTION);

    if (confirm != JOptionPane.YES_OPTION) {
        return;
    }

    // Step 4️⃣: Update database using DAO
    boolean success = BudgetDAO.updateBudgetStatus(budgetId, "Approved");

    // Step 5️⃣: Show result to user
    if (success) {
        JOptionPane.showMessageDialog(this, "Budget request approved successfully!",
                                      "Success", JOptionPane.INFORMATION_MESSAGE);
              loadAttendanceChart();
    loadFinancialChart();

        // Step 6️⃣: Refresh the table (if you have a method to reload)
        loadBudgetRequests();

    } else {
        JOptionPane.showMessageDialog(this, "Failed to approve the budget request. Please try again.",
                                      "Error", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_btnApproveBudgetRequestActionPerformed

    private void btnRejectBudgetRequestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRejectBudgetRequestActionPerformed
        int selectedRow = tblBudgetRequests.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a budget request to reject.",
                                      "No Selection", JOptionPane.WARNING_MESSAGE);
        return;
    }

    int budgetId = (int) tblBudgetRequests.getValueAt(selectedRow, 0);

    int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to reject this budget request?",
            "Confirm Rejection",
            JOptionPane.YES_NO_OPTION);

    if (confirm != JOptionPane.YES_OPTION) {
        return;
    }

    boolean success = BudgetDAO.updateBudgetStatus(budgetId, "Rejected");

    if (success) {
        JOptionPane.showMessageDialog(this, "Budget request rejected.",
                                      "Rejected", JOptionPane.INFORMATION_MESSAGE);
        loadBudgetRequests();
              loadAttendanceChart();
    loadFinancialChart();
    } else {
        JOptionPane.showMessageDialog(this, "Failed to reject budget request.",
                                      "Error", JOptionPane.ERROR_MESSAGE);
    }

    }//GEN-LAST:event_btnRejectBudgetRequestActionPerformed

    private void btnClubApprovalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClubApprovalActionPerformed
          // Get selected row index from the JTable
    int selectedRow = clublist.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a club to approve.");
        return;
    }

    // Assuming the club ID is in the first column (index 0)
    int clubId = (int) clublist.getValueAt(selectedRow, 0);

    // Call the approveClub method
    boolean success = approveClub(clubId);

    if (success) {
        JOptionPane.showMessageDialog(this, "Club approved successfully!");
        
        
        loadClubs(); 
    } else {
        JOptionPane.showMessageDialog(this, "Failed to approve club.", "Error", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_btnClubApprovalActionPerformed

    private void btnRejectClubActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRejectClubActionPerformed
             // Get selected row index from the JTable
    int selectedRow = clublist.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a club to reject.");
        return;
    }

    // Assuming the club ID is in the first column (index 0)
    int clubId = (int) clublist.getValueAt(selectedRow, 0);

    // Call the approveClub method
    boolean success = deactivateClub(clubId);

    if (success) {
        JOptionPane.showMessageDialog(this, "Club club has been rejected!");
        
        
        loadClubs(); 
    } else {
        JOptionPane.showMessageDialog(this, "Failed to reject club.", "Error", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_btnRejectClubActionPerformed

    private void btnDeleteduserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteduserActionPerformed
     
    int selectedRow = UsersList.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a user to delete.");
        return;
    }

    // Assuming the user ID is in the first column (index 0)
    int userId = (int) UsersList.getValueAt(selectedRow, 0);

    // Confirm before deleting
    int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this user?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
        boolean success = deleteUser(userId);

        if (success) {
            JOptionPane.showMessageDialog(this, "User deleted successfully!");

            //  refresh the table after deletion
               loadUsers(); 
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to delete user.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    }//GEN-LAST:event_btnDeleteduserActionPerformed

    private void btnDeactivateUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeactivateUserActionPerformed
          // Get selected row from the JTable
    int selectedRow = UsersList.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a user to deactivate.");
        return;
    }

    // Assuming the user ID is in the first column (index 0)
    int userId = (int) UsersList.getValueAt(selectedRow, 0);

    // Confirm before deactivation
    int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to deactivate this user?",
            "Confirm Deactivation",
            JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
        boolean success = deactivateUser(userId);

        if (success) {
            JOptionPane.showMessageDialog(this, "User deactivated successfully!");

            //  refresh the table to reflect changes
            loadUsers(); 
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to deactivate user.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    }//GEN-LAST:event_btnDeactivateUserActionPerformed

    private void btnChangeRoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChangeRoleActionPerformed
            // Get the selected row from the table
    int selectedRow = UsersList.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a user to change their role.");
        return;
    }

    // Get user ID from the first column (index 0)
    int userId = (int) UsersList.getValueAt(selectedRow, 0);

    // Get the selected role from the combo box
    String newRole = (String) comboboxRoles.getSelectedItem();

    if (newRole == null || newRole.trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please select a valid role.");
        return;
    }

    // Confirm the action
    int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to change this user's role to '" + newRole + "'?",
            "Confirm Role Change",
            JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
        boolean success = assignUserRole(userId, newRole);

        if (success) {
            JOptionPane.showMessageDialog(this, "User role updated successfully!");

            //  refresh the table to reflect changes
            loadUsers();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to update user role.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    }//GEN-LAST:event_btnChangeRoleActionPerformed

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
            java.util.logging.Logger.getLogger(AdminDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AdminDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AdminDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AdminDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AdminDashboard().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable UsersList;
    private javax.swing.JButton btnApproveBudgetRequest;
    private javax.swing.JButton btnApproveClub;
    private javax.swing.JButton btnAssignRole;
    private javax.swing.JButton btnChangeRole;
    private javax.swing.JButton btnClubApproval;
    private javax.swing.JButton btnDeactivate;
    private javax.swing.JButton btnDeactivateClub;
    private javax.swing.JButton btnDeactivateUser;
    private javax.swing.JButton btnDeleteduser;
    private javax.swing.JButton btnExportAttendance;
    private javax.swing.JButton btnExportMembers;
    private javax.swing.JButton btnRejectBudgetRequest;
    private javax.swing.JButton btnRejectClub;
    private javax.swing.JButton btnRemoveUser;
    private javax.swing.JButton btnUpdateClub;
    private java.awt.Panel budgetPanel;
    private javax.swing.JTable clublist;
    private javax.swing.JComboBox<String> cmbBudgets;
    private javax.swing.JComboBox<String> cmbRoles;
    private javax.swing.JComboBox<String> comboboxRoles;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JTabbedPane jTabbedPane4;
    private javax.swing.JPanel panelAttendanceChart;
    private javax.swing.JTable tblBudgetRequests;
    private javax.swing.JTable tblExpenses;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtSearchClub;
    // End of variables declaration//GEN-END:variables
}
