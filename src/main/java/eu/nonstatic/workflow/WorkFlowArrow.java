package eu.nonstatic.workflow;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class WorkFlowArrow<S, N extends WorkflowNode<S, N>> {

  private final N node;
  private final WorkflowListener<S> listener; // callback when reaching this node's state, or callback when coming from this node's state

  public WorkFlowArrow(N node, WorkflowListener<S> listener) {
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
    WorkFlowArrow<?, ?> that = (WorkFlowArrow<?, ?>) o;
    return Objects.equals(node, that.node) && Objects.equals(listener, that.listener);
  }

  @Override
  public int hashCode() {
    return Objects.hash(node, listener);
  }



  static <S, N extends WorkflowNode<S, N>> boolean equalsLoopSafe(List<WorkFlowArrow<S, N>> arrows1, List<WorkFlowArrow<S, N>> arrows2, Set<S> seenStates) {
    if(arrows1.size() != arrows2.size()) {
      return false;
    }

    var it2 = arrows2.iterator();
    for (WorkFlowArrow<S, N> arrow1 : arrows1) {
      if(!equalsloopSafe(arrow1, it2.next(), seenStates)) {
        return false;
      }
    }

    return true;
  }

  static <S, N extends WorkflowNode<S, N>> boolean equalsloopSafe(WorkFlowArrow<S, N> arrow1, WorkFlowArrow<S, N> arrow2, Set<S> seenStates) {
    return Objects.equals(arrow1.listener, arrow2.listener) && WorkflowNode.equalsLoopSafe(arrow1.node, arrow2.node, seenStates);
  }
}
