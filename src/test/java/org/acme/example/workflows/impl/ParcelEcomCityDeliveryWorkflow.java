package org.acme.example.workflows.impl;

import eu.nonstatic.workflow.WorkflowStep;
import java.util.List;
import org.acme.example.workflows.model.AbstractParcelWorkflow;
import org.acme.example.workflows.param.DeliveryMode;
import org.acme.example.workflows.param.ParcelState;

public final class ParcelEcomCityDeliveryWorkflow extends AbstractParcelWorkflow {

  public ParcelEcomCityDeliveryWorkflow() {
    super(DeliveryMode.ECOM_ORDER_CITY_DELIVERY,
        new WorkflowStep<>(ParcelState.NEW, List.of(
            new WorkflowStep<>(ParcelState.DISPATCHED, List.of(
                new WorkflowStep<>(ParcelState.DELIVERED),
                new WorkflowStep<>(ParcelState.RETURNING, List.of(
                    new WorkflowStep<>(ParcelState.RETURNED)
                ))
            ))
        ))
    );
  }
}
