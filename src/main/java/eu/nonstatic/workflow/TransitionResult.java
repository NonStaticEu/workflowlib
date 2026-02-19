package eu.nonstatic.workflow;

import java.io.Serializable;

public class TransitionResult<S> implements Serializable {

  private final WorkflowLink<S> link;
  private final Exception exception;

  public TransitionResult(WorkflowLink<S> link) {
    this(link, null);
  }

  public TransitionResult(WorkflowLink<S> link, Exception exception) {
    this.link = link;
    this.exception = exception;
  }

  public WorkflowLink<S> getLink() {
    return link;
  }

  public S getFrom() {
    return link.getFrom();
  }

  public S getTo() {
    return link.getTo();
  }

  public boolean isEnd(S state) {
    return link.isEnd(state);
  }

  public Exception getException() {
    return exception;
  }

  public boolean isSuccessful() {
    return exception == null;
  }

  public boolean isFailed() {
    return !isSuccessful();
  }

  @Override
  public String toString() {
    return "[" + getFrom() + ", " + getTo() + ", " + isSuccessful() + "]";
  }
}
