package org.acme.example.workflows.param;

import java.util.List;

public final class ParcelState {
  public static final String NEW = "new";
  public static final String PACKED = "bagged";
  public static final String DISPATCHED = "dispatched";
  public static final String RECEIVED = "received";
  public static final String DELIVERED = "delivered";
  public static final String COLLECTED = "collected";
  public static final String RETURNING = "returning";
  public static final String RETURNED = "returned";
  public static final String CANCELLED_UNPAID = "cancelled_no_refund";
  public static final String CANCELLED_PAID = "cancelled_refund";

  private static final List<String> ALL = List.of(NEW, PACKED, DISPATCHED,
      RECEIVED,DELIVERED, COLLECTED, RETURNING, RETURNED, CANCELLED_PAID, CANCELLED_UNPAID);

  private static final List<String> WITHDRAWING = List.of(CANCELLED_UNPAID, CANCELLED_PAID, RETURNING, RETURNED); // not sure about the returns
  private static final List<String> WITHDRAWN = List.of(CANCELLED_UNPAID, CANCELLED_PAID);

  private ParcelState() {}

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
