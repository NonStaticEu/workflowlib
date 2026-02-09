package eu.nonstatic.workflow;

public class TransitionResult<S> {

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

  public Exception getException() {
    return exception;
  }

  public boolean isSuccessful() {
    return exception == null;
  }
}
