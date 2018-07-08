package org.notification.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * This collection will hold the information about the clients who can make use
 * of ms-notification to send out notifications.
 * 
 * @author dinuka
 *
 */
@Document(collection = "notificationClientConfig")
public class NotificationClientConfig {

	@Id
	private String id;

	private String clientId;

	private boolean isCallbackRequired;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public boolean isCallbackRequired() {
		return isCallbackRequired;
	}

	public void setCallbackRequired(boolean isCallbackRequired) {
		this.isCallbackRequired = isCallbackRequired;
	}

	@Override
	public String toString() {
		return "NotificationClientConfig [id=" + id + ", clientId=" + clientId + ", isCallbackRequired="
				+ isCallbackRequired + "]";
	}

}
