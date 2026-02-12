package eu.nonstatic.workflow;

@FunctionalInterface
public interface WorkflowListener<S> {

  void invoke(S from, S to, TransitionContext context);
}
