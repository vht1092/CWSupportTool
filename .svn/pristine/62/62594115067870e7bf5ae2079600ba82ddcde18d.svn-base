package cwst.com.services;

import cwst.com.TimeConverter;
import cwst.com.entities.CwstSendmail;
import cwst.com.repositories.CwstSendMailRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class CwstSendmailServiceImpl implements CwstSendmailService {

	@Autowired
	private CwstSendMailRepo cwstSendMailRepo;
	private final TimeConverter timeConverter = new TimeConverter();

	@Override
	public void save(final String content, final String caseid, final String mailtype, final String mailto) {

		CwstSendmail cwstSendmail = cwstSendMailRepo.findOneByCaseIdAndMailtype(caseid, mailtype);
		if (cwstSendmail != null) {
			cwstSendMailRepo.delete(cwstSendmail);
		}
		cwstSendmail = new CwstSendmail();
		cwstSendmail.setCaseId(caseid);
		cwstSendmail.setCreTms(Long.parseLong(timeConverter.getCurrentTime("FULL")));
		cwstSendmail.setMailcontent(content);
		cwstSendmail.setMailtype(mailtype);
		cwstSendmail.setMailtitle("Case Remind");
		cwstSendmail.setMailto(mailto);
		cwstSendMailRepo.save(cwstSendmail);
	}

	@Override
	public List<CwstSendmail> findAllByMailtypeIn(final Collection<String> mailtype) {
		return cwstSendMailRepo.findAllByMailtypeIn(mailtype);
	}

	@Override
	public void delete(final String caseid, final String mailtype) {
		CwstSendmail cwstSendmail = cwstSendMailRepo.findOneByCaseIdAndMailtype(caseid, mailtype);
		if (cwstSendmail != null) {
			cwstSendMailRepo.delete(cwstSendmail);
		}
	}

	@Override
	public String getEmailUserCreate(String usercreate) {
		return cwstSendMailRepo.getUserEmail(usercreate);
	}

}
