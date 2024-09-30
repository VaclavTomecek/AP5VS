import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailSender {
    public static void main(String[] args) {
        String host = "smtp.gmail.com";
        final String user = "your-email@gmail.com"; // změňte na svůj e-mail
        final String password = "your-password"; // změňte na svoji e-mailovou heslo

        String to = "recipient-email@example.com"; // změňte na cílový e-mail

        // 1) Nastavení vlastností e-mailu:
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // 2) Autentizační server SMTP poskytuje vs. autentizační ověřovač:
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });

        // 3) Sestavení e-mailu:
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("Testovací předmět");
            message.setText("Toto je text emailu.");

            // 4) Odeslání e-mailu
            Transport.send(message);
            System.out.println("E-mail byl úspěšně odeslán.");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}