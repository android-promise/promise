package promise.promisedb;

import android.database.sqlite.SQLiteDatabase;

import promise.model.List;
import promise.model.Result;
import promise.model.SModel;

/** Created on 7/23/18 by yoctopus. */
public class FastDBStore<T extends SModel> implements Store<T, Table<T, SQLiteDatabase>, Throwable> {
  private FastDB fastDB;

  public FastDBStore(FastDB fastDB) {
    this.fastDB = fastDB;
  }

  @Override
  public void get(
      Table<T, SQLiteDatabase> tsqLiteDatabaseTable,
      Result<Extras<T>, Throwable> callBack) {
    new StoreExtra<T, Throwable>() {
      @Override
      public <Y> List<T> filter(List<T> list, Y... y) {
        return list;
      }
    }.getExtras(fastDB.readAll(tsqLiteDatabaseTable), callBack);
  }

  @Override
  public void delete(
      Table<T, SQLiteDatabase> tsqLiteDatabaseTable,
      T t,
      Result<Boolean, Throwable> callBack) {
    try {
      callBack.response(fastDB.delete(tsqLiteDatabaseTable, t));
    } catch (Throwable e) {
      callBack.error(e);
    }
  }

  @Override
  public void update(
      Table<T, SQLiteDatabase> tsqLiteDatabaseTable,
      T t,
      Result<Boolean, Throwable> callBack) {
    try {
      callBack.response(fastDB.update(t, tsqLiteDatabaseTable));
    } catch (Throwable e) {
      callBack.error(e);
    }
  }

  @Override
  public void save(
      Table<T, SQLiteDatabase> tsqLiteDatabaseTable,
      T t,
      Result<Boolean, Throwable> callBack) {
    try {
      callBack.response(fastDB.save(t, tsqLiteDatabaseTable) > 0);
    } catch (Throwable e) {
      callBack.error(e);
    }
  }

  @Override
  public void clear(
      Table<T, SQLiteDatabase> tsqLiteDatabaseTable,
      Result<Boolean, Throwable> callBack) {
    try {
      callBack.response(fastDB.delete(tsqLiteDatabaseTable));
    } catch (Throwable e) {
      callBack.error(e);
    }
  }

  @Override
  public void clear(Result<Boolean, Throwable> callBack) {
    try {
      callBack.response(fastDB.deleteAll());
    } catch (Throwable e) {
      callBack.error(e);
    }
  }
}
