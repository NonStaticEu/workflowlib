package org.acme.example.workflows.model;

import org.acme.example.workflows.param.OrderState;

public class OrderWorkflowNode extends AbstractAcmeWorkflowNode<OrderWorkflowNode> {

  OrderWorkflowNode(String state) {
    super(state);
  }

  public boolean isWithdrawing() {
    return OrderState.isWithdrawing(state);
  }
  public boolean isWithdrawn() {
    return OrderState.isWithdrawn(state);
  }
}
