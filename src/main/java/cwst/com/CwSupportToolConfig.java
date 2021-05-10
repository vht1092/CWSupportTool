package cwst.com;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import cwst.com.entities.CwstSendmail;
import cwst.com.services.CrdDetailService;
import cwst.com.services.CwstSendmailService;
import cwst.com.services.CwstSentmailService;

@Configuration
@EnableScheduling
@EnableAsync
public class CwSupportToolConfig {

	@Autowired
	private JavaMailSender javaMailSender;
	@Autowired
	private CwstSendmailService cwstSendmailService;
	@Autowired
	private CwstSentmailService cwstSentmailService;
	@Autowired
	private CrdDetailService crdDetailService;

	// private static final Logger LOGGER = LoggerFactory.getLogger(CwSupportToolConfig.class);
	private static final Logger LOGGER = LogManager.getLogger(CwSupportToolConfig.class);

	private static final List<String> CASE_STATUS = Arrays.asList("DECLINED", "CANCELED", "ROUTED", "NEW", "APPROVED");
	private static final List<String> CASE_REMIND = Arrays.asList("REMIND");

	@Value("${cwst.scandata.enable}")
	private Boolean cwstScandataEnable;

	@Value("${cwst.getnewestdata.enable}")
	private Boolean cwstGetNewestEnable;

	@Value("${cwst.sendmailer.enable}")
	private Boolean cwstSendmailerEnable;

	@Value("${cwst.sendmail8houreveryday.enable}")
	private Boolean cwstSendmail8HourEveryDayEnable;

	@Value("${cwst.sendmaildistcard.enable}")
	private Boolean cwstSendDistCardEnable;

	@Value("${cwst.sendmaildistcard.numofday}")
	private int sendmaildistcard_numofday;

	// Gui mail moi 20s
	@Scheduled(cron = "${cwst.sendmailer}")
	private void sendMailer() {
		if (cwstSendmailerEnable) {
			cwstSendmailService.findAllByMailtypeIn(CASE_STATUS).forEach(s -> {
				if (CASE_STATUS.contains(s.getMailtype())) {
					try {
						// Khong luu vao database mail da gui chuyen sang luu vao file log
						// cwstSentmailService.save(s.getCaseId(), s.getMailtype(), s.getMailcontent());
						sendMail(s.getMailto(), s.getMailcontent(), s.getMailtitle());
						cwstSendmailService.delete(s.getCaseId(), s.getMailtype());
					} catch (Exception e) {
						LOGGER.error("SendMailer - Message: " + e.getMessage());
					}
				}
			});
		}
	}

	// Gui mail sau 5 ngay da gui the ve don vi nhung don vi khong xac nhan (Quan ly phan phoi the) chua dung toi
	@Scheduled(cron = "${cwst.sendmaildistcard}")
	private void sendMail5Days() {
		if (cwstSendDistCardEnable) {
			final int numberOfDay = sendmaildistcard_numofday;
			final List<Object[]> listBrchCde = crdDetailService.findBrchBeforeNumberOfDay(numberOfDay);
			listBrchCde.forEach(rsBrchCde -> {
				final String listEmail = rsBrchCde[1] != null ? rsBrchCde[1].toString() : "";
				if (!listEmail.equals("")) {
					final List<Object[]> listCase = crdDetailService.findCaseDistributingCardBeforeNumberOfDay(rsBrchCde[0].toString(), numberOfDay);
					final String workingDate = crdDetailService.findWorkingDate();// Ngay lam viec trong nam lay tu cardwork 366 ky tu N: di lam; Y: nghi
					final TimeConverter timeConverter = new TimeConverter();
					final StringBuilder sEmailContent = new StringBuilder(
							"<p>P.TNT & NHĐT thông báo đến Đơn vị Danh sách thẻ phát hành đã được chuyển về đơn vị</p>"
									+ "<table style='border:1px solid;border-collapse: collapse;width: 100%;text-align: center;' border=1>" + "<tr>"
									+ "<th>STT</th>" + "<th>CIF</th>" + "<th>Loại Thẻ</th>" + "<th>Tên Chủ Thẻ</th>" + "<th>Chính/Phụ</th>"
									+ "<th>Số Thẻ</th>" + "<th>Loại Phát Hành</th>" + "<th>Ngày Phát Hành</th>" + "<th>Mã Đơn Vị</th>"
									+ "<th>Họ & tên ĐV Nhận Thẻ</th>" + "<th>MNV ĐV Nhận Thẻ</th>" + "<th>Ngày Giao Thẻ Về Đơn Vị</th>" + "</tr>");
					if (!workingDate.isEmpty()) {
						int n = LocalDate.now().get(ChronoField.DAY_OF_YEAR);// Lay thu tu ngay trong nam
						if (workingDate.substring(n - 1, n).equals("N")) {
							listCase.forEach(s -> {
								final String sCif = String.valueOf(s[0]);
								final String sCrdType = String.valueOf(s[1]);
								final String sCustName = String.valueOf(s[2]);
								final String sPrinSupp = String.valueOf(s[3]);
								final String sPan = String.valueOf(s[4]);
								final String sIssueType = String.valueOf(s[5]);
								final String sIssueDate = timeConverter.convertStrToDateTime(String.valueOf(s[6]));
								final String sBrchCde = String.valueOf(s[7]);
								final String sEmpName = String.valueOf(s[8]);
								final String sEmpCode = String.valueOf(s[9]);
								final String sDate = timeConverter.convertStrToDateTime(String.valueOf(s[10]));
								final String rownum = String.valueOf(s[12]);
						//@formatter:off		
						sEmailContent.append("<tr>" + 
			    				"<td>"+rownum+"</td>" + 
			    				"<td>"+sCif+"</td>" + 
			    				"<td>"+sCrdType+"</td>" + 
			    				"<td>"+sCustName+"</td>" + 
			    				"<td>"+sPrinSupp+"</td>" + 
			    				"<td>"+sPan+"</td>" + 
			    				"<td>"+sIssueType+"</td>" + 
			    				"<td>"+sIssueDate+"</td>" + 
			    				"<td>"+sBrchCde+"</td>" + 
			    				"<td>"+sEmpName+"</td>" + 
			    				"<td>"+sEmpCode+"</td>" + 
			    				"<td>"+sDate+"</th>" + 
			    				"</tr>"); 		
						// @formatter:on
							});
							sEmailContent.append("</table>"
									+ "<p>Đơn vị vui lòng kiểm tra thông tin những thẻ theo danh sách trên và cập nhập vào chương trình quản lý phôi thẻ về việc đã nhận thẻ</p>"
									+ "<p>Nếu Đơn vị chưa nhận được những thẻ trên liên hệ về P.TNT&NHĐT để kiểm tra.</p>");
							System.out.println("emailcontent: " + sEmailContent.toString());
							try {
								sendMail(listEmail, sEmailContent.toString(), "Quản Lý Phân Phối Thẻ");
							} catch (Exception e) {
								LOGGER.error("Gui email cach bao quan ly phan phoi the that bai - Message: " + e.getMessage());
							}
						} else {
							LOGGER.info("Hom nay khong phai ngay lam viec");
						}

					} else {
						LOGGER.error("Khong tim thay du lieu working date");
					}
				}
			});
		}
	}

	private void sendMail(final String address, final String content, final String subject) throws MessagingException {
		if (address != null && !address.isEmpty()) {
			MimeMessage mail = javaMailSender.createMimeMessage();
			try {
				MimeMessageHelper helper = new MimeMessageHelper(mail);
				// address String to String []
				helper.setTo(address.split(","));
				helper.setFrom(new InternetAddress("cardworks_support@scb.com.vn"));
				helper.setReplyTo(new InternetAddress("cardworks_support@scb.com.vn"));
				helper.setSubject(subject);
				helper.setText(content, true);
				javaMailSender.send(mail);
				LOGGER.info("Send mail to: " + address + "|subject: " + subject + "|content: " + content);
			} catch (MessagingException e) {
				LOGGER.error("Send mail error: " + e.getMessage() + " - mail to: - " + address + "|subject:" + subject + "content: " + content);
				throw new MessagingException();
			}
		}
	}
}