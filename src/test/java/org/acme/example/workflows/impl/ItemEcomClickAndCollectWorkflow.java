package org.acme.example.workflows.impl;

import eu.nonstatic.workflow.WorkflowStep;
import java.util.List;
import org.acme.example.workflows.model.AbstractLineItemWorkflow;
import org.acme.example.workflows.param.DeliveryMode;
import org.acme.example.workflows.param.ItemState;

public final class ItemEcomClickAndCollectWorkflow extends AbstractLineItemWorkflow {

  public ItemEcomClickAndCollectWorkflow() {
    super(DeliveryMode.ECOM_CLICK_AND_COLLECT_DELIVERY,
        new WorkflowStep<>(ItemState.QUEUED, List.of(
            new WorkflowStep<>(ItemState.CANCELLED_UNPAID),
            new WorkflowStep<>(ItemState.FOOTPRINT, List.of(
                new WorkflowStep<>(ItemState.CANCELLED_UNPAID),
                new WorkflowStep<>(ItemState.FILED, List.of(
                    new WorkflowStep<>(ItemState.CANCELLED_UNPAID),
                    new WorkflowStep<>(ItemState.RESERVED_STORE, List.of(
                        new WorkflowStep<>(ItemState.CANCELLED_UNPAID),
                        new WorkflowStep<>(ItemState.PICKED, List.of(
                            new WorkflowStep<>(ItemState.CANCELLED_UNPAID), /* customer no-show */
                            new WorkflowStep<>(ItemState.BACK_TO_SHELF, List.of(
                                new WorkflowStep<>(ItemState.CANCELLED_UNPAID)
                            )),
                            new WorkflowStep<>(ItemState.PAYMENT_PENDING, List.of(
                                new WorkflowStep<>(ItemState.BACK_TO_SHELF  /* No need to add next steps, they are already above */),
                                new WorkflowStep<>(ItemState.PAID, List.of(
                                    new WorkflowStep<>(ItemState.CANCELLED_PAID),
                                    new WorkflowStep<>(ItemState.BACK_TO_SHELF, List.of(
                                        new WorkflowStep<>(ItemState.CANCELLED_PAID)
                                    )),
                                    new WorkflowStep<>(ItemState.FULFILLED)
                                ))
                            ))

                        ))
                    ))
                ))

            ))

        ))
    );
  }
}
