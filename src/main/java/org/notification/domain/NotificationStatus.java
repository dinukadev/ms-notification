package org.notification.domain;

public enum NotificationStatus {
  SENT {
    @Override
    public String toString() {
      return "Sent";
    }
  },
  FAILED {
    @Override
    public String toString() {
      return "Failed";
    }
  },
  QUEUED {
    @Override
    public String toString() {
      return "Queued";
    }
  },
  CREATED {
    @Override
    public String toString() {
      return "Created";
    }
  },
  RESPONSE_RECEIVED {
    @Override
    public String toString() {
      return "Response Received";
    }
  }
}
