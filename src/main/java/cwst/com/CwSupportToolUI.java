package cwst.com;

import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import cwst.com.components.HeaderComponent;
import cwst.com.components.MenuBarCustomComponent;
import cwst.com.components.ThongBao;
import cwst.com.views.AccessDeniedView;
import cwst.com.views.CardDistributionView;
import cwst.com.views.ErrorView;
import cwst.com.views.LoginView;
import org.springframework.beans.factory.annotation.Autowired;

@SpringUI
@Theme("mytheme")
public class CwSupportToolUI extends UI {

	private static final long serialVersionUID = 1L;
	@Autowired
	private SpringViewProvider viewProvider;
	@Autowired
	private ErrorView errorView;

	@Override
	protected void init(VaadinRequest request) {

		final VerticalLayout rootLayout = new VerticalLayout();
		setContent(rootLayout);
		setSizeFull();
		final VerticalLayout contentLayout = new VerticalLayout();
		contentLayout.setSizeFull();
		final MenuBarCustomComponent menuBar = new MenuBarCustomComponent();
		final HeaderComponent header = new HeaderComponent();
		final ThongBao thongbao = new ThongBao();

		Navigator navigator = new Navigator(this, contentLayout);
		navigator.setErrorView(errorView);
		// this.viewProvider.setAccessDeniedViewClass(AccessDeniedView.class);
		navigator.addProvider(this.viewProvider);
		navigator.addViewChangeListener(new ViewChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean beforeViewChange(ViewChangeEvent event) {
				boolean isLoggedIn = SecurityUtils.isLoggedIn();
				boolean isLoginView = event.getNewView() instanceof LoginView;
				if (SecurityUtils.isLoggedIn()) {
					menuBar.setVisible(true);
				} else {
					menuBar.setVisible(false);
				}
				menuBar.initMenu();

				if (!isLoggedIn && !isLoginView) {
					navigator.navigateTo(LoginView.VIEW_NAME);
					return false;
				} else if (isLoggedIn && isLoginView) {
					// menuBar.initMenu();
					// rootLayout.addComponent(menuBar);
					navigator.navigateTo(CardDistributionView.VIEW_NAME);

					return false;
				}
				return true;
			}

			@Override
			public void afterViewChange(ViewChangeEvent event) {

			}

		});

		rootLayout.addComponent(header);
//		rootLayout.addComponent(thongbao);
		rootLayout.addComponent(menuBar);
		rootLayout.addComponent(contentLayout);

	}

}
