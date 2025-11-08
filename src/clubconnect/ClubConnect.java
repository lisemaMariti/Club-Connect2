/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package clubconnect;

import clubconnect.db.DBManager;
import clubconnect.db.DatabaseSeeder;
import clubconnect.services.EventNotificationService;
import clubconnect.ui.Home;
import clubconnect.ui.LoginForm;
import javax.swing.*;
/**
 *
 * @author User
 */
public class ClubConnect {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    DBManager.initializeDatabase(); // Keep database setup

    SwingUtilities.invokeLater(() -> new Home().setVisible(true)); 
    DatabaseSeeder.seedAdminUser();
    EventNotificationService.sendUpcomingEventReminders();

}


    
    }
    
