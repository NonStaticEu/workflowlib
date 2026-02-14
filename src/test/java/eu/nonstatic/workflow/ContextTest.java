package eu.nonstatic.workflow;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContextTest {

  @Test
  void should_put_and_get_values() {
    Context context = new Context();

    Object complexObject = new Object() {
      private final String field = "test";
      public String getField() { return field; }
    };

    context.put("string", "text");
    context.put("integer", 42);
    context.put("boolean", true);
    context.put("complex", complexObject);

    String stringValue = context.get("string");
    int intValue = context.get("integer");
    boolean boolValue = context.get("boolean");
    Object complexValue = context.get("complex");

    assertEquals("text", stringValue);
    assertEquals(42, intValue);
    assertTrue(boolValue);
    assertSame(complexObject, complexValue);
  }

  @Test
  void should_return_null_for_nonexistent_key() {
    Context context = new Context();

    Object result = context.get("nonexistent");
    assertNull(result);
  }

  @Test
  void should_throw_when_key_is_null() {
    Context context = new Context();

    assertThrows(NullPointerException.class, () -> context.put(null, "value"));
  }

  @Test
  void should_allow_null_value() {
    Context context = new Context();
    context.put("key", null);

    Object result = context.get("key");
    assertNull(result);
  }

  @Test
  void should_return_previous_value_when_replacing() {
    Context context = new Context();
    String previous0 = context.put("key", "oldValue");
    assertNull(previous0);

    String previous1 = context.put("key", "newValue");
    assertEquals("oldValue", previous1);
    assertEquals("newValue", context.get("key"));
  }

  @Test
  void should_remove_value() {
    Context context = new Context();
    context.put("key", "value");

    String removed = context.remove("key");

    assertEquals("value", removed);
    assertNull(context.get("key"));
  }

  @Test
  void should_return_null_when_removing_nonexistent_key() {
    Context context = new Context();

    Object removed = context.remove("nonexistent");

    assertNull(removed);
  }

  @Test
  void should_return_correct_size() {
    Context context = new Context();

    assertEquals(0, context.size());

    context.put("key1", "value1");
    assertEquals(1, context.size());

    context.put("key2", "value2");
    assertEquals(2, context.size());

    context.put("key1", "updatedValue");
    assertEquals(2, context.size());
  }

  @Test
  void should_report_empty_correctly() {
    Context context = new Context();

    assertTrue(context.isEmpty());

    context.put("key", "value");
    assertFalse(context.isEmpty());

    context.remove("key");
    assertTrue(context.isEmpty());
  }

  @Test
  void should_clear_all_values() {
    Context context = new Context();
    context.put("key1", "value1");
    context.put("key2", "value2");
    context.put("key3", "value3");

    context.clear();

    assertTrue(context.isEmpty());
    assertEquals(0, context.size());
    assertNull(context.get("key1"));
    assertNull(context.get("key2"));
    assertNull(context.get("key3"));
  }

  @Test
  void should_work_with_transition_context() {
    TransitionContext context = new TransitionContext();
    context.put("key", "data");

    String result = context.get("key");
    assertEquals("data", result);
  }

  @Test
  void should_work_with_workflow_metas() {
    WorkflowMetas metas = new WorkflowMetas();
    metas.put("key", "meta");

    String result = metas.get("key");
    assertEquals("meta", result);
  }
}
