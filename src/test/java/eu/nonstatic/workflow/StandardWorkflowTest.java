package eu.nonstatic.workflow;

import static eu.nonstatic.workflow.WorkflowStep.builder;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class StandardWorkflowTest {

  @Test
  void should_not_create_workflow_without_id() {
    WorkflowStep<String> steps = builder("state1")
        .nextff("state2")
        .build();

    var ex = assertThrows(NullPointerException.class, () -> {
      StandardWorkflow.<String>builder()
          .metas(new WorkflowMetas())
          .start(steps)
          .build();
    });

    assertEquals("This workflow must have an id", ex.getMessage());
  }

  @Test
  void should_not_build_workflow_with_listener_at_start() {
    WorkflowStep<String> steps = builder("state2")
        .listener((from, to, context) -> System.out.println("Hello World"))
        .build();

    var ex = assertThrows(IllegalArgumentException.class, () -> {
      StandardWorkflow.<String>builder("test")
          .metas(new WorkflowMetas())
          .start(steps)
          .build();
    });

    assertEquals("The first step cannot have a listener", ex.getMessage());
  }

  @Test
  void should_not_build_workflow_with_null_elsewhere_than_start() {
    WorkflowStep<String> steps = builder("state1")
        .nextff("state2")
        .nextff(null)
        .nextff("state2")
        .build();

    var ex = assertThrows(IllegalArgumentException.class, () -> {
      StandardWorkflow.<String>builder("test")
          .metas(new WorkflowMetas())
          .start(steps)
          .build();
    });

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


  @Test
  void should_equal() {
    var workflow = StandardWorkflow.<String>builder("test")
        .metas(new WorkflowMetas())
        .start(builder("state0")
            .nextff("state1")
            .nextff("state2")
            .build())
        .build();

    var workflowEqual = StandardWorkflow.<String>builder()
        .id("test")
        .metas(new WorkflowMetas())
        .start(builder("state0")
            .nextff("state1")
            .nextff("state2")
            .build())
        .build();

    var workflowNotEqual1 = StandardWorkflow.<String>builder("test")
        .id("test999") // overwrote value
        .metas(new WorkflowMetas())
        .start(builder("state0")
            .nextff("state1")
            .nextff("state2")
            .build())
        .build();

    var metasNotEqual2 = new WorkflowMetas();
    metasNotEqual2.put("key", "value");
    var workflowNotEqual2 = StandardWorkflow.<String>builder("test")
        .metas(metasNotEqual2) // different metas
        .start(builder("state0")
            .nextff("state1")
            .nextff("state2")
            .build())
        .build();

    var workflowNotEqual3 = StandardWorkflow.<String>builder("test")
        .metas(new WorkflowMetas())
        .start(builder("state0")
            .nextff("state999") // different state
            .nextff("state2")
            .build())
        .build();

    var workflowNotEqual4 = StandardWorkflow.<String>builder("test")
        // no metas
        .start(builder("state0")
            .nextff("state1")
            .nextff("state2")
            .build())
        .build();

    assertEquals(workflow, workflowEqual);
    assertNotEquals(workflow, workflowNotEqual1);
    assertNotEquals(workflow, workflowNotEqual2);
    assertNotEquals(workflow, workflowNotEqual3);
    assertNotEquals(workflow, workflowNotEqual4);
  }
}
