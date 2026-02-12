package eu.nonstatic.workflow;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class StandardWorkflow extends AbstractWorkflow<Serializable, StandardWorkflowNode> {

  private final Serializable id;
  private final Map<String, Object> metas;

  protected StandardWorkflow(Serializable id, Map<String, Object> metas, WorkflowStep<Serializable> start) {
    super(start);
    this.id = id;
    this.metas = new LinkedHashMap<>(metas);
  }

  @Override
  protected StandardWorkflowNode newNode(Serializable state) {
    return new StandardWorkflowNode(state);
  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public boolean equals(Object o) {
    return super.equals(o)
        && Objects.equals(id, ((StandardWorkflow)o).id)
        && Objects.equals(metas, ((StandardWorkflow)o).metas);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), id, metas);
  }


  public static final class Builder {
    private Serializable id;
    private Map<String, Object> metas;
    private WorkflowStep<Serializable> start;

    public Builder id(Serializable id) {
      this.id = id;
      return this;
    }

    public Builder metas(Map<String, Object> metas) {
      this.metas = metas;
      return this;
    }

    public Builder start(WorkflowStep<Serializable> start) {
      this.start = start;
      return this;
    }

    public StandardWorkflow build() {
      return new StandardWorkflow(id, metas, start);
    }
  }
}
