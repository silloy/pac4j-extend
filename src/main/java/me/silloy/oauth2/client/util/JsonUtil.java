package me.silloy.oauth2.client.util;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

public class JsonUtil {

  private static final ObjectMapper mapper = new ObjectMapper();

  private JsonUtil() {
    throw new UnsupportedOperationException();
  }

  @SneakyThrows
  public static String toJson(Object obj) {
    mapper.setSerializationInclusion(NON_NULL);
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return mapper.writeValueAsString(obj);
  }

}
