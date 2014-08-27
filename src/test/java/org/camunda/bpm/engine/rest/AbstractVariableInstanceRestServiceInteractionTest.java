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
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.*;

import javax.ws.rs.core.Response.Status;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.impl.variable.ByteArrayType;
import org.camunda.bpm.engine.impl.variable.SerializableType;
import org.camunda.bpm.engine.rest.helper.MockProvider;
import org.camunda.bpm.engine.rest.helper.MockSerializedValueBuilder;
import org.camunda.bpm.engine.rest.helper.MockVariableInstanceBuilder;
import org.camunda.bpm.engine.runtime.VariableInstance;
import org.camunda.bpm.engine.runtime.VariableInstanceQuery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;

/**
 * @author Daniel Meyer
 *
 */
public class AbstractVariableInstanceRestServiceInteractionTest extends AbstractRestServiceTest {

  protected static final String SERVICE_URL = TEST_RESOURCE_ROOT_PATH + "/variable-instance";
  protected static final String VARIABLE_INSTANCE_URL = SERVICE_URL + "/{id}";
  protected static final String VARIABLE_INSTANCE_BINARY_DATA_URL = VARIABLE_INSTANCE_URL + "/data";

  protected RuntimeService runtimeServiceMock;

  protected VariableInstanceQuery variableInstanceQueryMock;

  @Before
  public void setupTestData() {
    runtimeServiceMock = mock(RuntimeService.class);
    variableInstanceQueryMock = mock(VariableInstanceQuery.class);

    // mock runtime service.
    when(processEngine.getRuntimeService()).thenReturn(runtimeServiceMock);
    when(runtimeServiceMock.createVariableInstanceQuery()).thenReturn(variableInstanceQueryMock);
  }

  @Test
  public void testGetSingleVariableInstance() {

    MockVariableInstanceBuilder builder = MockProvider.mockVariableInstance();
    VariableInstance variableInstanceMock = builder.build();

    when(variableInstanceQueryMock.variableId(variableInstanceMock.getId())).thenReturn(variableInstanceQueryMock);
    when(variableInstanceQueryMock.disableBinaryFetching()).thenReturn(variableInstanceQueryMock);
    when(variableInstanceQueryMock.singleResult()).thenReturn(variableInstanceMock);

    given().pathParam("id", MockProvider.EXAMPLE_VARIABLE_INSTANCE_ID)
    .then().expect().statusCode(Status.OK.getStatusCode())
    .and()
      .body("id", equalTo(builder.getId()))
      .body("name", equalTo(builder.getName()))
      .body("type", equalTo(builder.getValueTypeName()))
      .body("value", equalTo(builder.getValue()))
      .body("processInstanceId", equalTo(builder.getProcessInstanceId()))
      .body("executionId", equalTo(builder.getExecutionId()))
      .body("caseInstanceId", equalTo(builder.getCaseInstanceId()))
      .body("caseExecutionId", equalTo(builder.getCaseExecutionId()))
      .body("taskId", equalTo(builder.getTaskId()))
      .body("activityInstanceId", equalTo(builder.getActivityInstanceId()))
      .body("errorMessage", equalTo(builder.getErrorMessage()))
      .body("serializedValue", nullValue())
    .when().get(VARIABLE_INSTANCE_URL);

    verify(variableInstanceQueryMock, times(1)).disableBinaryFetching();

  }

  @Test
  public void testGetSingleVariableInstanceForBinaryVariable() {
    final ByteArrayType type = new ByteArrayType();

    MockVariableInstanceBuilder builder = MockProvider.mockVariableInstance();
    VariableInstance variableInstanceMock =
        builder
          .typeName(type.getTypeName())
          .valueTypeName("byte[]")
          .value(null)
          .serializedValue(null)
          .build();

    when(variableInstanceQueryMock.variableId(variableInstanceMock.getId())).thenReturn(variableInstanceQueryMock);
    when(variableInstanceQueryMock.disableBinaryFetching()).thenReturn(variableInstanceQueryMock);
    when(variableInstanceQueryMock.singleResult()).thenReturn(variableInstanceMock);

    given().pathParam("id", MockProvider.EXAMPLE_VARIABLE_INSTANCE_ID)
    .then().expect().statusCode(Status.OK.getStatusCode())
    .and()
      .body("type", equalTo("byte[]"))
      .body("value", nullValue())
      .body("serializedValue", nullValue())
    .when().get(VARIABLE_INSTANCE_URL);

    verify(variableInstanceQueryMock, times(1)).disableBinaryFetching();

  }

  @Test
  public void testGetNonExistingVariableInstance() {

    String nonExistingId = "nonExistingId";

    when(variableInstanceQueryMock.variableId(nonExistingId)).thenReturn(variableInstanceQueryMock);
    when(variableInstanceQueryMock.disableBinaryFetching()).thenReturn(variableInstanceQueryMock);
    when(variableInstanceQueryMock.singleResult()).thenReturn(null);

    given().pathParam("id", nonExistingId)
    .then().expect().statusCode(Status.NOT_FOUND.getStatusCode())
    .body(containsString("Variable instance with Id 'nonExistingId' does not exist."))
    .when().get(VARIABLE_INSTANCE_URL);

    verify(variableInstanceQueryMock, times(1)).disableBinaryFetching();

  }

  @Test
  public void testBinaryDataForBinaryVariable() {
    final ByteArrayType type = new ByteArrayType();
    final byte[] byteContent = "some bytes".getBytes();

    MockSerializedValueBuilder serializedValueBuilder =
        new MockSerializedValueBuilder()
          .value(byteContent);

    VariableInstance variableInstanceMock =
        MockProvider.mockVariableInstance()
          .valueTypeName(type.getTypeNameForValue(null))
          .typeName(type.getTypeName())
          .value(byteContent)
          .serializedValue(serializedValueBuilder)
          .build();

    when(variableInstanceQueryMock.variableId(variableInstanceMock.getId())).thenReturn(variableInstanceQueryMock);
    when(variableInstanceQueryMock.singleResult()).thenReturn(variableInstanceMock);

    Response response = given().pathParam("id", MockProvider.EXAMPLE_VARIABLE_INSTANCE_ID)
    .then().expect()
      .statusCode(Status.OK.getStatusCode())
      .contentType(ContentType.BINARY.toString())
    .when().get(VARIABLE_INSTANCE_BINARY_DATA_URL);

    byte[] responseBytes = response.getBody().asByteArray();
    Assert.assertEquals(new String(byteContent), new String(responseBytes));
    verify(variableInstanceQueryMock, never()).disableBinaryFetching();

  }

  @Test
  public void testBinaryDataForSerializableVariable() {
    final SerializableType type = new SerializableType();
    String value = "some bytes";
    final byte[] serializedValue = value.getBytes();

    MockSerializedValueBuilder serializedValueBuilder =
        new MockSerializedValueBuilder()
          .value(serializedValue);

    VariableInstance variableInstanceMock =
        MockProvider.mockVariableInstance()
          .valueTypeName(type.getTypeNameForValue(null))
          .typeName(type.getTypeName())
          .value(value)
          .serializedValue(serializedValueBuilder)
          .build();

    when(variableInstanceQueryMock.variableId(variableInstanceMock.getId())).thenReturn(variableInstanceQueryMock);
    when(variableInstanceQueryMock.singleResult()).thenReturn(variableInstanceMock);

    Response response = given().pathParam("id", MockProvider.EXAMPLE_VARIABLE_INSTANCE_ID)
    .then().expect()
      .statusCode(Status.OK.getStatusCode())
      .contentType(ContentType.BINARY.toString())
    .when().get(VARIABLE_INSTANCE_BINARY_DATA_URL);

    byte[] responseBytes = response.getBody().asByteArray();
    Assert.assertEquals(new String(serializedValue), new String(responseBytes));
    verify(variableInstanceQueryMock, never()).disableBinaryFetching();

  }

  @Test
  public void testBinaryDataForNonBinaryVariable() {
    VariableInstance variableInstanceMock = MockProvider.mockVariableInstance().build();

    when(variableInstanceQueryMock.variableId(variableInstanceMock.getId())).thenReturn(variableInstanceQueryMock);
    when(variableInstanceQueryMock.singleResult()).thenReturn(variableInstanceMock);

    given().pathParam("id", MockProvider.EXAMPLE_VARIABLE_INSTANCE_ID)
    .then().expect()
      .statusCode(Status.BAD_REQUEST.getStatusCode())
      .body(containsString("Variable instance with Id '"+variableInstanceMock.getId()+"' is not a binary variable"))
    .when().get(VARIABLE_INSTANCE_BINARY_DATA_URL);

    verify(variableInstanceQueryMock, never()).disableBinaryFetching();

  }

  @Test
  public void testGetBinaryDataForNonExistingVariableInstance() {

    String nonExistingId = "nonExistingId";

    when(variableInstanceQueryMock.variableId(nonExistingId)).thenReturn(variableInstanceQueryMock);
    when(variableInstanceQueryMock.singleResult()).thenReturn(null);

    given().pathParam("id", nonExistingId)
    .then().expect().statusCode(Status.NOT_FOUND.getStatusCode())
    .body(containsString("Variable instance with Id 'nonExistingId' does not exist."))
    .when().get(VARIABLE_INSTANCE_BINARY_DATA_URL);

    verify(variableInstanceQueryMock, never()).disableBinaryFetching();

  }

}
