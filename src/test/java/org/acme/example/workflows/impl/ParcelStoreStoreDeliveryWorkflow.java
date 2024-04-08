package org.acme.example.workflows.impl;

import eu.nonstatic.workflow.WorkflowStep;
import java.util.List;
import org.acme.example.workflows.model.AbstractParcelWorkflow;
import org.acme.example.workflows.param.DeliveryMode;
import org.acme.example.workflows.param.ParcelState;

public final class ParcelStoreStoreDeliveryWorkflow extends AbstractParcelWorkflow {

  public ParcelStoreStoreDeliveryWorkflow() {
    super(DeliveryMode.STORE_ORDER_STORE_DELIVERY,
        new WorkflowStep<>(ParcelState.NEW, List.of(
            new WorkflowStep<>(ParcelState.CANCELLED_UNPAID),
            new WorkflowStep<>(ParcelState.PACKED, List.of(
                new WorkflowStep<>(ParcelState.CANCELLED_UNPAID),
                new WorkflowStep<>(ParcelState.COLLECTED)
            ))
        ))
    );
  }
}
