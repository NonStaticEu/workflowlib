package org.acme.example.workflows.param;

import java.util.List;

public final class DeliveryMode {

  public static final String STORE_ORDER_STORE_DELIVERY = "storedelivery";
  public static final String ECOM_ORDER_CITY_DELIVERY = "ecomcitydelivery";
  public static final String ECOM_ORDER_STORE_DELIVERY = "ecomstoredelivery";
  public static final String ECOM_CLICK_AND_COLLECT_DELIVERY = "clickandcollect";

  private static final List<String> ALL = List.of(STORE_ORDER_STORE_DELIVERY, ECOM_ORDER_CITY_DELIVERY, ECOM_ORDER_STORE_DELIVERY, ECOM_CLICK_AND_COLLECT_DELIVERY);

  private DeliveryMode() {}

  public static List<String> getAll() {
    return ALL;
  }

  public static boolean isKnown(String someState) {
    return ALL.contains(someState);
  }
}
