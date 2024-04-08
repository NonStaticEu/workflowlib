package eu.nonstatic.workflow;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface WorkflowLink<S, L extends WorkflowLink<S, L>> extends Comparable<L> {

  S getState();

  List<L> getPrevious();
  Optional<L> getPrevious(S state);

  List<L> getNext();
  Optional<L> getNext(S state);

  boolean isEqual(S state);

  boolean isAfter(S state);
  boolean isAfter(S state, Collection<S> visited);
  boolean isAfterOrEqual(S state);

  boolean isBefore(S state);
  boolean isBefore(S state, Collection<S> visited);
  boolean isBeforeOrEqual(S state);

  boolean isTwoWay(S state);

  boolean isTerminal();
}
