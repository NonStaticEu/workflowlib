package org.acme.example.workflows.model;

import eu.nonstatic.workflow.WorkflowStep;
import org.acme.example.workflows.param.EntityType;

public abstract class AbstractLineItemWorkflow extends AbstractAcmeWorkflow<ItemWorkflowLink> {

  protected AbstractLineItemWorkflow(String deliveryMode, WorkflowStep<String> start) {
    super(EntityType.ITEM, deliveryMode, start);
  }

  @Override
  protected ItemWorkflowLink newLink(String state) {
    return new ItemWorkflowLink(state);
  }
}
