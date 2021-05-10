package cwst.com.services;

import cwst.com.entities.CwstEmailList;

import java.util.List;

public interface CwstEmailListService {
	public List<CwstEmailList> findAllByBrchCde(String brchcde);

	public List<CwstEmailList> findAllByEmail(String email);

}
