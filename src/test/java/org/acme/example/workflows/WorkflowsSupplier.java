package org.acme.example.workflows;

import eu.nonstatic.workflow.AbstractWorkflowSupplier;
import java.util.Optional;
import java.util.stream.Stream;
import org.acme.example.workflows.impl.ItemEcomCityDeliveryWorkflow;
import org.acme.example.workflows.impl.ItemEcomClickAndCollectWorkflow;
import org.acme.example.workflows.impl.ItemEcomStoreDeliveryWorkflow;
import org.acme.example.workflows.impl.ItemStoreStoreDeliveryWorkflow;
import org.acme.example.workflows.impl.OrderEcomCityDeliveryWorkflow;
import org.acme.example.workflows.impl.OrderEcomClickAndCollectWorkflow;
import org.acme.example.workflows.impl.OrderEcomStoreDeliveryWorkflow;
import org.acme.example.workflows.impl.OrderStoreStoreDeliveryWorkflow;
import org.acme.example.workflows.impl.ParcelEcomCityDeliveryWorkflow;
import org.acme.example.workflows.impl.ParcelEcomClickAndCollectWorkflow;
import org.acme.example.workflows.impl.ParcelEcomStoreDeliveryWorkflow;
import org.acme.example.workflows.impl.ParcelStoreStoreDeliveryWorkflow;
import org.acme.example.workflows.model.AbstractAcmeWorkflow;
import org.acme.example.workflows.param.Context;
import org.acme.example.workflows.param.EntityType;

public final class WorkflowsSupplier extends AbstractWorkflowSupplier<AbstractAcmeWorkflow> {

  public WorkflowsSupplier() {
    super(
        new OrderStoreStoreDeliveryWorkflow(),
        new ItemStoreStoreDeliveryWorkflow(),
        new ParcelStoreStoreDeliveryWorkflow(),

        new OrderEcomCityDeliveryWorkflow(),
        new ItemEcomCityDeliveryWorkflow(),
        new ParcelEcomCityDeliveryWorkflow(),

        new OrderEcomStoreDeliveryWorkflow(),
        new ItemEcomStoreDeliveryWorkflow(),
        new ParcelEcomStoreDeliveryWorkflow(),

        new OrderEcomClickAndCollectWorkflow(),
        new ItemEcomClickAndCollectWorkflow(),
        new ParcelEcomClickAndCollectWorkflow());
  }

  @Override
  protected String toKey(AbstractAcmeWorkflow workflow) {
    return toKey(workflow.getEntityType(), workflow.getDeliveryMode());
  }

  private static String toKey(String entityType, String deliveryMode) {
    return entityType + '-' + deliveryMode;
  }

  public Optional<AbstractAcmeWorkflow> get(String entityType, String deliveryMode) {
    return EntityType.Safe.get(entityType).flatMap(safeType -> get(safeType, deliveryMode));
  }

  public Optional<AbstractAcmeWorkflow> get(String entityType, String salesChannel, boolean storeDelivery, boolean expressDelivery) {
    return EntityType.Safe.get(entityType).flatMap(safeType -> get(safeType, salesChannel, storeDelivery, expressDelivery));
  }

  public <W extends AbstractAcmeWorkflow> Optional<W> get(EntityType.Safe<W> entityType, String deliveryMode) {
    return Optional.ofNullable((W)get(toKey(entityType.name(), deliveryMode)));
  }

  public <W extends AbstractAcmeWorkflow> Optional<W> get(EntityType.Safe<W> entityType, String salesChannel, boolean storeDelivery, boolean expressDelivery) {
    return Context.get(salesChannel, storeDelivery, expressDelivery)
        .flatMap(context -> get(entityType, context.getDeliveryMode()));
  }

  public Stream<AbstractAcmeWorkflow> filter(String entityType) {
    return filterByEntityType(stream(), entityType);
  }

  public Stream<AbstractAcmeWorkflow> filter(String entityType, String salesChannel) {
    return filterByEntityType(Context.filter(salesChannel)
        .map(context -> get(entityType, context.getDeliveryMode()))
        .filter(Optional::isPresent)
        .map(Optional::get), entityType);
  }

  private static Stream<AbstractAcmeWorkflow> filterByEntityType(Stream<AbstractAcmeWorkflow> stream, String entityType) {
    return stream
        .filter(workflow -> workflow.getEntityType().equals(entityType));
  }
}
