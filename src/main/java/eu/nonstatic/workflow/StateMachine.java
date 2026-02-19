package eu.nonstatic.workflow;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

public class StateMachine<S, N extends WorkflowNode<S, N>, E> {

  private final Serializable id;
  private final Workflow<S, N> workflow;
  private final Map<E, Map<S, StateMachineTransition<S, E>>> transitions; // event -> from -> (to, link)

  private S state;


  private StateMachine(Serializable id, Workflow<S, N> workflow, Collection<StateMachineTransition<S, E>> transitions) {
    if(id == null) {
      throw new IllegalArgumentException("Id must not be null");
    }
    this.id = id;

    if(workflow == null) {
      throw new IllegalArgumentException("Workflow must not be null; id: " + id);
    }
    this.workflow = workflow;

    this.transitions = new HashMap<>();
    for (StateMachineTransition<S, E> transition : transitions) {
      validateTransition(id, workflow, transition);

      Map<S, StateMachineTransition<S, E>> toMap = this.transitions.computeIfAbsent(transition.getEvent(), e -> new HashMap<>());

      if(toMap.containsKey(transition.getFrom())) {
        throw new IllegalArgumentException("Duplicate transition from " + transition.getFrom() + " for event " + transition.getEvent());
      } else {
        toMap.put(transition.getFrom(), transition);
      }
    }
  }

  private static <S, N extends WorkflowNode<S, N>, E> void validateTransition(Serializable id, Workflow<S, N> workflow, StateMachineTransition<S, E> transition) {
    try {
      workflow.path(transition.getFrom(), transition.getTo())
          .orElseThrow(() -> new IllegalArgumentException("Impossible path between: " + transition.getFrom() + " and: " + transition.getTo()));
    } catch (NoSuchElementException e) {
      throw new NoSuchElementException("State doesn't belong to workflow " + workflow + " : " + e.getMessage() + "; id: " + id);
    }
  }


  public Serializable getId() {
    return id;
  }

  public Workflow<S, N> getWorkflow() {
    return workflow;
  }

  public S getState() {
    return state;
  }

  public StateMachine<S, N, E> setState(S state) {
    if(state != null && !workflow.exists(state)) {
      throw new NoSuchElementException("State doesn't belong to workflow " + workflow + " : Unknown state: " + state + "; id: " + id);
    }
    this.state = state;
    return this;
  }

  public WorkflowPath<S> getPathTo(S newState) {
    if(state == null) {
      throw new IllegalStateException("Cannot compute path from unset state");
    }

    return workflow.path(state, newState)
        .orElseThrow(() -> new IllegalArgumentException("Cannot build path from " + state + " to " + newState));
  }


  public TransitionReport<S> send(E event) {
    return send(event, null);
  }

  public TransitionReport<S> send(E event, TransitionContext context) {
    return send(event, context, false);
  }

  public TransitionReport<S> send(E event, TransitionContext context, boolean lenient) {
    var fromMap = this.transitions.get(Objects.requireNonNull(event));
    if(fromMap != null) {
      StateMachineTransition<S, E> transition = fromMap.get(state);
      if(transition != null) {
        if(transition.testGuard(context)) {
          TransitionReport<S> report = transition(transition.getTo(), context, lenient);
          // That listener is only triggered on the last step of the path, and after the workflow's "internal" listeners have been invoked.
          // Should also be invoked on a self-transition
          transition.getLink().fire(context);
          return report;
        } else {
          return TransitionReport.guarded(getPathTo(transition.getTo()));
        }
      }
    }
    return TransitionReport.noTransition(state);
  }

  public TransitionReport<S> transition(S newState, TransitionContext context) throws StateMachineException {
    return transition(newState, context, false);
  }

  public TransitionReport<S> transition(S newState, TransitionContext context, boolean lenient) throws StateMachineException {
    WorkflowPath<S> path = getPathTo(newState);
    var results = new ArrayList<TransitionResult<S>>(path.size());
    for (WorkflowLink<S> link : path) {
      try {
        link.fire(context);
        results.add(new TransitionResult<>(link));
      } catch(Exception e) {
        results.add(new TransitionResult<>(link, e));
        if(!lenient) {
          throw new StateMachineException(id, new TransitionReport<>(path, results, state));
        }
      }
      setState(link.getTo());
    }
    return new TransitionReport<>(path, results, state);
  }

  public static <S, N extends WorkflowNode<S, N>, E> StateMachine.Builder<S, N, E> builder(Serializable id, Workflow<S, N> workflow) {
    return new StateMachine.Builder<>(id, workflow);
  }


  public static class Builder<S, N extends WorkflowNode<S, N>, E> {

    private final Serializable id;
    private final Workflow<S, N> workflow;
    private final List<StateMachineTransition<S, E>> transitions = new LinkedList<>();
    private S state;

    public Builder(Serializable id, Workflow<S, N> workflow) {
      this.id = id;
      this.workflow = workflow;
    }

    public StateMachine.Builder<S, N, E> state(S state) {
      this.state = state;
      return this;
    }

    public StateMachine.Builder<S, N, E> add(StateMachineTransition<S, E> transition) {
      transitions.add(transition);
      return this;
    }

    public StateMachine<S, N, E> build() {
      return new StateMachine<>(id, workflow, transitions).setState(state);
    }
  }
}
