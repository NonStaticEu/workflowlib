package eu.nonstatic.workflow;

import java.util.Objects;
import java.util.function.Predicate;

public class StateMachineTransition<S, E> {

  private final WorkflowLink<S> link;
  private final E event;
  private final Predicate<TransitionContext> guard;

  public StateMachineTransition(S from, S to, E event) {
    this(from, to, event, null, null);
  }
  public StateMachineTransition(S from, S to, E event, WorkflowListener<S> listener) {
    this(from, to, event, null, listener);
  }

  public StateMachineTransition(S from, S to, E event, Predicate<TransitionContext> guard) {
    this(from, to, event, guard, null);
  }

  public StateMachineTransition(S from, S to, E event, Predicate<TransitionContext> guard, WorkflowListener<S> listener) {
    this.guard = guard;
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

  public boolean test(TransitionContext context) {
    return guard == null || guard.test(context);
  }

  public static <S, E> Builder<S, E> builder() {
    return new Builder<>();
  }



  public static class Builder<S, E> {

    private S from;
    private S to;
    private E event;
    private Predicate<TransitionContext> guard;
    private WorkflowListener<S> listener;

    public Builder<S, E> event(E event) {
      this.event = event;
      return this;
    }

    public Builder<S, E> from(S state) {
      this.from = state;
      return this;
    }

    public Builder<S, E> to(S state) {
      this.to = state;
      return this;
    }

    public Builder<S, E> condition(Predicate<TransitionContext> condition) {
      this.guard = condition;
      return this;
    }

    public Builder<S, E> listener(WorkflowListener<S> listener) {
      this.listener = listener;
      return this;
    }

    public StateMachineTransition<S, E> build() {
      return new StateMachineTransition<>(from, to, event, guard, listener);
    }
  }
}
