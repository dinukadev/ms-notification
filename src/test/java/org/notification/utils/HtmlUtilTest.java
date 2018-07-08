package org.notification.utils;

import org.junit.Assert;
import org.junit.Test;

public class HtmlUtilTest {

	@Test
	public void testHtmlContent() {
		String htmlContent = "<html><body>dsf</body></html>";
		Assert.assertTrue(HtmlUtil.doesStringContainHtml(htmlContent));
	}

	@Test
	public void testNonHtmlContent() {
		String htmlContent = "plain test";
		Assert.assertFalse(HtmlUtil.doesStringContainHtml(htmlContent));
	}

}
