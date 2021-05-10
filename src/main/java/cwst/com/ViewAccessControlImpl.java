package cwst.com;

import com.vaadin.spring.access.ViewAccessControl;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.UI;

@SpringComponent
public class ViewAccessControlImpl implements ViewAccessControl {
	@Override
	public boolean isAccessGranted(UI ui, String beanName) {
		if (beanName.equals("cardDistributionView")) {
			if (SecurityUtils.hasRole("ROLE_CARDDISTRIBUTIONVIEW_VIEW") || SecurityUtils.hasRole("ROLE_SUPERADMIN")
					|| SecurityUtils.hasRole("ROLE_DONVI") || SecurityUtils.hasRole("ROLE_HOCHECKER")
					|| SecurityUtils.hasRole("ROLE_DONVICHECKER") || SecurityUtils.hasRole("ROLE_HO")
					|| SecurityUtils.hasRole("ROLE_DVKH")) {
				return true;
			}
		}
		if (beanName.equals("cardWorkSupportToolView")) {
			if (SecurityUtils.hasRole("ROLE_CARDWORKSUPPORTTOOLVIEW_VIEW")
					|| SecurityUtils.hasRole("ROLE_SUPERADMIN")) {
				return true;
			}
		}
		if (beanName.equals("reportCardDistributionView")) {
			if (SecurityUtils.hasRole("ROLE_REPORTCARDDISTRIBUTIONVIEW_VIEW")
					|| SecurityUtils.hasRole("ROLE_SUPERADMIN") || SecurityUtils.hasRole("ROLE_DONVI")
					|| SecurityUtils.hasRole("ROLE_DONVICHECKER") || SecurityUtils.hasRole("ROLE_HO")
					|| SecurityUtils.hasRole("ROLE_HOCHECKER") || SecurityUtils.hasRole("ROLE_DVKH")) {
				return true;
			}
		}

		if (beanName.equals("reportCardWorkSupportToolView")) {
			if (SecurityUtils.hasRole("ROLE_REPORTCARDWORKSUPPORTTOOLVIEW_VIEW")
					|| SecurityUtils.hasRole("ROLE_SUPERADMIN")) {
				return true;
			}
		}

		if (beanName.equals("updateBranchView")) {
			if (SecurityUtils.hasRole("ROLE_SUPERADMIN") || SecurityUtils.hasRole("ROLE_DONVI")
					|| SecurityUtils.hasRole("ROLE_DONVICHECKER") || SecurityUtils.hasRole("ROLE_HO")
					|| SecurityUtils.hasRole("ROLE_HOCHECKER") || SecurityUtils.hasRole("ROLE_DVKH")) {
				return true;
			}
		}

		if (beanName.equals("loginView")) {
			return true;
		}
		if (beanName.equals("userManagerView")) {
			return true;
		}
		if (beanName.equals("reportView")) {
			return true;
		}
		return false;
	}
}
