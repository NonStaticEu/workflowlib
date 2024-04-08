package eu.nonstatic.workflow;

import java.util.List;
import java.util.Optional;

public interface Workflow<S, L extends WorkflowLink<S, L>> {

  List<L> getStart();

  Optional<L> peek(S state);

  boolean exists(S state);

  Optional<WorkflowPath<S>> path(S from, S to);
}
