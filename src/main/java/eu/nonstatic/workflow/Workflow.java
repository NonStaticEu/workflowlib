package eu.nonstatic.workflow;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface Workflow<S, N extends WorkflowNode<S, N>> {

  List<N> getStart();

  Optional<N> peek(S state);

  boolean exists(S state);

  Optional<WorkflowPath<S>> path(S from, S to);

  Object getKey();

  static <S> StandardWorkflow.Builder<S> builder() {
    return StandardWorkflow.builder();
  }

  default StateMachine<S, N> toMachine(S state) {
    return toMachine(null, state);
  }

  default StateMachine<S, N> toMachine(Serializable id, S state) {
    if(id == null) {
      id = UUID.randomUUID().toString();
    }
    return new StateMachine<>(id, this, state);
  }
}
