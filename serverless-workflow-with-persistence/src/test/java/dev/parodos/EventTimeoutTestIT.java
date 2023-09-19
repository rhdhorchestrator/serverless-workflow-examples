package dev.parodos;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.jackson.JsonCloudEventData;
import io.cloudevents.jackson.JsonFormat;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

@QuarkusIntegrationTest
public class EventTimeoutTestIT {
  static {
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
  }

  private static final String WORKFLOW_PATH = "/event-timeout-with-persitence";

  /**
   * Event type expected by the event_state_timeouts sw to execute the associated actions.
   */
  private static final String EVENT1_EVENT_TYPE = "event1_event_type";
  /**
   * Event type expected by the event_state_timeouts sw to execute the associated actions.
   */
  private static final String EVENT2_EVENT_TYPE = "event2_event_type";

  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void testCallbackRest() {
    objectMapper.registerModule(JsonFormat.getCloudEventJacksonModule());

    String id = given()
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .post(WORKFLOW_PATH)
        .then()
        .statusCode(201)
        .extract()
        .path("id");
/*
@POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ClientHeaderParam(name = "Content-Type", value = "application/cloudevents+json")
 */
    given()
        .contentType("application/cloudevents+json")
        .accept(ContentType.JSON)
        .body(generateCloudEvent(id, EVENT1_EVENT_TYPE, new Event("Test even 1")))
        .post("/")
        .then()
        .statusCode(202);

    given()
        .contentType("application/cloudevents+json")
        .accept(ContentType.JSON)
        .body(generateCloudEvent(id, EVENT2_EVENT_TYPE, new Event("Test even 2")))
        .post("/")
        .then()
        .statusCode(202);

    await()
        .atLeast(1, SECONDS)
        .atMost(60, SECONDS)
        .with().pollInterval(1, SECONDS)
        .untilAsserted(() -> given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .get(WORKFLOW_PATH + "/{id}", id)
            .then()
            .statusCode(404));
  }

  private String generateCloudEvent(String processInstanceId, String eventType, Event event) {
    try {
      return objectMapper.writeValueAsString(CloudEventBuilder.v1()
          .withId(UUID.randomUUID().toString())
          .withSource(URI.create("events-producer"))
          .withType(eventType)
          .withTime(OffsetDateTime.now())
          .withExtension("kogitoprocrefid", processInstanceId)
          .withDataContentType(MediaType.APPLICATION_JSON)
          .withData(JsonCloudEventData.wrap(objectMapper.createObjectNode().put("eventData", event.getEventData())))
          .build());
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public static class Event {

    private String eventData;

    public Event() {
    }

    public Event(String eventData) {
      this.eventData = eventData;
    }

    public String getEventData() {
      return eventData;
    }

    public void setEventData(String eventData) {
      this.eventData = eventData;
    }
  }
}