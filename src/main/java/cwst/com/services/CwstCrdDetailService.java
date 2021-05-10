package cwst.com.services;

import cwst.com.entities.CwstCrdDetailChecker;

public interface CwstCrdDetailService {
	CwstCrdDetailChecker findByCrdId(long crdid);
}
