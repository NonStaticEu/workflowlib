package org.acme.example.workflows.model;

import org.acme.example.workflows.param.OrderState;

public class OrderWorkflowLink extends AbstractAcmeWorkflowLink<OrderWorkflowLink> {

  OrderWorkflowLink(String state) {
    super(state);
  }

  public boolean isWithdrawing() {
    return OrderState.isWithdrawing(state);
  }
  public boolean isWithdrawn() {
    return OrderState.isWithdrawn(state);
  }
}
