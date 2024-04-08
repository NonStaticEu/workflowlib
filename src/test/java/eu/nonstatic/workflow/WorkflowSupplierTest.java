package eu.nonstatic.workflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.acme.example.workflows.WorkflowsSupplier;
import org.acme.example.workflows.impl.ItemEcomClickAndCollectWorkflow;
import org.acme.example.workflows.impl.OrderEcomStoreDeliveryWorkflow;
import org.acme.example.workflows.model.AbstractLineItemWorkflow;
import org.acme.example.workflows.model.AbstractOrderWorkflow;
import org.acme.example.workflows.param.DeliveryMode;
import org.acme.example.workflows.param.EntityType;
import org.acme.example.workflows.param.SalesChannel;
import org.junit.jupiter.api.Test;

class WorkflowSupplierTest {

  static WorkflowsSupplier supplier = new WorkflowsSupplier();

  @Test
  void should_build_supplier() {
    TestWorkflow[] testWorkflows = { new TestWorkflow("fooWF", new WorkflowStep<>("foo")), new TestWorkflow("barWF", new WorkflowStep<>("bar")) };
    NamedWorkflowSupplier<TestWorkflow> sup = new NamedWorkflowSupplier<>(testWorkflows);
    assertEquals(2, sup.getSize());
    assertEquals(2, sup.stream().count());

    List<TestWorkflow> testWorkflowsList = Stream.concat(
        sup.stream(),
        Stream.of(new TestWorkflow("bazWF", new WorkflowStep<>("baz")))
    ).collect(Collectors.toList());
    sup = new NamedWorkflowSupplier<>(testWorkflowsList);
    assertEquals(3, sup.getSize());
    assertNotNull(sup.get("bazWF"));
  }

  @Test
  void should_get_from_delivery_mode() {
    var workflow = supplier.get(EntityType.ORDER, DeliveryMode.ECOM_ORDER_STORE_DELIVERY).get();
    assertEquals(new OrderEcomStoreDeliveryWorkflow(), workflow);
  }

  @Test
  void should_get_from_params() {
    var workflow = supplier.get(EntityType.ITEM, SalesChannel.ECOM, true, true).get();
    assertEquals(new ItemEcomClickAndCollectWorkflow(), workflow);
  }

  @Test
  void should_get_from_delivery_mode_safe() {
    AbstractOrderWorkflow workflow = supplier.get(EntityType.Safe.ORDER, DeliveryMode.ECOM_ORDER_STORE_DELIVERY).get();
    assertEquals(new OrderEcomStoreDeliveryWorkflow(), workflow);
  }

  @Test
  void should_get_from_params_safe() {
    AbstractLineItemWorkflow workflow = supplier.get(EntityType.Safe.ITEM, SalesChannel.ECOM, true, true).get();
    assertEquals(new ItemEcomClickAndCollectWorkflow(), workflow);
  }

  @Test
  void should_filter_by_entity_type() {
    long count = supplier.filter(EntityType.ORDER).count();
    assertEquals(4, count);
  }

  @Test
  void should_filter_by_entity_type_and_sales_channel() {
    long count = supplier.filter(EntityType.ORDER, SalesChannel.ECOM).count();
    assertEquals(3, count);
  }

  @Test
  void should_be_immutable() {
    Iterator<?> it = supplier.stream().iterator();
    assertThrows(UnsupportedOperationException.class, it::remove);
  }

  @Test
  void should_toString() {
    EntityType.getAll()
        .stream()
        .flatMap(supplier::filter)
        .forEach(workflow -> {
          assertEquals(workflow.getClass().getSimpleName(), workflow.toString());
        });
  }

  @Test
  void should_equals_hashcode() {
    List<Workflow> wfs = supplier.stream().collect(Collectors.toList());

    for (int i = 0; i < wfs.size(); i++) {
      Workflow wfi = wfs.get(i);
      for (int j = 0; j < wfs.size(); j++) {
        Workflow wfj = wfs.get(j);
        assertEquals(i == j, wfi.hashCode() == wfj.hashCode());
        assertEquals(i == j, wfi.equals(wfj));
        assertEquals(i == j, wfj.equals(wfi));
      }
    }
  }
}
