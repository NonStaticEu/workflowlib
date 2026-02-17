package eu.nonstatic.workflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Class aiming for easier navigation in the workflow graph
 */
public interface WorkflowNode<S, N extends WorkflowNode<S, N>> extends Comparable<N> {

  S getState();

  List<WorkFlowArrow<S, N>> getPrevious();
  Optional<WorkFlowArrow<S, N>> getPrevious(S state);

  List<WorkFlowArrow<S, N>> getNext();
  Optional<WorkFlowArrow<S, N>> getNext(S state);


  default boolean isOn(S state) {
    return getState() != null && getState().equals(state);
  }

  default boolean isAfter(S state) {
    return isAfter(state, list(getState()));
  }

  default boolean isAfter(S state, Collection<S> visited) {
    return getPrevious().stream()
        .filter(trans -> !visited.contains(trans.getNode().getState()))
        .anyMatch(trans -> {
          N node = trans.getNode();
          return node.isOn(state) || node.isAfter(state, concat(visited, node.getState()));
        });
  }

  default boolean isAfterOrOn(S state) {
    return isOn(state) || isAfter(state);
  }

  default boolean isBefore(S state) {
    return isBefore(state, list(getState()));
  }

  default boolean isBefore(S state, Collection<S> visited) {
    return getNext().stream()
        .filter(trans -> !visited.contains(trans.getNode().getState()))
        .anyMatch(trans -> {
          N node = trans.getNode();
          return node.isOn(state) || node.isBefore(state, concat(visited, node.getState()));
        });
  }

  default boolean isTwoWay(S state) {
    return getNext(state).flatMap(trans -> trans.getNode().getPrevious(getState())).isPresent()
        || getPrevious(state).flatMap(trans -> trans.getNode().getNext(getState())).isPresent();
  }

  default boolean isBeforeOrOn(S state) {
    return isOn(state) || isBefore(state);
  }

  default boolean isTerminal() {
    return getNext().isEmpty();
  }


  private static <S> List<S> list(S state) {
    var list = new ArrayList<S>(1); // must be null-tolerant, so no List.of(state)
    list.add(state);
    return list;
  }

  private static <S> List<S> concat(Collection<S> states, S extra) {
    var concat = new ArrayList<S>(states.size() + 1); // must be null-tolerant
    concat.addAll(states);
    concat.add(extra);
    return concat;
  }


  static <S, N extends WorkflowNode<S, N>> boolean equalsLoopSafe(List<N> nodes1, List<N> nodes2, Set<S> seenStates) {
    if(nodes1.size() != nodes2.size()) {
      return false;
    }

    var it2 = nodes2.iterator();
    for (N node1 : nodes1) {
      if(!equalsLoopSafe(node1, it2.next(), seenStates)) {
        return false;
      }
    }

    return true;
  }

  static <S, N extends WorkflowNode<S, N>> boolean equalsLoopSafe(WorkflowNode<S, N> node1, WorkflowNode<S, N> node2, Set<S> seenStates) {
    S state1 = node1.getState();
    if(seenStates.contains(state1)) {
      return true;
    }

    if(!Objects.equals(state1, node2.getState())) {
      return false;
    }

    seenStates.add(state1);
    return WorkFlowArrow.equalsLoopSafe(node1.getNext(), node2.getNext(), seenStates);
  }

  static <S, N extends WorkflowNode<S, N>> int hashCodeLoopSafe(WorkflowNode<S, N> node) {
    if(node.isTerminal()) {
      return node.getState().hashCode(); // I could also add {node.getState(), null, null} to the links
    } else {
      var links = new HashSet<WorkflowLink<S>>();
      WorkflowNode.unwindLinks(node, links);
      return Objects.hash(links);
    }
  }

  static <S, N extends WorkflowNode<S, N>> void unwindLinks(WorkflowNode<S, N> node, Set<WorkflowLink<S>> links) {
    S from = node.getState();
    for (WorkFlowArrow<S, N> arrow : node.getNext()) {
      N nextNode = arrow.getNode();
      if(links.add(new WorkflowLink<>(from, nextNode.getState(), arrow.getListener()))) {
        unwindLinks(nextNode, links);
      }
    }
  }
}
