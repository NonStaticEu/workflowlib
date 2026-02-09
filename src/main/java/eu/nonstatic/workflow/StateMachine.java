package eu.nonstatic.workflow;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class StateMachine<S, N extends WorkflowNode<S, N>> {

  private final Serializable id;
  private final Workflow<S, N> workflow;
  private final S state;

  public StateMachine(Serializable id, Workflow<S, N> workflow, S state) {
    if(workflow == null) {
      throw new NullPointerException("Workflow must not be null; id: " + id);
    }
    if(state == null) {
      throw new NullPointerException("State must not be null; id: " + id);
    }
    if(!workflow.exists(state)) {
      throw new NoSuchElementException("State " + state + " doesn't belong to workflow " + workflow + "; id: " + id);
    }

    this.id = id;
    this.workflow = workflow;
    this.state = state;
  }

  public Workflow<S, ?> getWorkflow() {
    return workflow;
  }

  public S getState() {
    return state;
  }

  public <C> TransitionReport<S> transition(S newState, C context) throws StateMachineException {
    return transition(newState, context, false);
  }

  public <C> TransitionReport<S> transition(S newState, C context, boolean lenient) throws StateMachineException {
    WorkflowPath<S> path = workflow.path(state, newState)
        .orElseThrow(() -> new NoSuchElementException("Cannot move from " + state + " to " + newState));

    var results = new ArrayList<TransitionResult<S>>(path.size());
    for (WorkflowLink<S> link : path) {
      try {
        link.getListener().invoke(link.getFrom(), link.getTo(), context);
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
