package org.acme.example.workflows.impl;

import eu.nonstatic.workflow.WorkflowStep;
import java.util.List;
import org.acme.example.workflows.model.AbstractOrderWorkflow;
import org.acme.example.workflows.model.OrderStoreDeliveryPaymentHint;
import org.acme.example.workflows.model.OrderWorkflowLink;
import org.acme.example.workflows.param.DeliveryMode;
import org.acme.example.workflows.param.OrderState;

public class OrderStoreStoreDeliveryWorkflow extends AbstractOrderWorkflow implements OrderStoreDeliveryPaymentHint<OrderWorkflowLink> {

  public OrderStoreStoreDeliveryWorkflow() {
    super(DeliveryMode.STORE_ORDER_STORE_DELIVERY,
        new WorkflowStep<>(OrderState.QUEUED,
            new WorkflowStep<>(OrderState.ORCHESTRATING, List.of(
                new WorkflowStep<>(OrderState.CANCELLED_UNPAID),
                new WorkflowStep<>(OrderState.SUPPLYING, List.of(
                    new WorkflowStep<>(OrderState.CANCELLED_UNPAID),
                    new WorkflowStep<>(OrderState.PROCESSING, List.of(
                        new WorkflowStep<>(OrderState.CANCELLED_UNPAID),
                        new WorkflowStep<>(OrderState.COLLECTABLE, List.of(
                            new WorkflowStep<>(OrderState.CANCELLED_UNPAID),
                            new WorkflowStep<>(OrderState.FULFILLED)
                        ))
                    ))
                ))
            ))
        )
    );
  }
}
