package clubconnect.dao;

import clubconnect.db.DBManager;
import clubconnect.dao.ClubDAO;
import clubconnect.models.Club;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.Image;
import java.io.File;
import javax.swing.*;
import java.sql.*;

public class EditClubDialog extends JDialog {
    
    private Club club;
    private JTextField txtName;
    private JTextArea txtDescription;
    private JLabel lblImage;
    private JButton btnBrowse, btnSave, btnCancel;
    private File selectedFile;
    
    public EditClubDialog(JFrame parent, boolean modal, Club club) {
        super(parent, modal);
        this.club = club;
        setTitle("Edit Club - " + club.getName());
        setSize(450, 400);
        setLocationRelativeTo(parent);
        setLayout(null);
        
        JLabel lbl1 = new JLabel("Name:");
        lbl1.setBounds(20, 20, 100, 25);
        add(lbl1);
        
        txtName = new JTextField(club.getName());
        txtName.setBounds(130, 20, 250, 25);
        add(txtName);
        
        JLabel lbl2 = new JLabel("Description:");
        lbl2.setBounds(20, 60, 100, 25);
        add(lbl2);
        
        txtDescription = new JTextArea(club.getDescription());
        JScrollPane scroll = new JScrollPane(txtDescription);
        scroll.setBounds(130, 60, 250, 100);
        add(scroll);
        
        JLabel lbl3 = new JLabel("Photo:");
        lbl3.setBounds(20, 180, 100, 25);
        add(lbl3);
    /*   
        lblImage = new JLabel();
        lblImage.setBounds(130, 180, 100, 100);
        lblImage.setBorder(BorderFactory.createEtchedBorder());
        if (club.getImagePath() != null && !club.getImagePath().isEmpty()) {
            ImageIcon imgIcon = new ImageIcon(club.getImagePath());
            Image scaled = imgIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            lblImage.setIcon(new ImageIcon(scaled));
        }
        add(lblImage);
        */
        btnBrowse = new JButton("Browse");
        btnBrowse.setBounds(250, 210, 100, 25);
        add(btnBrowse);
        btnBrowse.addActionListener(e -> chooseImage());
        
        btnSave = new JButton("Save");
        btnSave.setBounds(130, 320, 100, 30);
        add(btnSave);
        btnSave.addActionListener(e -> saveChanges());
        
        btnCancel = new JButton("Cancel");
        btnCancel.setBounds(250, 320, 100, 30);
        add(btnCancel);
        btnCancel.addActionListener(e -> dispose());
    }
    
    private void chooseImage() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
            ImageIcon icon = new ImageIcon(selectedFile.getAbsolutePath());
            Image scaled = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            lblImage.setIcon(new ImageIcon(scaled));
        }
    }
    
    private void saveChanges() {
        club.setName(txtName.getText());
        club.setDescription(txtDescription.getText());
        if (selectedFile != null) {
         //   club.setImagePath(selectedFile.getAbsolutePath());
        }
        
      /*  boolean success = ClubDAO.updateClubDetails(club);
        
        if (success) {
            JOptionPane.showMessageDialog(this, "Club updated successfully!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error updating club.");
        }*/
    }
}
