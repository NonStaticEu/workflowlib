package org.acme.example.workflows.model;

import eu.nonstatic.workflow.AbstractWorkflowNode;

public abstract class AbstractAcmeWorkflowNode<L extends AbstractAcmeWorkflowNode<L>> extends
    AbstractWorkflowNode<String, L> {

  protected AbstractAcmeWorkflowNode(String state) {
    super(state);
  }

  public abstract boolean isWithdrawing();
  public abstract boolean isWithdrawn();
}
