package eu.nonstatic.workflow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Instances of this class are meant to be immutable
 */
public final class WorkflowPath<S> implements Iterable<WorkflowLink<S>>, Serializable {

  private final S from;
  private final S to;
  private final List<WorkflowLink<S>> links;

  WorkflowPath(S from, List<WorkflowLink<S>> links) {
    if(!links.isEmpty() && !links.get(0).getFrom().equals(from)) {
      throw new IllegalArgumentException(String.format("The given from: %s and the first link from field: %s must match", from, links.get(0).getFrom()));
    }
    this.from = from;
    this.to = links.isEmpty() ? from : links.get(links.size()-1).getTo();
    this.links = List.copyOf(links);
  }

  public S getFrom() {
    return from;
  }

  public S getTo() {
    return to;
  }

  public WorkflowLink<S> getLink(int index) {
    return links.get(index);
  }

  public boolean contains(WorkflowLink<S> link) {
    return links.contains(link);
  }

  public int indexOf(S state) {
    int i = 0;
    for (WorkflowLink<S> link : links) {
      if (link.getTo().equals(state)) {
        return i;
      }
      i++;
    }
    return -1;
  }

  public static <S> WorkflowPath<S> empty(S from) {
    return new WorkflowPath<>(from, List.of());
  }

  public boolean isEmpty() {
    return links.isEmpty();
  }

  public int size() {
    return links.size();
  }

  public boolean contains(S state) {
    return stream().anyMatch(step -> step.getTo().equals(state));
  }

  public Stream<WorkflowLink<S>> stream() {
    return links.stream();
  }

  @Override
  public Iterator<WorkflowLink<S>> iterator() {
    return links.iterator();
  }

  @Override
  public String toString() {
    return links.stream().map(WorkflowLink::getTo).collect(Collectors.toList()).toString();
  }


  static final class Builder<S> {

    private final S from;
    private final List<WorkflowNode<S, ?>> nodes;

    Builder(S from) {
      this(from, 0);
    }

    private Builder(S from, int initialCapacity) {
      this.from = from;
      this.nodes = new ArrayList<>(initialCapacity);
    }

    S getFrom() {
      return from;
    }

    int size() {
      return nodes.size();
    }

    boolean contains(S state) {
      return nodes.stream().anyMatch(node -> node.getState().equals(state));
    }

    Builder<S> prepend(WorkflowNode<S, ?> node) {
      Builder<S> path = new Builder<>(from, 1 + size());
      path.nodes.add(node);
      path.nodes.addAll(this.nodes);
      return path;
    }

    WorkflowPath<S> build() {
      var links = new ArrayList<WorkflowLink<S>>(nodes.size());

      WorkflowNode<S, ?> prev = null;
      for (WorkflowNode<S, ?> node : nodes) {
        S linkFrom = (prev == null) ? this.from : prev.getState();
        S linkTo = node.getState();
        WorkflowListener<S> listener = node.getPrevious(linkFrom).map(WorkFlowArrow::getListener).orElse(null);
        WorkflowLink<S> link = new WorkflowLink<>(linkFrom, linkTo, listener);
        links.add(link);
        prev = node;
      }
      return new WorkflowPath<>(from, links);
    }
  }
}
