package org.acme.example.workflows.model;

import eu.nonstatic.workflow.WorkflowStep;

public abstract class AbstractOrderEcomWorkflow extends AbstractOrderWorkflow implements OrderAnyDeliveryPaymentHint<OrderWorkflowNode> {

  protected AbstractOrderEcomWorkflow(String deliveryMode, WorkflowStep<String> start) {
    super(deliveryMode, start);
  }
}
