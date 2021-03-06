// Generated by Dagger (https://google.github.io/dagger).
package promise.app_base.repos;

import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;
import promise.app_base.models.Todo;
import promise.repo.StoreRepository;

public final class ReposModule_ProvideStoreRepositoryFactory implements Factory<StoreRepository<Todo>> {
  private final Provider<AsyncTodoRepository> asyncTodoRepositoryProvider;

  private final Provider<SyncTodoRepository> syncTodoRepositoryProvider;

  public ReposModule_ProvideStoreRepositoryFactory(
      Provider<AsyncTodoRepository> asyncTodoRepositoryProvider,
      Provider<SyncTodoRepository> syncTodoRepositoryProvider) {
    this.asyncTodoRepositoryProvider = asyncTodoRepositoryProvider;
    this.syncTodoRepositoryProvider = syncTodoRepositoryProvider;
  }

  @Override
  public StoreRepository<Todo> get() {
    return provideStoreRepository(asyncTodoRepositoryProvider.get(), syncTodoRepositoryProvider.get());
  }

  public static ReposModule_ProvideStoreRepositoryFactory create(
      Provider<AsyncTodoRepository> asyncTodoRepositoryProvider,
      Provider<SyncTodoRepository> syncTodoRepositoryProvider) {
    return new ReposModule_ProvideStoreRepositoryFactory(asyncTodoRepositoryProvider, syncTodoRepositoryProvider);
  }

  public static StoreRepository<Todo> provideStoreRepository(
      AsyncTodoRepository asyncTodoRepository, SyncTodoRepository syncTodoRepository) {
    return Preconditions.checkNotNull(ReposModule.provideStoreRepository(asyncTodoRepository, syncTodoRepository), "Cannot return null from a non-@Nullable @Provides method");
  }
}
