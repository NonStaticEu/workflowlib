package eu.nonstatic.workflow;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public abstract class AbstractWorkflowNode<S, N extends AbstractWorkflowNode<S, N>> implements WorkflowNode<S, N> {

  private static final int PREVIOUS_NEXT_DEFAULT_CAPACITY = 4;

  final S state; // may be null only in the *first* node of the chain if the workflow starts with 2 different steps
  final List<WorkFlowArrow<S, N>> previous;
  final List<WorkFlowArrow<S, N>> next;

  protected AbstractWorkflowNode(S state) {
    this(state, new ArrayList<>(PREVIOUS_NEXT_DEFAULT_CAPACITY), new ArrayList<>(PREVIOUS_NEXT_DEFAULT_CAPACITY));
  }

  protected AbstractWorkflowNode(S state, List<WorkFlowArrow<S, N>> previous, List<WorkFlowArrow<S, N>> next) {
    this.state = state;
    this.previous = previous;
    this.next = next;
  }

  @Override
  public S getState() {
    return state;
  }

  @Override
  public List<WorkFlowArrow<S, N>> getPrevious() {
    return unmodifiableList(previous);
  }

  @Override
  public Optional<WorkFlowArrow<S, N>> getPrevious(S state) {
    return previous.stream().filter(trans -> trans.getNode().isOn(state)).findAny();
  }

  @Override
  public List<WorkFlowArrow<S, N>> getNext() {
    return unmodifiableList(next);
  }

  @Override
  public Optional<WorkFlowArrow<S, N>> getNext(S state) {
    return next.stream().filter(trans -> trans.getNode().isOn(state)).findAny();
  }


  /**
   * Caution when using, because comparing two different terminal states will generate an IllegalArgumentException
   */
  public int compareTo(N otherNode) {
    S otherState = otherNode.getState();

    if(isOn(otherState)) {
      return 0;
    } else if (isTerminal() && otherNode.isTerminal()) {
      throw new IllegalArgumentException("Cannot compare two terminal states: " + state + " vs " + otherState);
    } else if(otherNode.isTerminal() || isBefore(otherState)) {
      return -1;
    } else if(isTerminal() || isAfter(otherState)) {
      return 1;
    } else {
      throw new IllegalArgumentException("State " + otherState + " doesn't belong to this " + state + " hierarchy");
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WorkflowNode<S, N> that = (WorkflowNode<S, N>) o;
    return WorkflowNode.equalsLoopSafe(this, that, new HashSet<>());
  }

  @Override
  public int hashCode() {
    return WorkflowNode.hashCodeLoopSafe(this);
  }

  @Override
  public String toString() {
    return state != null ? state.toString() : "<null>";
  }
}
