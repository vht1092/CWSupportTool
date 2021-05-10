package cwst.com;

import com.vaadin.spring.annotation.SpringComponent;
import cwst.com.entities.CwstSysPermission;
import cwst.com.entities.CwstSysUser;
import cwst.com.services.CwstSysPermissionService;
import cwst.com.services.SysUserService;
import cwst.com.views.ReportCardDistributionView;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SpringComponent
public class MyUserDetailsContextMapper implements UserDetailsContextMapper {

	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private CwstSysPermissionService cwstSysPermissionService;
	// private static final Logger LOGGER = LoggerFactory.getLogger(MyUserDetailsContextMapper.class);
	//private static final Logger LOGGER = LogManager.getLogger(MyUserDetailsContextMapper.class);

	@Override
	public UserDetails mapUserFromContext(DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> authorities) {
		final CwstSysUser user = sysUserService.findOneByUsernameAndIsLockFalse(username);
		if (user == null) {
			return null;
		}
		final Collection<GrantedAuthority> listAuth = new ArrayList<>();
		// Loai user HO, DONVI
		listAuth.add(new SimpleGrantedAuthority("ROLE_" + user.getUsrType().toUpperCase()));
//		listAuth.add(new SimpleGrantedAuthority("ROLE_CARDDISTRIBUTIONVIEW_VIEW"));
//		mapUserFromContext
		final List<CwstSysPermission> permissions = cwstSysPermissionService.findByUserId(user.getId());
		permissions.forEach(s -> {
			if (s.getIsedit()) {
				listAuth.add(new SimpleGrantedAuthority("ROLE_" + s.getCwstSysView().getViewname().toUpperCase() + "_EDIT"));
			}
			if (s.getIsview()) {
				listAuth.add(new SimpleGrantedAuthority("ROLE_" + s.getCwstSysView().getViewname().toUpperCase() + "_VIEW"));
			}
			if (s.getIscheker()) {
				listAuth.add(new SimpleGrantedAuthority("ROLE_" + s.getCwstSysView().getViewname().toUpperCase() + "_CHECK"));
			}
			if (s.getIsdelete()) {
				listAuth.add(new SimpleGrantedAuthority("ROLE_" + s.getCwstSysView().getViewname().toUpperCase() + "_DELETE"));
			}
		});

		// Supper man xuat hien
		if (username.equals("huyennt")) {
			listAuth.add(new SimpleGrantedAuthority("ROLE_SUPERADMIN"));
		}

		final CwstLdapUserDetails ldapUserDetails = new CwstLdapUserDetails();
		ldapUserDetails.setId(user.getId());
		ldapUserDetails.setUsername(username);
		ldapUserDetails.setFullname(user.getFullname());
		ldapUserDetails.setAuthorities(listAuth);
		//Debug
		//listAuth.forEach(s -> {System.out.println(s.getAuthority()); });

		if (sysUserService.findUserCardWorks(username) != null) {
			ldapUserDetails.setUsernamecardwork(sysUserService.findUserCardWorks(username));
			// LOGGER.info("User CardWorks: " + sysUserService.findUserCardWorks(username));
		}

		return ldapUserDetails;
	}

	@Override
	public void mapUserToContext(UserDetails user, DirContextAdapter ctx) {

	}
}
