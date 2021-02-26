package br.com.taskmanager.service;

import br.com.taskmanager.domain.EmailEntity;
import br.com.taskmanager.domain.UserEntity;
import br.com.taskmanager.exceptions.InvalidInputException;
import br.com.taskmanager.utils.EmailTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Properties;

@Service
@Slf4j
public class EmailService {


    private final Environment environment;

    public EmailService(Environment environment) {
        this.environment = environment;
    }

    public void sendEmail(EmailEntity email) throws MessagingException, InvalidInputException {
        Properties p = new Properties();
        p.put("mail.smtp.auth", "true");
        p.put("mail.smtp.starttls.enable", "true");
        p.put("mail.smtp.host", "smtp.gmail.com");
        p.put("mail.smtp.port", "587");

        if (StringUtils.isEmpty(email.getEmailAddress())) {
            throw new InvalidInputException("CLIENTE SEM EMAIL CADASTRADO");
        }

        Message message = prepareMessage(getSession(), email.getUser().getEmail(), email.getUser().getName(), email.getEmailSubject(), email.getMessage());
        log.info("sending email to -> {}", email.getEmailAddress());
        Transport.send(message);
        log.info("email sented successfully to -> {}", email.getEmailAddress());
    }

    private Message prepareMessage(Session s, String emailCliente, String clienteName, String subjectEmail, String emailBody) throws MessagingException {
        Message m = new MimeMessage(s);
        m.setFrom(new InternetAddress(environment.getProperty("email.login")));
        m.setRecipient(Message.RecipientType.TO, new InternetAddress(emailCliente));
        m.setSubject(subjectEmail);
        m.setText(emailBody);
        return m;
    }

    private Session getSession() {
        Session s = Session.getInstance(getPropriets(), new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(environment.getProperty("email.login"), environment.getProperty("email.senha"));
            }
        });
        return s;
    }

    private Properties getPropriets() {
        Properties p = new Properties();
        p.put("mail.smtp.auth", "true");
        p.put("mail.smtp.starttls.enable", "true");
        p.put("mail.smtp.host", "smtp.gmail.com");
        p.put("mail.smtp.port", "587");
        return p;
    }

    public EmailEntity sendEmailToUser(UserEntity user, String emailSubject, EmailTypeEnum emailTypeEnum, String emailMessage){
        EmailEntity email = new EmailEntity();

        email.setEmailAddress(user.getEmail());
        email.setEmailSubject(emailSubject);
        email.setUser(user);
        email.setDateCreated(LocalDateTime.now());
        email.setDateSented(LocalDateTime.now());
        email.setSented(0);
        email.setType(emailTypeEnum);
        email.setMessage(emailMessage);

       return email;

    }
}
