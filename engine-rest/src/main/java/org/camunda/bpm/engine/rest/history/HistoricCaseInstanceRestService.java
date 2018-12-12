/*
 * Copyright © 2013-2018 camunda services GmbH and various authors (info@camunda.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.engine.rest.history;

import org.camunda.bpm.engine.history.HistoricCaseInstanceQuery;
import org.camunda.bpm.engine.rest.dto.CountResultDto;
import org.camunda.bpm.engine.rest.dto.history.HistoricCaseInstanceDto;
import org.camunda.bpm.engine.rest.dto.history.HistoricCaseInstanceQueryDto;
import org.camunda.bpm.engine.rest.sub.history.HistoricCaseInstanceResource;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Path(HistoricCaseInstanceRestService.PATH)
@Produces(MediaType.APPLICATION_JSON)
public interface HistoricCaseInstanceRestService {

  public static final String PATH = "/case-instance";

  @Path("/{id}")
  HistoricCaseInstanceResource getHistoricCaseInstance(@PathParam("id") String caseInstanceId);

  /**
   * Exposes the {@link HistoricCaseInstanceQuery} interface as a REST
   * service.
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  List<HistoricCaseInstanceDto> getHistoricCaseInstances(@Context UriInfo uriInfo,
                                                         @QueryParam("firstResult") Integer firstResult,
                                                         @QueryParam("maxResults") Integer maxResults);

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  List<HistoricCaseInstanceDto> queryHistoricCaseInstances(HistoricCaseInstanceQueryDto query,
                                                           @QueryParam("firstResult") Integer firstResult,
                                                           @QueryParam("maxResults") Integer maxResults);

  @GET
  @Path("/count")
  @Produces(MediaType.APPLICATION_JSON)
  CountResultDto getHistoricCaseInstancesCount(@Context UriInfo uriInfo);

  @POST
  @Path("/count")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  CountResultDto queryHistoricCaseInstancesCount(HistoricCaseInstanceQueryDto query);

}
