package org.notification.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

public class JsonJodaDateTimeDeserializer extends JsonDeserializer<DateTime> {

  @Override
  public DateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
    DateTimeFormatter isoDateTimeFormatter = DateTimeCommon.getIsoDateTimeFormatter();
    String dateValueAsString = jp.getValueAsString();
    DateTime dateTime = isoDateTimeFormatter.parseDateTime(dateValueAsString);
    return dateTime;
  }
}
