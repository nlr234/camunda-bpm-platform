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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.camunda.bpm.engine.repository.ProcessDefinitionQuery;
import org.camunda.bpm.engine.rest.dto.CountResultDto;
import org.camunda.bpm.engine.rest.dto.StatisticsResultDto;
import org.camunda.bpm.engine.rest.dto.repository.ProcessDefinitionDiagramDto;
import org.camunda.bpm.engine.rest.dto.repository.ProcessDefinitionDto;
import org.camunda.bpm.engine.rest.dto.runtime.ProcessInstanceDto;
import org.camunda.bpm.engine.rest.dto.runtime.StartProcessInstanceDto;
import org.camunda.bpm.engine.rest.dto.task.FormDto;

@Path(ProcessDefinitionRestService.PATH)
@Produces(MediaType.APPLICATION_JSON)
public interface ProcessDefinitionRestService {

  public static final String APPLICATION_BPMN20_XML = "application/bpmn20+xml";
  public static final MediaType APPLICATION_BPMN20_XML_TYPE =
      new MediaType("application", "bpmn20+xml");
  
  public static final String PATH = "/process-definition";

  /**
   * Exposes the {@link ProcessDefinitionQuery} interface as a REST service.
   * @param query
   * @param firstResult
   * @param maxResults
   * @return
   */
	@GET
  @Produces(MediaType.APPLICATION_JSON)
	List<ProcessDefinitionDto> getProcessDefinitions(@Context UriInfo uriInfo,
	    @QueryParam("firstResult") Integer firstResult, @QueryParam("maxResults") Integer maxResults);

	@GET
  @Path("/count")
  @Produces(MediaType.APPLICATION_JSON)
  CountResultDto getProcessDefinitionsCount(@Context UriInfo uriInfo);

	@GET
	@Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
	ProcessDefinitionDto getProcessDefinition(@PathParam("id") String processDefinitionId);

	@GET
	@Path("/{id}/xml")
  @Produces(MediaType.APPLICATION_JSON)
	ProcessDefinitionDiagramDto getProcessDefinitionBpmn20Xml(@PathParam("id") String processDefinitionId);

	@POST
	@Path("/{id}/start")
	@Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
	ProcessInstanceDto startProcessInstance(@Context UriInfo context, @PathParam("id") String processDefinitionId, StartProcessInstanceDto parameters);

	@GET
	@Path("/statistics")
  @Produces(MediaType.APPLICATION_JSON)
	List<StatisticsResultDto> getStatistics(@QueryParam("failedJobs") Boolean includeFailedJobs);

	@GET
	@Path("/{id}/statistics")
  @Produces(MediaType.APPLICATION_JSON)
  List<StatisticsResultDto> getActivityStatistics(@PathParam("id") String processDefinitionId, @QueryParam("failedJobs") Boolean includeFailedJobs);

  @GET
  @Path("/{id}/startForm")
  @Produces(MediaType.APPLICATION_JSON)
  FormDto getStartForm(@PathParam("id") String processDefinitionId);
}
