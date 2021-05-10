package cwst.com.views;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;
import cwst.com.CwSupportToolUI;
import cwst.com.SecurityUtils;
import cwst.com.services.MessageByLocalService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
//import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.PostConstruct;

@SpringView(name = LoginView.VIEW_NAME, ui = CwSupportToolUI.class)
public class LoginView extends VerticalLayout implements View {

	private static final long serialVersionUID = 6866257742568730367L;
	public static final String VIEW_NAME = "";
	// private static final Logger LOGGER =
	// LoggerFactory.getLogger(LoginView.class);
	private static final Logger LOGGER = LogManager.getLogger(LoginView.class);
	@Autowired
	private AuthenticationManager authentionManager;
	private TextField txtfUserName;
	private PasswordField pfPassword;
	@Autowired
	private MessageByLocalService messageResource;

	@PostConstruct
	void init() {
		setSpacing(true);
		Page.getCurrent().setTitle(messageResource.getMessage("page.title"));
		final Panel panelLogin = new Panel();

		final Label lbHeader = new Label("</br></br>");
		lbHeader.setContentMode(ContentMode.HTML);
		addComponent(lbHeader);

		panelLogin.setCaption(messageResource.getMessage("login.panel.caption"));
		panelLogin.setStyleName(ValoTheme.PANEL_WELL);
		panelLogin.setWidth(null);
		final VerticalLayout contentLayout = new VerticalLayout();
		contentLayout.setMargin(true);
		contentLayout.setSpacing(true);

		txtfUserName = new TextField(messageResource.getMessage("login.txtfield.username.caption"));
		txtfUserName.setIcon(FontAwesome.USER);
		txtfUserName.setWidth("400px");

		txtfUserName.addValidator(new StringLengthValidator(
				messageResource.getMessage("login.txtfield.username.validate.null"), 1, 20, false));
		txtfUserName.setValidationVisible(false);
		contentLayout.addComponent(txtfUserName);

		pfPassword = new PasswordField(messageResource.getMessage("login.txtfield.password.caption"));
		pfPassword.setWidth("400px");
		pfPassword.setIcon(FontAwesome.LOCK);
		pfPassword.addValidator(new StringLengthValidator(
				messageResource.getMessage("login.txtfield.password.validate.null"), 3, 50, false));
		pfPassword.setValidationVisible(false);
		contentLayout.addComponent(pfPassword);

		final Button btLogin = new Button(messageResource.getMessage("login.button.login"), evt -> {
			txtfUserName.setValidationVisible(false);
			pfPassword.setValidationVisible(false);
			try {
				txtfUserName.validate();
				pfPassword.validate();
			} catch (InvalidValueException e) {
				txtfUserName.setValidationVisible(true);
				pfPassword.setValidationVisible(true);
			}

		});
		btLogin.setStyleName(ValoTheme.BUTTON_PRIMARY);
		btLogin.setIcon(FontAwesome.SIGN_IN);
		btLogin.setClickShortcut(ShortcutAction.KeyCode.ENTER);
		btLogin.addClickListener(event -> cmdLoginButtonClick(event));
		contentLayout.addComponent(btLogin);

		panelLogin.setContent(contentLayout);

		// final Label lbHeader = new
		// Label(messageResource.getMessage("login.label.header"));
		// lbHeader.setContentMode(ContentMode.HTML);
		final Label thongBao = new Label("THÔNG BÁO");
		thongBao.setCaptionAsHtml(true);

		final Label lbFooter = new Label(messageResource.getMessage("login.label.footer"));
		lbFooter.setCaptionAsHtml(true);
		lbFooter.setContentMode(ContentMode.HTML);
		lbFooter.setHeight("100px");
		lbFooter.setStyleName(ValoTheme.TEXTFIELD_ALIGN_CENTER);

		// addComponent(lbHeader);
		// setComponentAlignment(lbHeader, Alignment.TOP_CENTER);
		// setExpandRatio(lbHeader, 1f);
		addComponent(panelLogin);
		setComponentAlignment(panelLogin, Alignment.MIDDLE_CENTER);
		setExpandRatio(panelLogin, 0.5f);
		addComponent(lbFooter);
		setComponentAlignment(lbFooter, Alignment.BOTTOM_CENTER);
		setExpandRatio(lbFooter, 2f);
	}

	@Override
	public void enter(final ViewChangeEvent event) {

	}

	public String getUsername() {
		return txtfUserName.getValue();
	}

	private String getPassword() {
		return pfPassword.getValue();
	}

	private void cmdLoginButtonClick(Button.ClickEvent event) {
		try {
			
			txtfUserName.validate();
			pfPassword.validate();

			Authentication token = authentionManager
					.authenticate(new UsernamePasswordAuthenticationToken(getUsername().toLowerCase(), getPassword()));
			VaadinService.reinitializeSession(VaadinService.getCurrentRequest());
			SecurityContextHolder.getContext().setAuthentication(token);
			LOGGER.info(getUsername() + " login successful");

			/*
			 * if (SecurityUtils.hasRole("ROLE_CARDDISTRIBUTIONVIEW_VIEW") ||
			 * SecurityUtils.hasRole("ROLE_HO") ||
			 * SecurityUtils.hasRole("ROLE_DONVI")) {
			 * getUI().getNavigator().navigateTo(CardDistributionView.VIEW_NAME)
			 * ; }
			 */

			if (SecurityUtils.hasRole("ROLE_DONVI") || SecurityUtils.hasRole("ROLE_DONVICHECKER")
					|| SecurityUtils.hasRole("ROLE_HO") || SecurityUtils.hasRole("ROLE_DVKH")
					|| SecurityUtils.hasRole("ROLE_HOCHECKER") || SecurityUtils.hasRole("ROLE_SUPERADMIN")) {
				getUI().getNavigator().navigateTo(CardDistributionView.VIEW_NAME);
			}

		} catch (InvalidValueException e) {
			txtfUserName.setValidationVisible(true);
			pfPassword.setValidationVisible(true);
		} catch (NullPointerException ex) {
			LOGGER.error(getUsername() + " login - " + ex.getMessage());
			Notification.show("Không có quyền truy cập ứng dụng", Type.ERROR_MESSAGE);
		} catch (Exception ex) {
			LOGGER.error(getUsername() + " login - " + ex.getMessage());
			Notification.show("Đăng nhập không thành công", Type.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}

}
