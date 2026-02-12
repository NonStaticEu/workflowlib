package eu.nonstatic.workflow;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractWorkflowNode<S, N extends AbstractWorkflowNode<S, N>> implements WorkflowNode<S, N> {

  private static final int PREVIOUS_NEXT_DEFAULT_CAPACITY = 4;

  final S state; // may be null only in the *first* node of the chain if the workflow starts with 2 different steps
  final List<WorkFlowTransition<S, N>> previous;
  final List<WorkFlowTransition<S, N>> next;

  protected AbstractWorkflowNode(S state) {
    this(state, new ArrayList<>(PREVIOUS_NEXT_DEFAULT_CAPACITY), new ArrayList<>(PREVIOUS_NEXT_DEFAULT_CAPACITY));
  }

  protected AbstractWorkflowNode(S state, List<WorkFlowTransition<S, N>> previous, List<WorkFlowTransition<S, N>> next) {
    this.state = state;
    this.previous = previous;
    this.next = next;
  }

  @Override
  public S getState() {
    return state;
  }

  @Override
  public List<WorkFlowTransition<S, N>> getPrevious() {
    return unmodifiableList(previous);
  }

  @Override
  public Optional<WorkFlowTransition<S, N>> getPrevious(S state) {
    return previous.stream().filter(trans -> trans.getNode().isOn(state)).findAny();
  }

  @Override
  public List<WorkFlowTransition<S, N>> getNext() {
    return unmodifiableList(next);
  }

  @Override
  public Optional<WorkFlowTransition<S, N>> getNext(S state) {
    return next.stream().filter(trans -> trans.getNode().isOn(state)).findAny();
  }

  @Override
  public boolean isOn(S state) {
    return this.state != null && this.state.equals(state);
  }

  @Override
  public boolean isAfter(S state) {
    return isAfter(state, list(this.state));
  }

  @Override
  public boolean isAfter(S state, Collection<S> visited) {
    return previous.stream()
        .filter(trans -> !visited.contains(trans.getNode().state))
        .anyMatch(trans -> {
          N node = trans.getNode();
          return node.isOn(state) || node.isAfter(state, concat(visited, node.state));
        });
  }

  @Override
  public boolean isAfterOrOn(S state) {
    return isOn(state) || isAfter(state);
  }

  @Override
  public boolean isBefore(S state) {
    return isBefore(state, list(this.state));
  }

  @Override
  public boolean isBefore(S state, Collection<S> visited) {
    return next.stream()
        .filter(trans -> !visited.contains(trans.getNode().state))
        .anyMatch(trans -> {
          N node = trans.getNode();
          return node.isOn(state) || node.isBefore(state, concat(visited, node.state));
        });
  }

  @Override
  public boolean isBeforeOrOn(S state) {
    return isOn(state) || isBefore(state);
  }

  @Override
  public boolean isTwoWay(S state) {
    return getNext(state).flatMap(trans -> trans.getNode().getPrevious(this.state)).isPresent()
        || getPrevious(state).flatMap(trans -> trans.getNode().getNext(this.state)).isPresent();
  }

  @Override
  public boolean isTerminal() {
    return next.isEmpty();
  }

  private static <S> List<S> list(S state) {
    var list = new ArrayList<S>(1); // must be null-tolerant, so no List.of(state)
    list.add(state);
    return list;
  }

  private static <S> List<S> concat(Collection<S> states, S extra) {
    var concat = new ArrayList<S>(states.size() + 1); // must be null-tolerant
    concat.addAll(states);
    concat.add(extra);
    return concat;
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
    AbstractWorkflowNode<?,?> that = (AbstractWorkflowNode<?,?>) o;
    return Objects.equals(state, that.state);
  }

  @Override
  public int hashCode() {
    return Objects.hash(state);
  }

  @Override
  public String toString() {
    return state != null ? state.toString() : "<null>";
  }
}
