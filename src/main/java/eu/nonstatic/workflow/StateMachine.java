package eu.nonstatic.workflow;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class StateMachine<S, N extends WorkflowNode<S, N>> {

  private final Serializable id;
  private final Workflow<S, N> workflow;
  private S state;

  public StateMachine(Serializable id, Workflow<S, N> workflow, S state) {
    if(id == null) {
      throw new IllegalArgumentException("Id must not be null");
    }
    if(workflow == null) {
      throw new IllegalArgumentException("Workflow must not be null; id: " + id);
    }
    if(state != null && !workflow.exists(state)) {
      throw new NoSuchElementException("State " + state + " doesn't belong to workflow " + workflow + "; id: " + id);
    }

    this.id = id;
    this.workflow = workflow;
    this.state = state;
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

  public void setState(S state) {
    this.state = state;
  }

  public TransitionReport<S> transition(S newState, TransitionContext context) throws StateMachineException {
    return transition(newState, context, false);
  }

  public TransitionReport<S> transition(S newState, TransitionContext context, boolean lenient) throws StateMachineException {
    if(state == null) {
      throw new IllegalStateException("Cannot transition from unset state");
    }

    WorkflowPath<S> path = workflow.path(state, newState)
        .orElseThrow(() -> new IllegalArgumentException("Cannot move from " + state + " to " + newState));

    var results = new ArrayList<TransitionResult<S>>(path.size());
    for (WorkflowLink<S> link : path) {
      setState(link.getTo());
      try {
        link.fire(context);
        results.add(new TransitionResult<>(link));
      } catch(Exception e) {
        results.add(new TransitionResult<>(link, e));
        if(!lenient) {
          throw new StateMachineException(id, new TransitionReport<>(path, results));
        }
      }
    }
    return new TransitionReport<>(path, results);
  }
}
