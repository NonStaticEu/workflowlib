package eu.nonstatic.workflow;

import static eu.nonstatic.workflow.WorkflowStep.builder;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class StandardWorkflowTest {

  @Test
  void should_not_create_workflow_without_id() {
    var ex = assertThrows(NullPointerException.class, () -> StandardWorkflow.<String>builder()
        .metas(new WorkflowMetas())
        .start(builder("state1")
            .nextff("state2")
            .build())
        .build());

    assertEquals("This workflow must have an id", ex.getMessage());
  }

  @Test
  void should_not_build_workflow_with_listener_at_start() {
    var ex = assertThrows(IllegalArgumentException.class, () -> StandardWorkflow.<String>builder("test")
        .metas(new WorkflowMetas())
        .start(builder("state2")
            .listener((from, to, context) -> System.out.println("Hello World"))
            .build())
        .build());

    assertEquals("The first step cannot have a listener", ex.getMessage());
  }

  @Test
  void should_not_build_workflow_with_null_elsewhere_than_start() {
    var ex = assertThrows(IllegalArgumentException.class, () -> StandardWorkflow.<String>builder("test")
        .metas(new WorkflowMetas())
        .start(builder("state1")
            .nextff("state2")
            .nextff(null)
            .nextff("state2")
            .build())
        .build());

    assertEquals("Only the first step may have a null state", ex.getMessage());
  }

  @Test
  void should_build_workflow_with_null_state_at_start() {
    var workflow = assertDoesNotThrow(() -> StandardWorkflow.<String>builder("test")
        .metas(new WorkflowMetas())
        .start(WorkflowStep.<String>builder(null)
            .nextff("state1")
            .nextff("state2")
            .build())
        .build());

    var start = workflow.getStart();
    assertEquals("state1", start.get(0).getState());
    assertEquals("state2", start.get(1).getState());
  }

}
