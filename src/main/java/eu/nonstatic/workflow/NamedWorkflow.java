package eu.nonstatic.workflow;

public interface NamedWorkflow<S, L extends WorkflowLink<S, L>> extends Workflow<S, L> {
  String getName();
}
