package net.staretta.businesslogic.services;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EmailService extends BaseService
{
	public static class EmailCredentials
	{
		public String smtpHost;
		public String username;
		public String password;
		public boolean authenticate;
		public boolean ttls;
		public String port;
		
		public String defaultReplyEmail = "RawrBot <noreply@staretta.com>";
	}
	
	private EmailCredentials emailCredentials;
	
	public void setGlobalCredentials(EmailCredentials emailCredentials)
	{
		this.emailCredentials = emailCredentials;
	}
	
	public class MyAuthenticator extends Authenticator
	{
		private String user;
		private String pw;
		
		public MyAuthenticator(String username, String password)
		{
			super();
			this.user = username;
			this.pw = password;
		}
		
		public PasswordAuthentication getPasswordAuthentication()
		{
			return new PasswordAuthentication(user, pw);
		}
	}
	
	public void sendMail(String toAddress, String fromAddress, String subject, String body)
	{
		// Get system properties
		Properties properties = System.getProperties();
		
		// Setup mail server
		properties.setProperty("mail.smtp.host", emailCredentials.smtpHost);
		if (emailCredentials.username != null)
			properties.setProperty("mail.user", emailCredentials.username);
		if (emailCredentials.password != null)
			properties.setProperty("mail.password", emailCredentials.password);
		properties.setProperty("mail.smtp.auth", emailCredentials.authenticate ? "true" : "false");
		properties.setProperty("mail.smtp.starttls.enable", emailCredentials.ttls ? "true" : "false");
		properties.setProperty("mail.smtp.port", emailCredentials.port);
		properties.setProperty("mail.smtp.ssl.trust", "*");
		properties.setProperty("mail.smtp.ssl.checkserveridentity", "false");
		
		// Get the default Session object.
		// Session session = Session.getDefaultInstance(properties);
		Session session = Session.getInstance(properties, new MyAuthenticator(emailCredentials.username, emailCredentials.password));
		
		try
		{
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);
			
			// Set From: header field of the header.
			message.setFrom(new InternetAddress(fromAddress));
			
			// Set To: header field of the header.
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
			
			// Set Subject: header field
			message.setSubject(subject);
			
			// Send the actual message
			message.setText(body);
			
			// Send the actual HTML message, as big as you like
			// message.setContent(html.toString(), "text/html");
			
			// Send message
			Transport.send(message);
		}
		catch (MessagingException mex)
		{
			getLogger().error("Email Error: ", mex);
		}
	}
}
