package cwst.com.services;

import cwst.com.entities.CwstCrdDetailChecker;
import cwst.com.repositories.CwstCrdDetailCheckerRepo;
import org.springframework.beans.factory.annotation.Autowired;

public class CwstCrdDetailServiceImpl implements CwstCrdDetailService {

	@Autowired
	CwstCrdDetailCheckerRepo cwstCrdDetailCheckerRepo;

	@Override
	public CwstCrdDetailChecker findByCrdId(long crdid) {
		return cwstCrdDetailCheckerRepo.findByCrdId(crdid);
	}

}
