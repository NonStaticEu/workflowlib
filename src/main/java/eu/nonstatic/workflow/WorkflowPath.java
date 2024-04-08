package eu.nonstatic.workflow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Instances of this class are meant to be immutable
 */
public final class WorkflowPath<S> implements Iterable<S> {

  private static final WorkflowPath<?> EMPTY = new WorkflowPath<>(0);
  private final List<S> steps;

  private WorkflowPath(int initialCapacity) {
    this.steps = new ArrayList<>(initialCapacity);
  }

  public S get(int index) {
    return steps.get(index);
  }

  public int indexOf(S state) {
    return steps.indexOf(state);
  }

  public boolean isEmpty() {
    return steps.isEmpty();
  }

  public int size() {
    return steps.size();
  }

  public boolean contains(S state) {
    return steps.contains(state);
  }

  @Override
  public Iterator<S> iterator() {
    return Collections.unmodifiableList(steps).iterator();
  }

  public static <S> WorkflowPath<S> empty() {
    return (WorkflowPath<S>)EMPTY;
  }

  public WorkflowPath<S> prepend(S state) {
    WorkflowPath<S> path = new WorkflowPath<>(1 + size());
    path.steps.add(state);
    path.steps.addAll(this.steps);
    return path;
  }

  @Override
  public String toString() {
    return steps.toString();
  }
}
