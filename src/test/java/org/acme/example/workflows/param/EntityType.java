package org.acme.example.workflows.param;

import eu.nonstatic.workflow.Workflow;
import java.util.List;
import java.util.Optional;
import org.acme.example.workflows.model.AbstractLineItemWorkflow;
import org.acme.example.workflows.model.AbstractOrderWorkflow;
import org.acme.example.workflows.model.AbstractParcelWorkflow;

public final class EntityType {

  public static final String ORDER = "order";
  public static final String ITEM = "item";
  public static final String PARCEL = "parcel";

  private static final List<String> ALL = List.of(ORDER, ITEM, PARCEL);

  private EntityType() {}

  public static List<String> getAll() {
    return ALL;
  }



  public static final class Safe<W extends Workflow> {
    public static final Safe<AbstractOrderWorkflow> ORDER = new Safe<>(EntityType.ORDER, AbstractOrderWorkflow.class);
    public static final Safe<AbstractLineItemWorkflow> ITEM = new Safe<>(EntityType.ITEM, AbstractLineItemWorkflow.class);
    public static final Safe<AbstractParcelWorkflow> PARCEL = new Safe<>(EntityType.PARCEL, AbstractParcelWorkflow.class);

    private static final List<Safe> ALL = List.of(ORDER, ITEM, PARCEL);

    private final String name;
    private final Class<? extends W> workflowType;

    private Safe(String name, Class<? extends W> workflowType) {
      this.name = name;
      this.workflowType = workflowType;
    }

    public static Optional<Safe> get(String name) {
      return ALL.stream().filter(type -> type.name.equals(name)).findFirst();
    }

    public static List<Safe> getAll() { return ALL; }

    public String name() { return name; }

    public Class<? extends W> getWorkflowType() { return workflowType; }

    @Override
    public String toString() { return name(); }
  }
}
