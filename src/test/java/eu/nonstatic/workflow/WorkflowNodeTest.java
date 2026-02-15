package eu.nonstatic.workflow;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class WorkflowNodeTest {

  WorkflowListener<String> listener = (from, to, context) -> System.out.println("Hello World");

  TestWorkflow workflow = new TestWorkflow("test",
      new WorkflowStep<>("state1", List.of(
          new WorkflowStep<>("catch1"),
          new WorkflowStep<>("state4"),
          new WorkflowStep<>("state6", List.of(
              new WorkflowStep<>("catch1")
          )),
          new WorkflowStep<>("state2", List.of(
              new WorkflowStep<>("catch2", listener),
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
    assertTrue(workflow.peek("state1").get().isOn("state1"));
    assertTrue(workflow.peek("state2").get().isOn("state2"));
    assertTrue(workflow.peek("state3").get().isOn("state3"));
    assertTrue(workflow.peek("state4").get().isOn("state4"));
    assertTrue(workflow.peek("state5").get().isOn("state5"));
    assertTrue(workflow.peek("catch1").get().isOn("catch1"));
    assertTrue(workflow.peek("catch2").get().isOn("catch2"));
  }

  @Test
  void should_be_after() {
    var state4 = workflow.peek("state4").get();

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
    var state4 = workflow.peek("state4").orElseThrow();

    assertTrue(state4.isAfterOrOn("state1"));
    assertTrue(state4.isAfterOrOn("state2"));
    assertTrue(state4.isAfterOrOn("state3"));

    assertFalse(state4.isAfterOrOn("catch2"));
    assertFalse(state4.isAfterOrOn("catch1"));

    assertTrue(state4.isAfterOrOn("state4"));
    assertFalse(state4.isAfterOrOn("state5"));
  }

  @Test
  void should_be_before() {
    var state2 = workflow.peek("state2").orElseThrow();

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
    var state2 = workflow.peek("state2").orElseThrow();

    assertTrue(state2.isBeforeOrOn("catch2"));
    assertTrue(state2.isBeforeOrOn("state3"));
    assertTrue(state2.isBeforeOrOn("state4"));
    assertTrue(state2.isBeforeOrOn("state5"));

    assertFalse(state2.isBeforeOrOn("catch1"));

    assertTrue(state2.isBeforeOrOn("state2"));
    assertFalse(state2.isBeforeOrOn("state1"));
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
    var state2 = workflow.peek("state2").get();
    var state4 = workflow.peek("state4").get();
    var state5 = workflow.peek("state5").get();
    var state6 = workflow.peek("state6").get();
    var catch1 = workflow.peek("catch1").get();
    var catch2 = workflow.peek("catch2").get();

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
    int pathLength = path.size();
    assertEquals(2, pathLength);
    Iterator<WorkflowLink<String>> it = path.iterator();

    var link0 = it.next();
    assertEquals("state1", link0.getFrom());
    assertEquals("state2", link0.getTo());
    assertNull(link0.getListener());

    var link1 = it.next();
    assertEquals("state2", link1.getFrom());
    assertEquals("catch2", link1.getTo());
    assertEquals(listener, link1.getListener());

    assertSame(link1, path.get(1));
    assertThrows(IndexOutOfBoundsException.class, () -> path.get(pathLength));

    assertTrue(path.contains("state2"));
    assertTrue(path.contains("catch2"));
    assertEquals(1, path.indexOf("catch2"));
    assertEquals(-1, path.indexOf("state999"));
    assertTrue(path.contains(link0));
    assertFalse(path.contains(new WorkflowLink<>("state42", "state69", null)));

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

    assertEquals("<null>", new StandardWorkflowNode<>(null).toString());
    WorkflowPath<String> path = workflow
        .path("state1", "state5")
        .get();
    assertFalse(path.isEmpty());
    for (WorkflowLink<String> link : path) {
      assertEquals(link.getTo(), workflow.peek(link.getTo()).get().toString());
    }
  }

  @Test
  void should_accept_null_start() {
    WorkflowStep<String> steps = WorkflowStep.<String>builder(null)
        .nextff("state1")
        .nextff("state2")
        .build();

    assertDoesNotThrow(() -> new TestWorkflow("test", steps));
  }

  @Test
  void should_not_accept_null_next() {
    WorkflowStep<String> steps = WorkflowStep.builder("state1")
        .nextff("state2")
        .nextff(null)
        .nextff("state4")
        .build();

    assertThrows(IllegalArgumentException.class, () -> new TestWorkflow("test", steps));
  }
}
