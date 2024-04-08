package org.acme.example.workflows.model;

public interface OrderPaymentHint {
  default boolean isPaid(String state) {
    throw new UnsupportedOperationException();
  }

  default String getWithdrawnState(String state) {
    throw new UnsupportedOperationException();
  }
}
