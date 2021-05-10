package cwst.com;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends GlobalMethodSecurityConfiguration {

	@Value("${ldap.domain}")
	private String sLdapDomain;

	@Value("${ldap.host}")
	private String sLdapHost;

	@Value("${ldap.port}")
	private String sLdapPort;

	@Autowired
	private MyUserDetailsContextMapper myUserDetailsContextMapper;

	@Bean
	public CustomActiveDirectoryLdapAuthenticationProvider customActiveDirectoryLdapAuthenticationProvider() {

		CustomActiveDirectoryLdapAuthenticationProvider provider = new CustomActiveDirectoryLdapAuthenticationProvider(
				sLdapDomain, "ldap://" + sLdapHost + ":" + sLdapPort, "OU=OU_Synced_Users,DC=scb,DC=local");
		provider.setConvertSubErrorCodesToExceptions(true);
		// username@domain.com {0}; username {1}
		provider.setSearchFilter("(&(CN={1}))");
		provider.setUserDetailsContextMapper(myUserDetailsContextMapper);
		return provider;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// DEVELOP dung de test nhanh bo qua buoc dang nhap qua AD nho
		// comment
		// production
		 auth.inMemoryAuthentication().withUser("ho").password("123")
		 .roles("HO", "CARDDISTRIBUTIONVIEW_VIEW",
		 "CARDDISTRIBUTIONVIEW_EDIT",
		 "REPORTCARDDISTRIBUTIONVIEW_VIEW")
		 .and().withUser("donvi").password("123")
		 .roles("DONVI", "CARDDISTRIBUTIONVIEW_VIEW",
		 "CARDDISTRIBUTIONVIEW_EDIT").and().withUser("thunbp")
		 .password("123").roles("DVKH", "CARDDISTRIBUTIONVIEW_VIEW",
		 "CARDDISTRIBUTIONVIEW_VIEW").and()
		 .withUser("hochecker").password("123")
		 .roles("HOCHECKER", "CARDDISTRIBUTIONVIEW_VIEW",
		 "CARDDISTRIBUTIONVIEW_CHECK").and()
		 .withUser("donvichecker").password("123")
		 .roles("DONVICHECKER", "CARDDISTRIBUTIONVIEW_VIEW",
		 "CARDDISTRIBUTIONVIEW_CHECK").and()
		 .withUser("khoanm").password("123")
		 .roles("SUPERADMIN", "CARDDISTRIBUTIONVIEW_VIEW",
		 "CARDDISTRIBUTIONVIEW_EDIT")
		 // .and().withUser("ho").password("123").roles("HO",
		 // "CARDDISTRIBUTIONVIEW_VIEW", "CARDDISTRIBUTIONVIEW_EDIT")
		 // .and().withUser("ho").password("123").roles("HO",
		 // "CARDDISTRIBUTIONVIEW_VIEW", "CARDDISTRIBUTIONVIEW_EDIT")
		
		 .and().withUser("cwst").password("123")
		 .roles("CARDWORKSUPPORTTOOLVIEW_VIEW",
		 "REPORTCARDWORKSUPPORTTOOLVIEW_VIEW").and()
		 .withUser("phuongntd2").password("123")
		 .roles("CARDWORKSUPPORTTOOLVIEW_VIEW",
		 "REPORTCARDWORKSUPPORTTOOLVIEW_VIEW").and().withUser("linhntd1")
		 .password("123").roles("CARDWORKSUPPORTTOOLVIEW_VIEW",
		 "REPORTCARDWORKSUPPORTTOOLVIEW_VIEW").and()
		 .withUser("nguyenha").password("123")
		 .roles("CARDWORKSUPPORTTOOLVIEW_VIEW",
		 "REPORTCARDWORKSUPPORTTOOLVIEW_VIEW").and().withUser("vanph1")
		 .password("123").roles("CARDWORKSUPPORTTOOLVIEW_VIEW",
		 "REPORTCARDWORKSUPPORTTOOLVIEW_VIEW").and()
		 .withUser("luyennth").password("123")
		 .roles("CARDWORKSUPPORTTOOLVIEW_VIEW",
		 "REPORTCARDWORKSUPPORTTOOLVIEW_VIEW");
		// PRODUCTION
		auth.eraseCredentials(true).authenticationProvider(customActiveDirectoryLdapAuthenticationProvider());
	}

	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return authenticationManager();
	}

	static {
		SecurityContextHolder.setStrategyName(VaadinSessionSecurityContextHolderStrategy.class.getName());
	}

}
