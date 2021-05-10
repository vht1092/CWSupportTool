package cwst.com.services;

import cwst.com.TimeConverter;
import cwst.com.entities.CwstSentmail;
import cwst.com.repositories.CwstSentmailRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CwstSentmailServiceImpl implements CwstSentmailService {

	@Autowired
	private CwstSentmailRepo cwstSentmailRepo;
	private final TimeConverter timeConverter = new TimeConverter();

	@Override
	public void save(final String caseid, final String caseresult, final String mailcontent) {
		final CwstSentmail cwstSentmail = new CwstSentmail();
		cwstSentmail.setMailcontent(mailcontent);
		cwstSentmail.setCaseId(caseid);
		cwstSentmail.setMailtype(caseresult);
		cwstSentmail.setCreTms(new BigDecimal(timeConverter.getCurrentTime("FULL")));
		cwstSentmailRepo.save(cwstSentmail);

	}

}
