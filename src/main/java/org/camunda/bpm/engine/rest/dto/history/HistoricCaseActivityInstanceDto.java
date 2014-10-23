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
package org.camunda.bpm.engine.rest.dto.history;

import java.util.Date;

import org.camunda.bpm.engine.history.HistoricCaseActivityInstance;

public class HistoricCaseActivityInstanceDto {

  private String id;
  private String parentCaseActivityInstanceId;
  private String caseActivityId;
  private String caseActivityName;
  private String caseDefinitionId;
  private String caseInstanceId;
  private String caseExecutionId;
  private String taskId;
  private String calledProcessInstanceId;
  private String calledCaseInstanceId;
  private Date createTime;
  private Date endTime;
  private Long durationInMillis;
  private Boolean available;
  private Boolean enabled;
  private Boolean disabled;
  private Boolean active;
  private Boolean failed;
  private Boolean suspended;
  private Boolean completed;
  private Boolean terminated;

  public String getId() {
    return id;
  }

  public String getParentCaseActivityInstanceId() {
    return parentCaseActivityInstanceId;
  }

  public String getCaseActivityId() {
    return caseActivityId;
  }

  public String getCaseActivityName() {
    return caseActivityName;
  }

  public String getCaseDefinitionId() {
    return caseDefinitionId;
  }

  public String getCaseInstanceId() {
    return caseInstanceId;
  }

  public String getCaseExecutionId() {
    return caseExecutionId;
  }

  public String getTaskId() {
    return taskId;
  }

  public String getCalledProcessInstanceId() {
    return calledProcessInstanceId;
  }

  public String getCalledCaseInstanceId() {
    return calledCaseInstanceId;
  }

  public Date getCreateTime() {
    return createTime;
  }

  public Date getEndTime() {
    return endTime;
  }

  public Long getDurationInMillis() {
    return durationInMillis;
  }

  public Boolean getAvailable() {
    return available;
  }

  public Boolean getEnabled() {
    return enabled;
  }

  public Boolean getDisabled() {
    return disabled;
  }

  public Boolean getActive() {
    return active;
  }

  public Boolean getFailed() {
    return failed;
  }

  public Boolean getSuspended() {
    return suspended;
  }

  public Boolean getCompleted() {
    return completed;
  }

  public Boolean getTerminated() {
    return terminated;
  }

  public static HistoricCaseActivityInstanceDto fromHistoricCaseActivityInstance(HistoricCaseActivityInstance historicCaseActivityInstance) {

    HistoricCaseActivityInstanceDto dto = new HistoricCaseActivityInstanceDto();

    dto.id = historicCaseActivityInstance.getId();
    dto.parentCaseActivityInstanceId = historicCaseActivityInstance.getParentCaseActivityInstanceId();
    dto.caseActivityId = historicCaseActivityInstance.getCaseActivityId();
    dto.caseActivityName = historicCaseActivityInstance.getCaseActivityName();
    dto.caseDefinitionId = historicCaseActivityInstance.getCaseDefinitionId();
    dto.caseInstanceId = historicCaseActivityInstance.getCaseInstanceId();
    dto.caseExecutionId = historicCaseActivityInstance.getCaseExecutionId();
    dto.taskId = historicCaseActivityInstance.getTaskId();
    dto.calledProcessInstanceId = historicCaseActivityInstance.getCalledProcessInstanceId();
    dto.calledCaseInstanceId = historicCaseActivityInstance.getCalledCaseInstanceId();
    dto.createTime = historicCaseActivityInstance.getCreateTime();
    dto.endTime = historicCaseActivityInstance.getEndTime();
    dto.durationInMillis = historicCaseActivityInstance.getDurationInMillis();
    dto.available = historicCaseActivityInstance.isAvailable();
    dto.enabled = historicCaseActivityInstance.isEnabled();
    dto.disabled = historicCaseActivityInstance.isDisabled();
    dto.active = historicCaseActivityInstance.isActive();
    dto.failed = historicCaseActivityInstance.isFailed();
    dto.suspended = historicCaseActivityInstance.isSuspended();
    dto.completed = historicCaseActivityInstance.isCompleted();
    dto.terminated = historicCaseActivityInstance.isTerminated();

    return dto;
  }
}
