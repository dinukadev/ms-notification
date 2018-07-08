package org.notification.domain;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class SMSInfo {

	private String toNumber;

	public SMSInfo(String toNumber) {
		this.toNumber = toNumber;
	}

	public String getToNumber() {
		return toNumber;
	}
}
