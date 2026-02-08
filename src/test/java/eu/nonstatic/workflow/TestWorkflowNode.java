package eu.nonstatic.workflow;

class TestWorkflowNode extends AbstractWorkflowNode<String, TestWorkflowNode> {
  TestWorkflowNode(String state) {
    super(state);
  }
}
