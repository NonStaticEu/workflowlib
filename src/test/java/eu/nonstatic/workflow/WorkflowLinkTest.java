package eu.nonstatic.workflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class WorkflowLinkTest {

  TestWorkflow workflow = new TestWorkflow("test",
      new WorkflowStep<>("state1", List.of(
          new WorkflowStep<>("catch1"),
          new WorkflowStep<>("state4"),
          new WorkflowStep<>("state6", List.of(
              new WorkflowStep<>("catch1")
          )),
          new WorkflowStep<>("state2", List.of(
              new WorkflowStep<>("catch2"),
              new WorkflowStep<>("state3", List.of(
                  new WorkflowStep<>("state4", List.of(
                      new WorkflowStep<>("catch2"),
                      new WorkflowStep<>("state5")
                  )),
                  new WorkflowStep<>("state7",
                      new WorkflowStep<>("state3")) // backwards!
              ))
          ))
      ))
  );

  @Test
  void should_get_state() {
    assertEquals("state4", workflow.peek("state4").get().getState());
  }

  @Test
  void should_be_equal() {
    assertTrue(workflow.peek("state1").get().isEqual("state1"));
    assertTrue(workflow.peek("state2").get().isEqual("state2"));
    assertTrue(workflow.peek("state3").get().isEqual("state3"));
    assertTrue(workflow.peek("state4").get().isEqual("state4"));
    assertTrue(workflow.peek("state5").get().isEqual("state5"));
    assertTrue(workflow.peek("catch1").get().isEqual("catch1"));
    assertTrue(workflow.peek("catch2").get().isEqual("catch2"));
  }

  @Test
  void should_be_after() {
    TestWorkflowLink state4 = workflow.peek("state4").get();

    assertTrue(state4.isAfter("state1"));
    assertTrue(state4.isAfter("state2"));
    assertTrue(state4.isAfter("state3"));

    assertFalse(state4.isAfter("catch2"));
    assertFalse(state4.isAfter("catch1"));

    assertFalse(state4.isAfter("state4"));
    assertFalse(state4.isAfter("state5"));
  }

  @Test
  void should_be_after_or_equal() {
    TestWorkflowLink state4 = workflow.peek("state4").orElseThrow();

    assertTrue(state4.isAfterOrEqual("state1"));
    assertTrue(state4.isAfterOrEqual("state2"));
    assertTrue(state4.isAfterOrEqual("state3"));

    assertFalse(state4.isAfterOrEqual("catch2"));
    assertFalse(state4.isAfterOrEqual("catch1"));

    assertTrue(state4.isAfterOrEqual("state4"));
    assertFalse(state4.isAfterOrEqual("state5"));
  }

  @Test
  void should_be_before() {
    TestWorkflowLink state2 = workflow.peek("state2").orElseThrow();

    assertTrue(state2.isBefore("catch2"));
    assertTrue(state2.isBefore("state3"));
    assertTrue(state2.isBefore("state4"));
    assertTrue(state2.isBefore("state5"));

    assertFalse(state2.isBefore("catch1"));

    assertFalse(state2.isBefore("state2"));
    assertFalse(state2.isBefore("state1"));
  }

  @Test
  void should_be_before_or_equal() {
    TestWorkflowLink state2 = workflow.peek("state2").orElseThrow();

    assertTrue(state2.isBeforeOrEqual("catch2"));
    assertTrue(state2.isBeforeOrEqual("state3"));
    assertTrue(state2.isBeforeOrEqual("state4"));
    assertTrue(state2.isBeforeOrEqual("state5"));

    assertFalse(state2.isBeforeOrEqual("catch1"));

    assertTrue(state2.isBeforeOrEqual("state2"));
    assertFalse(state2.isBeforeOrEqual("state1"));
  }

  @Test
  void should_be_two_ways() {
    assertTrue(workflow.peek("state7").get().isTwoWay("state3"));
    assertTrue(workflow.peek("state3").get().isTwoWay("state7"));
    assertFalse(workflow.peek("state7").get().isTwoWay("catch2"));
    assertFalse(workflow.peek("catch2").get().isTwoWay("state7"));
    assertFalse(workflow.peek("state7").get().isTwoWay("state6"));
    assertFalse(workflow.peek("state6").get().isTwoWay("state7"));
  }

  @Test
  void should_be_terminal() {
    assertTrue(workflow.peek("catch1").get().isTerminal());
    assertTrue(workflow.peek("catch2").get().isTerminal());
    assertTrue(workflow.peek("state5").get().isTerminal());
  }

  @Test
  void should_not_be_terminal() {
    assertFalse(workflow.peek("state1").get().isTerminal());
    assertFalse(workflow.peek("state2").get().isTerminal());
    assertFalse(workflow.peek("state3").get().isTerminal());
    assertFalse(workflow.peek("state4").get().isTerminal());
  }

  @Test
  void should_compare_state() {
    TestWorkflowLink state2 = workflow.peek("state2").get();
    TestWorkflowLink state4 = workflow.peek("state4").get();
    TestWorkflowLink state5 = workflow.peek("state5").get();
    TestWorkflowLink state6 = workflow.peek("state6").get();
    TestWorkflowLink catch1 = workflow.peek("catch1").get();
    TestWorkflowLink catch2 = workflow.peek("catch2").get();

    assertEquals(0, state2.compareTo(state2));
    assertEquals(0, state4.compareTo(state4));

    assertTrue(state2.compareTo(state4) < 0);
    assertTrue(state4.compareTo(state2) > 0);

    assertTrue(state2.compareTo(catch2) < 0);
    assertTrue(state4.compareTo(catch2) < 0);

    assertTrue(state2.compareTo(catch1) < 0); // because catch1 is terminal

    assertThrows(IllegalArgumentException.class, () -> state4.compareTo(state6));
    assertThrows(IllegalArgumentException.class, () -> state6.compareTo(state4));

    assertThrows(IllegalArgumentException.class, () -> catch1.compareTo(catch2));
    assertThrows(IllegalArgumentException.class, () -> catch1.compareTo(state5));
    assertThrows(IllegalArgumentException.class, () -> state5.compareTo(catch1));
  }

  @Test
  void should_refuse_unknown_path_bounds() {
    assertThrows(NoSuchElementException.class, () -> workflow.path("xxx", "state2"));
    assertThrows(NoSuchElementException.class, () -> workflow.path("state1", "xxx"));
  }

  @Test
  void should_get_empty_path() {
    Optional<WorkflowPath<String>> path = workflow.path("state4", "state4");
    assertTrue(path.get().isEmpty());
  }

  @Test
  void should_get_path() {
    WorkflowPath<String> path = workflow.path("state1", "catch2").get();
    assertEquals(2, path.size());
    assertEquals("state2", path.iterator().next());
    assertEquals("catch2", path.get(1));

    assertEquals(1, path.indexOf("catch2"));

    assertEquals("[state2, catch2]", path.toString());
  }

  @Test
  void should_not_get_path() {
    assertTrue(workflow.path("catch1", "catch2").isEmpty()); // no path between them
  }

  @Test
  void should_toString() {
    assertEquals("<null>", new WorkflowStep<>(null).toString());
    assertEquals("state42", new WorkflowStep<>("state42").toString());

    assertEquals("<null>", new TestWorkflowLink(null).toString());
    WorkflowPath<String> path = workflow
        .path("state1", "state5")
        .get();
    assertFalse(path.isEmpty());
    for (String step : path) {
      assertEquals(step, workflow.peek(step).get().toString());
    }
  }
}
