package org.acme.example.workflows.impl;

import eu.nonstatic.workflow.WorkflowStep;
import java.util.List;
import org.acme.example.workflows.model.AbstractParcelWorkflow;
import org.acme.example.workflows.param.DeliveryMode;
import org.acme.example.workflows.param.ParcelState;

public final class ParcelEcomClickAndCollectWorkflow extends AbstractParcelWorkflow {

  public ParcelEcomClickAndCollectWorkflow() {
    super(DeliveryMode.ECOM_CLICK_AND_COLLECT_DELIVERY,
        new WorkflowStep<>(null, List.of(
          new WorkflowStep<>(ParcelState.CANCELLED_UNPAID),
          new WorkflowStep<>(ParcelState.NEW, List.of(
            new WorkflowStep<>(ParcelState.PACKED, List.of(
                new WorkflowStep<>(ParcelState.CANCELLED_UNPAID),
                new WorkflowStep<>(ParcelState.CANCELLED_PAID),
                new WorkflowStep<>(ParcelState.COLLECTED)
                ))
            ))
        ))
    );
  }
}
