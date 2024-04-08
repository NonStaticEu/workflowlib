package eu.nonstatic.workflow;

import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Stream;

public abstract class AbstractWorkflowSupplier<W extends Workflow> {

  private final HashMap<Object, W> workflows = new HashMap<>();

  protected AbstractWorkflowSupplier(W... workflows) {
    this(Arrays.asList(workflows));
  }

  protected AbstractWorkflowSupplier(Iterable<W> workflows) {
    for (W workflow : workflows) {
      add(workflow);
    }
  }

  protected void add(W workflow) {
    workflows.putIfAbsent(toKey(workflow), workflow);
  }

  protected abstract Object toKey(W workflow);

  protected W get(String key) {
    return workflows.get(key);
  }

  public int getSize() {
    return workflows.size();
  }

  public Stream<W> stream() {
    return workflows.values().stream();
  }
}
