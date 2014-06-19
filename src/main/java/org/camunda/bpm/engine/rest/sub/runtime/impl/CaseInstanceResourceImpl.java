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
package org.camunda.bpm.engine.rest.sub.runtime.impl;

import javax.ws.rs.core.Response.Status;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.rest.dto.runtime.CaseInstanceDto;
import org.camunda.bpm.engine.rest.exception.InvalidRequestException;
import org.camunda.bpm.engine.rest.sub.VariableResource;
import org.camunda.bpm.engine.rest.sub.runtime.CaseInstanceResource;
import org.camunda.bpm.engine.runtime.CaseInstance;

/**
 *
 * @author Roman Smirnov
 *
 */
public class CaseInstanceResourceImpl implements CaseInstanceResource {

  protected ProcessEngine engine;
  protected String caseInstanceId;

  public CaseInstanceResourceImpl(ProcessEngine engine, String caseInstanceId) {
    this.engine = engine;
    this.caseInstanceId = caseInstanceId;
  }

  public CaseInstanceDto getCaseInstance() {
    CaseService caseService = engine.getCaseService();

    CaseInstance instance = caseService
        .createCaseInstanceQuery()
        .caseInstanceId(caseInstanceId)
        .singleResult();

    if (instance == null) {
      throw new InvalidRequestException(Status.NOT_FOUND, "Case instance with id " + caseInstanceId + " does not exist.");
    }

    CaseInstanceDto result = CaseInstanceDto.fromCaseInstance(instance);
    return result;
  }

  public VariableResource getVariablesResource() {
    return new CaseExecutionVariablesResource(engine, caseInstanceId);
  }

}
