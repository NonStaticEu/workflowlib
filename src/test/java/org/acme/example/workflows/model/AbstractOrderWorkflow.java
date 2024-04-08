package org.acme.example.workflows.model;

import eu.nonstatic.workflow.WorkflowStep;
import org.acme.example.workflows.param.EntityType;

public abstract class AbstractOrderWorkflow extends AbstractAcmeWorkflow<OrderWorkflowLink> implements OrderPaymentHint {

  protected AbstractOrderWorkflow(String deliveryMode, WorkflowStep<String> start) {
    super(EntityType.ORDER, deliveryMode, start);
  }

  @Override
  protected OrderWorkflowLink newLink(String state) {
    return new OrderWorkflowLink(state);
  }
}
