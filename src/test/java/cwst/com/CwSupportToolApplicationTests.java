package cwst.com;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CwSupportToolApplicationTests {

	private static final Logger LOGGER = LogManager.getLogger(CwSupportToolApplicationTests.class);
	private JavaMailSender javaMailSender;

	@Test
	public void contextLoads() {
		sendMail("", "test", "test");
	}

	private void sendMail(final String address, final String content, final String subject) {
		if (address != null && !address.isEmpty()) {
			MimeMessage mail = javaMailSender.createMimeMessage();
			try {
				MimeMessageHelper helper = new MimeMessageHelper(mail);
				// address String to String []
				// helper.setTo(address.split(","));
				helper.setTo(new InternetAddress("chautk@scb.com.vn"));
				helper.setFrom(new InternetAddress("cardworks_support@scb.com.vn"));
				helper.setReplyTo(new InternetAddress("cardworks_support@scb.com.vn"));
				helper.setSubject(subject);
				helper.setText(content, true);
				javaMailSender.send(mail);
				LOGGER.info("Send mail to: " + address + "|subject: " + subject + "|content: " + content);
			} catch (MessagingException e) {
				LOGGER.error("Send mail error: " + e.getMessage() + " - mail to: - " + address + "|subject:" + subject + "content: " + content);
			}
		}
	}
}
