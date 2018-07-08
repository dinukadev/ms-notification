package org.notification.utils;

import java.util.Base64;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatch;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;
import org.notification.domain.EmailAttachmentInfo;
import org.notification.exception.InvalidEmailAttachmentException;

/**
 * 
 * @author dinuka
 *
 */
public class EmailAttachmentValidator {

	/**
	 * This method will check if either the byte stream or the file name is
	 * empty. If that is fine, then it will check if the sent in encoded byte
	 * stream is a valid base 64 encoded string and if not will still throw an
	 * exception.
	 * 
	 * @param attachmentList
	 */
	public static void validateAttachmentContent(List<EmailAttachmentInfo> attachmentList) {
		if (CollectionUtils.isEmpty(attachmentList))
			return;
		for (EmailAttachmentInfo attInfo : attachmentList) {
			if (StringUtils.isEmpty(attInfo.getBase64EncodedByteStream())
					|| StringUtils.isEmpty(attInfo.getFileName())) {
				throw new InvalidEmailAttachmentException("The encoded byte stream or the file name cannot be empty");
			}
			try {
				byte[] byteArr = Base64.getDecoder().decode(attInfo.getBase64EncodedByteStream());
				MagicMatch contentMatcher = Magic.getMagicMatch(byteArr);
				String mimeType = contentMatcher.getMimeType();
				// We set the content type and mime type here so that we do not
				// need to do the same conversions again on the adapter level as
				// it is resource intensive.
				attInfo.setMimeType(mimeType);
				attInfo.setByteArr(byteArr);
			} catch (IllegalArgumentException e) {
				throw new InvalidEmailAttachmentException(
						"The passed in byte stream is not a valid base64 encoded string",e);
			} catch (MagicException | MagicMatchNotFoundException | MagicParseException e) {
				throw new InvalidEmailAttachmentException(
						"The content type cannot be drived based on the byte stream passed in.", e);
			}
		}
	}
}
