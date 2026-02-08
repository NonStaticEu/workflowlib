package org.acme.example.workflows.model;

import eu.nonstatic.workflow.WorkflowStep;
import org.acme.example.workflows.param.EntityType;

public abstract class AbstractOrderWorkflow extends AbstractAcmeWorkflow<OrderWorkflowNode> implements OrderPaymentHint {

  protected AbstractOrderWorkflow(String deliveryMode, WorkflowStep<String> start) {
    super(EntityType.ORDER, deliveryMode, start);
  }

  @Override
  protected OrderWorkflowNode newNode(String state) {
    return new OrderWorkflowNode(state);
  }
}
