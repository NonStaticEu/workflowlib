package eu.nonstatic.workflow;

import java.io.Serializable;
import java.util.Objects;

public class StandardWorkflow<S> extends AbstractWorkflow<S, StandardWorkflowNode<S>> {

  protected final Serializable id;
  protected final WorkflowMetas metas;

  protected StandardWorkflow(Serializable id, WorkflowMetas metas, WorkflowStep<S> start) {
    super(start);
    this.id = Objects.requireNonNull(id);
    this.metas = metas;
  }

  public Serializable getId() {
    return id;
  }

  public WorkflowMetas getMetas() {
    return metas;
  }

  @Override
  public Object getKey() {
    return getId();
  }

  @Override
  protected StandardWorkflowNode<S> newNode(S state) {
    return new StandardWorkflowNode<>(state);
  }

  public static <T> Builder<T> builder() {
    return new Builder<>();
  }

  @Override
  public boolean equals(Object o) {
    return super.equals(o)
        && Objects.equals(id, ((StandardWorkflow<?>)o).id)
        && Objects.equals(metas, ((StandardWorkflow<?>)o).metas);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), id, metas);
  }


  public static final class Builder<S> {
    private Serializable id;
    private WorkflowMetas metas;
    private WorkflowStep<S> start;

    public Builder<S> id(Serializable id) {
      this.id = id;
      return this;
    }

    public Builder<S> metas(WorkflowMetas metas) {
      this.metas = metas;
      return this;
    }

    public Builder<S> start(WorkflowStep<S> start) {
      this.start = start;
      return this;
    }

    public StandardWorkflow<S> build() {
      return new StandardWorkflow<>(id, metas, start);
    }
  }
}
