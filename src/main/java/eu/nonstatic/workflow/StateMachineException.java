package eu.nonstatic.workflow;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class StateMachineException extends RuntimeException {

  private final Serializable id;
  private final TransitionReport<?> report;

  public StateMachineException(Serializable id, TransitionReport<?> report) {
    super(validate(report));
    this.id = id;
    this.report = report;
  }

  private static Exception validate(TransitionReport<?> report) {
    var it = report.getResults().iterator();
    TransitionResult<?> result = null;
    while(it.hasNext()) {
      result = it.next();
      if(result.isSuccessful() ^ it.hasNext()) {
        throw new IllegalStateException("Only the last result must be in error");
      }
    }
    if(result == null) {
      throw new IllegalArgumentException("Report mustn't be empty");
    }
    return result.getException();
  }

  public Serializable getId() {
    return id;
  }

  public TransitionReport<?> getReport() {
    return report;
  }

  public WorkflowLink<?> getErroredTransition() {
    var results = report.getResults();
    return results.get(results.size()-1).getLink();
  }

  public List<WorkflowLink<?>> getSuccessfulTransition() {
    var results = report.getResults();
    return results.subList(0, results.size()-1).stream().map(TransitionResult::getLink).collect(Collectors.toList());
  }
}
