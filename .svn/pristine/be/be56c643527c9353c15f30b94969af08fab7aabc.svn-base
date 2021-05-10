package cwst.com.services;

import cwst.com.entities.CwstSysView;
import cwst.com.repositories.CwstSysViewRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CwstSysViewServiceImpl implements CwstSysViewService {
	@Autowired
	private CwstSysViewRepo cwstSysViewRepo;

	@Override
	public List<CwstSysView> findAll() {
		return cwstSysViewRepo.findAll();
	}

}
