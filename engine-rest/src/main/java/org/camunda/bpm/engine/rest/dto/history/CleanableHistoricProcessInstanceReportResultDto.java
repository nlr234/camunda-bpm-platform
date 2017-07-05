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

import java.util.ArrayList;
import java.util.List;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.CleanableHistoricProcessInstanceReport;
import org.camunda.bpm.engine.history.CleanableHistoricProcessInstanceReportResult;

public class CleanableHistoricProcessInstanceReportResultDto {

  protected String processDefinitionId;
  protected String processDefinitionKey;
  protected String processDefinitionName;
  protected int processDefinitionVersion;
  protected Integer historyTimeToLive;
  protected long finishedProcessInstancesCount;
  protected long cleanableProcessInstancesCount;

  public CleanableHistoricProcessInstanceReportResultDto() {
  }

  public void setProcessDefinitionId(String processDefinitionId) {
    this.processDefinitionId = processDefinitionId;
  }

  public void setProcessDefinitionKey(String processDefinitionKey) {
    this.processDefinitionKey = processDefinitionKey;
  }

  public void setProcessDefinitionName(String processDefinitionName) {
    this.processDefinitionName = processDefinitionName;
  }

  public void setProcessDefinitionVersion(int processDefinitionVersion) {
    this.processDefinitionVersion = processDefinitionVersion;
  }

  public void setHistoryTimeToLive(Integer historyTimeToLive) {
    this.historyTimeToLive = historyTimeToLive;
  }

  public void setFinishedProcessInstancesCount(Long finishedProcessInstancesCount) {
    this.finishedProcessInstancesCount = finishedProcessInstancesCount;
  }

  public void setCleanableProcessInstancesCount(Long cleanableProcessInstancesCount) {
    this.cleanableProcessInstancesCount = cleanableProcessInstancesCount;
  }

  public String getProcessDefinitionId() {
    return processDefinitionId;
  }

  public String getProcessDefinitionKey() {
    return processDefinitionKey;
  }

  public String getProcessDefinitionName() {
    return processDefinitionName;
  }

  public int getProcessDefinitionVersion() {
    return processDefinitionVersion;
  }

  public Integer getHistoryTimeToLive() {
    return historyTimeToLive;
  }

  public Long getFinishedProcessInstancesCount() {
    return finishedProcessInstancesCount;
  }

  public Long getCleanableProcessInstancesCount() {
    return cleanableProcessInstancesCount;
  }

  protected CleanableHistoricProcessInstanceReport createNewReportQuery(ProcessEngine engine) {
    return engine.getHistoryService().createCleanableHistoricProcessInstanceReport();
  }

  public static List<CleanableHistoricProcessInstanceReportResultDto> convert(List<CleanableHistoricProcessInstanceReportResult> reportResult) {
    List<CleanableHistoricProcessInstanceReportResultDto> dtos = new ArrayList<CleanableHistoricProcessInstanceReportResultDto>();
    for (CleanableHistoricProcessInstanceReportResult current : reportResult) {
      CleanableHistoricProcessInstanceReportResultDto dto = new CleanableHistoricProcessInstanceReportResultDto();
      dto.setProcessDefinitionId(current.getProcessDefinitionId());
      dto.setProcessDefinitionKey(current.getProcessDefinitionKey());
      dto.setProcessDefinitionName(current.getProcessDefinitionName());
      dto.setProcessDefinitionVersion(current.getProcessDefinitionVersion());
      dto.setHistoryTimeToLive(current.getHistoryTimeToLive());
      dto.setFinishedProcessInstancesCount(current.getFinishedProcessInstanceCount());
      dto.setCleanableProcessInstancesCount(current.getCleanableProcessInstanceCount());
      dtos.add(dto);
    }
    return dtos;
  }

}
