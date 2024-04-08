package eu.nonstatic.workflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class WorkflowImplTest {

  private TestWorkflow createWorkflow() {
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

  @Test
  void should_be_equal() {
    TestWorkflow workflow = createWorkflow();

    assertEquals(workflow, workflow);
    assertEquals(workflow, createWorkflow());

    assertNotEquals(workflow, new Object());
    assertNotEquals(null, workflow);
  }

  @Test
  void should_get_link() {
    assertTrue(createWorkflow().peek(null).isEmpty());

    assertNotNull(createWorkflow().peek("state1"));
    assertNotNull(createWorkflow().peek("state2"));
    assertNotNull(createWorkflow().peek("state3"));
    assertNotNull(createWorkflow().peek("catch1"));
  }
}
