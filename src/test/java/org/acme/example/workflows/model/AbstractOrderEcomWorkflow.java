package org.acme.example.workflows.model;

import eu.nonstatic.workflow.WorkflowStep;

public abstract class AbstractOrderEcomWorkflow extends AbstractOrderWorkflow implements OrderAnyDeliveryPaymentHint<OrderWorkflowLink> {

  protected AbstractOrderEcomWorkflow(String deliveryMode, WorkflowStep<String> start) {
    super(deliveryMode, start);
  }
}
