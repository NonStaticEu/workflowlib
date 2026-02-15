package eu.nonstatic.workflow;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class StateMachineException extends RuntimeException {

  private final Serializable machineId;
  private final TransitionReport<?> report;

  public StateMachineException(Serializable machineId, TransitionReport<?> report) {
    super(validate(report));
    this.machineId = machineId;
    this.report = report;
  }

  private static Exception validate(TransitionReport<?> report) {
    var it = report.getResults().iterator();
    TransitionResult<?> result = null;
    while(it.hasNext()) {
      result = it.next();
      if(result.isSuccessful() ^ it.hasNext()) {
        throw new IllegalStateException("Only the last result must be failed");
      }
    }
    if(result == null) {
      throw new IllegalArgumentException("Report mustn't be empty");
    }
    return result.getException();
  }

  public Serializable getMachineId() {
    return machineId;
  }

  public TransitionReport<?> getReport() {
    return report;
  }

  public WorkflowLink<?> getFailedTransition() {
    var results = report.getResults();
    return results.get(results.size()-1).getLink();
  }

  public List<WorkflowLink<?>> getSuccessfulTransitions() {
    var results = report.getResults();
    return results.subList(0, results.size()-1).stream().map(TransitionResult::getLink).collect(Collectors.toList());
  }
}
