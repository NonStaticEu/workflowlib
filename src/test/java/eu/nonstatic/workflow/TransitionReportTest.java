package eu.nonstatic.workflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import eu.nonstatic.workflow.TransitionReport.Outcome;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class TransitionReportTest {
  @Test
  void should_throw_when_results_longer_than_path() {
    WorkflowLink<String> link1 = new WorkflowLink<>("state1", "state2", null);
    WorkflowLink<String> link2 = new WorkflowLink<>("state2", "state3", null);
    WorkflowLink<String> link3 = new WorkflowLink<>("state3", "state4", null);

    WorkflowPath<String> path = createPath(link1, link2);
    List<TransitionResult<String>> results = List.of(
        new TransitionResult<>(link1),
        new TransitionResult<>(link2),
        new TransitionResult<>(link3)
    );

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      new TransitionReport<>(path, results, "whatever");
    });
    assertEquals("path is shorter than results: [state2, state3] vs [[state1, state2, true], [state2, state3, true], [state3, state4, true]]", exception.getMessage());
  }



  @Test
  void should_throw_when_path_and_results_are_unrelated() {
    WorkflowLink<String> link1 = new WorkflowLink<>("state1", "state2", null);
    WorkflowLink<String> link2 = new WorkflowLink<>("state2", "state3", null);
    WorkflowLink<String> link3 = new WorkflowLink<>("state3", "state4", null);

    WorkflowLink<String> differentLink = new WorkflowLink<>("state1", "state999", null);

    WorkflowPath<String> path = createPath(link1, link2, link3);
    List<TransitionResult<String>> results = List.of(
        new TransitionResult<>(link1),
        new TransitionResult<>(differentLink),
        new TransitionResult<>(link3)
    );

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new TransitionReport<>(path, results, "whatever"));
    assertEquals("path and results are unrelated: [state2, state3, state4] vs [[state1, state2, true], [state1, state999, true], [state3, state4, true]]", exception.getMessage());
  }

  @Test
  void should_throw_when_results_shorter_than_path_with_no_failed_element() {
    WorkflowLink<String> link1 = new WorkflowLink<>("state1", "state2", null);
    WorkflowLink<String> link2 = new WorkflowLink<>("state2", "state3", null);
    WorkflowLink<String> link3 = new WorkflowLink<>("state3", "state4", null);

    WorkflowPath<String> path = createPath(link1, link2, link3);
    List<TransitionResult<String>> results = List.of(
        new TransitionResult<>(link1),
        new TransitionResult<>(link2)
    );

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new TransitionReport<>(path, results, "whatever"));
    assertEquals("results is shorter than path but has no failed element: [state2, state3, state4] vs [[state1, state2, true], [state2, state3, true]]", exception.getMessage());
  }

  @Test
  void should_throw_when_results_shorter_than_path_with_multiple_failed_elements() {
    WorkflowLink<String> link1 = new WorkflowLink<>("state1", "state2", null);
    WorkflowLink<String> link2 = new WorkflowLink<>("state2", "state3", null);
    WorkflowLink<String> link3 = new WorkflowLink<>("state3", "state4", null);
    WorkflowLink<String> link4 = new WorkflowLink<>("state4", "state5", null);

    WorkflowPath<String> path = createPath(link1, link2, link3, link4);
    List<TransitionResult<String>> results = List.of(
        new TransitionResult<>(link1, new RuntimeException("Error 1")),
        new TransitionResult<>(link2),
        new TransitionResult<>(link3, new RuntimeException("Error 2"))
    );

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      new TransitionReport<>(path, results, "whatever");
    });
    assertEquals("results is shorter than path but has multiple failed elements: [state2, state3, state4, state5] vs [[state1, state2, false], [state2, state3, true], [state3, state4, false]]", exception.getMessage());
  }

  @Test
  void should_throw_when_results_shorter_and_failed_element_is_not_last() {
    WorkflowLink<String> link1 = new WorkflowLink<>("state1", "state2", null);
    WorkflowLink<String> link2 = new WorkflowLink<>("state2", "state3", null);
    WorkflowLink<String> link3 = new WorkflowLink<>("state3", "state4", null);
    WorkflowLink<String> link4 = new WorkflowLink<>("state4", "state5", null);

    WorkflowPath<String> path = createPath(link1, link2, link3, link4);
    List<TransitionResult<String>> results = List.of(
        new TransitionResult<>(link1),
        new TransitionResult<>(link2, new RuntimeException("Failed")),
        new TransitionResult<>(link3)
    );

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      new TransitionReport<>(path, results, "whatever");
    });
    assertEquals("results is shorter than path but the last element isn't failed: [state3, state4, true]", exception.getMessage());
  }

  @Test
  void should_throw_when_empty_path_and_final_state_mismatch() {
    WorkflowPath<String> path = WorkflowPath.empty("state1");
    List<TransitionResult<String>> results = List.of();

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new TransitionReport<>(path, results, "state2"));
    assertEquals("When the path is empty, the final state must match the initial state: state2 != state1", exception.getMessage());
  }

  @Test
  void should_throw_when_complete_path_and_final_state_inconsistent() {
    WorkflowLink<String> link1 = new WorkflowLink<>("state1", "state2", null);
    WorkflowLink<String> link2 = new WorkflowLink<>("state2", "state3", null);

    WorkflowPath<String> path = createPath(link1, link2);
    List<TransitionResult<String>> results = List.of(
        new TransitionResult<>(link1),
        new TransitionResult<>(link2)
    );

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new TransitionReport<>(path, results, "state1"));
    assertEquals("When the path is completely covered, the final state must match the ultimate or penultimate result state: state1 != " + link2, exception.getMessage());
  }

  @Test
  void should_throw_when_partial_path_and_final_state_mismatch() {
    WorkflowLink<String> link1 = new WorkflowLink<>("state1", "state2", null);
    WorkflowLink<String> link2 = new WorkflowLink<>("state2", "state3", null);
    WorkflowLink<String> link3 = new WorkflowLink<>("state3", "state4", null);

    WorkflowPath<String> path = createPath(link1, link2, link3);
    List<TransitionResult<String>> results = List.of(
        new TransitionResult<>(link1),
        new TransitionResult<>(link2, new RuntimeException("Failed"))
    );

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new TransitionReport<>(path, results, "state3"));
    assertEquals("When the path isn't completely covered, the final state must match the last successful state: state3 != state2", exception.getMessage());
  }

  @Test
  void should_throw_when_outcome_is_not_failed_but_has_failed_results() {
    WorkflowLink<String> link1 = new WorkflowLink<>("state1", "state2", null);
    WorkflowLink<String> link2 = new WorkflowLink<>("state2", "state3", null);

    WorkflowPath<String> path = createPath(link1, link2);
    List<TransitionResult<String>> results = List.of(
        new TransitionResult<>(link1),
        new TransitionResult<>(link2, new RuntimeException("Failed"))
    );

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new TransitionReport<>(Outcome.SUCCESSFUL, path, results, "state3"));
    assertEquals("Outcome must be FAILED if there are failed results: SUCCESSFUL vs [[state1, state2, true], [state2, state3, false]]", exception.getMessage());
  }

  @Test
  void should_throw_when_outcome_is_not_executed_but_path_is_not_empty() {
    WorkflowLink<String> link1 = new WorkflowLink<>("state1", "state2", null);
    WorkflowLink<String> link2 = new WorkflowLink<>("state2", "state3", null);

    WorkflowPath<String> path = createPath(link1, link2);
    List<TransitionResult<String>> results = List.of(
        new TransitionResult<>(link1),
        new TransitionResult<>(link2)
    );

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new TransitionReport<>(Outcome.GUARDED, path, results, "state3"));
    assertEquals("Outcome must reflect transition execution when results is not empty: GUARDED vs [[state1, state2, true], [state2, state3, true]]", exception.getMessage());
    assertThrows(IllegalArgumentException.class, () -> new TransitionReport<>(Outcome.NO_TRANSITION, path, results, "state3"));
  }

  @Test
  void should_create_report_with_empty_path_and_results() {
    WorkflowPath<String> path = WorkflowPath.empty("start");
    List<TransitionResult<String>> results = List.of();

    TransitionReport<String> report = new TransitionReport<>(path, results, "start");
    assertTrue(report.isEmpty());
    assertEquals("start", report.getInitialState());
    assertEquals("start", report.getFinalState());
    assertEquals("start", report.getTargetState());
    assertTrue(report.isExecuted());
    assertTrue(report.isSuccessful());
    assertFalse(report.isFailed());
    assertFalse(report.isTransitioned());
    assertTrue(report.isSelfPath());
    assertFalse(report.isPartialPath());
    assertTrue(report.isCompletePath());
  }

  @Test
  void should_create_report_when_path_and_results_size_match() {
    WorkflowLink<String> link1 = new WorkflowLink<>("state1", "state2", null);
    WorkflowLink<String> link2 = new WorkflowLink<>("state2", "state3", null);
    WorkflowLink<String> link3 = new WorkflowLink<>("state3", "state4", null);

    WorkflowPath<String> path = createPath(link1, link2, link3);
    List<TransitionResult<String>> results = List.of(
        new TransitionResult<>(link1),
        new TransitionResult<>(link2),
        new TransitionResult<>(link3)
    );

    TransitionReport<String> report = new TransitionReport<>(path, results, "state4");
    assertEquals(3, report.getResults().size());
    assertEquals("state1", report.getInitialState());
    assertEquals("state4", report.getFinalState());
    assertEquals("state4", report.getTargetState());
    assertTrue(report.isExecuted());
    assertTrue(report.isSuccessful());
    assertFalse(report.isFailed());
    assertTrue(report.isTransitioned());
    assertFalse(report.isSelfPath());
    assertFalse(report.isPartialPath());
    assertTrue(report.isCompletePath());
  }

  @Test
  void should_create_report_when_path_and_results_size_match_with_failures() {
    WorkflowLink<String> link1 = new WorkflowLink<>("state1", "state2", null);
    WorkflowLink<String> link2 = new WorkflowLink<>("state2", "state3", null);
    WorkflowLink<String> link3 = new WorkflowLink<>("state3", "state4", null);

    WorkflowPath<String> path = createPath(link1, link2, link3);
    List<TransitionResult<String>> results = List.of(
        new TransitionResult<>(link1, new RuntimeException("Error 1")),
        new TransitionResult<>(link2, new RuntimeException("Error 2")),
        new TransitionResult<>(link3) // implicit leniency
    );

    TransitionReport<String> report = new TransitionReport<>(path, results, "state4");
    assertEquals(3, report.getResults().size());
    assertEquals("state1", report.getInitialState());
    assertEquals("state4", report.getFinalState());
    assertEquals("state4", report.getTargetState());
    assertTrue(report.isExecuted());
    assertFalse(report.isSuccessful());
    assertTrue(report.isFailed());
    assertTrue(report.isTransitioned());
    assertFalse(report.isSelfPath());
    assertFalse(report.isPartialPath());
    assertTrue(report.isCompletePath());
  }

  @Test
  void should_create_report_when_results_shorter_with_last_failed() {
    WorkflowLink<String> link1 = new WorkflowLink<>("state1", "state2", null);
    WorkflowLink<String> link2 = new WorkflowLink<>("state2", "state3", null);
    WorkflowLink<String> link3 = new WorkflowLink<>("state3", "state4", null);

    WorkflowPath<String> path = createPath(link1, link2, link3);
    List<TransitionResult<String>> results = List.of(
        new TransitionResult<>(link1),
        new TransitionResult<>(link2, new RuntimeException("Failed at state3"))
    );

    TransitionReport<String> report = new TransitionReport<>(path, results, "state2");
    assertEquals(2, report.getResults().size());
    assertEquals("state1", report.getInitialState());
    assertEquals("state2", report.getFinalState());
    assertEquals("state4", report.getTargetState());
    assertTrue(report.isExecuted());
    assertFalse(report.isSuccessful());
    assertTrue(report.isFailed());
    assertTrue(report.isTransitioned());
    assertFalse(report.isSelfPath());
    assertTrue(report.isPartialPath());
    assertFalse(report.isCompletePath());
  }

  @Test
  void should_iterate_on_report() {
    WorkflowLink<String> link1 = new WorkflowLink<>("state1", "state2", null);
    WorkflowLink<String> link2 = new WorkflowLink<>("state2", "state3", null);
    WorkflowLink<String> link3 = new WorkflowLink<>("state3", "state4", null);

    WorkflowPath<String> path = createPath(link1, link2, link3);
    TransitionResult<String> tr1 = new TransitionResult<>(link1);
    TransitionResult<String> tr2 = new TransitionResult<>(link2);
    TransitionResult<String> tr3 = new TransitionResult<>(link3);

    var results = List.of(tr1, tr2, tr3);
    var report = new TransitionReport<>(path, results, "state4");

    assertEquals(results, report.stream().collect(Collectors.toList()));
    var it = report.iterator();
    assertEquals(tr1, it.next());
    assertEquals(tr2, it.next());
    assertEquals(tr3, it.next());
    assertFalse(it.hasNext());
  }

  @SafeVarargs
  private WorkflowPath<String> createPath(WorkflowLink<String>... links) {
    return new WorkflowPath<>(links[0].getFrom(), List.of(links));
  }
}
