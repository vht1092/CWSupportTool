package cwst.com.services;

import cwst.com.entities.CwstSendmail;

import java.util.Collection;
import java.util.List;

public interface CwstSendmailService {

	public void save(final String content, final String caseid, final String mailtype, final String mailto);

	public List<CwstSendmail> findAllByMailtypeIn(Collection<String> mailtype);

	public void delete(String caseid, String mailtype);
	
	public String getEmailUserCreate(String usercreate);
}
