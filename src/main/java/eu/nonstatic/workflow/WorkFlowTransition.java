package eu.nonstatic.workflow;

import java.util.List;
import java.util.Objects;
import java.util.Set;

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



  static <S, N extends WorkflowNode<S, N>> boolean equalsLoopSafe(List<WorkFlowTransition<S, N>> trans1, List<WorkFlowTransition<S, N>> trans2, Set<S> seenStates) {
    if(trans1.size() != trans2.size()) {
      return false;
    }

    var it2 = trans2.iterator();
    for (WorkFlowTransition<S, N> node1 : trans1) {
      if(!equalsloopSafe(node1, it2.next(), seenStates)) {
        return false;
      }
    }

    return true;
  }

  static <S, N extends WorkflowNode<S, N>> boolean equalsloopSafe(WorkFlowTransition<S, N> trans1, WorkFlowTransition<S, N> trans2, Set<S> seenStates) {
    return Objects.equals(trans1.listener, trans2.listener) && WorkflowNode.equalsLoopSafe(trans1.node, trans2.node, seenStates);
  }
}
