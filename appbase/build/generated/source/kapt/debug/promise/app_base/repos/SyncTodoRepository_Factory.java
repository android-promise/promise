// Generated by Dagger (https://google.github.io/dagger).
package promise.app_base.repos;

import dagger.internal.Factory;

public final class SyncTodoRepository_Factory implements Factory<SyncTodoRepository> {
  private static final SyncTodoRepository_Factory INSTANCE = new SyncTodoRepository_Factory();

  @Override
  public SyncTodoRepository get() {
    return new SyncTodoRepository();
  }

  public static SyncTodoRepository_Factory create() {
    return INSTANCE;
  }

  public static SyncTodoRepository newInstance() {
    return new SyncTodoRepository();
  }
}
