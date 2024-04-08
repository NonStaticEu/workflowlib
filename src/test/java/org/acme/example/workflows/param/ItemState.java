package org.acme.example.workflows.param;

import java.util.List;

public final class ItemState {
  public static final String QUEUED = "queued";
  public static final String FILED = "filed";
  public static final String NOT_IN_STOCK = "not_in_stock";
  public static final String PREORDER = "preorder";
  public static final String RESERVED_STORE = "reserved_store";
  public static final String RESERVED_WAREHOUSE = "reserved_warehouse";
  public static final String RESERVED_WAREHOUSE_ORDERED = "reserved_warehouse_ordered";
  public static final String RESERVED_WAREHOUSE_RECEIVED = "reserved_warehouse_received";
  public static final String RESERVED_WAREHOUSE_ON_SHELF = "reserved_warehouse_on_shelf";
  public static final String PROVIDED = "provided";
  public static final String TRANSFERRED = "transferred";
  public static final String PICKED = "picked";
  public static final String PAYMENT_PENDING = "payment_pending";
  public static final String FOOTPRINT = "footprint";
  public static final String PAID = "paid";
  public static final String FULFILLED = "fulfilled";
  public static final String CANCELLED_UNPAID = "cancelled_no_refund";
  public static final String CANCELLED_PAID = "cancelled_refund";
  public static final String BACK_TO_SHELF = "back_to_shelf";

  private static final List<String> ALL = List.of(QUEUED, FILED, NOT_IN_STOCK, RESERVED_STORE,
      PREORDER, RESERVED_WAREHOUSE, RESERVED_WAREHOUSE_ORDERED, RESERVED_WAREHOUSE_RECEIVED, RESERVED_WAREHOUSE_ON_SHELF,
      PROVIDED, TRANSFERRED, PICKED, PAYMENT_PENDING,
      PAID, FOOTPRINT, FULFILLED, CANCELLED_UNPAID, CANCELLED_PAID, BACK_TO_SHELF);

  private static final List<String> WITHDRAWING = List.of(CANCELLED_UNPAID, CANCELLED_PAID, BACK_TO_SHELF);
  private static final List<String> WITHDRAWN = List.of(CANCELLED_UNPAID, CANCELLED_PAID);

  private ItemState() {}

  public static List<String> getAll() {
    return ALL;
  }

  public static boolean isKnown(String someState) {
    return ALL.contains(someState);
  }

  public static boolean isWithdrawing(String someState) {
    return WITHDRAWING.contains(someState);
  }

  public static boolean isWithdrawn(String someState) {
    return WITHDRAWN.contains(someState);
  }
}
