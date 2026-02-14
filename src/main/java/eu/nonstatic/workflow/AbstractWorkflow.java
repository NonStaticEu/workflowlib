package eu.nonstatic.workflow;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractWorkflow<S, N extends AbstractWorkflowNode<S, N>> implements Workflow<S, N> {

  private final List<N> start;
  private final HashMap<S, N> nodes = new HashMap<>();


  protected AbstractWorkflow(WorkflowStep<S> start) {
    N firstNode = toNode(0, start, null);
    this.start = (firstNode.getState() == null)
        ? firstNode.getNext().stream().map(WorkFlowTransition::getNode).collect(Collectors.toUnmodifiableList())
        : List.of(firstNode);
  }

  private N toNode(int level, WorkflowStep<S> step, N previousNode) {
    if(step.getState() == null && level != 0) {
      throw new IllegalArgumentException("Only the first step may have a null state.");
    }

    N node = nodes.computeIfAbsent(step.getState(), this::newNode);

    if (previousNode != null) {
      node.previous.add(new WorkFlowTransition<>(previousNode, step.getListener()));
    }

    for (WorkflowStep<S> nextStep : step.getNext()) {
      node.next.add(new WorkFlowTransition<>(toNode(level+1, nextStep, node), nextStep.getListener()));
    }

    return node;
  }

  protected abstract N newNode(S state);

  @Override
  public List<N> getStart() {
    return start; // already unmodifiable as per constructor
  }

  public Optional<N> peek(S state) {
    return (state != null) ? Optional.ofNullable(nodes.get(state)) : Optional.empty(); // chain may have a null key for workflows having several starts, but it's not associated with a state per se.
  }

  @Override
  public boolean exists(S state) {
    return peek(state).isPresent();
  }

  public Optional<WorkflowPath<S>> path(S from, S to) {
    if(!exists(from)) {
      throw new NoSuchElementException("Unknown from value: " + from);
    }
    N nodeTo = peek(to).orElseThrow(() -> new NoSuchElementException("Unknown to value: " + to));
    return buildPath(nodeTo, new WorkflowPath.Builder<>(from))
        .stream()
        .min(Comparator.comparingInt(WorkflowPath.Builder::size))
        .map(WorkflowPath.Builder::build);
  }

  private static <S, N extends WorkflowNode<S, N>> List<WorkflowPath.Builder<S>> buildPath(N nodeTo, WorkflowPath.Builder<S> partialPath) {
    if(nodeTo.isOn(partialPath.getFrom())) {
      return List.of(partialPath);
    }

    WorkflowPath.Builder<S> increasedPath = partialPath.prepend(nodeTo);
    return nodeTo.getPrevious()
        .stream()
        .filter(previous -> !partialPath.contains(previous.getNode().getState())) // there shouldn't be loops but you never know CAUTION contains
        .flatMap(previous -> buildPath(previous.getNode(), increasedPath).stream())
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
    return Objects.hashCode(start); // No need to add the nodes, they are contained in the start node
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
