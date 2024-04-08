package org.acme.example.workflows.impl;

import eu.nonstatic.workflow.WorkflowStep;
import java.util.List;
import org.acme.example.workflows.model.AbstractLineItemWorkflow;
import org.acme.example.workflows.param.DeliveryMode;
import org.acme.example.workflows.param.ItemState;

public final class ItemEcomCityDeliveryWorkflow extends AbstractLineItemWorkflow {

  public ItemEcomCityDeliveryWorkflow() {
    super(DeliveryMode.ECOM_ORDER_CITY_DELIVERY,
        new WorkflowStep<>(ItemState.QUEUED, List.of(
            new WorkflowStep<>(ItemState.CANCELLED_UNPAID),
            new WorkflowStep<>(ItemState.FOOTPRINT, List.of(
                new WorkflowStep<>(ItemState.CANCELLED_UNPAID),
                new WorkflowStep<>(ItemState.FILED, List.of(
                    new WorkflowStep<>(ItemState.CANCELLED_UNPAID),
                    new WorkflowStep<>(ItemState.RESERVED_WAREHOUSE, List.of(
                        new WorkflowStep<>(ItemState.CANCELLED_UNPAID),
                        new WorkflowStep<>(ItemState.PREORDER, List.of(
                            new WorkflowStep<>(ItemState.CANCELLED_UNPAID),
                            new WorkflowStep<>(ItemState.RESERVED_WAREHOUSE_RECEIVED, List.of(
                                new WorkflowStep<>(ItemState.PAYMENT_PENDING, List.of(
                                    new WorkflowStep<>(ItemState.CANCELLED_UNPAID),
                                    new WorkflowStep<>(ItemState.PAID, List.of(
                                        new WorkflowStep<>(ItemState.CANCELLED_PAID),
                                        new WorkflowStep<>(ItemState.FULFILLED)
                                    ))
                                ))
                            )),
                            new WorkflowStep<>(ItemState.RESERVED_WAREHOUSE_ORDERED, List.of(
                                new WorkflowStep<>(ItemState.CANCELLED_UNPAID),
                                new WorkflowStep<>(ItemState.RESERVED_WAREHOUSE_RECEIVED /* No need to add next steps, they are already above */)
                            )),
                            new WorkflowStep<>(ItemState.RESERVED_WAREHOUSE_ON_SHELF, List.of(
                                new WorkflowStep<>(ItemState.CANCELLED_UNPAID),
                                new WorkflowStep<>(ItemState.PAYMENT_PENDING /* No need to add next steps, they are already above */)
                            ))
                        )),
                        new WorkflowStep<>(ItemState.PREORDER, List.of(
                            new WorkflowStep<>(ItemState.CANCELLED_UNPAID),
                            new WorkflowStep<>(ItemState.RESERVED_WAREHOUSE_ORDERED /* No need to add next steps, they are already above */),
                            new WorkflowStep<>(ItemState.RESERVED_WAREHOUSE_RECEIVED /* No need to add next steps, they are already above */)
                        )),
                        new WorkflowStep<>(ItemState.RESERVED_WAREHOUSE_ORDERED /* No need to add next steps, they are already above */),
                        new WorkflowStep<>(ItemState.RESERVED_WAREHOUSE_ON_SHELF /* No need to add next steps, they are already above */)
                    ))
                ))
            ))
      ))
    );
  }
}
