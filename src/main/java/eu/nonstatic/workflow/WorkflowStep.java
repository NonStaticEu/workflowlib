package eu.nonstatic.workflow;

import static java.util.Collections.unmodifiableList;

import java.util.List;

public class WorkflowStep<S> {

  private final S state;
  private final WorkflowListener<S> listener; // listener to invoke when reaching this state
  private final List<WorkflowStep<S>> next;

  public WorkflowStep(S state) {
    this(state, (WorkflowListener<S>) null);
  }

  public WorkflowStep(S state, WorkflowStep<S> next) {
    this(state, null, next);
  }

  public WorkflowStep(S state, List<WorkflowStep<S>> next) {
    this(state, null, next);
  }


  public WorkflowStep(S state, WorkflowListener<S> listener) {
    this(state, listener, List.of());
  }

  public WorkflowStep(S state, WorkflowListener<S> listener, WorkflowStep<S> next) {
    this(state, listener, List.of(next));
  }

  public WorkflowStep(S state, WorkflowListener<S> listener, List<WorkflowStep<S>> next) {
    if(next == null) {
      throw new NullPointerException("Next steps must not be null");
    }
    this.state = state;
    this.listener = listener;
    this.next = unmodifiableList(next);
  }

  public S getState() {
    return state;
  }

  public WorkflowListener<S> getListener() {
    return listener;
  }

  public List<WorkflowStep<S>> getNext() {
    return next;
  }

  @Override
  public String toString() {
    return state != null ? state.toString() : "<null>";
  }
}
