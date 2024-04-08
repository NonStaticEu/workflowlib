package org.acme.example.workflows.model;

import org.acme.example.workflows.param.ParcelState;

public class ParcelWorkflowLink extends AbstractAcmeWorkflowLink<ParcelWorkflowLink> {

  ParcelWorkflowLink(String state) {
    super(state);
  }

  public boolean isWithdrawing() {
    return ParcelState.isWithdrawing(state);
  }
  public boolean isWithdrawn() {
    return ParcelState.isWithdrawn(state);
  }
}
