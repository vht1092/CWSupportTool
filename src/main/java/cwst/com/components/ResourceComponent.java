package cwst.com.components;

import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

//@Component
public class ResourceComponent {
	private static final Logger LOGGER = LoggerFactory.getLogger(ResourceComponent.class);

	public StreamResource getStreamResource(final String nameofreport, final String nameofjasper, final String extension,
			final Map<String, Object> parameter, final String dirreport, final Connection con) throws Exception {
		return new StreamResource(new StreamSource() {
			@Override
			public InputStream getStream() {

				try {
					final ByteArrayOutputStream outpuf = makeFileForDownLoad(nameofjasper, extension, parameter, dirreport, con);
					return new ByteArrayInputStream(outpuf.toByteArray());

				} catch (JRException e) {
					LOGGER.error(e.getMessage());
				} catch (SQLException e) {
					LOGGER.error(e.getMessage());
				}
				return null;

			}
		}, nameofreport + "." + extension);
	}

	private ByteArrayOutputStream makeFileForDownLoad(final String filename, final String extension, final Map<String, Object> parameters,
			String dirreport, Connection con) throws JRException, SQLException {
		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		// Dung file da compile
		final JasperReport jasperReport = (JasperReport) JRLoader.loadObjectFromFile(dirreport + "/" + filename);
		// Load file XML va compile
		// final JasperReport jasperReport = JassperCompileManager.compileReport(sDirReport + "/" + filename);
		final JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, con);

		// Xuat file Excel
		if (extension.equals("xlsx")) {
			final JRXlsxExporter xlsx = new JRXlsxExporter();
			xlsx.setExporterInput(new SimpleExporterInput(jasperPrint));
			xlsx.setExporterOutput(new SimpleOutputStreamExporterOutput(output));
			xlsx.exportReport();
		} else if (extension.equals("pdf")) { // File PDF
			JasperExportManager.exportReportToPdfStream(jasperPrint, output);
		}
		return output;

	}

}
