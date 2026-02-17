package eu.nonstatic.workflow;

import java.util.Objects;

public class StateMachineTransition<S, E> {

  private final WorkflowLink<S> link;
  private final E event;

  public StateMachineTransition(S from, S to, E event) {
    this(from, to, event, null);
  }

  public StateMachineTransition(S from, S to, E event, WorkflowListener<S> listener) {
    this.link = new WorkflowLink<>(from, to, listener);
    this.event = Objects.requireNonNull(event);
  }

  public WorkflowLink<S> getLink() {
    return link;
  }

  public E getEvent() {
    return event;
  }

  public S getFrom() {
    return getLink().getFrom();
  }

  public S getTo() {
    return getLink().getTo();
  }

  public static <S, E> Builder<S, E> builder() {
    return new Builder<>();
  }


  public static class Builder<S, E> {

    private S from;
    private S to;
    private E event;
    private WorkflowListener<S> listener;

    public Builder<S, E> from(S state) {
      this.from = state;
      return this;
    }

    public Builder<S, E> to(S state) {
      this.to = state;
      return this;
    }

    public Builder<S, E> event(E event) {
      this.event = event;
      return this;
    }

    public Builder<S, E> listener(WorkflowListener<S> listener) {
      this.listener = listener;
      return this;
    }

    public StateMachineTransition<S, E> build() {
      return new StateMachineTransition<>(from, to, event, listener);
    }
  }
}
