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

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.camunda.bpm.engine.rest.dto.CountResultDto;
import org.camunda.bpm.engine.rest.dto.DeleteEngineEntityDto;
import org.camunda.bpm.engine.rest.dto.PatchVariablesDto;
import org.camunda.bpm.engine.rest.dto.runtime.ProcessInstanceDto;
import org.camunda.bpm.engine.rest.dto.runtime.ProcessInstanceQueryDto;
import org.camunda.bpm.engine.rest.dto.runtime.VariableListDto;
import org.camunda.bpm.engine.rest.dto.runtime.VariableValueDto;
import org.camunda.bpm.engine.rest.http.PATCH;
import org.camunda.bpm.engine.runtime.ProcessInstanceQuery;

@Path(ProcessInstanceRestService.PATH)
@Produces(MediaType.APPLICATION_JSON)
public interface ProcessInstanceRestService {

  public static final String PATH = "/process-instance";
  
  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  ProcessInstanceDto getProcessInstance(@PathParam("id") String processInstanceId);
  
  @DELETE
  @Path("/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  void deleteProcessInstance(@PathParam("id") String processInstanceId, DeleteEngineEntityDto dto);
  
  /**
   * Exposes the {@link ProcessInstanceQuery} interface as a REST service.
   * 
   * @param query
   * @param firstResult
   * @param maxResults
   * @return
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  List<ProcessInstanceDto> getProcessInstances(@Context UriInfo uriInfo,
      @QueryParam("firstResult") Integer firstResult,
      @QueryParam("maxResults") Integer maxResults);

  /**
   * Expects the same parameters as
   * {@link ProcessInstanceRestService#getProcessInstances(ProcessInstanceQueryDto, Integer, Integer)} (as a JSON message body)
   * and allows for any number of variable checks.
   * 
   * @param query
   * @param firstResult
   * @param maxResults
   * @return
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  List<ProcessInstanceDto> queryProcessInstances(ProcessInstanceQueryDto query,
      @QueryParam("firstResult") Integer firstResult,
      @QueryParam("maxResults") Integer maxResults);

  @GET
  @Path("/count")
  @Produces(MediaType.APPLICATION_JSON)
  CountResultDto getProcessInstancesCount(@Context UriInfo uriInfo);
  
  @POST
  @Path("/count")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  CountResultDto queryProcessInstancesCount(ProcessInstanceQueryDto query);

  @GET
  @Path("/{id}/variables")
  @Produces(MediaType.APPLICATION_JSON)
  VariableListDto getVariables(@PathParam("id") String processInstanceId);
  
  @GET
  @Path("/{id}/variables/{varId}")
  @Produces(MediaType.APPLICATION_JSON)
  VariableValueDto getVariable(@PathParam("id") String processInstanceId, @PathParam("varId") String variableName);
  
  @PUT
  @Path("/{id}/variables/{varId}")
  @Consumes(MediaType.APPLICATION_JSON)
  void putVariable(@PathParam("id") String processInstanceId, @PathParam("varId") String variableName, VariableValueDto variable);
  
  @DELETE
  @Path("/{id}/variables/{varId}")
  void deleteVariable(@PathParam("id") String processInstanceId, @PathParam("varId") String variableName);
  
  @PATCH
  @Path("/{id}/variables")
  @Consumes(MediaType.APPLICATION_JSON)
  void modifyVariables(@PathParam("id") String processInstanceId, PatchVariablesDto patch);
}
