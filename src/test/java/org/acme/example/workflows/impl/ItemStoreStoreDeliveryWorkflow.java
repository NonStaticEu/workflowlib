package org.acme.example.workflows.impl;

import eu.nonstatic.workflow.WorkflowStep;
import java.util.List;
import org.acme.example.workflows.model.AbstractLineItemWorkflow;
import org.acme.example.workflows.param.DeliveryMode;
import org.acme.example.workflows.param.ItemState;

public final class ItemStoreStoreDeliveryWorkflow extends AbstractLineItemWorkflow {

  public ItemStoreStoreDeliveryWorkflow() {
    super(DeliveryMode.STORE_ORDER_STORE_DELIVERY,
        new WorkflowStep<>(ItemState.QUEUED,
            new WorkflowStep<>(ItemState.FILED, List.of(
                new WorkflowStep<>(ItemState.CANCELLED_UNPAID),
                new WorkflowStep<>(ItemState.RESERVED_WAREHOUSE, List.of(
                    new WorkflowStep<>(ItemState.FILED), /* Backwards */
                    new WorkflowStep<>(ItemState.CANCELLED_UNPAID),
                    new WorkflowStep<>(ItemState.PREORDER, List.of(
                        new WorkflowStep<>(ItemState.RESERVED_WAREHOUSE_RECEIVED, List.of(
                            new WorkflowStep<>(ItemState.CANCELLED_UNPAID),
                            new WorkflowStep<>(ItemState.TRANSFERRED, List.of(
                                new WorkflowStep<>(ItemState.FILED), /* Backwards */
                                new WorkflowStep<>(ItemState.PICKED, List.of(
                                    new WorkflowStep<>(ItemState.CANCELLED_UNPAID),
                                    new WorkflowStep<>(ItemState.FULFILLED),
                                    new WorkflowStep<>(ItemState.BACK_TO_SHELF,
                                        new WorkflowStep<>(ItemState.CANCELLED_UNPAID))
                                ))
                            ))
                        ))
                    )),
                    new WorkflowStep<>(ItemState.RESERVED_WAREHOUSE_ORDERED, List.of(
                        new WorkflowStep<>(ItemState.CANCELLED_UNPAID),
                        new WorkflowStep<>(ItemState.RESERVED_WAREHOUSE_RECEIVED) /* No need to add next steps, they are already above */)
                    ))
                ))
            ))
    );
  }
}
