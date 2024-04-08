package org.acme.example.workflows.model;

import java.util.Optional;
import org.acme.example.workflows.param.ItemState;
import org.acme.example.workflows.param.OrderState;

public interface OrderAnyDeliveryPaymentHint<L extends AbstractAcmeWorkflowLink<L>> extends OrderPaymentHint {
  Optional<L> peek(String state);

  @Override
  default boolean isPaid(String state) {
    return !OrderState.CANCELLED_UNPAID.equals(state)
        && peek(state)
        .map(link -> link.isAfter(OrderState.PAYMENT_PENDING))
        .orElseThrow(() -> new IllegalArgumentException(state));
  }

  @Override
  default String getWithdrawnState(String state) {
    return isPaid(state) ? ItemState.CANCELLED_PAID : ItemState.CANCELLED_UNPAID;
  }
}
