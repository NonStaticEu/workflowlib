package org.acme.example.workflows.impl;

import eu.nonstatic.workflow.WorkflowStep;
import java.util.List;
import org.acme.example.workflows.model.AbstractParcelWorkflow;
import org.acme.example.workflows.param.DeliveryMode;
import org.acme.example.workflows.param.ParcelState;

public final class ParcelEcomStoreDeliveryWorkflow extends AbstractParcelWorkflow {

  public ParcelEcomStoreDeliveryWorkflow() {
    super(DeliveryMode.ECOM_ORDER_STORE_DELIVERY,
        new WorkflowStep<>(ParcelState.NEW, List.of(
            new WorkflowStep<>(ParcelState.DISPATCHED, List.of(
                new WorkflowStep<>(ParcelState.RECEIVED, List.of(
                    new WorkflowStep<>(ParcelState.COLLECTED),
                    new WorkflowStep<>(ParcelState.CANCELLED_PAID)
                ))
            ))
        ))
    );
  }
}
