package com.cwidanage.photoftp.util;

import com.cwidanage.photoftp.services.FileWalker;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * Created by Asim on 1/19/2018.
 */
@Component
public class EmailTemplate {
    private final static Logger logger = LogManager.getLogger(EmailTemplate.class);
	
	@Value("${logo.url}")
	private String logoImageUrl;
		
    public String FTP_NO_EVENT_FILE_EMAIL_TEMPLATE ;
    public  String FTP_FILE_UPLOAD_EMAIL_TEMPLATE;
    public String EVENT_TEMPLATE;
    public String FTP_ERROR_EMAIL_TEMPLATE;
    public  String FTP_CORRUPT_FILE_EMAIL_TEMPLATE;
    private String logoContentImgTag;

    @PostConstruct
    public void init() {
        logger.info("Logo URL Location = "+logoImageUrl);
        logoContentImgTag="<img src=\"" + logoImageUrl+"/logo24.png\"/>";

        EVENT_TEMPLATE = "<div>Hello {{Creator}} </div><br/>\n"
                + "<div>in attache you will find Xmp for {{eventName}}<br/><br/></div>\n"
                + "<div>IMPORTANT: Please Check that the Transmission Reference / Job Identifier, {{eventCode}}, is copied to all images.</div><br/><br/>\n"
                + "<div>"+logoContentImgTag+"</div>\n"
                + "<div style=\"margin-left:30px\"><div>{{source}}</div>\n"
                + "<div>{{CiUrlWork}}</div>\n"
                + "<div>{{CiEmailWork}}</div>\n"
                + "<div>{{CiTelWork}}</div></div>\n";
        FTP_ERROR_EMAIL_TEMPLATE = "Dear Administrator,\n"
                + "\n"
                + "There was a problem with the file you recently uploaded on {{date}}, the file(s) Was not sent to destination.\n"
                + "Please reupload the following file(s):\n"
                + "\n"
                + "{{fileName}}\n"
                + "\n"
                + "FTP Id: {{ftpId}}\n"
                + "FTP Name: {{ftpName}}\n"
                + "FTP Host: {{ftpHost}}\n"
                + "FTP Directory: {{ftpDirectory}}\n"
                + "\n"
                + "Many thanks,";

        FTP_CORRUPT_FILE_EMAIL_TEMPLATE = "Hi <<photographername>>,<br/><br/>\n"
                + "\n"
                + "There was a problem with the file you recently uploaded on {{date}}, the file(s) arrived corrupted or could not be saved.<br/>\n"
                + "Please reupload the following file(s):<br/>\n"
                + "<br/>\n"
                + "{{fileName}}<br/>\n"
                + "<br/>\n"
                + "Many thanks,<br/>"
                + "<div>"+logoContentImgTag+"</div>\n"
                + "<div style=\"margin-left:30px\"><div>{{source}}</div>\n"
                + "<div>{{CiUrlWork}}</div>\n"
                + "<div>{{CiEmailWork}}</div>\n"
                + "<div>{{CiTelWork}}</div></div>\n";
        FTP_NO_EVENT_FILE_EMAIL_TEMPLATE = "Dear Administrator,<br/>\n"
                + "<br/>\n"
                + "There was a problem with the file you recently uploaded on {{date}}, the file(s) arrived with wrong Transmission Reference or could not be sent.<br/>\n"
                + "Please reupload the following file(s):<br/>\n"
                + "<br/>\n"
                + "{{fileName}}<br/>\n"
                + "<br/>\n"
                + "Many thanks,"
                + "<div>"+logoContentImgTag+"</div>\n"
                + "<div style=\"margin-left:30px\"><div>Sportphoto24</div>\n"
                + "<div>http://www.sportphoto24.com</div>\n"
                + "<div>info@sportphoto24.com</div>\n"
                + "<div>+393286132039</div></div>\n";
        FTP_FILE_UPLOAD_EMAIL_TEMPLATE = "Dear Client,<br/>\n"
                + "<br/>\n"
                + "Headline: <<headline>>.\n <br/><br/>\n"
                + "Image Caption: <<description>>.\n <br/><br/>\n"
                + "Image Name: {{fileName}} attached.<br/><br/>\n"
                + "Please do not reply to this email. <br/>\n"
                + "This mailbox is not monitored and you will not receive a response. <br/>\n"
                + "Contact details are listed below.\n \n"
                + "<div>"+logoContentImgTag+"</div>\n"
                + "<div style=\"margin-left:30px\"><div>{{source}}</div>\n"
                + "<div>{{CiUrlWork}}</div>\n"
                + "<div>{{CiEmailWork}}</div>\n"
                + "<div>{{CiTelWork}}</div></div>\n";

    }

}
