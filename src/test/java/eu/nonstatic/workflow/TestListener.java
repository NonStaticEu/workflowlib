package eu.nonstatic.workflow;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

class TestListener implements WorkflowListener<String>, Iterable<TestListener.FromTo> {

    List<FromTo> calls = new LinkedList<>();

    @Override
    public void invoke(String from, String to, TransitionContext context) {
      calls.add(new FromTo(from, to));
      if(context != null) {
        context.put("calls", calls.size());
      }
    }

  public int size() {
    return calls.size();
  }

  public boolean isEmpty() {
    return calls.isEmpty();
  }

  void reset() { calls.clear(); }

  @Override
  public Iterator<FromTo> iterator() {
    return calls.iterator();
  }

  public FromTo get(int i) {
    return calls.get(i);
  }


  static class FromTo {
    String from;
    String to;

    public FromTo(String from, String to) {
      this.from = from;
      this.to = to;
    }

    @Override
    public String toString() {
      return from + '-' + to;
    }
  }
}

