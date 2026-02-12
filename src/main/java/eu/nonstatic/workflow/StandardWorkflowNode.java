package eu.nonstatic.workflow;

import java.io.Serializable;

public class StandardWorkflowNode extends AbstractWorkflowNode<Serializable, StandardWorkflowNode> {

  protected StandardWorkflowNode(Serializable state) {
    super(state);
  }
}
