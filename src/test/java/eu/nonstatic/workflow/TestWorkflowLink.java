package eu.nonstatic.workflow;

class TestWorkflowLink extends AbstractWorkflowLink<String, TestWorkflowLink> {
  TestWorkflowLink(String state) {
    super(state);
  }
}
