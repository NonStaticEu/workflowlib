package eu.nonstatic.workflow;

public class NamedWorkflowSupplier<W extends NamedWorkflow> extends AbstractWorkflowSupplier<W> {

  public NamedWorkflowSupplier(W... workflows) {
    super(workflows);
  }

  public NamedWorkflowSupplier(Iterable<W> workflows) {
    super(workflows);
  }
}
