/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.engine.rest;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.apache.wink.common.RestException;
import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.rest.dto.runtime.VariableNameDto;
import org.camunda.bpm.engine.rest.exception.InvalidRequestException;
import org.camunda.bpm.engine.rest.helper.MockProvider;
import org.camunda.bpm.engine.rest.util.VariablesBuilder;
import org.camunda.bpm.engine.runtime.CaseExecution;
import org.camunda.bpm.engine.runtime.CaseExecutionCommandBuilder;
import org.camunda.bpm.engine.runtime.CaseExecutionQuery;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;

/**
*
* @author Roman Smirnov
*
*/
public class AbstractCaseExecutionRestServiceInteractionTest extends AbstractRestServiceTest {

  protected static final String CASE_EXECUTION_URL = TEST_RESOURCE_ROOT_PATH + "/case-execution";
  protected static final String SINGLE_CASE_EXECUTION_URL = CASE_EXECUTION_URL + "/{id}";

  protected static final String CASE_EXECUTION_MANUAL_START_URL = SINGLE_CASE_EXECUTION_URL + "/manual-start";
  protected static final String CASE_EXECUTION_REENABLE_URL = SINGLE_CASE_EXECUTION_URL + "/reenable";
  protected static final String CASE_EXECUTION_DISABLE_URL = SINGLE_CASE_EXECUTION_URL + "/disable";
  protected static final String CASE_EXECUTION_COMPLETE_URL = SINGLE_CASE_EXECUTION_URL + "/complete";

  protected static final String CASE_EXECUTION_LOCAL_VARIABLES_URL = SINGLE_CASE_EXECUTION_URL + "/localVariables";
  protected static final String CASE_EXECUTION_VARIABLES_URL = SINGLE_CASE_EXECUTION_URL + "/variables";
  protected static final String SINGLE_CASE_EXECUTION_LOCAL_VARIABLE_URL = CASE_EXECUTION_LOCAL_VARIABLES_URL + "/{varId}";
  protected static final String SINGLE_CASE_EXECUTION_VARIABLE_URL = CASE_EXECUTION_VARIABLES_URL + "/{varId}";
  protected static final String SINGLE_CASE_EXECUTION_LOCAL_VARIABLE_DATA_URL = SINGLE_CASE_EXECUTION_LOCAL_VARIABLE_URL + "/data";
  protected static final String SINGLE_CASE_EXECUTION_VARIABLE_DATA_URL = SINGLE_CASE_EXECUTION_VARIABLE_URL + "/data";

  private CaseService caseServiceMock;
  private CaseExecutionQuery caseExecutionQueryMock;
  private CaseExecutionCommandBuilder caseExecutionCommandBuilderMock;

  @Before
  public void setUpRuntime() {
    CaseExecution mockCaseExecution = MockProvider.createMockCaseExecution();

    caseServiceMock = mock(CaseService.class);

    when(processEngine.getCaseService()).thenReturn(caseServiceMock);

    caseExecutionQueryMock = mock(CaseExecutionQuery.class);

    when(caseServiceMock.createCaseExecutionQuery()).thenReturn(caseExecutionQueryMock);
    when(caseExecutionQueryMock.caseExecutionId(MockProvider.EXAMPLE_CASE_EXECUTION_ID)).thenReturn(caseExecutionQueryMock);
    when(caseExecutionQueryMock.singleResult()).thenReturn(mockCaseExecution);

    when(caseServiceMock.getVariable(anyString(), anyString())).thenReturn(EXAMPLE_VARIABLE_VALUE);
    when(caseServiceMock.getVariables(anyString())).thenReturn(EXAMPLE_VARIABLES);

    when(caseServiceMock.getVariableLocal(anyString(), anyString())).thenReturn(EXAMPLE_VARIABLE_VALUE);
    when(caseServiceMock.getVariablesLocal(anyString())).thenReturn(EXAMPLE_VARIABLES);

    when(caseServiceMock.getVariables(anyString(), Matchers.<Collection<String>>any())).thenReturn(EXAMPLE_VARIABLES);
    when(caseServiceMock.getVariablesLocal(anyString(), Matchers.<Collection<String>>any())).thenReturn(EXAMPLE_VARIABLES);

    caseExecutionCommandBuilderMock = mock(CaseExecutionCommandBuilder.class);

    when(caseServiceMock.withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID)).thenReturn(caseExecutionCommandBuilderMock);

    when(caseExecutionCommandBuilderMock.setVariable(anyString(), any())).thenReturn(caseExecutionCommandBuilderMock);
    when(caseExecutionCommandBuilderMock.setVariableLocal(anyString(), any())).thenReturn(caseExecutionCommandBuilderMock);
    when(caseExecutionCommandBuilderMock.setVariables(Matchers.<Map<String, Object>>any())).thenReturn(caseExecutionCommandBuilderMock);
    when(caseExecutionCommandBuilderMock.setVariablesLocal(Matchers.<Map<String, Object>>any())).thenReturn(caseExecutionCommandBuilderMock);

    when(caseExecutionCommandBuilderMock.removeVariable(anyString())).thenReturn(caseExecutionCommandBuilderMock);
    when(caseExecutionCommandBuilderMock.removeVariableLocal(anyString())).thenReturn(caseExecutionCommandBuilderMock);
    when(caseExecutionCommandBuilderMock.removeVariables(Matchers.<Collection<String>>any())).thenReturn(caseExecutionCommandBuilderMock);
    when(caseExecutionCommandBuilderMock.removeVariablesLocal(Matchers.<Collection<String>>any())).thenReturn(caseExecutionCommandBuilderMock);

  }

  @Test
  public void testCaseExecutionRetrieval() {
    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
    .then()
      .expect()
        .statusCode(Status.OK.getStatusCode())
        .body("id", equalTo(MockProvider.EXAMPLE_CASE_EXECUTION_ID))
        .body("caseInstanceId", equalTo(MockProvider.EXAMPLE_CASE_EXECUTION_CASE_INSTANCE_ID))
        .body("active", equalTo(MockProvider.EXAMPLE_CASE_EXECUTION_IS_ACTIVE))
        .body("active", equalTo(MockProvider.EXAMPLE_CASE_EXECUTION_IS_ENABLED))
    .when()
      .get(SINGLE_CASE_EXECUTION_URL);

    verify(caseServiceMock).createCaseExecutionQuery();
    verify(caseExecutionQueryMock).caseExecutionId(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionQueryMock).singleResult();
  }

  @Test
  public void testManualStart() {
    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(EMPTY_JSON_OBJECT)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_MANUAL_START_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).manualStart();
  }

  @Test
  public void testUnsuccessfulManualStart() {
    doThrow(new ProcessEngineException("expected exception")).when(caseExecutionCommandBuilderMock).manualStart();

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(EMPTY_JSON_OBJECT)
    .then()
      .expect()
        .statusCode(Status.BAD_REQUEST.getStatusCode())
        .body("type", equalTo(InvalidRequestException.class.getSimpleName()))
        .body("message", containsString("Cannot start case execution with id '" + MockProvider.EXAMPLE_CASE_EXECUTION_ID + "' manually."))
    .when()
      .post(CASE_EXECUTION_MANUAL_START_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).manualStart();
  }

  @Test
  public void testManualStartWithSetVariable() {
    String aVariableKey = "aKey";
    int aVariableValue = 123;

    String anotherVariableKey = "anotherKey";
    String anotherVariableValue = "abc";

    Map<String, Object> variablesJson = new HashMap<String, Object>();

    Map<String, Object> variables = VariablesBuilder
        .create()
          .variable(aVariableKey, aVariableValue, "Integer")
          .variable(anotherVariableKey, anotherVariableValue, "String")
          .getVariables();

    variablesJson.put("variables", variables);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(variablesJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_MANUAL_START_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).setVariable(aVariableKey, aVariableValue);
    verify(caseExecutionCommandBuilderMock).setVariable(anotherVariableKey, anotherVariableValue);
    verify(caseExecutionCommandBuilderMock).manualStart();
  }

  @Test
  public void testManualStartWithSetVariableLocal() {
    String aVariableKey = "aKey";
    int aVariableValue = 123;

    String anotherVariableKey = "anotherKey";
    String anotherVariableValue = "abc";

    Map<String, Object> variablesJson = new HashMap<String, Object>();

    Map<String, Object> variables = VariablesBuilder
        .create()
          .variable(aVariableKey, aVariableValue, "Integer", true)
          .variable(anotherVariableKey, anotherVariableValue, "String", true)
          .getVariables();

    variablesJson.put("variables", variables);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(variablesJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_MANUAL_START_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).setVariableLocal(aVariableKey, aVariableValue);
    verify(caseExecutionCommandBuilderMock).setVariableLocal(anotherVariableKey, anotherVariableValue);
    verify(caseExecutionCommandBuilderMock).manualStart();
  }

  @Test
  public void testManualStartWithSetVariableAndVariableLocal() {
    String aVariableKey = "aKey";
    int aVariableValue = 123;

    String anotherVariableKey = "anotherKey";
    String anotherVariableValue = "abc";

    Map<String, Object> variablesJson = new HashMap<String, Object>();

    Map<String, Object> variables = VariablesBuilder
        .create()
          .variable(aVariableKey, aVariableValue, "Integer")
          .variable(anotherVariableKey, anotherVariableValue, "String", true)
          .getVariables();

    variablesJson.put("variables", variables);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(variablesJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_MANUAL_START_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).setVariable(aVariableKey, aVariableValue);
    verify(caseExecutionCommandBuilderMock).setVariableLocal(anotherVariableKey, anotherVariableValue);
    verify(caseExecutionCommandBuilderMock).manualStart();
  }

  @Test
  public void testManualStartWithRemoveVariable() {
    String aVariableKey = "aKey";
    String anotherVariableKey = "anotherKey";

    List<VariableNameDto> variableNames = new ArrayList<VariableNameDto>();

    VariableNameDto firstVariableName = new VariableNameDto(aVariableKey);
    variableNames.add(firstVariableName);
    VariableNameDto secondVariableName = new VariableNameDto(anotherVariableKey);
    variableNames.add(secondVariableName);

    Map<String, Object> variablesJson = new HashMap<String, Object>();

    variablesJson.put("deletions", variableNames);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(variablesJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_MANUAL_START_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).removeVariable(aVariableKey);
    verify(caseExecutionCommandBuilderMock).removeVariable(anotherVariableKey);
    verify(caseExecutionCommandBuilderMock).manualStart();
  }

  @Test
  public void testManualStartWithRemoveVariableLocal() {
    String aVariableKey = "aKey";
    String anotherVariableKey = "anotherKey";

    List<VariableNameDto> variableNames = new ArrayList<VariableNameDto>();

    VariableNameDto firstVariableName = new VariableNameDto(aVariableKey, true);
    variableNames.add(firstVariableName);
    VariableNameDto secondVariableName = new VariableNameDto(anotherVariableKey, true);
    variableNames.add(secondVariableName);

    Map<String, Object> variablesJson = new HashMap<String, Object>();

    variablesJson.put("deletions", variableNames);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(variablesJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_MANUAL_START_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).removeVariableLocal(aVariableKey);
    verify(caseExecutionCommandBuilderMock).removeVariableLocal(anotherVariableKey);
    verify(caseExecutionCommandBuilderMock).manualStart();
  }

  @Test
  public void testManualStartWithRemoveVariableAndVariableLocal() {
    String aVariableKey = "aKey";
    String anotherVariableKey = "anotherKey";

    List<VariableNameDto> variableNames = new ArrayList<VariableNameDto>();

    VariableNameDto firstVariableName = new VariableNameDto(aVariableKey, true);
    variableNames.add(firstVariableName);
    VariableNameDto secondVariableName = new VariableNameDto(anotherVariableKey);
    variableNames.add(secondVariableName);

    Map<String, Object> variablesJson = new HashMap<String, Object>();

    variablesJson.put("deletions", variableNames);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(variablesJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_MANUAL_START_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).removeVariableLocal(aVariableKey);
    verify(caseExecutionCommandBuilderMock).removeVariable(anotherVariableKey);
    verify(caseExecutionCommandBuilderMock).manualStart();
  }

  @Test
  public void testManualStartWithSetVariableAndRemoveVariable() {
    String aVariableKey = "aKey";
    String anotherVariableKey = "anotherKey";
    String anotherVariableValue = "abc";

    Map<String, Object> variables = VariablesBuilder
        .create()
          .variable(anotherVariableKey, anotherVariableValue, "String")
          .getVariables();

    List<VariableNameDto> variableNames = new ArrayList<VariableNameDto>();

    VariableNameDto firstVariableName = new VariableNameDto(aVariableKey);
    variableNames.add(firstVariableName);

    Map<String, Object> variablesJson = new HashMap<String, Object>();

    variablesJson.put("variables", variables);
    variablesJson.put("deletions", variableNames);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(variablesJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_MANUAL_START_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).removeVariable(aVariableKey);
    verify(caseExecutionCommandBuilderMock).setVariable(anotherVariableKey, anotherVariableValue);
    verify(caseExecutionCommandBuilderMock).manualStart();
  }

  @Test
  public void testManualStartWithSetVariableAndRemoveVariableLocal() {
    String aVariableKey = "aKey";
    String anotherVariableKey = "anotherKey";
    String anotherVariableValue = "abc";

    Map<String, Object> variables = VariablesBuilder
        .create()
          .variable(anotherVariableKey, anotherVariableValue, "String")
          .getVariables();

    List<VariableNameDto> variableNames = new ArrayList<VariableNameDto>();

    VariableNameDto firstVariableName = new VariableNameDto(aVariableKey, true);
    variableNames.add(firstVariableName);

    Map<String, Object> variablesJson = new HashMap<String, Object>();

    variablesJson.put("variables", variables);
    variablesJson.put("deletions", variableNames);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(variablesJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_MANUAL_START_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).removeVariableLocal(aVariableKey);
    verify(caseExecutionCommandBuilderMock).setVariable(anotherVariableKey, anotherVariableValue);
    verify(caseExecutionCommandBuilderMock).manualStart();
  }

  @Test
  public void testManualStartWithSetVariableLocalAndRemoveVariable() {
    String aVariableKey = "aKey";
    String anotherVariableKey = "anotherKey";
    String anotherVariableValue = "abc";

    Map<String, Object> variables = VariablesBuilder
        .create()
          .variable(anotherVariableKey, anotherVariableValue, "String", true)
          .getVariables();

    List<VariableNameDto> variableNames = new ArrayList<VariableNameDto>();

    VariableNameDto firstVariableName = new VariableNameDto(aVariableKey);
    variableNames.add(firstVariableName);

    Map<String, Object> variablesJson = new HashMap<String, Object>();

    variablesJson.put("variables", variables);
    variablesJson.put("deletions", variableNames);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(variablesJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_MANUAL_START_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).removeVariable(aVariableKey);
    verify(caseExecutionCommandBuilderMock).setVariableLocal(anotherVariableKey, anotherVariableValue);
    verify(caseExecutionCommandBuilderMock).manualStart();
  }

  @Test
  public void testManualStartWithSetVariableLocalAndRemoveVariableLocal() {
    String aVariableKey = "aKey";
    String anotherVariableKey = "anotherKey";
    String anotherVariableValue = "abc";

    Map<String, Object> variables = VariablesBuilder
        .create()
          .variable(anotherVariableKey, anotherVariableValue, "String", true)
          .getVariables();

    List<VariableNameDto> variableNames = new ArrayList<VariableNameDto>();

    VariableNameDto firstVariableName = new VariableNameDto(aVariableKey, true);
    variableNames.add(firstVariableName);

    Map<String, Object> variablesJson = new HashMap<String, Object>();

    variablesJson.put("variables", variables);
    variablesJson.put("deletions", variableNames);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(variablesJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_MANUAL_START_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).removeVariableLocal(aVariableKey);
    verify(caseExecutionCommandBuilderMock).setVariableLocal(anotherVariableKey, anotherVariableValue);
    verify(caseExecutionCommandBuilderMock).manualStart();
  }

  @Test
  public void testDisable() {
    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(EMPTY_JSON_OBJECT)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_DISABLE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).disable();
  }

  @Test
  public void testUnsuccessfulDisable() {
    doThrow(new ProcessEngineException("expected exception")).when(caseExecutionCommandBuilderMock).disable();

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(EMPTY_JSON_OBJECT)
    .then()
      .expect()
        .statusCode(Status.BAD_REQUEST.getStatusCode())
        .body("type", equalTo(InvalidRequestException.class.getSimpleName()))
        .body("message", containsString("Cannot disable case execution with id '" + MockProvider.EXAMPLE_CASE_EXECUTION_ID + "'."))
    .when()
      .post(CASE_EXECUTION_DISABLE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).disable();
  }

  @Test
  public void testDisableWithSetVariable() {
    String aVariableKey = "aKey";
    int aVariableValue = 123;

    String anotherVariableKey = "anotherKey";
    String anotherVariableValue = "abc";

    Map<String, Object> variablesJson = new HashMap<String, Object>();

    Map<String, Object> variables = VariablesBuilder
        .create()
          .variable(aVariableKey, aVariableValue, "Integer")
          .variable(anotherVariableKey, anotherVariableValue, "String")
          .getVariables();

    variablesJson.put("variables", variables);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(variablesJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_DISABLE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).setVariable(aVariableKey, aVariableValue);
    verify(caseExecutionCommandBuilderMock).setVariable(anotherVariableKey, anotherVariableValue);
    verify(caseExecutionCommandBuilderMock).disable();
  }

  @Test
  public void testDisableWithSetVariableLocal() {
    String aVariableKey = "aKey";
    int aVariableValue = 123;

    String anotherVariableKey = "anotherKey";
    String anotherVariableValue = "abc";

    Map<String, Object> variablesJson = new HashMap<String, Object>();

    Map<String, Object> variables = VariablesBuilder
        .create()
          .variable(aVariableKey, aVariableValue, "Integer", true)
          .variable(anotherVariableKey, anotherVariableValue, "String", true)
          .getVariables();

    variablesJson.put("variables", variables);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(variablesJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_DISABLE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).setVariableLocal(aVariableKey, aVariableValue);
    verify(caseExecutionCommandBuilderMock).setVariableLocal(anotherVariableKey, anotherVariableValue);
    verify(caseExecutionCommandBuilderMock).disable();
  }

  @Test
  public void testDisableWithSetVariableAndVariableLocal() {
    String aVariableKey = "aKey";
    int aVariableValue = 123;

    String anotherVariableKey = "anotherKey";
    String anotherVariableValue = "abc";

    Map<String, Object> variablesJson = new HashMap<String, Object>();

    Map<String, Object> variables = VariablesBuilder
        .create()
          .variable(aVariableKey, aVariableValue, "Integer")
          .variable(anotherVariableKey, anotherVariableValue, "String", true)
          .getVariables();

    variablesJson.put("variables", variables);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(variablesJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_DISABLE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).setVariable(aVariableKey, aVariableValue);
    verify(caseExecutionCommandBuilderMock).setVariableLocal(anotherVariableKey, anotherVariableValue);
    verify(caseExecutionCommandBuilderMock).disable();
  }

  @Test
  public void testDisableWithRemoveVariable() {
    String aVariableKey = "aKey";
    String anotherVariableKey = "anotherKey";

    List<VariableNameDto> variableNames = new ArrayList<VariableNameDto>();

    VariableNameDto firstVariableName = new VariableNameDto(aVariableKey);
    variableNames.add(firstVariableName);
    VariableNameDto secondVariableName = new VariableNameDto(anotherVariableKey);
    variableNames.add(secondVariableName);

    Map<String, Object> variablesJson = new HashMap<String, Object>();

    variablesJson.put("deletions", variableNames);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(variablesJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_DISABLE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).removeVariable(aVariableKey);
    verify(caseExecutionCommandBuilderMock).removeVariable(anotherVariableKey);
    verify(caseExecutionCommandBuilderMock).disable();
  }

  @Test
  public void testDisableWithRemoveVariableLocal() {
    String aVariableKey = "aKey";
    String anotherVariableKey = "anotherKey";

    List<VariableNameDto> variableNames = new ArrayList<VariableNameDto>();

    VariableNameDto firstVariableName = new VariableNameDto(aVariableKey, true);
    variableNames.add(firstVariableName);
    VariableNameDto secondVariableName = new VariableNameDto(anotherVariableKey, true);
    variableNames.add(secondVariableName);

    Map<String, Object> variablesJson = new HashMap<String, Object>();

    variablesJson.put("deletions", variableNames);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(variablesJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_DISABLE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).removeVariableLocal(aVariableKey);
    verify(caseExecutionCommandBuilderMock).removeVariableLocal(anotherVariableKey);
    verify(caseExecutionCommandBuilderMock).disable();
  }

  @Test
  public void testDisableWithRemoveVariableAndVariableLocal() {
    String aVariableKey = "aKey";
    String anotherVariableKey = "anotherKey";

    List<VariableNameDto> variableNames = new ArrayList<VariableNameDto>();

    VariableNameDto firstVariableName = new VariableNameDto(aVariableKey, true);
    variableNames.add(firstVariableName);
    VariableNameDto secondVariableName = new VariableNameDto(anotherVariableKey);
    variableNames.add(secondVariableName);

    Map<String, Object> variablesJson = new HashMap<String, Object>();

    variablesJson.put("deletions", variableNames);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(variablesJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_DISABLE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).removeVariableLocal(aVariableKey);
    verify(caseExecutionCommandBuilderMock).removeVariable(anotherVariableKey);
    verify(caseExecutionCommandBuilderMock).disable();
  }

  @Test
  public void testDisableWithSetVariableAndRemoveVariable() {
    String aVariableKey = "aKey";
    String anotherVariableKey = "anotherKey";
    String anotherVariableValue = "abc";

    Map<String, Object> variables = VariablesBuilder
        .create()
          .variable(anotherVariableKey, anotherVariableValue, "String")
          .getVariables();

    List<VariableNameDto> variableNames = new ArrayList<VariableNameDto>();

    VariableNameDto firstVariableName = new VariableNameDto(aVariableKey);
    variableNames.add(firstVariableName);

    Map<String, Object> variablesJson = new HashMap<String, Object>();

    variablesJson.put("variables", variables);
    variablesJson.put("deletions", variableNames);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(variablesJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_DISABLE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).removeVariable(aVariableKey);
    verify(caseExecutionCommandBuilderMock).setVariable(anotherVariableKey, anotherVariableValue);
    verify(caseExecutionCommandBuilderMock).disable();
  }

  @Test
  public void testDisableWithSetVariableAndRemoveVariableLocal() {
    String aVariableKey = "aKey";
    String anotherVariableKey = "anotherKey";
    String anotherVariableValue = "abc";

    Map<String, Object> variables = VariablesBuilder
        .create()
          .variable(anotherVariableKey, anotherVariableValue, "String")
          .getVariables();

    List<VariableNameDto> variableNames = new ArrayList<VariableNameDto>();

    VariableNameDto firstVariableName = new VariableNameDto(aVariableKey, true);
    variableNames.add(firstVariableName);

    Map<String, Object> variablesJson = new HashMap<String, Object>();

    variablesJson.put("variables", variables);
    variablesJson.put("deletions", variableNames);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(variablesJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_DISABLE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).removeVariableLocal(aVariableKey);
    verify(caseExecutionCommandBuilderMock).setVariable(anotherVariableKey, anotherVariableValue);
    verify(caseExecutionCommandBuilderMock).disable();
  }

  @Test
  public void testDisableWithSetVariableLocalAndRemoveVariable() {
    String aVariableKey = "aKey";
    String anotherVariableKey = "anotherKey";
    String anotherVariableValue = "abc";

    Map<String, Object> variables = VariablesBuilder
        .create()
          .variable(anotherVariableKey, anotherVariableValue, "String", true)
          .getVariables();

    List<VariableNameDto> variableNames = new ArrayList<VariableNameDto>();

    VariableNameDto firstVariableName = new VariableNameDto(aVariableKey);
    variableNames.add(firstVariableName);

    Map<String, Object> variablesJson = new HashMap<String, Object>();

    variablesJson.put("variables", variables);
    variablesJson.put("deletions", variableNames);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(variablesJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_DISABLE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).removeVariable(aVariableKey);
    verify(caseExecutionCommandBuilderMock).setVariableLocal(anotherVariableKey, anotherVariableValue);
    verify(caseExecutionCommandBuilderMock).disable();
  }

  @Test
  public void testDisableWithSetVariableLocalAndRemoveVariableLocal() {
    String aVariableKey = "aKey";
    String anotherVariableKey = "anotherKey";
    String anotherVariableValue = "abc";

    Map<String, Object> variables = VariablesBuilder
        .create()
          .variable(anotherVariableKey, anotherVariableValue, "String", true)
          .getVariables();

    List<VariableNameDto> variableNames = new ArrayList<VariableNameDto>();

    VariableNameDto firstVariableName = new VariableNameDto(aVariableKey, true);
    variableNames.add(firstVariableName);

    Map<String, Object> variablesJson = new HashMap<String, Object>();

    variablesJson.put("variables", variables);
    variablesJson.put("deletions", variableNames);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(variablesJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_DISABLE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).removeVariableLocal(aVariableKey);
    verify(caseExecutionCommandBuilderMock).setVariableLocal(anotherVariableKey, anotherVariableValue);
    verify(caseExecutionCommandBuilderMock).disable();
  }

  @Test
  public void testReenable() {
    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(EMPTY_JSON_OBJECT)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_REENABLE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).reenable();
  }

  @Test
  public void testUnsuccessfulReenable() {
    doThrow(new ProcessEngineException("expected exception")).when(caseExecutionCommandBuilderMock).reenable();

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(EMPTY_JSON_OBJECT)
    .then()
      .expect()
        .statusCode(Status.BAD_REQUEST.getStatusCode())
        .body("type", equalTo(InvalidRequestException.class.getSimpleName()))
        .body("message", containsString("Cannot re-enable case execution with id '" + MockProvider.EXAMPLE_CASE_EXECUTION_ID + "'."))
    .when()
      .post(CASE_EXECUTION_REENABLE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).reenable();
  }

  @Test
  public void testReenableWithSetVariable() {
    String aVariableKey = "aKey";
    int aVariableValue = 123;

    String anotherVariableKey = "anotherKey";
    String anotherVariableValue = "abc";

    Map<String, Object> variablesJson = new HashMap<String, Object>();

    Map<String, Object> variables = VariablesBuilder
        .create()
          .variable(aVariableKey, aVariableValue, "Integer")
          .variable(anotherVariableKey, anotherVariableValue, "String")
          .getVariables();

    variablesJson.put("variables", variables);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(variablesJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_REENABLE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).setVariable(aVariableKey, aVariableValue);
    verify(caseExecutionCommandBuilderMock).setVariable(anotherVariableKey, anotherVariableValue);
    verify(caseExecutionCommandBuilderMock).reenable();
  }

  @Test
  public void testReenableWithSetVariableLocal() {
    String aVariableKey = "aKey";
    int aVariableValue = 123;

    String anotherVariableKey = "anotherKey";
    String anotherVariableValue = "abc";

    Map<String, Object> variablesJson = new HashMap<String, Object>();

    Map<String, Object> variables = VariablesBuilder
        .create()
          .variable(aVariableKey, aVariableValue, "Integer", true)
          .variable(anotherVariableKey, anotherVariableValue, "String", true)
          .getVariables();

    variablesJson.put("variables", variables);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(variablesJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_REENABLE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).setVariableLocal(aVariableKey, aVariableValue);
    verify(caseExecutionCommandBuilderMock).setVariableLocal(anotherVariableKey, anotherVariableValue);
    verify(caseExecutionCommandBuilderMock).reenable();
  }

  @Test
  public void testReenableWithSetVariableAndVariableLocal() {
    String aVariableKey = "aKey";
    int aVariableValue = 123;

    String anotherVariableKey = "anotherKey";
    String anotherVariableValue = "abc";

    Map<String, Object> variablesJson = new HashMap<String, Object>();

    Map<String, Object> variables = VariablesBuilder
        .create()
          .variable(aVariableKey, aVariableValue, "Integer")
          .variable(anotherVariableKey, anotherVariableValue, "String", true)
          .getVariables();

    variablesJson.put("variables", variables);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(variablesJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_REENABLE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).setVariable(aVariableKey, aVariableValue);
    verify(caseExecutionCommandBuilderMock).setVariableLocal(anotherVariableKey, anotherVariableValue);
    verify(caseExecutionCommandBuilderMock).reenable();
  }

  @Test
  public void testReenableWithRemoveVariable() {
    String aVariableKey = "aKey";
    String anotherVariableKey = "anotherKey";

    List<VariableNameDto> variableNames = new ArrayList<VariableNameDto>();

    VariableNameDto firstVariableName = new VariableNameDto(aVariableKey);
    variableNames.add(firstVariableName);
    VariableNameDto secondVariableName = new VariableNameDto(anotherVariableKey);
    variableNames.add(secondVariableName);

    Map<String, Object> variablesJson = new HashMap<String, Object>();

    variablesJson.put("deletions", variableNames);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(variablesJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_REENABLE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).removeVariable(aVariableKey);
    verify(caseExecutionCommandBuilderMock).removeVariable(anotherVariableKey);
    verify(caseExecutionCommandBuilderMock).reenable();
  }

  @Test
  public void testReenableWithRemoveVariableLocal() {
    String aVariableKey = "aKey";
    String anotherVariableKey = "anotherKey";

    List<VariableNameDto> variableNames = new ArrayList<VariableNameDto>();

    VariableNameDto firstVariableName = new VariableNameDto(aVariableKey, true);
    variableNames.add(firstVariableName);
    VariableNameDto secondVariableName = new VariableNameDto(anotherVariableKey, true);
    variableNames.add(secondVariableName);

    Map<String, Object> variablesJson = new HashMap<String, Object>();

    variablesJson.put("deletions", variableNames);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(variablesJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_REENABLE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).removeVariableLocal(aVariableKey);
    verify(caseExecutionCommandBuilderMock).removeVariableLocal(anotherVariableKey);
    verify(caseExecutionCommandBuilderMock).reenable();
  }

  @Test
  public void testReenableWithRemoveVariableAndVariableLocal() {
    String aVariableKey = "aKey";
    String anotherVariableKey = "anotherKey";

    List<VariableNameDto> variableNames = new ArrayList<VariableNameDto>();

    VariableNameDto firstVariableName = new VariableNameDto(aVariableKey, true);
    variableNames.add(firstVariableName);
    VariableNameDto secondVariableName = new VariableNameDto(anotherVariableKey);
    variableNames.add(secondVariableName);

    Map<String, Object> variablesJson = new HashMap<String, Object>();

    variablesJson.put("deletions", variableNames);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(variablesJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_REENABLE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).removeVariableLocal(aVariableKey);
    verify(caseExecutionCommandBuilderMock).removeVariable(anotherVariableKey);
    verify(caseExecutionCommandBuilderMock).reenable();
  }

  @Test
  public void testReenableWithSetVariableAndRemoveVariable() {
    String aVariableKey = "aKey";
    String anotherVariableKey = "anotherKey";
    String anotherVariableValue = "abc";

    Map<String, Object> variables = VariablesBuilder
        .create()
          .variable(anotherVariableKey, anotherVariableValue, "String")
          .getVariables();

    List<VariableNameDto> variableNames = new ArrayList<VariableNameDto>();

    VariableNameDto firstVariableName = new VariableNameDto(aVariableKey);
    variableNames.add(firstVariableName);

    Map<String, Object> variablesJson = new HashMap<String, Object>();

    variablesJson.put("variables", variables);
    variablesJson.put("deletions", variableNames);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(variablesJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_REENABLE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).removeVariable(aVariableKey);
    verify(caseExecutionCommandBuilderMock).setVariable(anotherVariableKey, anotherVariableValue);
    verify(caseExecutionCommandBuilderMock).reenable();
  }

  @Test
  public void testReenableWithSetVariableAndRemoveVariableLocal() {
    String aVariableKey = "aKey";
    String anotherVariableKey = "anotherKey";
    String anotherVariableValue = "abc";

    Map<String, Object> variables = VariablesBuilder
        .create()
          .variable(anotherVariableKey, anotherVariableValue, "String")
          .getVariables();

    List<VariableNameDto> variableNames = new ArrayList<VariableNameDto>();

    VariableNameDto firstVariableName = new VariableNameDto(aVariableKey, true);
    variableNames.add(firstVariableName);

    Map<String, Object> variablesJson = new HashMap<String, Object>();

    variablesJson.put("variables", variables);
    variablesJson.put("deletions", variableNames);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(variablesJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_REENABLE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).removeVariableLocal(aVariableKey);
    verify(caseExecutionCommandBuilderMock).setVariable(anotherVariableKey, anotherVariableValue);
    verify(caseExecutionCommandBuilderMock).reenable();
  }

  @Test
  public void testReenableWithSetVariableLocalAndRemoveVariable() {
    String aVariableKey = "aKey";
    String anotherVariableKey = "anotherKey";
    String anotherVariableValue = "abc";

    Map<String, Object> variables = VariablesBuilder
        .create()
          .variable(anotherVariableKey, anotherVariableValue, "String", true)
          .getVariables();

    List<VariableNameDto> variableNames = new ArrayList<VariableNameDto>();

    VariableNameDto firstVariableName = new VariableNameDto(aVariableKey);
    variableNames.add(firstVariableName);

    Map<String, Object> variablesJson = new HashMap<String, Object>();

    variablesJson.put("variables", variables);
    variablesJson.put("deletions", variableNames);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(variablesJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_REENABLE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).removeVariable(aVariableKey);
    verify(caseExecutionCommandBuilderMock).setVariableLocal(anotherVariableKey, anotherVariableValue);
    verify(caseExecutionCommandBuilderMock).reenable();
  }

  @Test
  public void testReenableWithSetVariableLocalAndRemoveVariableLocal() {
    String aVariableKey = "aKey";
    String anotherVariableKey = "anotherKey";
    String anotherVariableValue = "abc";

    Map<String, Object> variables = VariablesBuilder
        .create()
          .variable(anotherVariableKey, anotherVariableValue, "String", true)
          .getVariables();

    List<VariableNameDto> variableNames = new ArrayList<VariableNameDto>();

    VariableNameDto firstVariableName = new VariableNameDto(aVariableKey, true);
    variableNames.add(firstVariableName);

    Map<String, Object> variablesJson = new HashMap<String, Object>();

    variablesJson.put("variables", variables);
    variablesJson.put("deletions", variableNames);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(variablesJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_REENABLE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).removeVariableLocal(aVariableKey);
    verify(caseExecutionCommandBuilderMock).setVariableLocal(anotherVariableKey, anotherVariableValue);
    verify(caseExecutionCommandBuilderMock).reenable();
  }

  @Test
  public void testGetLocalVariables() {
    Response response = given()
        .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .then()
        .expect()
          .statusCode(Status.OK.getStatusCode())
          .body(EXAMPLE_VARIABLE_KEY, notNullValue())
          .body(EXAMPLE_VARIABLE_KEY + ".value", equalTo(EXAMPLE_VARIABLE_VALUE))
          .body(EXAMPLE_VARIABLE_KEY + ".type", equalTo(String.class.getSimpleName()))
      .when()
        .get(CASE_EXECUTION_LOCAL_VARIABLES_URL);

    Assert.assertEquals("Should return exactly one variable", 1, response.jsonPath().getMap("").size());

    verify(caseServiceMock).getVariablesLocal(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
  }

  @Test
  public void testGetVariables() {
    Response response = given()
        .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .then()
        .expect()
          .statusCode(Status.OK.getStatusCode())
          .body(EXAMPLE_VARIABLE_KEY, notNullValue())
          .body(EXAMPLE_VARIABLE_KEY + ".value", equalTo(EXAMPLE_VARIABLE_VALUE))
          .body(EXAMPLE_VARIABLE_KEY + ".type", equalTo(String.class.getSimpleName()))
      .when()
        .get(CASE_EXECUTION_VARIABLES_URL);

    Assert.assertEquals("Should return exactly one variable", 1, response.jsonPath().getMap("").size());

    verify(caseServiceMock).getVariables(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
  }

  @Test
  public void testGetLocalVariablesForNonExistingExecution() {
    when(caseServiceMock.getVariablesLocal(anyString())).thenThrow(new ProcessEngineException("expected exception"));

    given()
      .pathParam("id", "aNonExistingExecutionId")
    .then()
      .expect()
        .statusCode(Status.INTERNAL_SERVER_ERROR.getStatusCode())
        .contentType(ContentType.JSON)
        .body("type", equalTo(ProcessEngineException.class.getSimpleName()))
        .body("message", equalTo("expected exception"))
      .when()
        .get(CASE_EXECUTION_LOCAL_VARIABLES_URL);

    verify(caseServiceMock).getVariablesLocal("aNonExistingExecutionId");
  }

  @Test
  public void testGetVariablesForNonExistingExecution() {
    when(caseServiceMock.getVariables(anyString())).thenThrow(new ProcessEngineException("expected exception"));

    given()
      .pathParam("id", "aNonExistingExecutionId")
    .then()
      .expect()
        .statusCode(Status.INTERNAL_SERVER_ERROR.getStatusCode())
        .contentType(ContentType.JSON)
        .body("type", equalTo(ProcessEngineException.class.getSimpleName()))
        .body("message", equalTo("expected exception"))
      .when()
        .get(CASE_EXECUTION_VARIABLES_URL);

    verify(caseServiceMock).getVariables("aNonExistingExecutionId");
  }

  @Test
  public void testLocalVariableModification() {
    Map<String, Object> messageBodyJson = new HashMap<String, Object>();

    String variableKey = "aKey";
    int variableValue = 123;

    Map<String, Object> modifications = VariablesBuilder.create().variable(variableKey, variableValue).getVariables();
    messageBodyJson.put("modifications", modifications);

    List<String> deletions = new ArrayList<String>();
    deletions.add("deleteKey");
    messageBodyJson.put("deletions", deletions);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(messageBodyJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_LOCAL_VARIABLES_URL);

    Map<String, Object> expectedMap = new HashMap<String, Object>();
    expectedMap.put(variableKey, variableValue);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).removeVariablesLocal(eq(deletions));
    verify(caseExecutionCommandBuilderMock).setVariablesLocal(eq(expectedMap));
    verify(caseExecutionCommandBuilderMock).execute();
  }

  @Test
  public void testVariableModification() {
    Map<String, Object> messageBodyJson = new HashMap<String, Object>();

    String variableKey = "aKey";
    int variableValue = 123;

    Map<String, Object> modifications = VariablesBuilder.create().variable(variableKey, variableValue).getVariables();
    messageBodyJson.put("modifications", modifications);

    List<String> deletions = new ArrayList<String>();
    deletions.add("deleteKey");
    messageBodyJson.put("deletions", deletions);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(messageBodyJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_VARIABLES_URL);

    Map<String, Object> expectedMap = new HashMap<String, Object>();
    expectedMap.put(variableKey, variableValue);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).removeVariables(eq(deletions));
    verify(caseExecutionCommandBuilderMock).setVariables(eq(expectedMap));
    verify(caseExecutionCommandBuilderMock).execute();
  }

  @Test
  public void testLocalVariableModificationForNonExistingExecution() {
    when(caseServiceMock.withCaseExecution("aNonExistingExecutionId")).thenReturn(caseExecutionCommandBuilderMock);

    doThrow(new ProcessEngineException("expected exception"))
      .when(caseExecutionCommandBuilderMock)
      .execute();

    Map<String, Object> messageBodyJson = new HashMap<String, Object>();

    String variableKey = "aKey";
    int variableValue = 123;
    Map<String, Object> modifications = VariablesBuilder
        .create()
        .variable(variableKey, variableValue)
        .getVariables();

    messageBodyJson.put("modifications", modifications);

    given()
      .pathParam("id", "aNonExistingExecutionId")
      .contentType(ContentType.JSON)
      .body(messageBodyJson)
    .then()
      .expect()
        .statusCode(Status.INTERNAL_SERVER_ERROR.getStatusCode()).contentType(ContentType.JSON)
        .body("type", equalTo(RestException.class.getSimpleName()))
        .body("message", equalTo("Cannot modify variables for case execution " + "aNonExistingExecutionId" + ": expected exception"))
    .when()
      .post(CASE_EXECUTION_LOCAL_VARIABLES_URL);

    Map<String, Object> expectedMap = new HashMap<String, Object>();
    expectedMap.put(variableKey, variableValue);

    verify(caseServiceMock).withCaseExecution("aNonExistingExecutionId");
    verify(caseExecutionCommandBuilderMock).removeVariablesLocal(null);
    verify(caseExecutionCommandBuilderMock).setVariablesLocal(eq(expectedMap));
    verify(caseExecutionCommandBuilderMock).execute();
  }

  @Test
  public void testVariableModificationForNonExistingExecution() {
    when(caseServiceMock.withCaseExecution("aNonExistingExecutionId")).thenReturn(caseExecutionCommandBuilderMock);

    doThrow(new ProcessEngineException("expected exception"))
      .when(caseExecutionCommandBuilderMock)
      .execute();

    Map<String, Object> messageBodyJson = new HashMap<String, Object>();

    String variableKey = "aKey";
    int variableValue = 123;
    Map<String, Object> modifications = VariablesBuilder
        .create()
        .variable(variableKey, variableValue)
        .getVariables();

    messageBodyJson.put("modifications", modifications);

    given()
      .pathParam("id", "aNonExistingExecutionId")
      .contentType(ContentType.JSON)
      .body(messageBodyJson)
    .then()
      .expect()
        .statusCode(Status.INTERNAL_SERVER_ERROR.getStatusCode()).contentType(ContentType.JSON)
        .body("type", equalTo(RestException.class.getSimpleName()))
        .body("message", equalTo("Cannot modify variables for case execution " + "aNonExistingExecutionId" + ": expected exception"))
    .when()
      .post(CASE_EXECUTION_VARIABLES_URL);

    Map<String, Object> expectedMap = new HashMap<String, Object>();
    expectedMap.put(variableKey, variableValue);

    verify(caseServiceMock).withCaseExecution("aNonExistingExecutionId");
    verify(caseExecutionCommandBuilderMock).removeVariables(null);
    verify(caseExecutionCommandBuilderMock).setVariables(eq(expectedMap));
    verify(caseExecutionCommandBuilderMock).execute();
  }

  @Test
  public void testEmptyLocalVariableModification() {
    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(EMPTY_JSON_OBJECT)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_LOCAL_VARIABLES_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).removeVariablesLocal(null);
    verify(caseExecutionCommandBuilderMock).setVariablesLocal(null);
    verify(caseExecutionCommandBuilderMock).execute();
  }

  @Test
  public void testEmptyVariableModification() {
    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(EMPTY_JSON_OBJECT)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_VARIABLES_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).removeVariables(null);
    verify(caseExecutionCommandBuilderMock).setVariables(null);
    verify(caseExecutionCommandBuilderMock).execute();
  }

  @Test
  public void testGetSingleLocalVariable() {
    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", EXAMPLE_VARIABLE_KEY)
    .then()
      .expect()
        .statusCode(Status.OK.getStatusCode())
        .body("value", is(EXAMPLE_VARIABLE_VALUE))
        .body("type", is("String"))
    .when()
      .get(SINGLE_CASE_EXECUTION_LOCAL_VARIABLE_URL);

    verify(caseServiceMock).getVariableLocal(MockProvider.EXAMPLE_CASE_EXECUTION_ID, EXAMPLE_VARIABLE_KEY);
  }

  @Test
  public void testGetSingleVariable() {
    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", EXAMPLE_VARIABLE_KEY)
    .then()
      .expect()
        .statusCode(Status.OK.getStatusCode())
        .body("value", is(EXAMPLE_VARIABLE_VALUE))
        .body("type", is("String"))
    .when()
      .get(SINGLE_CASE_EXECUTION_VARIABLE_URL);

    verify(caseServiceMock).getVariable(MockProvider.EXAMPLE_CASE_EXECUTION_ID, EXAMPLE_VARIABLE_KEY);
  }

  @Test
  public void testNonExistingLocalVariable() {
    when(caseServiceMock.getVariableLocal(eq(MockProvider.EXAMPLE_CASE_EXECUTION_ID), eq(EXAMPLE_VARIABLE_KEY))).thenReturn(null);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", EXAMPLE_VARIABLE_KEY)
    .then()
      .expect()
        .statusCode(Status.NOT_FOUND.getStatusCode())
      .body("type", is(InvalidRequestException.class.getSimpleName()))
      .body("message", is("case execution variable with name " + EXAMPLE_VARIABLE_KEY + " does not exist or is null"))
    .when()
      .get(SINGLE_CASE_EXECUTION_LOCAL_VARIABLE_URL);
  }

  @Test
  public void testNonExistingVariable() {
    when(caseServiceMock.getVariable(eq(MockProvider.EXAMPLE_CASE_EXECUTION_ID), eq(EXAMPLE_VARIABLE_KEY))).thenReturn(null);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", EXAMPLE_VARIABLE_KEY)
    .then()
      .expect()
        .statusCode(Status.NOT_FOUND.getStatusCode())
      .body("type", is(InvalidRequestException.class.getSimpleName()))
      .body("message", is("case execution variable with name " + EXAMPLE_VARIABLE_KEY + " does not exist or is null"))
    .when()
      .get(SINGLE_CASE_EXECUTION_VARIABLE_URL);
  }

  @Test
  public void testGetLocalVariableForNonExistingExecution() {
    when(caseServiceMock.getVariableLocal(eq(MockProvider.EXAMPLE_CASE_EXECUTION_ID), eq(EXAMPLE_VARIABLE_KEY)))
      .thenThrow(new ProcessEngineException("expected exception"));

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", EXAMPLE_VARIABLE_KEY)
    .then()
      .expect()
        .statusCode(Status.INTERNAL_SERVER_ERROR.getStatusCode())
        .body("type", is(RestException.class.getSimpleName()))
        .body("message", is("Cannot get case execution variable " + EXAMPLE_VARIABLE_KEY + ": expected exception"))
    .when()
      .get(SINGLE_CASE_EXECUTION_LOCAL_VARIABLE_URL);
  }

  @Test
  public void testGetVariableForNonExistingExecution() {
    when(caseServiceMock.getVariable(eq(MockProvider.EXAMPLE_CASE_EXECUTION_ID), eq(EXAMPLE_VARIABLE_KEY)))
      .thenThrow(new ProcessEngineException("expected exception"));

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", EXAMPLE_VARIABLE_KEY)
    .then()
      .expect()
        .statusCode(Status.INTERNAL_SERVER_ERROR.getStatusCode())
        .body("type", is(RestException.class.getSimpleName()))
        .body("message", is("Cannot get case execution variable " + EXAMPLE_VARIABLE_KEY + ": expected exception"))
    .when()
      .get(SINGLE_CASE_EXECUTION_VARIABLE_URL);
  }

  @Test
  public void testPutSingleLocalVariable() {
    Map<String, Object> variableJson = VariablesBuilder.getVariableValueMap(EXAMPLE_VARIABLE_VALUE);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", EXAMPLE_VARIABLE_KEY)
      .contentType(ContentType.JSON)
      .body(variableJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .put(SINGLE_CASE_EXECUTION_LOCAL_VARIABLE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).setVariableLocal(EXAMPLE_VARIABLE_KEY, EXAMPLE_VARIABLE_VALUE);
    verify(caseExecutionCommandBuilderMock).execute();
  }

  @Test
  public void testPutSingleVariable() {
    Map<String, Object> variableJson = VariablesBuilder.getVariableValueMap(EXAMPLE_VARIABLE_VALUE);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", EXAMPLE_VARIABLE_KEY)
      .contentType(ContentType.JSON)
      .body(variableJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .put(SINGLE_CASE_EXECUTION_VARIABLE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).setVariable(EXAMPLE_VARIABLE_KEY, EXAMPLE_VARIABLE_VALUE);
    verify(caseExecutionCommandBuilderMock).execute();
  }

  @Test
  public void testPutSingleLocalVariableWithTypeInteger() {
    String variableKey = "aVariableKey";
    Integer variableValue = 123;
    String type = "Integer";

    Map<String, Object> variableJson = VariablesBuilder.getVariableValueMap(variableValue, type);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", variableKey)
      .contentType(ContentType.JSON)
      .body(variableJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .put(SINGLE_CASE_EXECUTION_LOCAL_VARIABLE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).setVariableLocal(variableKey, variableValue);
    verify(caseExecutionCommandBuilderMock).execute();
  }

  @Test
  public void testPutSingleVariableWithTypeInteger() {
    String variableKey = "aVariableKey";
    Integer variableValue = 123;
    String type = "Integer";

    Map<String, Object> variableJson = VariablesBuilder.getVariableValueMap(variableValue, type);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", variableKey)
      .contentType(ContentType.JSON)
      .body(variableJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .put(SINGLE_CASE_EXECUTION_VARIABLE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).setVariable(variableKey, variableValue);
    verify(caseExecutionCommandBuilderMock).execute();
  }

  @Test
  public void testPutSingleLocalVariableWithUnparseableInteger() {
    String variableKey = "aVariableKey";
    String variableValue = "1abc";
    String type = "Integer";

    Map<String, Object> variableJson = VariablesBuilder.getVariableValueMap(variableValue, type);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", variableKey)
      .contentType(ContentType.JSON)
      .body(variableJson)
    .then()
      .expect()
        .statusCode(Status.BAD_REQUEST.getStatusCode())
        .body("type", equalTo(RestException.class.getSimpleName()))
        .body("message", equalTo("Cannot put case execution variable aVariableKey due to number format exception: For input string: \"1abc\""))
    .when()
      .put(SINGLE_CASE_EXECUTION_LOCAL_VARIABLE_URL);
  }

  @Test
  public void testPutSingleVariableWithUnparseableInteger() {
    String variableKey = "aVariableKey";
    String variableValue = "1abc";
    String type = "Integer";

    Map<String, Object> variableJson = VariablesBuilder.getVariableValueMap(variableValue, type);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", variableKey)
      .contentType(ContentType.JSON)
      .body(variableJson)
    .then()
      .expect()
        .statusCode(Status.BAD_REQUEST.getStatusCode())
        .body("type", equalTo(RestException.class.getSimpleName()))
        .body("message", equalTo("Cannot put case execution variable aVariableKey due to number format exception: For input string: \"1abc\""))
    .when()
      .put(SINGLE_CASE_EXECUTION_VARIABLE_URL);
  }

  @Test
  public void testPutSingleLocalVariableWithTypeShort() {
    String variableKey = "aVariableKey";
    Short variableValue = 123;
    String type = "Short";

    Map<String, Object> variableJson = VariablesBuilder.getVariableValueMap(variableValue, type);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", variableKey)
      .contentType(ContentType.JSON)
      .body(variableJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .put(SINGLE_CASE_EXECUTION_LOCAL_VARIABLE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).setVariableLocal(variableKey, variableValue);
    verify(caseExecutionCommandBuilderMock).execute();
  }

  @Test
  public void testPutSingleVariableWithTypeShort() {
    String variableKey = "aVariableKey";
    Short variableValue = 123;
    String type = "Short";

    Map<String, Object> variableJson = VariablesBuilder.getVariableValueMap(variableValue, type);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", variableKey)
      .contentType(ContentType.JSON)
      .body(variableJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .put(SINGLE_CASE_EXECUTION_VARIABLE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).setVariable(variableKey, variableValue);
    verify(caseExecutionCommandBuilderMock).execute();
  }

  @Test
  public void testPutSingleLocalVariableWithUnparseableShort() {
    String variableKey = "aVariableKey";
    String variableValue = "1abc";
    String type = "Short";

    Map<String, Object> variableJson = VariablesBuilder.getVariableValueMap(variableValue, type);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", variableKey)
      .contentType(ContentType.JSON)
      .body(variableJson)
    .then()
      .expect()
        .statusCode(Status.BAD_REQUEST.getStatusCode())
        .body("type", equalTo(RestException.class.getSimpleName()))
        .body("message", equalTo("Cannot put case execution variable aVariableKey due to number format exception: For input string: \"1abc\""))
    .when()
      .put(SINGLE_CASE_EXECUTION_LOCAL_VARIABLE_URL);
  }

  @Test
  public void testPutSingleVariableWithUnparseableShort() {
    String variableKey = "aVariableKey";
    String variableValue = "1abc";
    String type = "Short";

    Map<String, Object> variableJson = VariablesBuilder.getVariableValueMap(variableValue, type);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", variableKey)
      .contentType(ContentType.JSON).
      body(variableJson)
    .then()
      .expect()
        .statusCode(Status.BAD_REQUEST.getStatusCode())
        .body("type", equalTo(RestException.class.getSimpleName()))
        .body("message", equalTo("Cannot put case execution variable aVariableKey due to number format exception: For input string: \"1abc\""))
    .when()
      .put(SINGLE_CASE_EXECUTION_VARIABLE_URL);
  }

  @Test
  public void testPutSingleLocalVariableWithTypeLong() {
    String variableKey = "aVariableKey";
    Long variableValue = Long.valueOf(123);
    String type = "Long";

    Map<String, Object> variableJson = VariablesBuilder.getVariableValueMap(variableValue, type);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", variableKey)
      .contentType(ContentType.JSON)
      .body(variableJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .put(SINGLE_CASE_EXECUTION_LOCAL_VARIABLE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).setVariableLocal(variableKey, variableValue);
    verify(caseExecutionCommandBuilderMock).execute();
  }

  @Test
  public void testPutSingleVariableWithTypeLong() {
    String variableKey = "aVariableKey";
    Long variableValue = Long.valueOf(123);
    String type = "Long";

    Map<String, Object> variableJson = VariablesBuilder.getVariableValueMap(variableValue, type);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", variableKey)
      .contentType(ContentType.JSON)
      .body(variableJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .put(SINGLE_CASE_EXECUTION_VARIABLE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).setVariable(variableKey, variableValue);
    verify(caseExecutionCommandBuilderMock).execute();
  }

  @Test
  public void testPutSingleLocalVariableWithUnparseableLong() {
    String variableKey = "aVariableKey";
    String variableValue = "1abc";
    String type = "Long";

    Map<String, Object> variableJson = VariablesBuilder.getVariableValueMap(variableValue, type);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", variableKey)
      .contentType(ContentType.JSON)
      .body(variableJson)
    .then()
      .expect()
        .statusCode(Status.BAD_REQUEST.getStatusCode())
        .body("type", equalTo(RestException.class.getSimpleName()))
        .body("message", equalTo("Cannot put case execution variable aVariableKey due to number format exception: For input string: \"1abc\""))
    .when()
      .put(SINGLE_CASE_EXECUTION_LOCAL_VARIABLE_URL);
  }

  @Test
  public void testPutSingleVariableWithUnparseableLong() {
    String variableKey = "aVariableKey";
    String variableValue = "1abc";
    String type = "Long";

    Map<String, Object> variableJson = VariablesBuilder.getVariableValueMap(variableValue, type);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", variableKey)
      .contentType(ContentType.JSON)
      .body(variableJson)
    .then()
      .expect()
        .statusCode(Status.BAD_REQUEST.getStatusCode())
        .body("type", equalTo(RestException.class.getSimpleName()))
        .body("message", equalTo("Cannot put case execution variable aVariableKey due to number format exception: For input string: \"1abc\""))
    .when()
      .put(SINGLE_CASE_EXECUTION_LOCAL_VARIABLE_URL);
  }

  @Test
  public void testPutSingleLocalVariableWithTypeDouble() {
    String variableKey = "aVariableKey";
    Double variableValue = 123.456;
    String type = "Double";

    Map<String, Object> variableJson = VariablesBuilder.getVariableValueMap(variableValue, type);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", variableKey)
      .contentType(ContentType.JSON)
      .body(variableJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .put(SINGLE_CASE_EXECUTION_LOCAL_VARIABLE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).setVariableLocal(variableKey, variableValue);
    verify(caseExecutionCommandBuilderMock).execute();
  }

  @Test
  public void testPutSingleVariableWithTypeDouble() {
    String variableKey = "aVariableKey";
    Double variableValue = 123.456;
    String type = "Double";

    Map<String, Object> variableJson = VariablesBuilder.getVariableValueMap(variableValue, type);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", variableKey)
      .contentType(ContentType.JSON)
      .body(variableJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .put(SINGLE_CASE_EXECUTION_VARIABLE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).setVariable(variableKey, variableValue);
    verify(caseExecutionCommandBuilderMock).execute();
  }

  @Test
  public void testPutSingleLocalVariableWithUnparseableDouble() {
    String variableKey = "aVariableKey";
    String variableValue = "1abc";
    String type = "Double";

    Map<String, Object> variableJson = VariablesBuilder.getVariableValueMap(variableValue, type);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", variableKey)
      .contentType(ContentType.JSON)
      .body(variableJson)
    .then()
      .expect()
        .statusCode(Status.BAD_REQUEST.getStatusCode())
        .body("type", equalTo(RestException.class.getSimpleName()))
        .body("message", equalTo("Cannot put case execution variable aVariableKey due to number format exception: For input string: \"1abc\""))
    .when()
      .put(SINGLE_CASE_EXECUTION_LOCAL_VARIABLE_URL);
  }

  @Test
  public void testPutSingleVariableWithUnparseableDouble() {
    String variableKey = "aVariableKey";
    String variableValue = "1abc";
    String type = "Double";

    Map<String, Object> variableJson = VariablesBuilder.getVariableValueMap(variableValue, type);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", variableKey)
      .contentType(ContentType.JSON)
      .body(variableJson)
    .then()
      .expect()
        .statusCode(Status.BAD_REQUEST.getStatusCode())
        .body("type", equalTo(RestException.class.getSimpleName()))
        .body("message", equalTo("Cannot put case execution variable aVariableKey due to number format exception: For input string: \"1abc\""))
    .when()
      .put(SINGLE_CASE_EXECUTION_VARIABLE_URL);
  }

  @Test
  public void testPutSingleLocalVariableWithTypeBoolean() {
    String variableKey = "aVariableKey";
    Boolean variableValue = true;
    String type = "Boolean";

    Map<String, Object> variableJson = VariablesBuilder.getVariableValueMap(variableValue, type);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", variableKey)
      .contentType(ContentType.JSON)
      .body(variableJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .put(SINGLE_CASE_EXECUTION_LOCAL_VARIABLE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).setVariableLocal(variableKey, variableValue);
    verify(caseExecutionCommandBuilderMock).execute();
  }

  @Test
  public void testPutSingleVariableWithTypeBoolean() {
    String variableKey = "aVariableKey";
    Boolean variableValue = true;
    String type = "Boolean";

    Map<String, Object> variableJson = VariablesBuilder.getVariableValueMap(variableValue, type);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", variableKey)
      .contentType(ContentType.JSON)
      .body(variableJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .put(SINGLE_CASE_EXECUTION_VARIABLE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).setVariable(variableKey, variableValue);
    verify(caseExecutionCommandBuilderMock).execute();
  }

  @Test
  public void testPutSingleLocalVariableWithTypeDate() throws Exception {
    Date now = new Date();
    SimpleDateFormat pattern = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    String variableKey = "aVariableKey";
    String variableValue = pattern.format(now);
    String type = "Date";

    Date expectedValue = pattern.parse(variableValue);

    Map<String, Object> variableJson = VariablesBuilder.getVariableValueMap(variableValue, type);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", variableKey)
      .contentType(ContentType.JSON)
      .body(variableJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .put(SINGLE_CASE_EXECUTION_LOCAL_VARIABLE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).setVariableLocal(variableKey, expectedValue);
    verify(caseExecutionCommandBuilderMock).execute();
  }

  @Test
  public void testPutSingleVariableWithTypeDate() throws Exception {
    Date now = new Date();
    SimpleDateFormat pattern = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    String variableKey = "aVariableKey";
    String variableValue = pattern.format(now);
    String type = "Date";

    Date expectedValue = pattern.parse(variableValue);

    Map<String, Object> variableJson = VariablesBuilder.getVariableValueMap(variableValue, type);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", variableKey)
      .contentType(ContentType.JSON)
      .body(variableJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .put(SINGLE_CASE_EXECUTION_VARIABLE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).setVariable(variableKey, expectedValue);
    verify(caseExecutionCommandBuilderMock).execute();
  }

  @Test
  public void testPutSingleLocalVariableWithUnparseableDate() {
    String variableKey = "aVariableKey";
    String variableValue = "1abc";
    String type = "Date";

    Map<String, Object> variableJson = VariablesBuilder.getVariableValueMap(variableValue, type);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", variableKey)
      .contentType(ContentType.JSON)
      .body(variableJson)
    .then()
      .expect()
        .statusCode(Status.BAD_REQUEST.getStatusCode())
        .body("type", equalTo(RestException.class.getSimpleName()))
        .body("message", equalTo("Cannot put case execution variable aVariableKey due to parse exception: Unparseable date: \"1abc\""))
    .when()
      .put(SINGLE_CASE_EXECUTION_LOCAL_VARIABLE_URL);
  }

  @Test
  public void testPutSingleVariableWithUnparseableDate() {
    String variableKey = "aVariableKey";
    String variableValue = "1abc";
    String type = "Date";

    Map<String, Object> variableJson = VariablesBuilder.getVariableValueMap(variableValue, type);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", variableKey)
      .contentType(ContentType.JSON)
      .body(variableJson)
    .then()
      .expect()
        .statusCode(Status.BAD_REQUEST.getStatusCode())
        .body("type", equalTo(RestException.class.getSimpleName()))
        .body("message", equalTo("Cannot put case execution variable aVariableKey due to parse exception: Unparseable date: \"1abc\""))
    .when()
      .put(SINGLE_CASE_EXECUTION_VARIABLE_URL);
  }

  @Test
  public void testPutSingleLocalVariableWithNotSupportedType() {
    String variableKey = "aVariableKey";
    String variableValue = "1abc";
    String type = "X";

    Map<String, Object> variableJson = VariablesBuilder.getVariableValueMap(variableValue, type);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", variableKey)
      .contentType(ContentType.JSON)
      .body(variableJson)
    .then()
      .expect()
        .statusCode(Status.BAD_REQUEST.getStatusCode())
        .body("type", equalTo(RestException.class.getSimpleName()))
        .body("message", equalTo("Cannot put case execution variable aVariableKey: The variable type 'X' is not supported."))
    .when()
      .put(SINGLE_CASE_EXECUTION_LOCAL_VARIABLE_URL);
  }

  @Test
  public void testPutSingleVariableWithNotSupportedType() {
    String variableKey = "aVariableKey";
    String variableValue = "1abc";
    String type = "X";

    Map<String, Object> variableJson = VariablesBuilder.getVariableValueMap(variableValue, type);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", variableKey)
      .contentType(ContentType.JSON)
      .body(variableJson)
    .then()
      .expect()
        .statusCode(Status.BAD_REQUEST.getStatusCode())
        .body("type", equalTo(RestException.class.getSimpleName()))
        .body("message", equalTo("Cannot put case execution variable aVariableKey: The variable type 'X' is not supported."))
    .when()
      .put(SINGLE_CASE_EXECUTION_VARIABLE_URL);
  }

  @Test
  public void testPutSingleLocalBinaryVariable() throws Exception {
    byte[] bytes = "someContent".getBytes();

    String variableKey = "aVariableKey";

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID).pathParam("varId", variableKey)
      .multiPart("data", "unspecified", bytes)
    .expect()
      .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(SINGLE_CASE_EXECUTION_LOCAL_VARIABLE_DATA_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).setVariableLocal(variableKey, bytes);
    verify(caseExecutionCommandBuilderMock).execute();
  }

  @Test
  public void testPutSingleBinaryVariable() throws Exception {
    byte[] bytes = "someContent".getBytes();

    String variableKey = "aVariableKey";

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID).pathParam("varId", variableKey)
      .multiPart("data", "unspecified", bytes)
    .expect()
      .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(SINGLE_CASE_EXECUTION_VARIABLE_DATA_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).setVariable(variableKey, bytes);
    verify(caseExecutionCommandBuilderMock).execute();
  }

  @Test
  public void testPutSingleLocalBinaryVariableWithNoValue() throws Exception {
    byte[] bytes = new byte[0];

    String variableKey = "aVariableKey";

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", variableKey)
      .multiPart("data", "unspecified", bytes)
    .expect()
      .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(SINGLE_CASE_EXECUTION_LOCAL_VARIABLE_DATA_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).setVariableLocal(variableKey, bytes);
    verify(caseExecutionCommandBuilderMock).execute();
  }

  @Test
  public void testPutSingleBinaryVariableWithNoValue() throws Exception {
    byte[] bytes = new byte[0];

    String variableKey = "aVariableKey";

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", variableKey)
      .multiPart("data", "unspecified", bytes)
    .expect()
      .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(SINGLE_CASE_EXECUTION_VARIABLE_DATA_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).setVariable(variableKey, bytes);
    verify(caseExecutionCommandBuilderMock).execute();
  }

  @Test
  public void testPutSingleLocalSerializableVariable() throws Exception {

    ArrayList<String> serializable = new ArrayList<String>();
    serializable.add("foo");

    ObjectMapper mapper = new ObjectMapper();
    String jsonBytes = mapper.writeValueAsString(serializable);
    String typeName = TypeFactory.type(serializable.getClass()).toCanonical();

    String variableKey = "aVariableKey";

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", variableKey)
      .multiPart("data", jsonBytes, MediaType.APPLICATION_JSON)
      .multiPart("type", typeName, MediaType.TEXT_PLAIN)
    .expect()
      .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(SINGLE_CASE_EXECUTION_LOCAL_VARIABLE_DATA_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).setVariableLocal(variableKey, serializable);
    verify(caseExecutionCommandBuilderMock).execute();
  }

  @Test
  public void testPutSingleSerializableVariable() throws Exception {

    ArrayList<String> serializable = new ArrayList<String>();
    serializable.add("foo");

    ObjectMapper mapper = new ObjectMapper();
    String jsonBytes = mapper.writeValueAsString(serializable);
    String typeName = TypeFactory.type(serializable.getClass()).toCanonical();

    String variableKey = "aVariableKey";

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", variableKey)
      .multiPart("data", jsonBytes, MediaType.APPLICATION_JSON)
      .multiPart("type", typeName, MediaType.TEXT_PLAIN)
    .expect()
      .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(SINGLE_CASE_EXECUTION_VARIABLE_DATA_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).setVariable(variableKey, serializable);
    verify(caseExecutionCommandBuilderMock).execute();
  }

  @Test
  public void testPutSingleLocalSerializableVariableUnsupportedMediaType() throws Exception {

    ArrayList<String> serializable = new ArrayList<String>();
    serializable.add("foo");

    ObjectMapper mapper = new ObjectMapper();
    String jsonBytes = mapper.writeValueAsString(serializable);
    String typeName = TypeFactory.type(serializable.getClass()).toCanonical();

    String variableKey = "aVariableKey";

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", variableKey)
      .multiPart("data", jsonBytes, "unsupported")
      .multiPart("type", typeName, MediaType.TEXT_PLAIN)
    .expect()
      .statusCode(Status.BAD_REQUEST.getStatusCode())
      .body(containsString("Unrecognized content type for serialized java type: unsupported"))
    .when()
      .post(SINGLE_CASE_EXECUTION_LOCAL_VARIABLE_DATA_URL);

    verify(caseServiceMock, never()).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
  }

  @Test
  public void testPutSingleSerializableVariableUnsupportedMediaType() throws Exception {

    ArrayList<String> serializable = new ArrayList<String>();
    serializable.add("foo");

    ObjectMapper mapper = new ObjectMapper();
    String jsonBytes = mapper.writeValueAsString(serializable);
    String typeName = TypeFactory.type(serializable.getClass()).toCanonical();

    String variableKey = "aVariableKey";

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", variableKey)
      .multiPart("data", jsonBytes, "unsupported")
      .multiPart("type", typeName, MediaType.TEXT_PLAIN)
    .expect()
      .statusCode(Status.BAD_REQUEST.getStatusCode())
      .body(containsString("Unrecognized content type for serialized java type: unsupported"))
    .when()
      .post(SINGLE_CASE_EXECUTION_VARIABLE_DATA_URL);

    verify(caseServiceMock, never()).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
  }

  @Test
  public void testPutSingleLocalVariableWithNoValue() {
    String variableKey = "aVariableKey";

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", variableKey)
      .contentType(ContentType.JSON)
      .body(EMPTY_JSON_OBJECT)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .put(SINGLE_CASE_EXECUTION_LOCAL_VARIABLE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).setVariableLocal(eq(variableKey), isNull());
    verify(caseExecutionCommandBuilderMock).execute();
  }

  @Test
  public void testPutSingleVariableWithNoValue() {
    String variableKey = "aVariableKey";

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", variableKey)
      .contentType(ContentType.JSON)
      .body(EMPTY_JSON_OBJECT)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .put(SINGLE_CASE_EXECUTION_VARIABLE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).setVariable(eq(variableKey), isNull());
    verify(caseExecutionCommandBuilderMock).execute();
  }

  @Test
  public void testPutLocalVariableForNonExistingExecution() {
    String variableKey = "aVariableKey";
    String variableValue = "aVariableValue";

    Map<String, Object> variableJson = VariablesBuilder.getVariableValueMap(variableValue);

    doThrow(new ProcessEngineException("expected exception")).when(caseExecutionCommandBuilderMock).execute();

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", variableKey)
      .contentType(ContentType.JSON)
      .body(variableJson)
    .then()
      .expect()
        .statusCode(Status.INTERNAL_SERVER_ERROR.getStatusCode())
        .body("type", is(RestException.class.getSimpleName()))
        .body("message", is("Cannot put case execution variable " + variableKey + ": expected exception"))
    .when()
      .put(SINGLE_CASE_EXECUTION_LOCAL_VARIABLE_URL);
  }

  @Test
  public void testPutVariableForNonExistingExecution() {
    String variableKey = "aVariableKey";
    String variableValue = "aVariableValue";

    Map<String, Object> variableJson = VariablesBuilder.getVariableValueMap(variableValue);

    doThrow(new ProcessEngineException("expected exception")).when(caseExecutionCommandBuilderMock).execute();

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", variableKey)
      .contentType(ContentType.JSON)
      .body(variableJson)
    .then()
      .expect()
        .statusCode(Status.INTERNAL_SERVER_ERROR.getStatusCode())
        .body("type", is(RestException.class.getSimpleName()))
        .body("message", is("Cannot put case execution variable " + variableKey + ": expected exception"))
    .when()
      .put(SINGLE_CASE_EXECUTION_VARIABLE_URL);
  }

  @Test
  public void testDeleteSingleLocalVariable() {
    String variableKey = "aVariableKey";

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", variableKey)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .delete(SINGLE_CASE_EXECUTION_LOCAL_VARIABLE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).removeVariableLocal(variableKey);
    verify(caseExecutionCommandBuilderMock).execute();
  }

  @Test
  public void testDeleteSingleVariable() {
    String variableKey = "aVariableKey";

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", variableKey)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .delete(SINGLE_CASE_EXECUTION_VARIABLE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).removeVariable(variableKey);
    verify(caseExecutionCommandBuilderMock).execute();
  }

  @Test
  public void testDeleteLocalVariableForNonExistingExecution() {
    String variableKey = "aVariableKey";

    doThrow(new ProcessEngineException("expected exception"))
      .when(caseExecutionCommandBuilderMock).execute();

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", variableKey)
    .then()
      .expect()
        .statusCode(Status.INTERNAL_SERVER_ERROR.getStatusCode())
        .contentType(ContentType.JSON)
        .body("type", is(RestException.class.getSimpleName()))
        .body("message", is("Cannot delete case execution variable " + variableKey + ": expected exception"))
    .when()
      .delete(SINGLE_CASE_EXECUTION_LOCAL_VARIABLE_URL);
  }

  @Test
  public void testDeleteVariableForNonExistingExecution() {
    String variableKey = "aVariableKey";

    doThrow(new ProcessEngineException("expected exception"))
      .when(caseExecutionCommandBuilderMock).execute();

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .pathParam("varId", variableKey)
    .then()
      .expect()
        .statusCode(Status.INTERNAL_SERVER_ERROR.getStatusCode())
        .contentType(ContentType.JSON)
        .body("type", is(RestException.class.getSimpleName()))
        .body("message", is("Cannot delete case execution variable " + variableKey + ": expected exception"))
    .when()
      .delete(SINGLE_CASE_EXECUTION_VARIABLE_URL);
  }

  @Test
  public void testComplete() {
    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(EMPTY_JSON_OBJECT)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_COMPLETE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).complete();
  }

  @Test
  public void testUnsuccessfulComplete() {
    doThrow(new ProcessEngineException("expected exception")).when(caseExecutionCommandBuilderMock).complete();

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(EMPTY_JSON_OBJECT)
    .then()
      .expect()
        .statusCode(Status.BAD_REQUEST.getStatusCode())
        .body("type", equalTo(InvalidRequestException.class.getSimpleName()))
        .body("message", containsString("Cannot complete case execution with id '" + MockProvider.EXAMPLE_CASE_EXECUTION_ID + "'."))
    .when()
      .post(CASE_EXECUTION_COMPLETE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).complete();
  }

  @Test
  public void testCompleteWithSetVariable() {
    String aVariableKey = "aKey";
    int aVariableValue = 123;

    String anotherVariableKey = "anotherKey";
    String anotherVariableValue = "abc";

    Map<String, Object> variablesJson = new HashMap<String, Object>();

    Map<String, Object> variables = VariablesBuilder
        .create()
          .variable(aVariableKey, aVariableValue, "Integer")
          .variable(anotherVariableKey, anotherVariableValue, "String")
          .getVariables();

    variablesJson.put("variables", variables);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(variablesJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_COMPLETE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).setVariable(aVariableKey, aVariableValue);
    verify(caseExecutionCommandBuilderMock).setVariable(anotherVariableKey, anotherVariableValue);
    verify(caseExecutionCommandBuilderMock).complete();
  }

  @Test
  public void testCompleteWithSetVariableLocal() {
    String aVariableKey = "aKey";
    int aVariableValue = 123;

    String anotherVariableKey = "anotherKey";
    String anotherVariableValue = "abc";

    Map<String, Object> variablesJson = new HashMap<String, Object>();

    Map<String, Object> variables = VariablesBuilder
        .create()
          .variable(aVariableKey, aVariableValue, "Integer", true)
          .variable(anotherVariableKey, anotherVariableValue, "String", true)
          .getVariables();

    variablesJson.put("variables", variables);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(variablesJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_COMPLETE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).setVariableLocal(aVariableKey, aVariableValue);
    verify(caseExecutionCommandBuilderMock).setVariableLocal(anotherVariableKey, anotherVariableValue);
    verify(caseExecutionCommandBuilderMock).complete();
  }

  @Test
  public void testCompleteWithSetVariableAndVariableLocal() {
    String aVariableKey = "aKey";
    int aVariableValue = 123;

    String anotherVariableKey = "anotherKey";
    String anotherVariableValue = "abc";

    Map<String, Object> variablesJson = new HashMap<String, Object>();

    Map<String, Object> variables = VariablesBuilder
        .create()
          .variable(aVariableKey, aVariableValue, "Integer")
          .variable(anotherVariableKey, anotherVariableValue, "String", true)
          .getVariables();

    variablesJson.put("variables", variables);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(variablesJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_COMPLETE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).setVariable(aVariableKey, aVariableValue);
    verify(caseExecutionCommandBuilderMock).setVariableLocal(anotherVariableKey, anotherVariableValue);
    verify(caseExecutionCommandBuilderMock).complete();
  }

  @Test
  public void testCompleteWithRemoveVariable() {
    String aVariableKey = "aKey";
    String anotherVariableKey = "anotherKey";

    List<VariableNameDto> variableNames = new ArrayList<VariableNameDto>();

    VariableNameDto firstVariableName = new VariableNameDto(aVariableKey);
    variableNames.add(firstVariableName);
    VariableNameDto secondVariableName = new VariableNameDto(anotherVariableKey);
    variableNames.add(secondVariableName);

    Map<String, Object> variablesJson = new HashMap<String, Object>();

    variablesJson.put("deletions", variableNames);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(variablesJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_COMPLETE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).removeVariable(aVariableKey);
    verify(caseExecutionCommandBuilderMock).removeVariable(anotherVariableKey);
    verify(caseExecutionCommandBuilderMock).complete();
  }

  @Test
  public void testCompleteWithRemoveVariableLocal() {
    String aVariableKey = "aKey";
    String anotherVariableKey = "anotherKey";

    List<VariableNameDto> variableNames = new ArrayList<VariableNameDto>();

    VariableNameDto firstVariableName = new VariableNameDto(aVariableKey, true);
    variableNames.add(firstVariableName);
    VariableNameDto secondVariableName = new VariableNameDto(anotherVariableKey, true);
    variableNames.add(secondVariableName);

    Map<String, Object> variablesJson = new HashMap<String, Object>();

    variablesJson.put("deletions", variableNames);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(variablesJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_COMPLETE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).removeVariableLocal(aVariableKey);
    verify(caseExecutionCommandBuilderMock).removeVariableLocal(anotherVariableKey);
    verify(caseExecutionCommandBuilderMock).complete();
  }

  @Test
  public void testCompleteWithRemoveVariableAndVariableLocal() {
    String aVariableKey = "aKey";
    String anotherVariableKey = "anotherKey";

    List<VariableNameDto> variableNames = new ArrayList<VariableNameDto>();

    VariableNameDto firstVariableName = new VariableNameDto(aVariableKey, true);
    variableNames.add(firstVariableName);
    VariableNameDto secondVariableName = new VariableNameDto(anotherVariableKey);
    variableNames.add(secondVariableName);

    Map<String, Object> variablesJson = new HashMap<String, Object>();

    variablesJson.put("deletions", variableNames);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(variablesJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_COMPLETE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).removeVariableLocal(aVariableKey);
    verify(caseExecutionCommandBuilderMock).removeVariable(anotherVariableKey);
    verify(caseExecutionCommandBuilderMock).complete();
  }

  @Test
  public void testCompleteWithSetVariableAndRemoveVariable() {
    String aVariableKey = "aKey";
    String anotherVariableKey = "anotherKey";
    String anotherVariableValue = "abc";

    Map<String, Object> variables = VariablesBuilder
        .create()
          .variable(anotherVariableKey, anotherVariableValue, "String")
          .getVariables();

    List<VariableNameDto> variableNames = new ArrayList<VariableNameDto>();

    VariableNameDto firstVariableName = new VariableNameDto(aVariableKey);
    variableNames.add(firstVariableName);

    Map<String, Object> variablesJson = new HashMap<String, Object>();

    variablesJson.put("variables", variables);
    variablesJson.put("deletions", variableNames);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(variablesJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_COMPLETE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).removeVariable(aVariableKey);
    verify(caseExecutionCommandBuilderMock).setVariable(anotherVariableKey, anotherVariableValue);
    verify(caseExecutionCommandBuilderMock).complete();
  }

  @Test
  public void testCompleteWithSetVariableAndRemoveVariableLocal() {
    String aVariableKey = "aKey";
    String anotherVariableKey = "anotherKey";
    String anotherVariableValue = "abc";

    Map<String, Object> variables = VariablesBuilder
        .create()
          .variable(anotherVariableKey, anotherVariableValue, "String")
          .getVariables();

    List<VariableNameDto> variableNames = new ArrayList<VariableNameDto>();

    VariableNameDto firstVariableName = new VariableNameDto(aVariableKey, true);
    variableNames.add(firstVariableName);

    Map<String, Object> variablesJson = new HashMap<String, Object>();

    variablesJson.put("variables", variables);
    variablesJson.put("deletions", variableNames);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(variablesJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_COMPLETE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).removeVariableLocal(aVariableKey);
    verify(caseExecutionCommandBuilderMock).setVariable(anotherVariableKey, anotherVariableValue);
    verify(caseExecutionCommandBuilderMock).complete();
  }

  @Test
  public void testCompleteWithSetVariableLocalAndRemoveVariable() {
    String aVariableKey = "aKey";
    String anotherVariableKey = "anotherKey";
    String anotherVariableValue = "abc";

    Map<String, Object> variables = VariablesBuilder
        .create()
          .variable(anotherVariableKey, anotherVariableValue, "String", true)
          .getVariables();

    List<VariableNameDto> variableNames = new ArrayList<VariableNameDto>();

    VariableNameDto firstVariableName = new VariableNameDto(aVariableKey);
    variableNames.add(firstVariableName);

    Map<String, Object> variablesJson = new HashMap<String, Object>();

    variablesJson.put("variables", variables);
    variablesJson.put("deletions", variableNames);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(variablesJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_COMPLETE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).removeVariable(aVariableKey);
    verify(caseExecutionCommandBuilderMock).setVariableLocal(anotherVariableKey, anotherVariableValue);
    verify(caseExecutionCommandBuilderMock).complete();
  }

  @Test
  public void testCompleteWithSetVariableLocalAndRemoveVariableLocal() {
    String aVariableKey = "aKey";
    String anotherVariableKey = "anotherKey";
    String anotherVariableValue = "abc";

    Map<String, Object> variables = VariablesBuilder
        .create()
          .variable(anotherVariableKey, anotherVariableValue, "String", true)
          .getVariables();

    List<VariableNameDto> variableNames = new ArrayList<VariableNameDto>();

    VariableNameDto firstVariableName = new VariableNameDto(aVariableKey, true);
    variableNames.add(firstVariableName);

    Map<String, Object> variablesJson = new HashMap<String, Object>();

    variablesJson.put("variables", variables);
    variablesJson.put("deletions", variableNames);

    given()
      .pathParam("id", MockProvider.EXAMPLE_CASE_EXECUTION_ID)
      .contentType(ContentType.JSON)
      .body(variablesJson)
    .then()
      .expect()
        .statusCode(Status.NO_CONTENT.getStatusCode())
    .when()
      .post(CASE_EXECUTION_COMPLETE_URL);

    verify(caseServiceMock).withCaseExecution(MockProvider.EXAMPLE_CASE_EXECUTION_ID);
    verify(caseExecutionCommandBuilderMock).removeVariableLocal(aVariableKey);
    verify(caseExecutionCommandBuilderMock).setVariableLocal(anotherVariableKey, anotherVariableValue);
    verify(caseExecutionCommandBuilderMock).complete();
  }

}
