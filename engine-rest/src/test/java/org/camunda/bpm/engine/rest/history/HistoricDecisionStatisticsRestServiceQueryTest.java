/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.camunda.bpm.engine.rest.history;

import org.camunda.bpm.engine.history.HistoricDecisionInstanceStatistics;
import org.camunda.bpm.engine.history.HistoricDecisionInstanceStatisticsQuery;
import org.camunda.bpm.engine.rest.AbstractRestServiceTest;
import org.camunda.bpm.engine.rest.helper.MockProvider;
import org.camunda.bpm.engine.rest.util.container.TestContainerRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.List;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Askar Akhmerov
 */
public class HistoricDecisionStatisticsRestServiceQueryTest extends AbstractRestServiceTest {

  @ClassRule
  public static TestContainerRule rule = new TestContainerRule();

  protected static final String HISTORY_URL = TEST_RESOURCE_ROOT_PATH + "/history";
  protected static final String HISTORIC_DECISION_STATISTICS_URL = HISTORY_URL + "/decision-requirements-definition/{id}/statistics";

  private HistoricDecisionInstanceStatisticsQuery historicDecisionInstanceStatisticsQuery;

  @Before
  public void setUpRuntimeData() {
    setupHistoricDecisionStatisticsMock();
  }

  private void setupHistoricDecisionStatisticsMock() {
    List<HistoricDecisionInstanceStatistics> mocks = MockProvider.createMockHistoricDecisionStatistics();

    historicDecisionInstanceStatisticsQuery = mock(HistoricDecisionInstanceStatisticsQuery.class);
    when(processEngine.getHistoryService()
        .createHistoricDecisionInstanceStatisticsQuery(eq(MockProvider.EXAMPLE_DECISION_REQUIREMENTS_DEFINITION_ID)))
    .thenReturn(historicDecisionInstanceStatisticsQuery);

    when(historicDecisionInstanceStatisticsQuery.decisionInstanceId(MockProvider.EXAMPLE_DECISION_INSTANCE_ID)).thenReturn(historicDecisionInstanceStatisticsQuery);
    when(historicDecisionInstanceStatisticsQuery.list()).thenReturn(mocks);
  }

  @Test
  public void testHistoricDefinitionInstanceStatisticsRetrieval() {
    given().pathParam("id", MockProvider.EXAMPLE_DECISION_REQUIREMENTS_DEFINITION_ID)
        .then().expect()
        .statusCode(Response.Status.OK.getStatusCode())
          .body("$.size()", is(2))
          .body("decisionDefinitionKey", hasItems(MockProvider.EXAMPLE_DECISION_DEFINITION_KEY, MockProvider.ANOTHER_DECISION_DEFINITION_KEY))
          .body("evaluations", hasItems(1, 2))
        .when().get(HISTORIC_DECISION_STATISTICS_URL);
  }

  @Test
  public void testHistoricDefinitionInstanceStatisticsRetrievalWithDefinitionInstance() {
    given().pathParam("id", MockProvider.EXAMPLE_DECISION_REQUIREMENTS_DEFINITION_ID)
        .queryParam("decisionInstanceId", MockProvider.EXAMPLE_DECISION_INSTANCE_ID)
        .then().expect()
        .statusCode(Response.Status.OK.getStatusCode())
          .body("$.size()", is(2))
          .body("decisionDefinitionKey", hasItems(MockProvider.EXAMPLE_DECISION_DEFINITION_KEY, MockProvider.ANOTHER_DECISION_DEFINITION_KEY))
          .body("evaluations", hasItems(1, 2))
        .when().get(HISTORIC_DECISION_STATISTICS_URL);
  }
}
