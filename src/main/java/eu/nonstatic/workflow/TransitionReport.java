package eu.nonstatic.workflow;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class TransitionReport<S> implements Iterable<TransitionResult<S>>, Serializable {

  private final Outcome outcome; // "composite" indicator, may be null if the requested transition isn't defined in the state machine
  private final WorkflowPath<S> path; // What's planned; also contains the initial state
  private final List<TransitionResult<S>> results; // may be empty if the transition isn't executed (eg: guarded)
  private final S finalState;


  public TransitionReport(WorkflowPath<S> path, List<TransitionResult<S>> results, S finalState) {
    this(null, path, results, finalState);
  }

  TransitionReport(Outcome outcome, WorkflowPath<S> path, List<TransitionResult<S>> results, S finalState) {
    // TODO use flexible constructors when migrating to JDK25
    int failures = validatePathVsResults(path, results);
    validateFinalState(path, results, finalState);

    this.outcome = validateOutcome(outcome, results, failures);
    this.path = path;
    this.results = Collections.unmodifiableList(results);
    this.finalState = finalState;
  }

  /**
   * Checks that: - either both args have the same size and match each other, no matter the success of the results - or the available results match the path until the last result
   * which is the only one failed
   */
  private static <S> int validatePathVsResults(WorkflowPath<S> path, List<TransitionResult<S>> results) {
    if(results.isEmpty()) { // transition not executed (eg: guarded)
      return 0;
    } else if(path.size() < results.size()) {
      throw new IllegalArgumentException("path is shorter than results: " + path + " vs " + results);
    }

    int failures = 0;
    var rit = results.iterator();
    TransitionResult<S> result = null;
    for (WorkflowLink<S> link : path) {
      if(rit.hasNext()) {
        result = rit.next();
        if(result.getLink().equals(link)) {
          if(result.isFailed()) {
            failures++;
          }
        } else {
          throw new IllegalArgumentException("path and results are unrelated: " + path + " vs " + results);
        }
      } else if(failures == 0) { // when guarded, there are no results, no failures, hence the test at the beginning
        throw new IllegalArgumentException("results is shorter than path but has no failed element: " + path + " vs " + results);
      } else if(failures > 1) {
        throw new IllegalArgumentException("results is shorter than path but has multiple failed elements: " + path + " vs " + results);
      } else if(result.isSuccessful()) {
        throw new IllegalArgumentException("results is shorter than path but the last element isn't failed: " + result);
      }
    }

    return failures;
  }

  private static <S> void validateFinalState(WorkflowPath<S> path, List<TransitionResult<S>> results, S finalState) {
    if(results.isEmpty() || path.isEmpty()) { // actually an empty path means an empty results, so the 2nd clause isn't needed per se
      if(!path.getFrom().equals(finalState)) {
        throw new IllegalArgumentException("When the path is empty, the final state must match the initial state: " + finalState + " != " + path.getFrom());
      }
    } else {
      TransitionResult<S> lastTrans = results.get(results.size() - 1); // TODO getLast when migrating to JDK17
      if(path.size() == results.size()) {
        if(!lastTrans.isEnd(finalState)) {
          throw new IllegalArgumentException("When the path is completely covered, the final state must match the ultimate or penultimate result state: " + finalState + " != " + lastTrans.getLink());
        }
      } else if(!lastTrans.getFrom().equals(finalState)) {
        throw new IllegalArgumentException("When the path isn't completely covered, the final state must match the last successful state: " + finalState + " != " + lastTrans.getFrom());
      }
    }
  }


  private static <S> Outcome validateOutcome(Outcome outcome, List<TransitionResult<S>> results, int failures) {
    outcome = Objects.requireNonNullElseGet(outcome, () -> failures == 0 ? Outcome.SUCCESSFUL : Outcome.FAILED);

    if(failures > 0 && !Outcome.FAILED.equals(outcome)) {
      throw new IllegalArgumentException("Outcome must be FAILED if there are failed results: " + outcome + " vs " + results);
    } else if(!results.isEmpty() && !outcome.executed) {
      throw new IllegalArgumentException("Outcome must reflect transition execution when results is not empty: " + outcome + " vs " + results);
    }

    return outcome;
  }


  public static <S> TransitionReport<S> noTransition(S state) {
    return new TransitionReport<>(Outcome.NO_TRANSITION, WorkflowPath.empty(state), List.of(), state);
  }

  public static <S> TransitionReport<S> guarded(WorkflowPath<S> path) {
    return new TransitionReport<>(Outcome.GUARDED, path, List.of(), path.getFrom());
  }


  public WorkflowPath<S> getPath() {
    return path;
  }

  public List<TransitionResult<S>> getResults() {
    return results;
  }

  public S getInitialState() {
    return path.getFrom();
  }

  public S getTargetState() {
    return outcome == Outcome.NO_TRANSITION ? null : path.getTo();
  }

  public S getFinalState() {
    return finalState;
  }

  public boolean isExecuted() {
    return outcome.executed; // == !isEmpty()
  }

  public boolean isSuccessful() {
    return outcome.successful;
  }

  public boolean isFailed() {
    return !outcome.successful;
  }

  /**
   * It doesn't imply the path has been completely covered.
   * We just moved at least once.
   */
  public boolean isTransitioned() {
    return !getFinalState().equals(getInitialState()); // I could also check at least one result is failed
  }

  /**
   * NOT equivalent to isTransitioned() && isFailed() where A -> !B -> C is possible (with leniency) contrary to here.
   */
  public boolean isPartialPath() {
    return isTransitioned() && !isCompletePath();
  }

  public boolean isCompletePath() {
    return getFinalState().equals(getTargetState());
  }

  public boolean isEmpty() {
    return results.isEmpty();
  }

  public boolean isSelfPath() {
    return path.isEmpty();
  }


  @Override
  public Iterator<TransitionResult<S>> iterator() {
    return results.iterator();
  }

  public Stream<TransitionResult<S>> stream() {
    return results.stream();
  }

  enum Outcome {
    SUCCESSFUL(true, true),
    FAILED(true, false), // This outcome appears as soon as at least on transition failed (think of the leniency case)
    GUARDED(false, false),
    NO_TRANSITION(false, false),
    ;

    final boolean executed;
    final boolean successful;

    Outcome(boolean executed, boolean successful) {
      this.executed = executed;
      this.successful = successful;
    }
  }
}
