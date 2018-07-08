package org.notification.utils;

/**
 * @author dinuka
 */
public class HtmlUtil {

  public static boolean doesStringContainHtml(final String content) {
    return content.replaceAll("\r\n|\n", "").matches(".*\\<[a-zA-Z]{1,}\\>.*\\</[a-zA-Z]{1,}\\>.*");

  }

}
