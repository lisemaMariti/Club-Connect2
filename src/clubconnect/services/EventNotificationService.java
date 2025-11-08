package clubconnect.services;

import clubconnect.dao.AttendanceDAO;
import clubconnect.dao.EventDAO;
import clubconnect.models.Event;
import clubconnect.models.User;
import clubconnect.utils.EmailUtil;
import jakarta.mail.MessagingException;
import java.util.List;

public class EventNotificationService {

    // This method sends reminders for all upcoming events
    public static void sendUpcomingEventReminders() {
        List<Event> upcomingEvents = EventDAO.getUpcomingEvents(); // your enhanced DAO

        for (Event event : upcomingEvents) {
            List<User> attendees = AttendanceDAO.getAttendeesForEvent(event.getEventId()); 

            for (User user : attendees) {
                String message = "Reminder: The event '" + event.getName() + "' is coming up on " + event.getEventDate();
                String emailBody = EmailUtil.createNotificationEmailTemplate(user.getName(), event.getClubName(), message);

                try {
                    EmailUtil.sendEmail(user.getEmail(), "Upcoming Event: " + event.getName(), emailBody);
                } catch (MessagingException e) {
                    System.err.println("Failed to send email to " + user.getEmail() + ": " + e.getMessage());
                }
            }
        }
    }
}
