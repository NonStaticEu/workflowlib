package org.acme.example.workflows.param;

import java.util.List;

public final class Features {
  public static final List<String> STORE_STORE_DELIVERY = List.of("warehouse_origin", "other_shop_origin", "store_dest");
  public static final List<String> ECOM_CITY_DELIVERY = List.of("warehouse_origin");
  public static final List<String> ECOM_STORE_DELIVERY = List.of("warehouse_origin", "store_dest");
  public static final List<String> ECOM_CLICK_AND_COLLECT = List.of("store_dest");

  private Features() {}
}
