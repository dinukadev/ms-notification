package org.notification.domain;

/**
 * 
 * @author dinuka
 *
 */
public class EmailAttachmentInfo {

	private String base64EncodedByteStream;

	private String fileName;

	private byte[] byteArr;

	private String mimeType;

	public EmailAttachmentInfo(String base64EncodedByteStream, String fileName) {
		this.base64EncodedByteStream = base64EncodedByteStream;
		this.fileName = fileName;
	}

	public String getBase64EncodedByteStream() {
		return base64EncodedByteStream;
	}

	public String getFileName() {
		return fileName;
	}

	public byte[] getByteArr() {
		return byteArr;
	}

	public void setByteArr(byte[] byteArr) {
		this.byteArr = byteArr;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public void setBase64EncodedByteStream(String base64EncodedByteStream) {
		this.base64EncodedByteStream = base64EncodedByteStream;
	}

}
