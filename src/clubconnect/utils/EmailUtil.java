package clubconnect.utils;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class EmailUtil {

    private static final Logger logger = Logger.getLogger(EmailUtil.class.getName());

    private static final String username = System.getenv("EMAIL_USERNAME") != null ?
            System.getenv("EMAIL_USERNAME") : "sclubconnect@gmail.com";
    private static final String password = System.getenv("EMAIL_PASSWORD") != null ?
            System.getenv("EMAIL_PASSWORD") : "rmdx qwle ljvi gsof";

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    // ---------------- Send Email ----------------
    public static void sendEmail(String toEmail, String subject, String body) throws MessagingException {
        sendEmail(new String[]{toEmail}, subject, body);
    }

    public static void sendEmail(String[] toEmails, String subject, String body) throws MessagingException {
        // Validate email addresses
        for (String email : toEmails) {
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                throw new MessagingException("Invalid email: " + email);
            }
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));

        InternetAddress[] addresses = new InternetAddress[toEmails.length];
        for (int i = 0; i < toEmails.length; i++) {
            addresses[i] = new InternetAddress(toEmails[i]);
        }
        message.setRecipients(Message.RecipientType.TO, addresses);
        message.setSubject(subject);
        message.setContent(body, "text/html; charset=utf-8");
        message.setSentDate(new java.util.Date());

        Transport.send(message);
        logger.info("✅ Email sent to: " + String.join(", ", toEmails));
    }

    
    public static String createGenericNotificationTemplate(String message) {
    return """
        <!DOCTYPE html>
        <html>
        <head>
            <style>
                body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                .header { background: #4CAF50; color: white; padding: 10px; text-align: center; }
                .content { padding: 20px; background: #f9f9f9; }
                .footer { padding: 10px; text-align: center; font-size: 12px; color: #666; }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header"><h2>New Notification</h2></div>
                <div class="content">
                    <p>%s</p>
                </div>
                <div class="footer">
                    <p>This is an automated message from ClubConnect. Please do not reply.</p>
                </div>
            </div>
        </body>
        </html>
        """.formatted(message);
}

    // ---------------- Notification Email Template ----------------
    public static String createNotificationEmailTemplate(String userName, String clubName, String message) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background: #4CAF50; color: white; padding: 10px; text-align: center; }
                        .content { padding: 20px; background: #f9f9f9; }
                        .footer { padding: 10px; text-align: center; font-size: 12px; color: #666; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h2>New Notification from %s</h2>
                        </div>
                        <div class="content">
                            <p>Hello <strong>%s</strong>,</p>
                            <p>You have a new notification from your club:</p>
                            <blockquote>%s</blockquote>
                            <p>Check your app for more details.</p>
                            <p>Best regards,<br>Your ClubConnect Team</p>
                        </div>
                        <div class="footer">
                            <p>This is an automated message. Do not reply to this email.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(clubName, userName, message);
    }
}
