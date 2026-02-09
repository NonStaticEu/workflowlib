package eu.nonstatic.workflow;

import java.util.Objects;

public class WorkFlowTransition<S, N extends WorkflowNode<S, N>> {

  private final N node;
  private final WorkflowListener<S> listener; // callback when reaching this node's state, or callback when coming from this node's state

  public WorkFlowTransition(N node, WorkflowListener<S> listener) {
    this.node = node;
    this.listener = listener;
  }

  public N getNode() {
    return node;
  }

  public WorkflowListener<S> getListener() {
    return listener;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WorkFlowTransition<?, ?> that = (WorkFlowTransition<?, ?>) o;
    return Objects.equals(node, that.node) && Objects.equals(listener, that.listener);
  }

  @Override
  public int hashCode() {
    return Objects.hash(node, listener);
  }
}
