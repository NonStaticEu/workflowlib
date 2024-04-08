package org.acme.example.workflows.model;

import org.acme.example.workflows.param.ItemState;

public class ItemWorkflowLink extends AbstractAcmeWorkflowLink<ItemWorkflowLink> {

  ItemWorkflowLink(String state) {
    super(state);
  }

  public boolean isWithdrawing() {
    return ItemState.isWithdrawing(state);
  }
  public boolean isWithdrawn() {
    return ItemState.isWithdrawn(state);
  }
}
