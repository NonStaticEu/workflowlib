package org.acme.example.workflows.model;

import org.acme.example.workflows.param.ParcelState;

public class ParcelWorkflowNode extends AbstractAcmeWorkflowNode<ParcelWorkflowNode> {

  ParcelWorkflowNode(String state) {
    super(state);
  }

  public boolean isWithdrawing() {
    return ParcelState.isWithdrawing(state);
  }
  public boolean isWithdrawn() {
    return ParcelState.isWithdrawn(state);
  }
}
