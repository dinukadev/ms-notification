package org.notification.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;


public class JsonJodaDateTimeSerializer extends JsonSerializer<DateTime> {

  @Override
  public void serialize(DateTime dateTime, JsonGenerator jsonGenerator,
                        SerializerProvider serializerProvider) throws IOException {
    DateTimeFormatter isoDateTimeFormatter = DateTimeCommon.getIsoDateTimeFormatter();
    jsonGenerator.writeString(isoDateTimeFormatter.print(dateTime));
  }
}
