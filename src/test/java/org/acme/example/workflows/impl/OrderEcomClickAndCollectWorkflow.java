package org.acme.example.workflows.impl;

import eu.nonstatic.workflow.WorkflowStep;
import java.util.List;
import org.acme.example.workflows.model.AbstractOrderEcomWorkflow;
import org.acme.example.workflows.param.DeliveryMode;
import org.acme.example.workflows.param.OrderState;

public final class OrderEcomClickAndCollectWorkflow extends AbstractOrderEcomWorkflow {

  public OrderEcomClickAndCollectWorkflow() {
    super(DeliveryMode.ECOM_CLICK_AND_COLLECT_DELIVERY,
        new WorkflowStep<>(OrderState.QUEUED, List.of(
            new WorkflowStep<>(OrderState.CANCELLED_UNPAID),
            new WorkflowStep<>(OrderState.FOOTPRINT,
                new WorkflowStep<>(OrderState.ORCHESTRATING, List.of(
                    new WorkflowStep<>(OrderState.CANCELLED_UNPAID),
                    new WorkflowStep<>(OrderState.PROCESSING, List.of(
                        new WorkflowStep<>(OrderState.CANCELLED_UNPAID),
                        new WorkflowStep<>(OrderState.PAYMENT_PENDING, List.of(
                            new WorkflowStep<>(OrderState.CANCELLED_UNPAID),
                            new WorkflowStep<>(OrderState.COLLECTABLE, List.of(
                                new WorkflowStep<>(OrderState.CANCELLED_PAID),
                                new WorkflowStep<>(OrderState.FULFILLED)
                            ))
                        ))
                    ))
                ))
            )
        ))
    );
  }
}
