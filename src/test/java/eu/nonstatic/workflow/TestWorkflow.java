package eu.nonstatic.workflow;

class TestWorkflow extends AbstractWorkflow<String, TestWorkflowNode> implements NamedWorkflow<String, TestWorkflowNode> {
  protected final String name;

  TestWorkflow(String name, WorkflowStep<String> start) {
    super(start);
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override protected TestWorkflowNode newNode(String state) {
    return new TestWorkflowNode(state);
  }
}
