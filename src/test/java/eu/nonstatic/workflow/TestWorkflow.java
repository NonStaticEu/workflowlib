package eu.nonstatic.workflow;

class TestWorkflow extends StandardWorkflow<String> implements NamedWorkflow<String, StandardWorkflowNode<String>> {

  TestWorkflow(String name, WorkflowStep<String> start) {
    super(name, null, start);
  }

  public String getName() {
    return (String) getId();
  }
}
