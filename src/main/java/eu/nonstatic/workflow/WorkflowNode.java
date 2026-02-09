package eu.nonstatic.workflow;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Class aiming for easier navigation in the workflow graph
 */
public interface WorkflowNode<S, N extends WorkflowNode<S, N>> extends Comparable<N> {

  S getState();

  List<WorkFlowTransition<S, N>> getPrevious();
  Optional<WorkFlowTransition<S, N>> getPrevious(S state);

  List<WorkFlowTransition<S, N>> getNext();
  Optional<WorkFlowTransition<S, N>> getNext(S state);

  boolean isOn(S state);

  boolean isAfter(S state);
  boolean isAfter(S state, Collection<S> visited);
  boolean isAfterOrOn(S state);

  boolean isBefore(S state);
  boolean isBefore(S state, Collection<S> visited);
  boolean isBeforeOrOn(S state);

  boolean isTwoWay(S state);

  boolean isTerminal();
}
