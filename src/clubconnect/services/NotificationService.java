package clubconnect.services;

import clubconnect.dao.NotificationDAO;
import clubconnect.models.Notification;
import clubconnect.utils.EmailUtil;

import jakarta.mail.MessagingException;

public class NotificationService {

    /**
     * Creates a notification in the database and sends an email to the user.
     *
     * @param userId   the ID of the user to notify
     * @param userEmail the user's email
     * @param userName  the user's name
     * @param clubId   the ID of the club (optional)
     * @param clubName the club's name (optional, used in email)
     * @param message  the notification message
     * @return true if notification created and email sent successfully
     */
    public static boolean notifyUser(int userId, String userEmail, String userName,
                                     Integer clubId, String clubName, String message) {
       Notification notification = new Notification(userId, message, clubId);

        notification.setUserId(userId);
        notification.setClubId(clubId != null ? clubId : 0);
        notification.setMessage(message);

        boolean created = NotificationDAO.createNotification(notification);
        if (!created) {
            System.err.println("Failed to create notification in DB for userId: " + userId);
            return false;
        }

        // 2️⃣ Send email
        try {
            String emailBody = EmailUtil.createNotificationEmailTemplate(userName, clubName != null ? clubName : "Your Club", message);
            EmailUtil.sendEmail(userEmail, "New Club Notification", emailBody);
            System.out.println("Notification email sent to: " + userEmail);
        } catch (MessagingException e) {
            System.err.println("Failed to send notification email to: " + userEmail + " - " + e.getMessage());
            return false;
        }

        return true;
    }
}
