package org.camunda.bpm.engine.rest;

import static com.jayway.restassured.RestAssured.given;
import static org.camunda.bpm.engine.rest.helper.MockProvider.createMockBatch;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response.Status;

import org.camunda.bpm.engine.AuthorizationException;
import org.camunda.bpm.engine.BadUserRequestException;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.batch.Batch;
import org.camunda.bpm.engine.impl.ProcessInstanceQueryImpl;
import org.camunda.bpm.engine.rest.dto.runtime.ProcessInstanceQueryDto;
import org.camunda.bpm.engine.rest.exception.InvalidRequestException;
import org.camunda.bpm.engine.rest.util.ModificationInstructionBuilder;
import org.camunda.bpm.engine.rest.util.container.TestContainerRule;
import org.camunda.bpm.engine.runtime.ModificationBuilder;
import org.camunda.bpm.engine.runtime.ProcessInstanceQuery;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import com.jayway.restassured.http.ContentType;

public class ModificationRestServiceInteractionTest extends AbstractRestServiceTest {

  @ClassRule
  public static TestContainerRule rule = new TestContainerRule();

  protected static final String PROCESS_INSTANCE_URL = TEST_RESOURCE_ROOT_PATH + "/process-instance";
  protected static final String EXECUTE_MODIFICATION_SYNC_URL  = PROCESS_INSTANCE_URL + "/modification";
  protected static final String EXECUTE_MODIFICATION_ASYNC_URL = PROCESS_INSTANCE_URL + "/modification-async";

  protected RuntimeService runtimeServiceMock;
  protected ModificationBuilder modificationBuilderMock;

  @Before
  public void setUpRuntimeData() {
    runtimeServiceMock = mock(RuntimeService.class);
    when(processEngine.getRuntimeService()).thenReturn(runtimeServiceMock);

    modificationBuilderMock = mock(ModificationBuilder.class);
    when(modificationBuilderMock.cancelAllForActivity(anyString())).thenReturn(modificationBuilderMock);
    when(modificationBuilderMock.startAfterActivity(anyString())).thenReturn(modificationBuilderMock);
    when(modificationBuilderMock.startBeforeActivity(anyString())).thenReturn(modificationBuilderMock);
    when(modificationBuilderMock.startTransition(anyString())).thenReturn(modificationBuilderMock);
    when(modificationBuilderMock.processInstanceIds(anyListOf(String.class))).thenReturn(modificationBuilderMock);

    Batch batchMock = createMockBatch();
    when(modificationBuilderMock.executeAsync()).thenReturn(batchMock);

    when(runtimeServiceMock.createModification(anyString())).thenReturn(modificationBuilderMock);
  }

  @Test
  public void executeModificationSync() {
    Map<String, Object> json = new HashMap<String, Object>();
    json.put("skipCustomListeners", true);
    json.put("skipIoMappings", true);
    json.put("processDefinitionId", "processDefinitionId");
    json.put("processInstanceIds", Arrays.asList("100", "20"));
    List<Map<String, Object>> instructions = new ArrayList<Map<String, Object>>();

    instructions.add(ModificationInstructionBuilder.cancellation().activityId("activityId").getJson());
    instructions.add(ModificationInstructionBuilder.startBefore().activityId("activityId").getJson());
    instructions.add(ModificationInstructionBuilder.startAfter().activityId("activityId").getJson());
    instructions.add(ModificationInstructionBuilder.startTransition().transitionId("transitionId").getJson());

    json.put("instructions", instructions);

    given()
      .contentType(ContentType.JSON)
      .body(json)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
      .when()
        .post(EXECUTE_MODIFICATION_SYNC_URL);

    verify(runtimeServiceMock).createModification("processDefinitionId");
    verify(modificationBuilderMock).cancelAllForActivity("activityId");
    verify(modificationBuilderMock).startBeforeActivity("activityId");
    verify(modificationBuilderMock).startAfterActivity("activityId");
    verify(modificationBuilderMock).startTransition("transitionId");
    verify(modificationBuilderMock).skipCustomListeners();
    verify(modificationBuilderMock).skipIoMappings();
    verify(modificationBuilderMock).execute();
  }

  @Test
  public void executeModificationWithNullProcessDefinitionIdAsync() {
    doThrow(new BadUserRequestException("processDefinitionId must be set")).when(modificationBuilderMock).executeAsync();

    Map<String, Object> json = new HashMap<String, Object>();
    json.put("skipCustomListeners", true);
    json.put("skipIoMappings", true);
    json.put("processInstanceIds", Arrays.asList("100", "20"));
    List<Map<String, Object>> instructions = new ArrayList<Map<String, Object>>();

    instructions.add(ModificationInstructionBuilder.cancellation().activityId("activityId").getJson());
    instructions.add(ModificationInstructionBuilder.startBefore().activityId("activityId").getJson());
    instructions.add(ModificationInstructionBuilder.startAfter().activityId("activityId").getJson());
    instructions.add(ModificationInstructionBuilder.startTransition().transitionId("transitionId").getJson());

    json.put("instructions", instructions);

    given()
      .contentType(ContentType.JSON)
      .body(json)
    .then()
      .expect()
        .statusCode(Status.BAD_REQUEST.getStatusCode())
      .when()
        .post(EXECUTE_MODIFICATION_ASYNC_URL);

    verify(modificationBuilderMock).cancelAllForActivity("activityId");
    verify(modificationBuilderMock).startBeforeActivity("activityId");
    verify(modificationBuilderMock).startAfterActivity("activityId");
    verify(modificationBuilderMock).startTransition("transitionId");
    verify(modificationBuilderMock).skipCustomListeners();
    verify(modificationBuilderMock).skipIoMappings();
    verify(modificationBuilderMock).executeAsync();
  }

  @Test
  public void executeModificationWithNullProcessDefinitionIdSync() {
    doThrow(new BadUserRequestException("processDefinitionId must be set")).when(modificationBuilderMock).execute();

    Map<String, Object> json = new HashMap<String, Object>();
    json.put("skipCustomListeners", true);
    json.put("skipIoMappings", true);
    json.put("processInstanceIds", Arrays.asList("100", "20"));
    List<Map<String, Object>> instructions = new ArrayList<Map<String, Object>>();

    instructions.add(ModificationInstructionBuilder.cancellation().activityId("activityId").getJson());
    instructions.add(ModificationInstructionBuilder.startBefore().activityId("activityId").getJson());
    instructions.add(ModificationInstructionBuilder.startAfter().activityId("activityId").getJson());
    instructions.add(ModificationInstructionBuilder.startTransition().transitionId("transitionId").getJson());

    json.put("instructions", instructions);

    given()
      .contentType(ContentType.JSON)
      .body(json)
    .then()
      .expect()
        .statusCode(Status.BAD_REQUEST.getStatusCode())
      .when()
        .post(EXECUTE_MODIFICATION_SYNC_URL);

    verify(modificationBuilderMock).cancelAllForActivity("activityId");
    verify(modificationBuilderMock).startBeforeActivity("activityId");
    verify(modificationBuilderMock).startAfterActivity("activityId");
    verify(modificationBuilderMock).startTransition("transitionId");
    verify(modificationBuilderMock).skipCustomListeners();
    verify(modificationBuilderMock).skipIoMappings();
    verify(modificationBuilderMock).execute();
  }

  @Test
  public void executeModificationWithNullProcessInstanceIdsSync() {
    Map<String, Object> json = new HashMap<String, Object>();
    String message = "Process instance ids is null";
    doThrow(new BadUserRequestException(message))
    .when(modificationBuilderMock).execute();

    List<Map<String, Object>> instructions = new ArrayList<Map<String, Object>>();
    instructions.add(ModificationInstructionBuilder.startAfter().activityId(EXAMPLE_ACTIVITY_ID).getJson());
    instructions.add(ModificationInstructionBuilder.startTransition().transitionId("transitionId").getJson());
    json.put("processDefinitionId", "processDefinitionId");
    json.put("instructions", instructions);

    given()
      .contentType(POST_JSON_CONTENT_TYPE)
      .body(json)
    .then().expect()
      .statusCode(Status.BAD_REQUEST.getStatusCode())
      .body("message", is(message))
    .when()
      .post(EXECUTE_MODIFICATION_SYNC_URL);
  }

  @Test
  public void executeModificationAsync() {
    Map<String, Object> json = new HashMap<String, Object>();
    List<Map<String, Object>> instructions = new ArrayList<Map<String, Object>>();
    instructions.add(ModificationInstructionBuilder.startTransition().transitionId("transitionId").getJson());
    instructions.add(ModificationInstructionBuilder.startBefore().activityId("activityId").getJson());
    instructions.add(ModificationInstructionBuilder.startAfter().activityId("activityId").getJson());
    instructions.add(ModificationInstructionBuilder.cancellation().activityId("activityId").getJson());
    json.put("processDefinitionId", "processDefinitionId");
    json.put("instructions", instructions);

    given()
    .contentType(POST_JSON_CONTENT_TYPE)
    .body(json)
    .then()
    .expect()
    .statusCode(Status.OK.getStatusCode())
    .when()
    .post(EXECUTE_MODIFICATION_ASYNC_URL);

    verify(runtimeServiceMock).createModification("processDefinitionId");
    verify(modificationBuilderMock).cancelAllForActivity("activityId");
    verify(modificationBuilderMock).startBeforeActivity("activityId");
    verify(modificationBuilderMock).startAfterActivity("activityId");
    verify(modificationBuilderMock).startTransition("transitionId");
    verify(modificationBuilderMock).executeAsync();
  }

  @Test
  public void executeModificationWithNullProcessInstanceIdsAsync() {
    Map<String, Object> json = new HashMap<String, Object>();

    String message = "Process instance ids is null";
    doThrow(new BadUserRequestException(message))
    .when(modificationBuilderMock).executeAsync();

    List<Map<String, Object>> instructions = new ArrayList<Map<String, Object>>();
    instructions.add(ModificationInstructionBuilder.startAfter().activityId(EXAMPLE_ACTIVITY_ID).getJson());
    instructions.add(ModificationInstructionBuilder.startTransition().transitionId("transitionId").getJson());

    json.put("instructions", instructions);

    given()
      .contentType(POST_JSON_CONTENT_TYPE)
      .body(json)
    .then().expect()
      .statusCode(Status.BAD_REQUEST.getStatusCode())
      .body("message", is(message))
    .when()
      .post(EXECUTE_MODIFICATION_ASYNC_URL);
  }

  @Test
  public void executeModificationWithValidProcessInstanceQuerySync() {

    when(runtimeServiceMock.createProcessInstanceQuery()).thenReturn(new ProcessInstanceQueryImpl());
    when(modificationBuilderMock.processInstanceQuery(any(ProcessInstanceQuery.class))).thenReturn(modificationBuilderMock);
    Map<String, Object> json = new HashMap<String, Object>();

    List<Map<String, Object>> instructions = new ArrayList<Map<String, Object>>();
    instructions.add(ModificationInstructionBuilder.startAfter().activityId("acivityId").getJson());
    json.put("processDefinitionId", "processDefinitionId");

    ProcessInstanceQueryDto processInstanceQuery = new ProcessInstanceQueryDto();
    json.put("processInstanceQuery", processInstanceQuery);
    json.put("instructions", instructions);

    given()
      .contentType(ContentType.JSON)
      .body(json)
    .then().expect()
      .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(EXECUTE_MODIFICATION_SYNC_URL);
  }

  @Test
  public void executeModificationWithValidProcessInstanceQueryAsync() {

    when(runtimeServiceMock.createProcessInstanceQuery()).thenReturn(new ProcessInstanceQueryImpl());
    when(modificationBuilderMock.processInstanceQuery(any(ProcessInstanceQuery.class))).thenReturn(modificationBuilderMock);
    Map<String, Object> json = new HashMap<String, Object>();

    List<Map<String, Object>> instructions = new ArrayList<Map<String, Object>>();
    instructions.add(ModificationInstructionBuilder.startAfter().activityId("acivityId").getJson());

    ProcessInstanceQueryDto processInstanceQuery = new ProcessInstanceQueryDto();
    json.put("processInstanceQuery", processInstanceQuery);
    json.put("instructions", instructions);
    json.put("processDefinitionId", "processDefinitionId");

    given()
      .contentType(ContentType.JSON)
      .body(json)
    .then().expect()
      .statusCode(Status.OK.getStatusCode())
    .when()
      .post(EXECUTE_MODIFICATION_ASYNC_URL);
  }

  @Test
  public void executeModificationWithInvalidProcessInstanceQuerySync() {

    when(runtimeServiceMock.createProcessInstanceQuery()).thenReturn(new ProcessInstanceQueryImpl());
    when(modificationBuilderMock.processInstanceQuery(any(ProcessInstanceQuery.class))).thenReturn(modificationBuilderMock);
    Map<String, Object> json = new HashMap<String, Object>();

    String message = "Process instance ids is null";
    doThrow(new BadUserRequestException(message))
      .when(modificationBuilderMock).execute();

    List<Map<String, Object>> instructions = new ArrayList<Map<String, Object>>();
    instructions.add(ModificationInstructionBuilder.startAfter().activityId("acivityId").getJson());

    ProcessInstanceQueryDto processInstanceQuery = new ProcessInstanceQueryDto();
    json.put("processInstanceQuery", processInstanceQuery);
    json.put("instructions", instructions);
    json.put("processDefinitionId", "processDefinitionId");

    given()
      .contentType(ContentType.JSON)
      .body(json)
    .then().expect()
      .statusCode(Status.BAD_REQUEST.getStatusCode())
    .when()
      .post(EXECUTE_MODIFICATION_SYNC_URL);
  }

  @Test
  public void executeModificationWithInvalidProcessInstanceQueryAsync() {

    when(runtimeServiceMock.createProcessInstanceQuery()).thenReturn(new ProcessInstanceQueryImpl());
    when(modificationBuilderMock.processInstanceQuery(any(ProcessInstanceQuery.class))).thenReturn(modificationBuilderMock);
    Map<String, Object> json = new HashMap<String, Object>();

    List<Map<String, Object>> instructions = new ArrayList<Map<String, Object>>();
    instructions.add(ModificationInstructionBuilder.startAfter().activityId("acivityId").getJson());

    ProcessInstanceQueryDto processInstanceQuery = new ProcessInstanceQueryDto();
    json.put("processInstanceQuery", processInstanceQuery);
    json.put("instructions", instructions);
    json.put("processDefinitionId", "processDefinitionId");

    given()
      .contentType(ContentType.JSON)
      .body(json)
    .then().expect()
      .statusCode(Status.OK.getStatusCode())
    .when()
      .post(EXECUTE_MODIFICATION_ASYNC_URL);
  }

  @Test
  public void executeModificationWithNullInstructionsSync() {
    doThrow(new BadUserRequestException("Instructions must be set")).when(modificationBuilderMock).execute();;

    Map<String, Object> json = new HashMap<String, Object>();
    json.put("processInstanceIds", Arrays.asList("200", "11"));
    json.put("skipIoMappings", true);
    json.put("processDefinitionId", "processDefinitionId");

    given()
      .contentType(ContentType.JSON)
      .body(json)
    .then().expect()
      .statusCode(Status.BAD_REQUEST.getStatusCode())
      .body("type", equalTo(InvalidRequestException.class.getSimpleName()))
      .body("message", equalTo("Instructions must be set"))
    .when()
      .post(EXECUTE_MODIFICATION_SYNC_URL);
  }

  @Test
  public void executeModificationWithNullInstructionsAsync() {
    doThrow(new BadUserRequestException("Instructions must be set")).when(modificationBuilderMock).executeAsync();
    Map<String, Object> json = new HashMap<String, Object>();
    json.put("processInstanceIds", Arrays.asList("200", "11"));
    json.put("skipIoMappings", true);
    json.put("processDefinitionId", "processDefinitionId");

    given()
      .contentType(ContentType.JSON)
      .body(json)
    .then().expect()
      .statusCode(Status.BAD_REQUEST.getStatusCode())
      .body("type", equalTo(InvalidRequestException.class.getSimpleName()))
      .body("message", equalTo("Instructions must be set"))
    .when()
      .post(EXECUTE_MODIFICATION_ASYNC_URL);
  }

  @Test
  public void executeModificationThrowsAuthorizationException() {
    String message = "expected exception";
    doThrow(new AuthorizationException(message)).when(modificationBuilderMock).executeAsync();

    Map<String, Object> json = new HashMap<String, Object>();

    List<Map<String, Object>> instructions = new ArrayList<Map<String, Object>>();

    instructions.add(
        ModificationInstructionBuilder.startBefore()
          .activityId("activityId")
          .getJson());
    instructions.add(
        ModificationInstructionBuilder.startAfter()
          .activityId("activityId")
          .getJson());

    json.put("instructions", instructions);
    json.put("processInstanceIds", Arrays.asList("200", "323"));
    json.put("processDefinitionId", "processDefinitionId");

    given()
      .contentType(ContentType.JSON)
      .body(json)
    .then().expect()
      .statusCode(Status.FORBIDDEN.getStatusCode())
      .contentType(ContentType.JSON)
      .body("type", equalTo(AuthorizationException.class.getSimpleName()))
      .body("message", equalTo(message))
    .when()
      .post(EXECUTE_MODIFICATION_ASYNC_URL);
  }

  @Test
  public void executeInvalidModificationForStartAfterSync() {
    Map<String, Object> json = new HashMap<String, Object>();
    List<Map<String, Object>> instructions = new ArrayList<Map<String, Object>>();

    json.put("skipIoMappings", true);
    json.put("processInstanceIds", Arrays.asList("200", "100"));
    instructions.add(ModificationInstructionBuilder.startAfter().getJson());
    json.put("instructions", instructions);
    json.put("processDefinitionId", "processDefinitionId");

    given()
      .contentType(ContentType.JSON)
      .body(json)
    .then().expect()
      .statusCode(Status.BAD_REQUEST.getStatusCode())
      .contentType(ContentType.JSON)
      .body("type", equalTo(InvalidRequestException.class.getSimpleName()))
      .body("message", equalTo("For instruction type 'startAfterActivity': 'activityId' must be set"))
    .when()
      .post(EXECUTE_MODIFICATION_SYNC_URL);
  }

  @Test
  public void executeInvalidModificationForStartAfterAsync() {
    Map<String, Object> json = new HashMap<String, Object>();
    List<Map<String, Object>> instructions = new ArrayList<Map<String, Object>>();

    json.put("skipIoMappings", true);
    json.put("processInstanceIds", Arrays.asList("200", "100"));
    instructions.add(ModificationInstructionBuilder.startAfter().getJson());
    json.put("instructions", instructions);
    json.put("processDefinitionId", "processDefinitionId");

    given()
      .contentType(ContentType.JSON)
      .body(json)
    .then().expect()
      .statusCode(Status.BAD_REQUEST.getStatusCode())
      .contentType(ContentType.JSON)
      .body("type", equalTo(InvalidRequestException.class.getSimpleName()))
      .body("message", equalTo("For instruction type 'startAfterActivity': 'activityId' must be set"))
    .when()
      .post(EXECUTE_MODIFICATION_ASYNC_URL);
  }

  @Test
  public void executeInvalidModificationForStartBeforeSync() {
    Map<String, Object> json = new HashMap<String, Object>();
    List<Map<String, Object>> instructions = new ArrayList<Map<String, Object>>();

    json.put("skipIoMappings", true);
    json.put("processInstanceIds", Arrays.asList("200", "100"));
    instructions.add(ModificationInstructionBuilder.startBefore().getJson());
    json.put("instructions", instructions);
    json.put("processDefinitionId", "processDefinitionId");

    given()
      .contentType(ContentType.JSON)
      .body(json)
    .then().expect()
      .statusCode(Status.BAD_REQUEST.getStatusCode())
      .contentType(ContentType.JSON)
      .body("type", equalTo(InvalidRequestException.class.getSimpleName()))
      .body("message", equalTo("For instruction type 'startBeforeActivity': 'activityId' must be set"))
    .when()
      .post(EXECUTE_MODIFICATION_SYNC_URL);
  }

  @Test
  public void executeInvalidModificationForStartBeforeAsync() {
    Map<String, Object> json = new HashMap<String, Object>();
    List<Map<String, Object>> instructions = new ArrayList<Map<String, Object>>();

    json.put("skipIoMappings", true);
    json.put("processInstanceIds", Arrays.asList("200", "100"));
    instructions.add(ModificationInstructionBuilder.startBefore().getJson());
    json.put("instructions", instructions);
    json.put("processDefinitionId", "processDefinitionId");

    given()
      .contentType(ContentType.JSON)
      .body(json)
    .then().expect()
      .statusCode(Status.BAD_REQUEST.getStatusCode())
      .contentType(ContentType.JSON)
      .body("type", equalTo(InvalidRequestException.class.getSimpleName()))
      .body("message", equalTo("For instruction type 'startBeforeActivity': 'activityId' must be set"))
    .when()
      .post(EXECUTE_MODIFICATION_ASYNC_URL);
  }

  @Test
  public void executeInvalidModificationForStartTransitionSync() {
    Map<String, Object> json = new HashMap<String, Object>();
    List<Map<String, Object>> instructions = new ArrayList<Map<String, Object>>();

    json.put("skipIoMappings", true);
    json.put("processInstanceIds", Arrays.asList("200", "100"));
    instructions.add(ModificationInstructionBuilder.startTransition().getJson());
    json.put("instructions", instructions);
    json.put("processDefinitionId", "processDefinitionId");

    given()
      .contentType(ContentType.JSON)
      .body(json)
    .then().expect()
      .statusCode(Status.BAD_REQUEST.getStatusCode())
      .contentType(ContentType.JSON)
      .body("type", equalTo(InvalidRequestException.class.getSimpleName()))
      .body("message", equalTo("For instruction type 'startTransition': 'transitionId' must be set"))
    .when()
      .post(EXECUTE_MODIFICATION_SYNC_URL);
  }

  @Test
  public void executeInvalidModificationForStartTransitionAsync() {
    Map<String, Object> json = new HashMap<String, Object>();
    List<Map<String, Object>> instructions = new ArrayList<Map<String, Object>>();

    json.put("skipIoMappings", true);
    json.put("processInstanceIds", Arrays.asList("200", "100"));
    instructions.add(ModificationInstructionBuilder.startTransition().getJson());
    json.put("instructions", instructions);
    json.put("processDefinitionId", "processDefinitionId");

    given()
      .contentType(ContentType.JSON)
      .body(json)
    .then().expect()
      .statusCode(Status.BAD_REQUEST.getStatusCode())
      .contentType(ContentType.JSON)
      .body("type", equalTo(InvalidRequestException.class.getSimpleName()))
      .body("message", equalTo("For instruction type 'startTransition': 'transitionId' must be set"))
    .when()
      .post(EXECUTE_MODIFICATION_ASYNC_URL);
  }

  @Test
  public void executeInvalidModificationForCancelAllSync() {
    Map<String, Object> json = new HashMap<String, Object>();
    List<Map<String, Object>> instructions = new ArrayList<Map<String, Object>>();

    json.put("skipIoMappings", true);
    json.put("processInstanceIds", Arrays.asList("200", "100"));
    instructions.add(ModificationInstructionBuilder.cancellation().getJson());
    json.put("instructions", instructions);
    json.put("processDefinitionId", "processDefinitionId");

    given()
      .contentType(ContentType.JSON)
      .body(json)
    .then().expect()
      .statusCode(Status.BAD_REQUEST.getStatusCode())
      .contentType(ContentType.JSON)
      .body("type", equalTo(InvalidRequestException.class.getSimpleName()))
      .body("message", equalTo("For instruction type 'cancel': 'activityId' must be set"))
    .when()
      .post(EXECUTE_MODIFICATION_SYNC_URL);
  }

  @Test
  public void executeInvalidModificationForCancelAllAsync() {
    Map<String, Object> json = new HashMap<String, Object>();
    List<Map<String, Object>> instructions = new ArrayList<Map<String, Object>>();

    json.put("skipIoMappings", true);
    json.put("processInstanceIds", Arrays.asList("200", "100"));
    instructions.add(ModificationInstructionBuilder.cancellation().getJson());
    json.put("instructions", instructions);
    json.put("processDefinitionId", "processDefinitionId");

    given()
      .contentType(ContentType.JSON)
      .body(json)
    .then().expect()
      .statusCode(Status.BAD_REQUEST.getStatusCode())
      .contentType(ContentType.JSON)
      .body("type", equalTo(InvalidRequestException.class.getSimpleName()))
      .body("message", equalTo("For instruction type 'cancel': 'activityId' must be set"))
    .when()
      .post(EXECUTE_MODIFICATION_ASYNC_URL);
  }

}
