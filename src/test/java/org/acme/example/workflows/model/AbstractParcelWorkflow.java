package org.acme.example.workflows.model;

import eu.nonstatic.workflow.WorkflowStep;
import org.acme.example.workflows.param.EntityType;

public abstract class AbstractParcelWorkflow extends AbstractAcmeWorkflow<ParcelWorkflowLink> {

  protected AbstractParcelWorkflow(String deliveryMode, WorkflowStep<String> start) {
    super(EntityType.PARCEL, deliveryMode, start);
  }

  @Override
  protected ParcelWorkflowLink newLink(String state) {
    return new ParcelWorkflowLink(state);
  }
}
