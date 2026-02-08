package org.acme.example.workflows.model;

import eu.nonstatic.workflow.WorkflowStep;
import org.acme.example.workflows.param.EntityType;

public abstract class AbstractLineItemWorkflow extends AbstractAcmeWorkflow<ItemWorkflowNode> {

  protected AbstractLineItemWorkflow(String deliveryMode, WorkflowStep<String> start) {
    super(EntityType.ITEM, deliveryMode, start);
  }

  @Override
  protected ItemWorkflowNode newNode(String state) {
    return new ItemWorkflowNode(state);
  }
}
