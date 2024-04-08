package eu.nonstatic.workflow;

import static java.util.Collections.unmodifiableList;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractWorkflow<S, L extends AbstractWorkflowLink<S, L>> implements Workflow<S, L> {

  protected final List<L> start;
  protected final HashMap<S, L> chain = new HashMap<>();


  protected AbstractWorkflow(WorkflowStep<S> start) {
    L firstStep = toWorkflowLink(start, (L)null);
    this.start = (firstStep.getState() == null) ? firstStep.getNext() : List.of(firstStep);
  }

  private L toWorkflowLink(WorkflowStep<S> step, L previousStep) {
    L link = chain.computeIfAbsent(step.getState(), this::newLink);

    if (previousStep != null) {
      link.previous.add(previousStep);
    }

    for (WorkflowStep<S> nextStep : step.getNext()) {
      link.next.add(toWorkflowLink(nextStep, link));
    }

    return link;
  }

  protected abstract L newLink(S state);

  @Override
  public List<L> getStart() {
    return unmodifiableList(start);
  }

  public Optional<L> peek(String state) {
    return (state != null) ? Optional.ofNullable(chain.get(state)) : Optional.empty(); // chain may have a null key for workflows having several starts, but it's not associated to a state per se.
  }

  @Override
  public boolean exists(S state) {
    return peek(state).isPresent();
  }

  public Optional<WorkflowPath<S>> path(S from, S to) {
    if(!exists(from)) {
      throw new NoSuchElementException("Unknown from value: " + from);
    }
    L wto = peek(to).orElseThrow(() -> new NoSuchElementException("Unknown to value: " + to));
    return path(from, wto, WorkflowPath.empty())
        .stream()
        .min(Comparator.comparingInt(WorkflowPath::size));
  }

  private static <S, K extends WorkflowLink<S, K>> List<WorkflowPath<S>> path(S from, K to, WorkflowPath<S> existingPath) {
    if(to.isEqual(from)) {
      return List.of(existingPath);
    }

    WorkflowPath<S> increasedPath = existingPath.prepend(to.getState());
    return to.getPrevious()
        .stream()
        .filter(previous -> !existingPath.contains(previous.getState())) // there shouldn't be loops but you never know
        .flatMap(previous -> path(from, previous, increasedPath).stream())
        .collect(Collectors.toList());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AbstractWorkflow<?, ?> that = (AbstractWorkflow<?, ?>) o;
    return Objects.equals(start, that.start);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(start);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
