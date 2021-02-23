package br.com.taskmanager.service;

import br.com.taskmanager.exceptions.InvalidInputException;
import br.com.taskmanager.repository.EmailRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.Properties;

@Service
@Slf4j
public class EmailService {

    private final String login;
    private final String senha;
    private final Environment environment;
    private final EmailRepository emailRepository;

    public EmailService(@Value("${email.login}") String login, @Value("${email.senha}") String senha, Environment environment, EmailRepository emailRepository) {
        this.login = login;
        this.senha = senha;
        this.environment = environment;
        this.emailRepository = emailRepository;
    }

    public void sendEmail(String emailCliente, String clienteName, String subjectEmail, String emailBody) throws MessagingException, InvalidInputException {
        Properties p = new Properties();
        p.put("mail.smtp.auth", "true");
        p.put("mail.smtp.starttls.enable", "true");
        p.put("mail.smtp.host", "smtp.gmail.com");
        p.put("mail.smtp.port", "587");

        if (StringUtils.isEmpty(emailCliente)) {
            throw new InvalidInputException("CLIENTE SEM EMAIL CADASTRADO");
        }

        Message message = prepareMessage(getSession(), emailCliente, clienteName, subjectEmail, emailBody);
        log.info("sending email to email -> {}", emailCliente);
        Transport.send(message);
        log.info("email sented successfully to -> {}", emailCliente);
    }

    private Message prepareMessage(Session s, String emailCliente, String clienteName, String subjectEmail, String emailBody) throws MessagingException {
        Message m = new MimeMessage(s);
        m.setFrom(new InternetAddress(login));
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


}
