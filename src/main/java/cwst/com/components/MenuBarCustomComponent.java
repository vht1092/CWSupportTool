package cwst.com.components;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.themes.ValoTheme;
import cwst.com.SecurityUtils;
import cwst.com.views.*;

@UIScope
@SpringComponent
public class MenuBarCustomComponent extends MenuBar {

	private static final long serialVersionUID = -387883656726281921L;

	public MenuBarCustomComponent() {
		addStyleName(ValoTheme.MENUBAR_BORDERLESS);
	}

	@SuppressWarnings("serial")
	public void initMenu() {
		this.removeItems();
		if (SecurityUtils.hasRole("ROLE_CARDDISTRIBUTIONVIEW_VIEW") || SecurityUtils.hasRole("ROLE_SUPERADMIN")
				|| SecurityUtils.hasRole("ROLE_HO") || SecurityUtils.hasRole("ROLE_HOCHECKER")
				|| SecurityUtils.hasRole("ROLE_DONVI") || SecurityUtils.hasRole("ROLE_DONVICHECKER")
				|| SecurityUtils.hasRole("ROLE_DVKH")) {

			this.addItem("QUẢN LÝ PHÂN PHỐI THẺ", FontAwesome.CREDIT_CARD, new MenuBar.Command() {

				private static final long serialVersionUID = 1L;

				@Override
				public void menuSelected(MenuItem selectedItem) {
					cmdNavigateToCardDistribution();
				}
			});
		}
		if (SecurityUtils.hasRole("ROLE_USERMANAGERVIEW_VIEW") || SecurityUtils.hasRole("ROLE_SUPERADMIN")) {
			MenuItem miManager = this.addItem("QUẢN LÝ", FontAwesome.GEAR, null);
			miManager.addItem("Người dùng", new MenuBar.Command() {
				private static final long serialVersionUID = 1L;

				@Override
				public void menuSelected(MenuItem selectedItem) {
					cmdUserManager();
				}
			});
		}

		MenuItem miReport = this.addItem("BÁO CÁO", FontAwesome.AREA_CHART, null);
		if (SecurityUtils.hasRole("ROLE_REPORTCARDDISTRIBUTIONVIEW_VIEW") || SecurityUtils.hasRole("ROLE_SUPERADMIN")
				|| SecurityUtils.hasRole("ROLE_HO") || SecurityUtils.hasRole("ROLE_HOCHECKER")
				|| SecurityUtils.hasRole("ROLE_DONVI") || SecurityUtils.hasRole("ROLE_DONVICHECKER")
				|| SecurityUtils.hasRole("ROLE_DVKH")) {
			miReport.addItem("Báo Cáo Quản Lý Phân Phối Thẻ", new MenuBar.Command() {
				@Override
				public void menuSelected(MenuItem selectedItem) {
					cmdNavigateToCardDistributionReport();
				}
			});

		}

		MenuItem brnManager = this.addItem("THÔNG TIN ĐƠN VỊ", FontAwesome.INFO, null);
		if (SecurityUtils.hasRole("ROLE_USERMANAGERVIEW_VIEW") || SecurityUtils.hasRole("ROLE_SUPERADMIN")
				|| SecurityUtils.hasRole("ROLE_HO") || SecurityUtils.hasRole("ROLE_HOCHECKER")
				|| SecurityUtils.hasRole("ROLE_DONVICHECKER")) {
			brnManager.addItem("Thông tin đơn vị", new MenuBar.Command() {
				private static final long serialVersionUID = 1L;

				@Override
				public void menuSelected(MenuItem selectedItem) {
					cmdNavigateUpdateBrn();
				}
			});
		}

		if (SecurityUtils.hasRole("ROLE_REPORTCARDWORKSUPPORTTOOLVIEW_VIEW")
				|| SecurityUtils.hasRole("ROLE_SUPERADMIN")) {
			miReport.addItem("Báo Cáo CWST", new MenuBar.Command() {
				@Override
				public void menuSelected(MenuItem selectedItem) {
					cmdNavigateTCWSTReport();
				}
			});
		}

		String userInfo = "" + SecurityUtils.getUserName().toUpperCase();
		MenuItem miAccount = this.addItem(userInfo, FontAwesome.USER, null);

		this.addItem("THOÁT", FontAwesome.SIGN_OUT, new MenuBar.Command() {
			private static final long serialVersionUID = 1L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				cmdLogout();
			}
		});
	}

	private void cmdLogout() {
		getUI().getSession().close();
		getUI().getPage().reload();
	}

	private void cmdUserManager() {

		if (SecurityUtils.hasRole("ROLE_USERMANAGERVIEW_VIEW") || SecurityUtils.hasRole("ROLE_SUPERADMIN")) {
			getUI().getNavigator().navigateTo(UserManagerView.VIEW_NAME);
		}
	}

	// Chuyen toi man hinh quan ly phan phoi the
	private void cmdNavigateToCardDistribution() {
		getUI().getNavigator().navigateTo(CardDistributionView.VIEW_NAME);
	}

	// Chuyen toi report quan ly phan phoi the
	private void cmdNavigateToCardDistributionReport() {
		String userLogin = SecurityUtils.getUserName();
		if (userLogin.equals("huynhnt")) {
			getUI().getNavigator().navigateTo(ReportCardDistributionView.VIEW_NAME);
			return;
		}
		getUI().getNavigator().navigateTo(ReportCardDistributionView.VIEW_NAME);
	}

	// Chuyen toi report ho tro cardworks
	private void cmdNavigateTCWSTReport() {
		getUI().getNavigator().navigateTo(ReportCardWorkSupportToolView.VIEW_NAME);
	}

	private void cmdNavigateUpdateBrn() {
		getUI().getNavigator().navigateTo(UpdateBranchView.VIEW_NAME);
	}

}
