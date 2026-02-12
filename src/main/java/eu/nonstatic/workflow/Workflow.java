package eu.nonstatic.workflow;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface Workflow<S, N extends WorkflowNode<S, N>> {

  List<N> getStart();

  Optional<N> peek(S state);

  boolean exists(S state);

  Optional<WorkflowPath<S>> path(S from, S to);

  Object getKey();

  static StandardWorkflow.Builder builder() {
    return StandardWorkflow.builder();
  }

  default StateMachine<S, N> toMachine(Serializable id, S state) {
    return new StateMachine<>(id, this, state);
  }
}
