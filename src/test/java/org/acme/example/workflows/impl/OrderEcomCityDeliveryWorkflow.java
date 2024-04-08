package org.acme.example.workflows.impl;

import eu.nonstatic.workflow.WorkflowStep;
import java.util.List;
import org.acme.example.workflows.model.AbstractOrderEcomWorkflow;
import org.acme.example.workflows.param.DeliveryMode;
import org.acme.example.workflows.param.OrderState;

public final class OrderEcomCityDeliveryWorkflow extends AbstractOrderEcomWorkflow {

  public OrderEcomCityDeliveryWorkflow() {
    super(DeliveryMode.ECOM_ORDER_CITY_DELIVERY,
        new WorkflowStep<>(OrderState.QUEUED, List.of(
            new WorkflowStep<>(OrderState.CANCELLED_UNPAID),
            new WorkflowStep<>(OrderState.FOOTPRINT, List.of(
                new WorkflowStep<>(OrderState.CANCELLED_UNPAID),
                new WorkflowStep<>(OrderState.ORCHESTRATING, List.of(
                    new WorkflowStep<>(OrderState.CANCELLED_UNPAID),
                    new WorkflowStep<>(OrderState.SUPPLYING, List.of(
                        new WorkflowStep<>(OrderState.CANCELLED_UNPAID),
                        new WorkflowStep<>(OrderState.AVAILABLE, List.of(
                            new WorkflowStep<>(OrderState.CANCELLED_UNPAID),
                            new WorkflowStep<>(OrderState.PAYMENT_PENDING, List.of(
                                new WorkflowStep<>(OrderState.CANCELLED_UNPAID),
                                new WorkflowStep<>(OrderState.PACKING, List.of(
                                    new WorkflowStep<>(OrderState.CANCELLED_PAID),
                                    new WorkflowStep<>(OrderState.FULFILLED)
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
