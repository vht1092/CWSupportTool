package cwst.com;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Dung de ho tro get value tu file config cho component khong quan ly boi
 * Spring
 */
@Component
public class SpringConfigurationValueHelper {

	@Value("${path.file.root}")
	private String pathFileRoot;

	@Value("${time.refresh.content}")
	private int sTimeRefreshContent;

	
	public int sTimeRefreshContent() {
		return sTimeRefreshContent;
	}

	public String getPathFileRoot() {
		return pathFileRoot;
	}

	

}
