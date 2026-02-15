package eu.nonstatic.workflow;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

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
    this.next = List.copyOf(next);
  }

  public static <S> Builder<S> builder(S state) {
    return new Builder<>(state);
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



  public static final class Builder<S> {
    private final S state;
    private WorkflowListener<S> listener;
    private final List<WorkflowStep<S>> next = new LinkedList<>();

    private Builder(S state) {
      this.state = state;
    }

    public Builder<S> listener(WorkflowListener<S> listener) {
      this.listener = listener;
      return this;
    }

    public Builder<S> next(WorkflowStep<S> next) {
      this.next.add(Objects.requireNonNull(next));
      return this;
    }

    public Builder<S> nextff(S state) {
      return nextff(state, null);
    }

    public Builder<S> nextff(S state, WorkflowListener<S> listener) {
      return next(new WorkflowStep<>(state, listener));
    }

    public WorkflowStep<S> build() {
      return new WorkflowStep<>(state, listener, next);
    }
  }

}
