package com.cwidanage.photoftp.services;

import com.cwidanage.photoftp.models.EventDetail;
import com.cwidanage.photoftp.models.FTPAccount;
import com.cwidanage.photoftp.util.EmailProperties;
import com.cwidanage.photoftp.util.EmailTemplate;
import com.cwidanage.photoftp.util.EmailUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import javax.mail.*;
import java.io.File;
import java.util.Properties;

import org.springframework.core.env.Environment;

/**
 * Created by Asim on 1/13/2018.
 */
@Service
@EnableAsync
public class EmailService extends AbstractService{
    private static Logger logger = LogManager.getLogger(EmailService.class);

    @Autowired
    private EmailProperties emailProperties;
    @Autowired
    private EmailTemplate emailTemplate;

    @Autowired
    public Environment env;

    @Async
    public void sendMessageWithAttachment(EventDetail event, String pathToAttachment) {
        try {

            Properties props = new Properties();
            props.put("mail.smtp.host", emailProperties.getHost());
            props.put("mail.smtp.socketFactory.port", emailProperties.getSocketFactoryPort());
            props.put("mail.smtp.socketFactory.class", emailProperties.getSocketFactoryClass());
            props.put("mail.smtp.auth", emailProperties.isAuth() ? "true" : "false");
            props.put("mail.smtp.port", emailProperties.getPort());

            String eventTemplate = emailTemplate.EVENT_TEMPLATE;
            eventTemplate = eventTemplate.replace("{{eventName}}", event.getName());
            eventTemplate = eventTemplate.replace("{{eventCode}}", event.getEventCode());
            eventTemplate = eventTemplate.replace("{{source}}", event.getSource());
            eventTemplate = eventTemplate.replace("{{CiUrlWork}}", event.getCiUrlWork());
            eventTemplate = eventTemplate.replace("{{CiEmailWork}}", event.getCiEmailWork());
            eventTemplate = eventTemplate.replace("{{CiTelWork}}", event.getCiTelWork());
            eventTemplate = eventTemplate.replace("{{Creator}}", event.getCreator());

            Authenticator auth = new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emailProperties.getUsername(), emailProperties.getPassword());
                }
            };
            Session session = Session.getDefaultInstance(props, auth);
            EmailUtil.sendEmailWithAttachment(session, event.getPemail(), "  Xmp for " + event.getName() + " [" + event.getEventCode() + "]", eventTemplate, pathToAttachment,pathToAttachment);
            File file = new File(pathToAttachment);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Async
    public void sendEmailAlertWithAttachment(String fileName,String dateAdded, FTPAccount ftpAccount) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", emailProperties.getHost());
            props.put("mail.smtp.socketFactory.port", emailProperties.getSocketFactoryPort());
            props.put("mail.smtp.socketFactory.class", emailProperties.getSocketFactoryClass());
            props.put("mail.smtp.auth", emailProperties.isAuth() ? "true" : "false");
            props.put("mail.smtp.port", emailProperties.getPort());
            

            String eventTemplate = emailTemplate.FTP_ERROR_EMAIL_TEMPLATE;
            eventTemplate = eventTemplate.replace("{{fileName}}", fileName);
            eventTemplate = eventTemplate.replace("{{date}}", dateAdded);
            eventTemplate = eventTemplate.replace("{{ftpId}}", ftpAccount != null ? ftpAccount.getId().toString() : "N/A");
            eventTemplate = eventTemplate.replace("{{ftpName}}", ftpAccount != null ? ftpAccount.getName() : "N/A");
            eventTemplate = eventTemplate.replace("{{ftpHost}}", ftpAccount != null ? ftpAccount.getHost() : "N/A");
            eventTemplate = eventTemplate.replace("{{ftpDirectory}}", ftpAccount != null && ftpAccount.getDirectory() != null ? ftpAccount.getDirectory() : "N/A");
            

            Authenticator auth = new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("xmp@sportphoto24.com", "Xmp12345678");
                }
            };
            Session session = Session.getDefaultInstance(props, auth);
            EmailUtil.sendAlertEmail(session, "xmp@sportphoto24.com", "  File Upload Error", eventTemplate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public void sendEmailWithAttachment(String fileName,String subject,String dateAdded,String eventTemplate,String emailto,String pathToAttachment) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", emailProperties.getHost());
            props.put("mail.smtp.socketFactory.port", emailProperties.getSocketFactoryPort());
            props.put("mail.smtp.socketFactory.class", emailProperties.getSocketFactoryClass());
            props.put("mail.smtp.auth", emailProperties.isAuth() ? "true" : "false");
            props.put("mail.smtp.port", emailProperties.getPort());
            

//            String eventTemplate = EmailTemplate.FTP_ERROR_EMAIL_TEMPLATE;
            logger.debug("filename => "+fileName);
            eventTemplate = eventTemplate.replace("{{fileName}}", fileName);
            logger.debug("Date => "+dateAdded);
            eventTemplate = eventTemplate.replace("{{date}}", dateAdded);

            

            Authenticator auth = new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("xmp@sportphoto24.com", "Xmp12345678");
                }
            };
            Session session = Session.getDefaultInstance(props, auth);
            EmailUtil.sendEmailWithAttachment(session, emailto, subject, eventTemplate, pathToAttachment,fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
