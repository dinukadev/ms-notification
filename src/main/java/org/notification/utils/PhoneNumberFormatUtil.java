package org.notification.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import org.notification.exception.NotificationException;

/**
 * 
 * @author dinuka
 *
 */
public class PhoneNumberFormatUtil {

	/**
	 * This method will convert the passed in number to the E164 standard.
	 * 
	 * @param numberToFormat
	 * @return
	 */
	public static String formatNumberToAUE164Standard(final String numberToFormat) {
		String noToFormat = numberToFormat;
		String formattedNumber = numberToFormat;
		PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
		if (StringUtils.isNotEmpty(noToFormat) && !noToFormat.startsWith("+")) {
			// This will format the number 0061457216435 to +61457216435
			if (noToFormat.startsWith("00")) {
				Pattern numerPattern = Pattern.compile("^(00)(.*)");
				Matcher numberMatcher = numerPattern.matcher(numberToFormat);
				if (numberMatcher.matches() && numberMatcher.groupCount() >= 2) {
					formattedNumber = "+" + numberMatcher.group(2);
				}
			} else {
				// Before sending the SMS, we need to format it to the E164
				// standard
				// which we do using a Google library which is also used in
				// Android.
				try {
					PhoneNumber phoneNumber = phoneNumberUtil.parse(numberToFormat, "AU");
					formattedNumber = phoneNumberUtil.format(phoneNumber, PhoneNumberFormat.E164);
				} catch (NumberParseException e) {
					throw new NotificationException("Invalid recipient phone number passed in");
				}
			}
		}
		return formattedNumber;
	}

}
