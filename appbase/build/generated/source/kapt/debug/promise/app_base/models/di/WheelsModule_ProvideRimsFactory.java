// Generated by Dagger (https://google.github.io/dagger).
package promise.app_base.models.di;

import dagger.internal.Factory;
import dagger.internal.Preconditions;

public final class WheelsModule_ProvideRimsFactory implements Factory<Rims> {
  private static final WheelsModule_ProvideRimsFactory INSTANCE = new WheelsModule_ProvideRimsFactory();

  @Override
  public Rims get() {
    return provideRims();
  }

  public static WheelsModule_ProvideRimsFactory create() {
    return INSTANCE;
  }

  public static Rims provideRims() {
    return Preconditions.checkNotNull(WheelsModule.provideRims(), "Cannot return null from a non-@Nullable @Provides method");
  }
}
