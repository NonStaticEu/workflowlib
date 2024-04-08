package org.acme.example.workflows.model;

import eu.nonstatic.workflow.AbstractWorkflowLink;

public abstract class AbstractAcmeWorkflowLink<L extends AbstractAcmeWorkflowLink<L>> extends AbstractWorkflowLink<String, L> {

  protected AbstractAcmeWorkflowLink(String state) {
    super(state);
  }

  public abstract boolean isWithdrawing();
  public abstract boolean isWithdrawn();
}
