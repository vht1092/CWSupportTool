package cwst.com.components;

import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FileUploader implements Receiver, SucceededListener {

	private static final long serialVersionUID = 1L;
	private File file = null;
	private String filename;
	private String sFileStoreDir = "..";
	private OutputStream output = null;
	private static final Logger LOGGER = LoggerFactory.getLogger(FileUploader.class);

	public String getFileStoreDir() {
		return sFileStoreDir;
	}

	public void setFileStoreDir(String dir) {
		this.sFileStoreDir = dir;
	}

	public String getFilename() {
		return filename;
	}

	@Override
	public void uploadSucceeded(SucceededEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		final String sExtension = filename.substring(filename.lastIndexOf(".")).toLowerCase();
		this.filename = RandomStringUtils.random(16, "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz") + sExtension;
		file = new File(this.sFileStoreDir + "/" + this.filename);
		if (!file.exists()) {
			try {
				file.createNewFile();
				output = new FileOutputStream(file);
			} catch (IOException e) {
				LOGGER.error("Khong the ghi file: " + this.filename + " - Message: " + e.getMessage());
			}
		}
		return output;
	}

}
