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
package org.camunda.bpm.engine.rest.dto.runtime;

import java.util.Map;

import org.camunda.bpm.engine.delegate.ProcessEngineVariableType;
import org.camunda.bpm.engine.runtime.VariableInstance;

/**
 * @author roman.smirnov
 */
public class VariableInstanceDto {

  protected String id;
  protected String name;
  protected String type;
  protected String variableType;
  protected Object value;
  protected Map<String, Object> serializationConfig;
  protected String processInstanceId;
  protected String executionId;
  protected String caseInstanceId;
  protected String caseExecutionId;
  protected String taskId;
  protected String activityInstanceId;
  protected String errorMessage;

  public VariableInstanceDto() { }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getVariableType() {
    return variableType;
  }

  public void setVariableType(String variableType) {
    this.variableType = variableType;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public String getProcessInstanceId() {
    return processInstanceId;
  }

  public void setProcessInstanceId(String processInstanceId) {
    this.processInstanceId = processInstanceId;
  }

  public String getExecutionId() {
    return executionId;
  }

  public void setExecutionId(String executionId) {
    this.executionId = executionId;
  }

  public String getTaskId() {
    return taskId;
  }

  public void setTaskId(String taskId) {
    this.taskId = taskId;
  }

  public String getActivityInstanceId() {
    return activityInstanceId;
  }

  public void setActivityInstanceId(String activityInstanceId) {
    this.activityInstanceId = activityInstanceId;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public String getCaseExecutionId() {
    return caseExecutionId;
  }

  public String getCaseInstanceId() {
    return caseInstanceId;
  }

  public Map<String, Object> getSerializationConfig() {
    return serializationConfig;
  }

  public void setSerializationConfig(Map<String, Object> serializationConfig) {
    this.serializationConfig = serializationConfig;
  }

  public static VariableInstanceDto fromVariableInstance(VariableInstance variableInstance) {
    VariableInstanceDto dto = new VariableInstanceDto();

    dto.id = variableInstance.getId();
    dto.name = variableInstance.getName();
    dto.processInstanceId = variableInstance.getProcessInstanceId();
    dto.executionId = variableInstance.getExecutionId();

    dto.caseExecutionId = variableInstance.getCaseExecutionId();
    dto.caseInstanceId = variableInstance.getCaseInstanceId();

    dto.taskId = variableInstance.getTaskId();
    dto.activityInstanceId = variableInstance.getActivityInstanceId();

    if (variableInstance.storesCustomObjects()) {
      if (ProcessEngineVariableType.SERIALIZABLE.getName().equals(variableInstance.getTypeName())) {
        if (variableInstance.getValue() != null) {
          dto.value = new SerializedObjectDto(variableInstance.getValue());
        }
      } else {
        dto.value = variableInstance.getSerializedValue().getValue();
        dto.serializationConfig = variableInstance.getSerializedValue().getConfig();
      }
    } else {
      dto.value = variableInstance.getValue();
    }

    dto.errorMessage = variableInstance.getErrorMessage();
    dto.type = variableInstance.getValueTypeName();
    dto.variableType = variableInstance.getTypeName();

    return dto;
  }

}
