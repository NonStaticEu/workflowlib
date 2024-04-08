package eu.nonstatic.workflow;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractWorkflowLink<S, L extends AbstractWorkflowLink<S, L>> implements WorkflowLink<S, L> {

  private static final int PREVIOUS_NEXT_DEFAULT_CAPACITY = 4;

  protected final S state; // may be null only in the *first* link of the chain if the workflow starts with 2 different steps
  protected final List<L> previous;
  protected final List<L> next;

  protected AbstractWorkflowLink(S state) {
    this(state, new ArrayList<>(PREVIOUS_NEXT_DEFAULT_CAPACITY), new ArrayList<>(PREVIOUS_NEXT_DEFAULT_CAPACITY));
  }

  protected AbstractWorkflowLink(S state, List<L> previous, List<L> next) {
    this.state = state;
    this.previous = previous;
    this.next = next;
  }

  @Override
  public S getState() {
    return state;
  }

  @Override
  public List<L> getPrevious() {
    return unmodifiableList(previous);
  }

  @Override
  public Optional<L> getPrevious(S state) {
    return previous.stream().filter(link -> link.isEqual(state)).findAny();
  }

  @Override
  public List<L> getNext() {
    return unmodifiableList(next);
  }

  @Override
  public Optional<L> getNext(S state) {
    return next.stream().filter(link -> link.isEqual(state)).findAny();
  }

  @Override
  public boolean isEqual(S state) {
    return this.state != null && this.state.equals(state);
  }

  @Override
  public boolean isAfter(S state) {
    return isAfter(state, list(this.state));
  }

  @Override
  public boolean isAfter(S state, Collection<S> visited) {
    return previous.stream()
        .filter(link -> !visited.contains(link.state))
        .anyMatch(link -> link.isEqual(state) || link.isAfter(state, concat(visited, link.state)));
  }

  @Override
  public boolean isAfterOrEqual(S state) {
    return isEqual(state) || isAfter(state);
  }

  @Override
  public boolean isBefore(S state) {
    return isBefore(state, list(this.state));
  }

  @Override
  public boolean isBefore(S state, Collection<S> visited) {
    return next.stream()
        .filter(link -> !visited.contains(link.state))
        .anyMatch(link -> link.isEqual(state) || link.isBefore(state, concat(visited, link.state)));
  }

  @Override
  public boolean isBeforeOrEqual(S state) {
    return isEqual(state) || isBefore(state);
  }

  @Override
  public boolean isTwoWay(S state) {
    return getNext(state).flatMap(n -> n.getPrevious(this.state)).isPresent()
        || getPrevious(state).flatMap(p -> p.getNext(this.state)).isPresent();
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
  public int compareTo(L otherLink) {
    S otherState = otherLink.getState();

    if(isEqual(otherState)) {
      return 0;
    } else if (isTerminal() && otherLink.isTerminal()) {
      throw new IllegalArgumentException("Cannot compare two terminal states: " + state + " vs " + otherState);
    } else if(otherLink.isTerminal() || isBefore(otherState)) {
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
    AbstractWorkflowLink<?,?> that = (AbstractWorkflowLink<?,?>) o;
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
