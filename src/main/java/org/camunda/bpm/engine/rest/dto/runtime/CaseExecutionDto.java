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

import org.camunda.bpm.engine.runtime.CaseExecution;


/**
 * @author Roman Smirnov
 *
 */
public class CaseExecutionDto {

  protected String id;
  protected String caseInstanceId;
  protected String caseDefinitionId;
  protected String activityId;
  protected String activityName;
  protected String parentId;
  protected boolean enabled;
  protected boolean active;
  protected boolean disabled;

  public String getId() {
    return id;
  }

  public String getCaseInstanceId() {
    return caseInstanceId;
  }

  public String getCaseDefinitionId() {
    return caseDefinitionId;
  }

  public String getActivityId() {
    return activityId;
  }

  public String getActivityName() {
    return activityName;
  }

  public String getParentId() {
    return parentId;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public boolean isActive() {
    return active;
  }

  public boolean isDisabled() {
    return disabled;
  }

  public static CaseExecutionDto fromCaseExecution(CaseExecution caseExecution) {
    CaseExecutionDto dto = new CaseExecutionDto();

    dto.id = caseExecution.getId();
    dto.caseInstanceId = caseExecution.getCaseInstanceId();
    dto.caseDefinitionId = caseExecution.getCaseDefinitionId();
    dto.activityId = caseExecution.getActivityId();
    dto.activityName = caseExecution.getActivityName();
    dto.parentId = caseExecution.getParentId();
    dto.active = caseExecution.isActive();
    dto.enabled = caseExecution.isEnabled();
    dto.disabled = caseExecution.isDisabled();

    return dto;
  }

}
