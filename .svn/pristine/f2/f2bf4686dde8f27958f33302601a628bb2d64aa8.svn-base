package cwst.com;

import com.vaadin.data.util.converter.Converter;

import java.util.Locale;

public class StringToDateFormat implements Converter<String, String> {

	/**
	 * Chuyen chuoi tu yyyymmdd sang dd/mm/yyyy
	 */
	private static final long serialVersionUID = 1L;
	private TimeConverter timeConverter = new TimeConverter();

	@Override
	public String convertToModel(String value, Class<? extends String> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		// TODO Auto-generated method stub
		if (value == null) {
			return "";
		}
		return "";

	}

	@Override
	public String convertToPresentation(String value, Class<? extends String> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		return timeConverter.convertStrToDateTime(value);
	}

	@Override
	public Class<String> getModelType() {
		return String.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}

}
