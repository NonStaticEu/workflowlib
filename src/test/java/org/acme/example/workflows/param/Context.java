package org.acme.example.workflows.param;

import static java.util.Collections.unmodifiableList;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public final class Context {

  public static final Context STORE_ORDER_STORE_DELIVERY = new Context(SalesChannel.STORE, DeliveryMode.STORE_ORDER_STORE_DELIVERY, Features.STORE_STORE_DELIVERY, true, false);
  public static final Context ECOM_ORDER_CITY_DELIVERY = new Context(SalesChannel.ECOM, DeliveryMode.ECOM_ORDER_CITY_DELIVERY, Features.ECOM_CITY_DELIVERY, false, false);
  public static final Context ECOM_ORDER_STORE_DELIVERY = new Context(SalesChannel.ECOM, DeliveryMode.ECOM_ORDER_STORE_DELIVERY, Features.ECOM_STORE_DELIVERY, true, false);
  public static final Context ECOM_ORDER_CLICK_AND_COLLECT_DELIVERY = new Context(SalesChannel.ECOM, DeliveryMode.ECOM_CLICK_AND_COLLECT_DELIVERY, Features.ECOM_CLICK_AND_COLLECT, true, true);

  private static final List<Context> CONTEXTS = List.of(
      STORE_ORDER_STORE_DELIVERY,
      ECOM_ORDER_CITY_DELIVERY,
      ECOM_ORDER_STORE_DELIVERY,
      ECOM_ORDER_CLICK_AND_COLLECT_DELIVERY
  );


  private final List<String> features;
  private final String salesChannel;  // Store or Ecom
  private final String deliveryMode;

  private final boolean storeDelivery;
  private final boolean expressDelivery;

  Context(String salesChannel, String deliveryMode, List<String> features, boolean storeDelivery, boolean expressDelivery) {
    this.features = features != null ? unmodifiableList(features) : null;
    this.salesChannel = salesChannel;
    this.deliveryMode = deliveryMode;

    // metas
    this.storeDelivery = storeDelivery;
    this.expressDelivery = expressDelivery;
  }

  public String getDeliveryMode() {
    return deliveryMode;
  }

  public String getSalesChannel() {
    return salesChannel;
  }

  public List<String> getFeatures() {
    return features;
  }

  public boolean isStoreDelivery() {
    return storeDelivery;
  }

  public boolean isExpressDelivery() {
    return expressDelivery;
  }

  public static Context get(String deliveryMode) {
    return CONTEXTS.stream().filter(context -> Objects.equals(context.getDeliveryMode(), deliveryMode)).findAny().orElse(null);
  }

  public static Optional<Context> get(String salesChannel, boolean storeDelivery, boolean expressDelivery) {
    return CONTEXTS.stream().filter(context -> context.matches(salesChannel, storeDelivery, expressDelivery)).findAny();
  }

  public static Stream<Context> filter(String salesChannel) {
    return CONTEXTS.stream().filter(context -> context.isSaleSChannel(salesChannel));
  }

  boolean matches(String salesChannel, boolean storeDelivery, boolean expressDelivery) {
    return isSaleSChannel(salesChannel)
        && this.storeDelivery == storeDelivery
        && this.expressDelivery == expressDelivery;
  }

  boolean isSaleSChannel(String salesChannel) {
    return Objects.equals(this.salesChannel, salesChannel);
  }

  @Override
  public String toString() {
    return "ExampleContext{" +
        " salesChannel='" + salesChannel + '\'' +
        ", deliveryMode='" + deliveryMode + '\'' +
        '}';
  }
}
