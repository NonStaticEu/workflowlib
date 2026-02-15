package eu.nonstatic.workflow;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Context {

  private final Map<String, Object> delegate = new HashMap<>();

  public <T> T get(Object key) {
    return (T)delegate.get(key);
  }

  public <T> T put(String key, Object value) {
    return (T)delegate.put(Objects.requireNonNull(key), value);
  }

  public <T> T remove(Object key) {
    return (T)delegate.remove(key);
  }

  public int size() {
    return delegate.size();
  }

  public boolean isEmpty() {
    return delegate.isEmpty();
  }

  public void clear() {
    delegate.clear();
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Context context = (Context) o;
    return Objects.equals(delegate, context.delegate);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(delegate);
  }
}
