package br.com.taskmanager.service;

import br.com.taskmanager.domain.EmailEntity;
import br.com.taskmanager.domain.UserEntity;
import br.com.taskmanager.exceptions.InvalidInputException;
import br.com.taskmanager.repository.EmailRepository;
import br.com.taskmanager.utils.EmailTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
@Slf4j
public class EmailService {


    private final Environment environment;
    private final EmailRepository emailRepository;

    public EmailService(Environment environment, EmailRepository emailRepository) {
        this.environment = environment;
        this.emailRepository = emailRepository;
    }

    public void sendEmail(EmailEntity email,String toAttach) throws MessagingException, InvalidInputException, IOException {
        Properties p = new Properties();
        p.put("mail.smtp.auth", "true");
        p.put("mail.smtp.starttls.enable", "true");
        p.put("mail.smtp.host", "smtp.gmail.com");
        p.put("mail.smtp.port", "587");

        if (StringUtils.isEmpty(email.getEmailAddress())) {
            throw new InvalidInputException("CLIENTE SEM EMAIL CADASTRADO");
        }

        Message message = prepareMessage(getSession(), email.getUser().getEmail(), email.getUser().getName(), email.getEmailSubject(), email.getMessage(),toAttach);
        try{
            log.info("sending email to -> {}", email.getEmailAddress());
            Transport.send(message);
            log.info("email sented successfully to -> {}", email.getEmailAddress());
        }catch (Exception e){
            log.error("Error to send email to -> {}{}",email.getEmailAddress(),e.getMessage());
        }
    }

    private Message prepareMessage(Session s, String emailCliente, String clienteName, String subjectEmail, String emailBody,String toAttach) throws MessagingException, IOException {
        if(toAttach.isBlank()){
            Message m = new MimeMessage(s);
            m.setFrom(new InternetAddress(environment.getProperty("email.login")));
            m.setRecipient(Message.RecipientType.TO, new InternetAddress(emailCliente));
            m.setSubject(subjectEmail);
            m.setText(emailBody);
            return m;
        }
        else {
            Message message = new MimeMessage(s);
            message.setFrom(new InternetAddress(environment.getProperty("email.login")));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(emailCliente));
            message.setSubject(subjectEmail);
            BodyPart messageBodyPart = new MimeBodyPart();

            messageBodyPart.setText(emailBody);
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            File f = new File("/home/dados.txt");
            BufferedWriter writer = new BufferedWriter(new FileWriter(f.getName()));
            writer.write(toAttach);
            messageBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(f.getName());
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(f.getName());
            multipart.addBodyPart(messageBodyPart);
            message.setContent(multipart);
            writer.close();
            f.deleteOnExit();

            return message;
        }

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
    public void sendEmailNow(EmailEntity email,String toAttach) throws InvalidInputException, MessagingException, IOException {
        email.setSented(1);
        email.setDateSented(LocalDateTime.now());
        emailRepository.save(email);
        this.sendEmail(email,toAttach);
    }
}
