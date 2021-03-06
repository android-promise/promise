// Generated by Dagger (https://google.github.io/dagger).
package promise.app_base.repos;

import dagger.internal.Factory;
import javax.inject.Provider;
import promise.app_base.data.db.async.AsyncAppDatabase;
import promise.app_base.data.net.TodoApi;

public final class AsyncTodoRepository_Factory implements Factory<AsyncTodoRepository> {
  private final Provider<AsyncAppDatabase> asyncAppDatabaseProvider;

  private final Provider<TodoApi> todoApiProvider;

  public AsyncTodoRepository_Factory(Provider<AsyncAppDatabase> asyncAppDatabaseProvider,
      Provider<TodoApi> todoApiProvider) {
    this.asyncAppDatabaseProvider = asyncAppDatabaseProvider;
    this.todoApiProvider = todoApiProvider;
  }

  @Override
  public AsyncTodoRepository get() {
    return new AsyncTodoRepository(asyncAppDatabaseProvider.get(), todoApiProvider.get());
  }

  public static AsyncTodoRepository_Factory create(
      Provider<AsyncAppDatabase> asyncAppDatabaseProvider, Provider<TodoApi> todoApiProvider) {
    return new AsyncTodoRepository_Factory(asyncAppDatabaseProvider, todoApiProvider);
  }

  public static AsyncTodoRepository newInstance(AsyncAppDatabase asyncAppDatabase,
      TodoApi todoApi) {
    return new AsyncTodoRepository(asyncAppDatabase, todoApi);
  }
}
