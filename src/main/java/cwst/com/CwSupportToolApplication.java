package cwst.com;

import com.ulisesbocchio.jasyptspringboot.annotation.EncryptablePropertySource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EncryptablePropertySource(name = "EncryptedProperties", value = "classpath:encrypted.properties")
public class CwSupportToolApplication extends SpringBootServletInitializer {
	
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(CwSupportToolApplication.class);
    }

	public static void main(String[] args) {
		SpringApplication.run(CwSupportToolApplication.class, args);
	}	

}
