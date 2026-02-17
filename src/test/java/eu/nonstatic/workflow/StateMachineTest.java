package eu.nonstatic.workflow;

import static eu.nonstatic.workflow.WorkflowStep.builder;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import eu.nonstatic.workflow.StateMachine.Builder;
import java.io.Serializable;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StateMachineTest {

  TestListener listener1 = new TestListener();
  TestListener listener2 = new TestListener();
  TestListener listener3 = new TestListener();
  TestListener listener5 = new TestListener();
  TestListener listener6 = new TestListener();
  TestListener listener7 = new TestListener() {
    @Override
    public void invoke(String from, String to, TransitionContext context) {
      throw new RuntimeException("listener6 failed");
    }
  };

  TestWorkflow workflow = new TestWorkflow("test",
      builder("state1")
        .next(builder("state2")
            .next(builder("state6").listener(listener6)
                .next(builder("state7").listener(listener7)
                    .nextff("state8")
                    .build())
                .build())
            .build())
        .next(builder("state3").listener(listener3)
          .nextff("state4")
          .nextff("state1", listener1)
          .nextff("state2", listener2)
          .nextff("state5", listener5)
          .build()
        ).build());

  @BeforeEach
  void beforeEach() {
    listener1.reset();
    listener2.reset();
    listener3.reset();
    listener5.reset();
    listener6.reset();
    listener7.reset();
  }

  @Test
  void should_not_create_machine() {
    Builder<String, StandardWorkflowNode<String>, Object> builder1 = StateMachine.builder(null, workflow).state("state1");
    assertThrows(IllegalArgumentException.class, builder1::build);

    UUID id = UUID.randomUUID();
    Builder<Object, ?, Object> builder2 = StateMachine.builder(id, null).state("state1");
    assertThrows(IllegalArgumentException.class, builder2::build);

    Builder<String, StandardWorkflowNode<String>, Object> builder3 = StateMachine.builder(id, workflow).state("state999");
    assertThrows(NoSuchElementException.class, builder3::build);
  }

  @Test
  void should_create_machine() {
    String id = UUID.randomUUID().toString();
    var machine = workflow.toMachine(id, "state3");
    assertEquals(id, machine.getId());
    assertEquals(workflow, machine.getWorkflow());
    assertEquals("state3", machine.getState());
  }

  @Test
  void should_generate_id() {
    var machine = workflow.toMachine("state1");
    Serializable id = machine.getId();
    assertNotNull(id);
    assertDoesNotThrow(() -> UUID.fromString((String)id));
  }

  @Test
  void should_transition() {
    var machine = workflow.toMachine("state3");

    var transition = machine.transition("state1", null);
    assertEquals(1, transition.getPath().size());
    assertEquals(1, transition.getResults().size());

    var result = transition.getResults().get(0);
    assertNull(result.getException());
    assertTrue(result.isSuccessful());

    assertTrue(listener3.isEmpty());
    assertEquals(1, listener1.size());
    assertEquals("state3", listener1.get(0).from);
    assertEquals("state1", listener1.get(0).to);
    assertTrue(listener2.isEmpty());

    TransitionContext context = new TransitionContext();
    machine.transition("state3", context);
    assertEquals(1, listener3.size());
    assertEquals(1, (int)context.get("calls"));
    assertEquals("state1", listener3.get(0).from);
    assertEquals("state3", listener3.get(0).to);
    assertEquals(1, listener1.size());
    assertTrue(listener2.isEmpty());

    machine.transition("state1", null);
    assertEquals(1, listener3.size());
    assertEquals(2, listener1.size());
    assertEquals("state3", listener1.get(1).from);
    assertEquals("state1", listener1.get(1).to);
    assertTrue(listener2.isEmpty());
  }

  @Test
  void should_transition_path() {
    var machine = workflow.toMachine("state1");

    var transition = machine.transition("state5", null);
    assertEquals(2, transition.getPath().size());
    assertEquals(2, transition.getResults().size());

    var result = transition.getResults().get(0);
    assertNull(result.getException());
    assertTrue(result.isSuccessful());

    assertTrue(listener1.isEmpty());
    assertEquals(1, listener3.size());
    assertEquals("state1", listener3.get(0).from);
    assertEquals("state3", listener3.get(0).to);
    assertTrue(listener1.isEmpty());
    assertTrue(listener2.isEmpty());
    assertEquals(1, listener5.size());
    assertEquals("state3", listener5.get(0).from);
    assertEquals("state5", listener5.get(0).to);
  }

  @Test
  void should_not_trigger_on_empty_path() {
    var machine = workflow.toMachine("state1");

    var transition2 = machine.transition("state1", null);
    assertTrue(transition2.isEmpty());
    assertTrue(listener1.isEmpty());
    assertTrue(listener3.isEmpty());
    assertTrue(listener1.isEmpty());
    assertTrue(listener2.isEmpty());
    assertTrue(listener5.isEmpty());
  }


  @Test
  void should_call_listener_on_right_link() {
    var machine = workflow.toMachine("state1");

    var transition = machine.transition("state2", null);
    assertEquals(1, transition.getPath().size());
    assertTrue(transition.getResults().get(0).isSuccessful());
    assertTrue(listener2.isEmpty());
  }

  @Test
  void should_fail_on_unset_state() {
    var machine = assertDoesNotThrow(() -> StateMachine.builder(UUID.randomUUID(), workflow).build());
    assertThrows(IllegalStateException.class, () -> machine.transition("state2", null));
  }


  @Test
  void should_fail_on_unchained_state() {
    var machine = workflow.toMachine("state2");
    assertThrows(IllegalArgumentException.class, () -> machine.transition("state4", null));
  }

  @Test
  void should_fail_on_unknown_state() {
    assertThrows(NoSuchElementException.class, () -> workflow.toMachine("state69"));

    var machine = workflow.toMachine("state2");
    assertThrows(NoSuchElementException.class, () -> machine.transition("state42", null));

    assertThrows(NoSuchElementException.class, () -> machine.setState("state99"));
  }

  @Test
  void should_fail_directly() {
    var machine = workflow.toMachine("state1");
    // path is state1 -> state2 -> state6 -> state7 (failed)
    var ex = assertThrows(StateMachineException.class, () -> machine.transition("state8", null));
    assertEquals(machine.getId(), ex.getMachineId());
    assertEquals(4, ex.getReport().getPath().size());
    assertEquals(3, ex.getReport().getResults().size());

    List<WorkflowLink<?>> successfulTransitions = ex.getSuccessfulTransitions();
    assertEquals(2, successfulTransitions.size());
    WorkflowLink<?> tr0 = successfulTransitions.get(0);
    assertEquals("state1", tr0.getFrom());
    assertEquals("state2", tr0.getTo());
    WorkflowLink<?> tr1 = successfulTransitions.get(1);
    assertEquals("state2", tr1.getFrom());
    assertEquals("state6", tr1.getTo());
    assertEquals(1, listener6.size());

    WorkflowLink<?> failedTransition = ex.getFailedTransition();
    assertEquals("state6", failedTransition.getFrom());
    assertEquals("state7", failedTransition.getTo());
    assertTrue(listener7.isEmpty());
  }

  @Test
  void should_fail_lenient() {
    var machine = workflow.toMachine("state1");
    // path is state1 -> state2 -> state6 -> state7 (failed) -> state8
    var report = assertDoesNotThrow(() -> machine.transition("state8", null, true));
    assertEquals(4, report.getPath().size());
    assertEquals(4, report.getResults().size());

    List<TransitionResult<String>> results = report.getResults();
    assertEquals(4, results.size());

    TransitionResult<String> tr0 = results.get(0);
    assertEquals("state1", tr0.getFrom());
    assertEquals("state2", tr0.getTo());

    TransitionResult<String> tr1 = results.get(1);
    assertEquals("state2", tr1.getFrom());
    assertEquals("state6", tr1.getTo());
    assertEquals(1, listener6.size());

    TransitionResult<String> tr2 = results.get(2);
    assertEquals("state6", tr2.getFrom());
    assertEquals("state7", tr2.getTo());
    assertTrue(listener7.isEmpty());

    TransitionResult<String> tr3 = results.get(3);
    assertEquals("state7", tr3.getFrom());
    assertEquals("state8", tr3.getTo());
  }

  enum Event {
    E1, E2, E3, E4
  }

  @Test
  void should_return_empty_report_when_event_not_in_transitions() {
    var machine = StateMachine.builder(UUID.randomUUID(), workflow)
        .add(new StateMachineTransition<>("state1", "state2", Event.E1))
        .add(new StateMachineTransition<>("state2", "state6", Event.E2))
        .add(new StateMachineTransition<>("state3", "state4", Event.E3))
        .add(new StateMachineTransition<>("state3", "state5", Event.E1))
        .state("state1")
        .build();

    var report = machine.send(Event.E4);
    assertTrue(report.isEmpty());
  }

  @Test
  void should_return_empty_report_when_current_state_not_in_event_transitions() {
    var machine = StateMachine.builder(UUID.randomUUID(), workflow)
        .add(new StateMachineTransition<>("state1", "state2", Event.E1))
        .add(new StateMachineTransition<>("state2", "state6", Event.E2))
        .add(new StateMachineTransition<>("state3", "state4", Event.E3))
        .add(new StateMachineTransition<>("state3", "state5", Event.E1))
        .state("state6")
        .build();

    var report = machine.send(Event.E1);
    assertTrue(report.isEmpty());
  }

  @Test
  void should_transition_one_step() {
    TestListener listener = new TestListener();
    var machine = StateMachine.builder(UUID.randomUUID(), workflow)
        .add(new StateMachineTransition<>("state1", "state2", Event.E1))
        .add(new StateMachineTransition<>("state2", "state6", Event.E2))
        .add(new StateMachineTransition<>("state3", "state4", Event.E3, listener))
        .add(new StateMachineTransition<>("state3", "state5", Event.E1))
        .state("state3")
        .build();

    var report = machine.send(Event.E3);
    assertEquals(1, report.getPath().size());
    assertEquals("state3", report.getResults().get(0).getFrom());
    assertEquals("state4", report.getResults().get(0).getTo());
    assertEquals("state4", machine.getState());

    assertEquals(1, listener.size());
    assertEquals("state3", listener.get(0).from);
    assertEquals("state4", listener.get(0).to);
  }

  @Test
  void should_not_transition_conditionally() {
    TestListener listener = new TestListener();
    var machine = StateMachine.builder(UUID.randomUUID(), workflow)
        .add(new StateMachineTransition<>("state1", "state2", Event.E1))
        .add(new StateMachineTransition<>("state2", "state6", Event.E2, c -> false, listener))
        .add(new StateMachineTransition<>("state3", "state4", Event.E3))
        .add(new StateMachineTransition<>("state3", "state5", Event.E1))
        .state("state2")
        .build();

    var report = machine.send(Event.E2);
    assertTrue(report.getPath().isEmpty());
    assertTrue(report.getResults().isEmpty());

    assertEquals(0, listener.size());
    assertEquals(0, listener6.size());
  }

  @Test
  void should_transition_multiple_step() {
    TestListener listener13 = new TestListener();
    TestListener listener14 = new TestListener();
    var machine = StateMachine.<String, StandardWorkflowNode<String>, Event>builder(UUID.randomUUID(), workflow)
        .add(StateMachineTransition.<String, Event>builder().from("state1").to("state2").event(Event.E1).build())
        .add(StateMachineTransition.<String, Event>builder().from("state1").to("state3").event(Event.E2).condition(c -> true).listener(listener13).build())
        .add(StateMachineTransition.<String, Event>builder().from("state1").to("state4").event(Event.E4).listener(listener14).build())
        .add(StateMachineTransition.<String, Event>builder().from("state3").to("state4").event(Event.E3).build())
        .add(StateMachineTransition.<String, Event>builder().from("state3").to("state5").event(Event.E1).build())
        .state("state1")
        .build();

    var report = machine.send(Event.E4);
    assertEquals(2, report.getPath().size());
    assertEquals("state1", report.getResults().get(0).getFrom());
    assertEquals("state3", report.getResults().get(0).getTo());
    assertEquals("state3", report.getResults().get(1).getFrom());
    assertEquals("state4", report.getResults().get(1).getTo());
    assertEquals("state4", machine.getState());

    assertTrue(listener13.isEmpty());

    assertEquals(1, listener14.size());
    assertEquals("state1", listener14.get(0).from); // NOT state3, the listener applies to the whole span of the transition
    assertEquals("state4", listener14.get(0).to);


    assertEquals(1, listener3.size());
    assertEquals("state1", listener3.get(0).from);
    assertEquals("state3", listener3.get(0).to);
  }

  @Test
  void should_fail_when_duplicate_event_and_from_state() {
    Builder<String, StandardWorkflowNode<String>, Object> builder = StateMachine.builder(UUID.randomUUID(), workflow)
        .add(new StateMachineTransition<>("state1", "state2", Event.E1))
        .add(new StateMachineTransition<>("state3", "state4", Event.E3))
        .add(new StateMachineTransition<>("state1", "state3", Event.E1));

    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, builder::build);

    assertEquals("Duplicate transition from state1 for event E1", ex.getMessage());
  }

  @Test
  void should_fail_when_transition_state_not_in_workflow() {
    UUID id = UUID.randomUUID();
    Builder<String, StandardWorkflowNode<String>, Object> builder = StateMachine.builder(id, workflow)
        .add(new StateMachineTransition<>("state1", "state999", Event.E1));

    NoSuchElementException ex = assertThrows(NoSuchElementException.class, builder::build);

    assertEquals("State doesn't belong to workflow TestWorkflow : Unknown to state: state999; id: " + id, ex.getMessage());
  }

  @Test
  void should_fail_when_impossible_transition() {
    Builder<String, StandardWorkflowNode<String>, Object> builder = StateMachine.builder(UUID.randomUUID(), workflow)
        .add(new StateMachineTransition<>("state1", "state2", Event.E1))
        .add(new StateMachineTransition<>("state6", "state3", Event.E3))
        .add(new StateMachineTransition<>("state1", "state3", Event.E1));

    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, builder::build);

    assertEquals("Impossible path between: state6 and: state3", ex.getMessage());
  }
}
