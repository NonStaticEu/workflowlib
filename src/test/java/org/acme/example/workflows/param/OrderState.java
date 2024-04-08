package org.acme.example.workflows.param;

import java.util.List;

public final class OrderState {

  public static final String QUEUED = "queued";
  public static final String FOOTPRINT = "footprint";
  public static final String ORCHESTRATING = "orchestrating";
  public static final String SUPPLYING = "supplying";
  public static final String AVAILABLE = "received";
  public static final String PAYMENT_PENDING = "payment_pending";
  public static final String PROCESSING = "processing";
  public static final String PACKING = "in_preparation";
  public static final String COLLECTABLE = "collectable";
  public static final String FULFILLED = "fulfilled";
  public static final String CANCELLED_UNPAID = "cancelled_no_refund";
  public static final String CANCELLED_PAID = "cancelled_refund";


  private static final List<String> ALL = List.of(CANCELLED_PAID, QUEUED, FOOTPRINT,
      ORCHESTRATING, SUPPLYING, PROCESSING, PACKING, COLLECTABLE,
      FULFILLED, CANCELLED_UNPAID, AVAILABLE, PAYMENT_PENDING);

  private static final List<String> WITHDRAWN = List.of(CANCELLED_UNPAID, CANCELLED_PAID);
  private static final List<String> WITHDRAWING = WITHDRAWN;

  private OrderState() {}


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
