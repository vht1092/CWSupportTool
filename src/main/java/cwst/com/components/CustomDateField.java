package cwst.com.components;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomDateField extends CustomField<String> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HorizontalLayout fieldLayout;	
	private DateField dateField;

	@Override
	protected Component initContent() {
		fieldLayout = new HorizontalLayout();
		fieldLayout.setWidth("100%");
		dateField = new DateField();
		dateField.setDateFormat("dd/MM/yyyy hh:mm:ss");
		fieldLayout.addComponent(dateField);
		fieldLayout.setExpandRatio(dateField, 1.0f);
		return fieldLayout;
	}

	@Override
	public Class<String> getType() {
		return String.class;
	}

	@Override
	protected void setInternalValue(String newValue) {
		super.setInternalValue(newValue);
		if (dateField == null) {
			return;
		}
		dateField.setValue(new Date());
	}

	@Override
	public String getInternalValue() {
		if (dateField == null) {
			return "";
		}
		if (dateField.getValue() != null) {
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
			return format.format(dateField.getValue());
		}
		return "";
	}

}
