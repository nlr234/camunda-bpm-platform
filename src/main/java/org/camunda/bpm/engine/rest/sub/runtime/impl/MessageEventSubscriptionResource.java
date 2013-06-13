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

import java.util.Map;

import javax.ws.rs.core.Response.Status;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.rest.dto.runtime.EventSubscriptionDto;
import org.camunda.bpm.engine.rest.dto.runtime.ExecutionTriggerDto;
import org.camunda.bpm.engine.rest.exception.InvalidRequestException;
import org.camunda.bpm.engine.rest.exception.RestException;
import org.camunda.bpm.engine.rest.sub.runtime.EventSubscriptionResource;
import org.camunda.bpm.engine.rest.util.DtoUtil;
import org.camunda.bpm.engine.runtime.EventSubscription;

public class MessageEventSubscriptionResource implements EventSubscriptionResource {

  protected static final String MESSAGE_EVENT_TYPE = "message";

  private ProcessEngine engine;
  private String executionId;
  private String messageName;
  
  public MessageEventSubscriptionResource(ProcessEngine engine, String executionId, String messageName) {
    this.engine = engine;
    this.executionId = executionId;
    this.messageName = messageName;
  }
  
  @Override
  public EventSubscriptionDto getEventSubscription() {
    RuntimeService runtimeService = engine.getRuntimeService();
    EventSubscription eventSubscription = runtimeService.createEventSubscriptionQuery()
        .executionId(executionId).eventName(messageName).eventType(MESSAGE_EVENT_TYPE).singleResult();
    
    if (eventSubscription == null) {
      String errorMessage = String.format("Message event subscription for execution %s named %s does not exist", executionId, messageName);
      throw new InvalidRequestException(Status.NOT_FOUND, errorMessage);
    }
    
    return EventSubscriptionDto.fromEventSubscription(eventSubscription);
  }

  @Override
  public void triggerEvent(ExecutionTriggerDto triggerDto) {
    RuntimeService runtimeService = engine.getRuntimeService();
    
    Map<String, Object> variables = DtoUtil.toMap(triggerDto.getVariables());
    
    try {
      runtimeService.messageEventReceived(messageName, executionId, variables);
    } catch (ProcessEngineException e) {
      throw new RestException(Status.INTERNAL_SERVER_ERROR, e, "Cannot trigger message " + messageName +
          " for execution " + executionId + ": " + e.getMessage());
    }
  }

}
