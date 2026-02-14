package eu.nonstatic.workflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class WorkflowImplTest {

  private static TestWorkflow createWorkflowWithConstructor() {
    return new TestWorkflow("test",
        new WorkflowStep<>(null, List.of(
            new WorkflowStep<>("state1"),
            new WorkflowStep<>("state2", List.of(
                new WorkflowStep<>("catch1"),
                new WorkflowStep<>("state3")
            ))
        ))
    );
  }

  private static TestWorkflow createWorkflowWithBuilder() {
    var steps = WorkflowStep.<String>builder(null)
        .next("state1")
        .next(WorkflowStep.builder("state2")
            .next("catch1")
            .next("state3")
            .build()
        ).build();
    return new TestWorkflow("test", steps);
  }

  @Test
  void should_be_equal() {
    TestWorkflow workflow = createWorkflowWithConstructor();

    assertEquals(workflow, workflow);
    assertEquals(workflow, createWorkflowWithConstructor());
    assertEquals(workflow, createWorkflowWithBuilder());

    assertNotEquals(workflow, new Object());
    assertNotEquals(null, workflow);
  }

  @Test
  void should_get_node() {
    assertTrue(createWorkflowWithConstructor().peek(null).isEmpty());

    assertNotNull(createWorkflowWithConstructor().peek("state1"));
    assertNotNull(createWorkflowWithConstructor().peek("state2"));
    assertNotNull(createWorkflowWithConstructor().peek("state3"));
    assertNotNull(createWorkflowWithConstructor().peek("catch1"));
  }
}
