package dev.parodos.escalationswf;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import javax.inject.Inject;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.index.model.ProcessInstanceState;
import org.kie.kogito.index.storage.DataIndexStorageService;
import org.kie.kogito.persistence.api.Storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;

import dev.parodos.escalationswf.model.CreateResponse;
import dev.parodos.escalationswf.model.EscalationRequest;
import dev.parodos.escalationswf.model.JiraIssue;
import dev.parodos.escalationswf.model.Namespace;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@QuarkusTest
public class EscalationSwfTest {
  private static Logger logger = Logger.getLogger(EscalationSwfTest.class);

  private static WireMockServer jira;
  private static WireMockServer openshift;
  private static WireMockServer mailtrap;

  @Inject
  DataIndexStorageService dataIndexService;
  @Inject
  static ObjectMapper mapper;

  @BeforeAll
  public static void startMockServers() throws JsonProcessingException {
    jira = new WireMockServer(options().port(8181));
    openshift = new WireMockServer(options().port(8282));
    mailtrap = new WireMockServer(options().port(8383));
    jira.start();
    JiraIssue jiraIssue = aJiraIssue();
    jira.stubFor(post("/rest/api/latest/issue").willReturn(
        aResponse().withHeader("Content-Type", "application/json")
            .withBody(new ObjectMapper().writeValueAsString(jiraIssue))
            .withStatus(201)));

    openshift.start();
    Namespace namespace = Namespace.of("NS");
    openshift.stubFor(post("/api/v1/namespaces").willReturn(
        aResponse().withHeader("Content-Type", "application/json")
            .withBody(new ObjectMapper().writeValueAsString(namespace)).withStatus(200)));

    mailtrap.start();
    mailtrap.stubFor(post("/").willReturn(aResponse().withBody("ok").withStatus(200)));
  }

  @AfterAll
  public static void stopMockServers() {
    if (jira != null) {
      jira.stop();
    }
    if (openshift != null) {
      openshift.stop();
    }
    if (mailtrap != null) {
      mailtrap.stop();
    }
  }

  private EscalationRequest aRequest() {
    return new EscalationRequest().setNamespace("NS").setManager("manager@company.com");
  }

  private static JiraIssue aJiraIssue() {
    return new JiraIssue().setKey("PRJ-1")
        .setSelf("https://your-domain.atlassian.net/rest/api/3/issue/10000");
  }

  @Test
  public void when_RequestIsApproved_ThenTheNamespaceIsCreated() {
    CreateResponse createResponse = startRequest();
    String workflowInstanceId = createResponse.getId();
    org.kie.kogito.index.model.ProcessInstance processInstance = readCurrentState(workflowInstanceId);
    assertTrue(isNodeRunning("CreateJiraIssue", processInstance), "CreateJiraIssue is Running");
    sendCompletionCloudEvent(createResponse.getId());
    processInstance = readCurrentState(workflowInstanceId);
    assertTrue(isNodeCompleted("CreateJiraIssue", processInstance), "CreateJiraIssue is Completed");
    assertTrue(isNodeCompleted("CreateK8sNamespace", processInstance), "CreateK8sNamespace is Completed");
    assertEquals(ProcessInstanceState.COMPLETED,
        ProcessInstanceState.fromStatus(processInstance.getState()), "SWF state is COMPLETED");
  }

  private CreateResponse startRequest() {
    EscalationRequest aRequest = aRequest();
    logger.infof("Sending request %s", aRequest);
    ExtractableResponse<Response> response = given()
        .when().contentType("application/json")
        .body(aRequest).post("/ticket-escalation")
        .then()
        .statusCode(201)
        .extract();
    logger.infof("Response is %s", response.asPrettyString());
    CreateResponse createResponse = response.as(CreateResponse.class);
    logger.infof("CreateResponse is %s", createResponse);
    return createResponse;
  }

  private void sendCompletionCloudEvent(String worflowInstanceId) {
    given()
        .header("ce-specversion", "1.0")
        .header("ce-id", UUID.randomUUID().toString())
        .header("ce-source", "jira.listener")
        .header("ce-type", "dev.parodos.escalation")
        .header("ce-kogitoprocrefid", worflowInstanceId)
        .contentType("application/cloudevents+json")
        .body("{\"event\": \"Closed ticket " + worflowInstanceId + "\"}")
        .post("/")
        .then()
        .statusCode(202);

    await()
        .atLeast(1, SECONDS)
        .atMost(2, SECONDS);
  }

  private org.kie.kogito.index.model.ProcessInstance readCurrentState(String worflowInstanceId) {
    logger.infof("Reading status of %s", worflowInstanceId);

    Storage<String, org.kie.kogito.index.model.ProcessInstance> cache = dataIndexService
        .getProcessInstancesCache();
    org.kie.kogito.index.model.ProcessInstance processInstance = cache.get(worflowInstanceId);
    logger.infof("Current status is %s", processInstance);

    return processInstance;
  }

  private boolean isNodeRunning(String nodeName, org.kie.kogito.index.model.ProcessInstance processInstance) {
    return processInstance.getNodes().stream().filter(
        n -> n.getName().equals(nodeName) && n.getEnter() != null && n.getExit() == null)
        .findFirst().isPresent();
  }

  private boolean isNodeCompleted(String nodeName, org.kie.kogito.index.model.ProcessInstance processInstance) {
    return processInstance.getNodes().stream().filter(
        n -> n.getName().equals(nodeName) && n.getEnter() != null && n.getExit() != null)
        .findFirst().isPresent();
  }
}
