package org.acme.example.workflows.model;

import eu.nonstatic.workflow.AbstractWorkflowNode;
import java.util.Optional;
import org.acme.example.workflows.param.ItemState;
import org.acme.example.workflows.param.OrderState;

public interface OrderStoreDeliveryPaymentHint<L extends AbstractWorkflowNode<String, L>> extends OrderPaymentHint {
  Optional<L> peek(String state);

  @Override
  default boolean isPaid(String state) {
    return !OrderState.CANCELLED_UNPAID.equals(state)
        && peek(state)
        .map(node -> node.isAfter(OrderState.COLLECTABLE))
        .orElseThrow(() -> new IllegalArgumentException(state));
  }

  @Override
  default String getWithdrawnState(String state) {
    if(peek(state).isPresent()) {
      return ItemState.CANCELLED_UNPAID;
    } else {
      throw new IllegalArgumentException(state);
    }
  }
}
