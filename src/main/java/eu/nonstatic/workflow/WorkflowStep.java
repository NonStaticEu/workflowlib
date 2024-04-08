package eu.nonstatic.workflow;

import static java.util.Collections.unmodifiableList;

import java.util.List;

public class WorkflowStep<S> {

  private final S state;
  private final List<WorkflowStep<S>> next;

  public WorkflowStep(S state) {
    this(state, List.of());
  }

  public WorkflowStep(S state, WorkflowStep<S> next) {
    this(state, List.of(next));
  }

  public WorkflowStep(S state, List<WorkflowStep<S>> next) {
    this.state = state;
    this.next = unmodifiableList(next);
  }

  public S getState() {
    return state;
  }

  public List<WorkflowStep<S>> getNext() {
    return next;
  }

  @Override
  public String toString() {
    return state != null ? state.toString() : "<null>";
  }
}
