package cwst.com.views;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@UIScope
@SpringComponent
public class ErrorView extends VerticalLayout implements View {

	private static final long serialVersionUID = 1L;
	private transient Label lbError;
	//private static final Logger LOGGER = LoggerFactory.getLogger(ErrorView.class);
	private static final Logger LOGGER = LogManager.getLogger(ErrorView.class);

	public ErrorView() {
		setMargin(true);
		lbError = new Label();
		lbError.addStyleName(ValoTheme.LABEL_FAILURE);
		lbError.setSizeUndefined();
		addComponent(lbError);
	}

	@Override
	public void enter(final ViewChangeEvent event) {
		lbError.setValue(String.format("View not found: %s", event.getViewName()));
		LOGGER.error("View not found: " + event.getViewName());
	}
}
