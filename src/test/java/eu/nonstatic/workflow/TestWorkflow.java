package eu.nonstatic.workflow;

class TestWorkflow extends AbstractWorkflow<String, TestWorkflowLink> implements NamedWorkflow<String, TestWorkflowLink> {
  protected final String name;

  TestWorkflow(String name, WorkflowStep<String> start) {
    super(start);
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override protected TestWorkflowLink newLink(String state) {
    return new TestWorkflowLink(state);
  }
}
