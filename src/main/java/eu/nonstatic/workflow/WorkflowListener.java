package eu.nonstatic.workflow;

@FunctionalInterface
public interface WorkflowListener<S> {

  <C> void invoke(S from, S to, C context); // TODO remove C generic to allow simplified declaration
}
