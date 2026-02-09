package eu.nonstatic.workflow;

import java.util.Objects;

public class WorkflowLink<S> {

  private final S from;
  private final S to;
  private final WorkflowListener<S> listener; // called when reaching this node's state

  public WorkflowLink(S from, S to, WorkflowListener<S> listener) {
    this.from = from;
    this.to = to;
    this.listener = listener;
  }

  public S getFrom() {
    return from;
  }

  public S getTo() {
    return to;
  }

  public WorkflowListener<S> getListener() {
    return listener;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WorkflowLink<?> that = (WorkflowLink<?>) o;
    return Objects.equals(from, that.from)
        && Objects.equals(to, that.to)
        && Objects.equals(listener, that.listener);
  }

  @Override
  public int hashCode() {
    return Objects.hash(from, to, listener);
  }
}
