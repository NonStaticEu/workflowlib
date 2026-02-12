package org.acme.example.workflows.model;

import org.acme.example.workflows.param.ItemState;

public class ItemWorkflowNode extends AbstractAcmeWorkflowNode<ItemWorkflowNode> {

  ItemWorkflowNode(String state) {
    super(state);
  }

  public boolean isWithdrawing() {
    return ItemState.isWithdrawing(getState());
  }
  public boolean isWithdrawn() {
    return ItemState.isWithdrawn(getState());
  }
}
