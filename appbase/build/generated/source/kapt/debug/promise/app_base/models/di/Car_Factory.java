// Generated by Dagger (https://google.github.io/dagger).
package promise.app_base.models.di;

import dagger.internal.Factory;
import javax.inject.Provider;

public final class Car_Factory implements Factory<Car> {
  private final Provider<Driver> driverProvider;

  private final Provider<Engine> engineProvider;

  private final Provider<Wheels> wheelsProvider;

  private final Provider<Remote> remoteProvider;

  public Car_Factory(Provider<Driver> driverProvider, Provider<Engine> engineProvider,
      Provider<Wheels> wheelsProvider, Provider<Remote> remoteProvider) {
    this.driverProvider = driverProvider;
    this.engineProvider = engineProvider;
    this.wheelsProvider = wheelsProvider;
    this.remoteProvider = remoteProvider;
  }

  @Override
  public Car get() {
    Car instance = new Car(driverProvider.get(), engineProvider.get(), wheelsProvider.get());
    Car_MembersInjector.injectEnableRemote(instance, remoteProvider.get());
    return instance;
  }

  public static Car_Factory create(Provider<Driver> driverProvider, Provider<Engine> engineProvider,
      Provider<Wheels> wheelsProvider, Provider<Remote> remoteProvider) {
    return new Car_Factory(driverProvider, engineProvider, wheelsProvider, remoteProvider);
  }

  public static Car newInstance(Driver driver, Engine engine, Wheels wheels) {
    return new Car(driver, engine, wheels);
  }
}
