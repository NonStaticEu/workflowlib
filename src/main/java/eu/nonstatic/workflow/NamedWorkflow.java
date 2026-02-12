package eu.nonstatic.workflow;

public interface NamedWorkflow<S, N extends WorkflowNode<S, N>> extends Workflow<S, N> {
  String getName();

  default Object getKey() {
    return getName();
  }
}
