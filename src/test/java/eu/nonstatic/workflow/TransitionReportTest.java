package eu.nonstatic.workflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
      new TransitionReport<>(path, results);
    });
    assertEquals("results is longer than the path", exception.getMessage());
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

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new TransitionReport<>(path, results));
    assertEquals("path and results are unrelated", exception.getMessage());
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

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new TransitionReport<>(path, results));
    assertEquals("results is shorter than path but has no failed element", exception.getMessage());
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
      new TransitionReport<>(path, results);
    });
    assertEquals("results is shorter than path but has multiple failed elements", exception.getMessage());
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
      new TransitionReport<>(path, results);
    });
    assertEquals("results is shorter than path but the failed element is not the last", exception.getMessage());
  }
  
  @Test
  void should_create_report_with_empty_path_and_results() {
    WorkflowPath<String> path = createPath();
    List<TransitionResult<String>> results = List.of();

    TransitionReport<String> report = new TransitionReport<>(path, results);
    assertTrue(report.isEmpty());
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

    TransitionReport<String> report = new TransitionReport<>(path, results);
    assertEquals(3, report.getResults().size());
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
        new TransitionResult<>(link3)
    );

    TransitionReport<String> report = new TransitionReport<>(path, results);
    assertEquals(3, report.getResults().size());
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

    TransitionReport<String> report = new TransitionReport<>(path, results);
    assertEquals(2, report.getResults().size());
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
    var report = new TransitionReport<>(path, results);

    assertEquals(results, report.stream().collect(Collectors.toList()));
    var it = report.iterator();
    assertEquals(tr1, it.next());
    assertEquals(tr2, it.next());
    assertEquals(tr3, it.next());
    assertFalse(it.hasNext());
  }

  @SafeVarargs
  private WorkflowPath<String> createPath(WorkflowLink<String>... links) {
    return new WorkflowPath<>(List.of(links));
  }
}
