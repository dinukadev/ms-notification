package org.notification.utils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import org.notification.exception.InvalidRequestException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.notification.domain.NotificationClientConfig;
import org.notification.repository.NotificationClientConfigMongoRepository;

/**
 * This class will hold common utility methods that is needed by the modules.
 *
 * @author dinuka
 *
 */
@Service
public class CommonUtil {

	@Autowired
	private NotificationClientConfigMongoRepository clientConfigRepo;

	public boolean isCallbackRequiredForClientId(@NotNull final String clientId) {
		NotificationClientConfig clientConfig = clientConfigRepo.findByClientId(clientId);
		return clientConfig != null && clientConfig.isCallbackRequired();
	}

	public String getClientIdFromHTTPHeader(final HttpServletRequest request) {
		String clientId = request.getHeader("Client-Id");
		if (clientId == null || StringUtils.isEmpty(clientId)) {
			throw new InvalidRequestException("ClientId not sent in the request header.");
		}
		return clientId;
	}
}
