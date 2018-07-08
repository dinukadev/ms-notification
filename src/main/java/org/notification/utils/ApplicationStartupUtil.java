package org.notification.utils;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.notification.constants.MSNotificationConstants.NotificationClientConfigProperties;
import org.notification.domain.NotificationClientConfig;
import org.notification.repository.NotificationClientConfigMongoRepository;

@Service
public class ApplicationStartupUtil {

	@Autowired
	private NotificationClientConfigMongoRepository clientConfigRepo;

	@PostConstruct
	public void init() {
		// At startup, the existing client configuration needs to be purged and
		// the new configuration defined in the property file should be
		// persisted.
		clientConfigRepo.deleteAll();
	}

	/**
	 * This method will read the configuration returned at application start up.
	 * The structure is as follows;
	 * 
	 * clientConfig: ms-test-request:callbackrequired=false
	 * ms-user-management:callbackrequired=true
	 * 
	 * The value of in the map will be a comma separate key value pair of
	 * properties required to be stored against a client id.
	 * 
	 * @param clientConfigInfo
	 */
	public void persistClientConfiguration(@NotNull final Map<String, String> clientConfigInfo) {
		for (String clientId : clientConfigInfo.keySet()) {
			NotificationClientConfig clientConfig = new NotificationClientConfig();
			clientConfig.setClientId(clientId);
			String[] configProperties = clientConfigInfo.get(clientId).split(",");
			if (configProperties.length > 0) {
				for (String configProp : configProperties) {
					String[] keyValPair = configProp.split("=");
					if (keyValPair.length == 2) {
						switch (keyValPair[0]) {
						case NotificationClientConfigProperties.CALLBACK_REQUIRED:
							clientConfig.setCallbackRequired(
									"true".equalsIgnoreCase(StringUtils.trimToNull(keyValPair[1])) ? true : false);
							break;
						}
					}
				}
			}
			clientConfigRepo.insert(clientConfig);
		}
	}
}
