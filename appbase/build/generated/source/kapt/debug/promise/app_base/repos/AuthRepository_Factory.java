// Generated by Dagger (https://google.github.io/dagger).
package promise.app_base.repos;

import dagger.internal.Factory;
import javax.inject.Provider;
import promise.app_base.data.net.AuthApi;

public final class AuthRepository_Factory implements Factory<AuthRepository> {
  private final Provider<AuthApi> authApiProvider;

  public AuthRepository_Factory(Provider<AuthApi> authApiProvider) {
    this.authApiProvider = authApiProvider;
  }

  @Override
  public AuthRepository get() {
    AuthRepository instance = new AuthRepository();
    AuthRepository_MembersInjector.injectAuthApi(instance, authApiProvider.get());
    return instance;
  }

  public static AuthRepository_Factory create(Provider<AuthApi> authApiProvider) {
    return new AuthRepository_Factory(authApiProvider);
  }

  public static AuthRepository newInstance() {
    return new AuthRepository();
  }
}
