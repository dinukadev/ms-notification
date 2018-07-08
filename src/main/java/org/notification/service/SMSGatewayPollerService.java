package org.notification.service;

/**
 *
 * @author dinuka
 *
 */
public interface SMSGatewayPollerService {

	/**
	 * This method will call the REST api on clicksend to retrieve all the
	 * messages in the inbox.
	 */
	void readMessages();
}
