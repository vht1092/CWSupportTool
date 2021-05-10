package cwst.com.components;

import javax.servlet.http.HttpServletRequest;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

@UIScope
@SpringComponent
public class HeaderComponent extends CustomComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HeaderComponent() {
		// A layout structure used for composition
		Panel panel = new Panel("");
		panel.setWidth("100%");
		VerticalLayout panelContent = new VerticalLayout();//#0072c6
		panel.setContent(panelContent);
		
		VaadinRequest vaadinRequest = VaadinService.getCurrentRequest();
		HttpServletRequest httpServletRequest = ((VaadinServletRequest)vaadinRequest).getHttpServletRequest();
		String urlServer = getBaseUrl(httpServletRequest);

		final Label lbHeader = new Label(
				"<div style=\"font-style: normal; box-shadow: 5px 10px 18px #888888; background-color: #1a25ab; width: 100%; height: 105px\";> <div style=\"width: 25%; float: left\"><img class=\"v-icon\" src=\"" + urlServer + "/VAADIN/themes/mytheme/img/logo.png\" style='height: 105px; width: 237px;'  /></div> <div <div style=\"width: 75%;  float: left\"> <center> <span style=\"font-size:24px; line-height: 100px; width: 75%; color:#ffffff;font-weight: bold;text-transform: uppercase;\">QUẢN LÝ PHÂN PHỐI THẺ</span> </center> </div>");
		lbHeader.setContentMode(ContentMode.HTML);
		panelContent.addComponent(lbHeader);
		setCompositionRoot(panel);
		panel.setSizeFull();
		panelContent.setSizeFull();
	}
	
	private String getBaseUrl(HttpServletRequest req) {
	    return req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();
	}
}
