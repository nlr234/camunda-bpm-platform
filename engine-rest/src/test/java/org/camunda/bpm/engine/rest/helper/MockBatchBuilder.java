/* Licensed under the Apache License, Version 2.0 (the "License");
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

package org.camunda.bpm.engine.rest.helper;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.camunda.bpm.engine.batch.Batch;

public class MockBatchBuilder {

  protected String id;
  protected String type;
  protected int size;
  protected int jobsCreated;
  protected int batchJobsPerSeed;
  protected int invocationsPerBatchJob;
  protected String seedJobDefinitionId;
  protected String monitorJobDefinitionId;
  protected String batchJobDefinitionId;
  protected String tenantId;

  public MockBatchBuilder id(String id) {
    this.id = id;
    return this;
  }

  public MockBatchBuilder type(String type) {
    this.type = type;
    return this;
  }

  public MockBatchBuilder size(int size) {
    this.size = size;
    return this;
  }

  public MockBatchBuilder jobsCreated(int jobsCreated) {
    this.jobsCreated = jobsCreated;
    return this;
  }

  public MockBatchBuilder batchJobsPerSeed(int batchJobsPerSeed) {
    this.batchJobsPerSeed = batchJobsPerSeed;
    return this;
  }

  public MockBatchBuilder invocationsPerBatchJob(int invocationsPerBatchJob) {
    this.invocationsPerBatchJob = invocationsPerBatchJob;
    return this;
  }

  public MockBatchBuilder seedJobDefinitionId(String seedJobDefinitionId) {
    this.seedJobDefinitionId = seedJobDefinitionId;
    return this;
  }

  public MockBatchBuilder monitorJobDefinitionId(String monitorJobDefinitionId) {
    this.monitorJobDefinitionId = monitorJobDefinitionId;
    return this;
  }

  public MockBatchBuilder batchJobDefinitionId(String batchJobDefinitionId) {
    this.batchJobDefinitionId = batchJobDefinitionId;
    return this;
  }

  public MockBatchBuilder tenantId(String tenantId) {
    this.tenantId = tenantId;
    return this;
  }

  public Batch build() {
    Batch batch = mock(Batch.class);
    when(batch.getId()).thenReturn(id);
    when(batch.getType()).thenReturn(type);
    when(batch.getSize()).thenReturn(size);
    when(batch.getJobsCreated()).thenReturn(jobsCreated);
    when(batch.getBatchJobsPerSeed()).thenReturn(batchJobsPerSeed);
    when(batch.getInvocationsPerBatchJob()).thenReturn(invocationsPerBatchJob);
    when(batch.getSeedJobDefinitionId()).thenReturn(seedJobDefinitionId);
    when(batch.getMonitorJobDefinitionId()).thenReturn(monitorJobDefinitionId);
    when(batch.getBatchJobDefinitionId()).thenReturn(batchJobDefinitionId);
    when(batch.getTenantId()).thenReturn(tenantId);
    return batch;
  }

}
