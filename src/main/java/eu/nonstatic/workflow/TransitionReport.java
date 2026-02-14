package eu.nonstatic.workflow;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class TransitionReport<S> implements Iterable<TransitionResult<S>>, Serializable {

  private final WorkflowPath<S> path;
  private final List<TransitionResult<S>> results;

  public TransitionReport(WorkflowPath<S> path, List<TransitionResult<S>> results) {
    validate(path, results);
    this.path = path;
    this.results = Collections.unmodifiableList(results);
  }

  /**
   * Checks that:
   * - either both args have the same size and match each other, no matter the success of the results
   * - or the available results match the path until the last result which is the only one failed
   */
  private static <S> void validate(WorkflowPath<S> path, List<TransitionResult<S>> results) {
    if(path.size() < results.size()) {
      throw new IllegalArgumentException("results is longer than the path");
    }

    int failed = 0;
    var rit = results.iterator();
    TransitionResult<S> result = null;
    for (WorkflowLink<S> link : path) {
      if(rit.hasNext()) {
        result = rit.next();
        if(result.getLink().equals(link)) {
          if(result.isFailed()) {
            failed++;
          }
        } else {
          throw new IllegalArgumentException("path and results are unrelated");
        }
      } else if(failed == 0) {
        throw new IllegalArgumentException("results is shorter than path but has no failed element");
      } else if(failed > 1) {
        throw new IllegalArgumentException("results is shorter than path but has multiple failed elements");
      } else if(result.isSuccessful()) {
        throw new IllegalArgumentException("results is shorter than path but the failed element is not the last");
      }
    }
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

  @Override
  public Iterator<TransitionResult<S>> iterator() {
    return results.iterator();
  }

  public Stream<TransitionResult<S>> stream() {
    return results.stream();
  }
}
