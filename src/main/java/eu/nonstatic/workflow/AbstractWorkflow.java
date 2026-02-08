package eu.nonstatic.workflow;

import static java.util.Collections.unmodifiableList;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractWorkflow<S, N extends AbstractWorkflowNode<S, N>> implements Workflow<S, N> {

  protected final List<N> start;
  protected final HashMap<S, N> chain = new HashMap<>();


  protected AbstractWorkflow(WorkflowStep<S> start) {
    N firstStep = toWorkflowNode(start, null);
    this.start = (firstStep.getState() == null) ? firstStep.getNext() : List.of(firstStep);
  }

  private N toWorkflowNode(WorkflowStep<S> step, N previousStep) {
    N node = chain.computeIfAbsent(step.getState(), this::newNode);

    if (previousStep != null) {
      node.previous.add(previousStep);
    }

    for (WorkflowStep<S> nextStep : step.getNext()) {
      node.next.add(toWorkflowNode(nextStep, node));
    }

    return node;
  }

  protected abstract N newNode(S state);

  @Override
  public List<N> getStart() {
    return unmodifiableList(start);
  }

  public Optional<N> peek(S state) {
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
    N wto = peek(to).orElseThrow(() -> new NoSuchElementException("Unknown to value: " + to));
    return path(from, wto, WorkflowPath.empty())
        .stream()
        .min(Comparator.comparingInt(WorkflowPath::size));
  }

  private static <S, K extends WorkflowNode<S, K>> List<WorkflowPath<S>> path(S from, K to, WorkflowPath<S> existingPath) {
    if(to.isOn(from)) {
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
