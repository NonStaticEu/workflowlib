package org.acme.example.workflows.model;

import eu.nonstatic.workflow.AbstractWorkflow;
import eu.nonstatic.workflow.WorkflowStep;
import java.util.Objects;

public abstract class AbstractAcmeWorkflow<L extends AbstractAcmeWorkflowNode<L>> extends AbstractWorkflow<String, L> {

  private final String entityType;
  private final String deliveryMode;


  protected AbstractAcmeWorkflow(String entityType, String deliveryMode, WorkflowStep<String> start) {
    super(start);
    this.entityType = entityType;
    this.deliveryMode = deliveryMode;
  }

  @Override
  public Object getKey() {
    return toKey(entityType, deliveryMode);
  }

  public static String toKey(String entityType, String deliveryMode) {
    return entityType + '-' + deliveryMode;
  }

  public String getEntityType() {
    return entityType;
  }

  public String getDeliveryMode() {
    return deliveryMode;
  }

  @Override
  protected abstract L newNode(String state);


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AbstractAcmeWorkflow workflow = (AbstractAcmeWorkflow) o;
    return entityType.equals(workflow.entityType) && deliveryMode.equals(workflow.deliveryMode) && super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(entityType, deliveryMode, getStart());
  }
}
