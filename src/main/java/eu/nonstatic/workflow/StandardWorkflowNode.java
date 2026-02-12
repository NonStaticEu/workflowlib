package eu.nonstatic.workflow;

public class StandardWorkflowNode<S> extends AbstractWorkflowNode<S, StandardWorkflowNode<S>> {

  protected StandardWorkflowNode(S state) {
    super(state);
  }
}
