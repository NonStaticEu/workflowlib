package eu.nonstatic.workflow;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TransitionReport<S> {

  private final WorkflowPath<S> path;
  private final List<TransitionResult<S>> results;

  public TransitionReport(WorkflowPath<S> path, List<TransitionResult<S>> results) {
    if(path.size() != results.size()
        || !path.stream().collect(Collectors.toList()).equals(results.stream().map(TransitionResult::getLink).collect(Collectors.toList()))) { //TODO rework
      throw new IllegalArgumentException("path and results are unrelated");
    }

    this.path = path;
    this.results = Collections.unmodifiableList(results);
  }

  public WorkflowPath<S> getPath() {
    return path;
  }

  public List<TransitionResult<S>> getResults() {
    return results;
  }

  public boolean isEmpty() {
    return results.isEmpty();
  }
}
