/*
 *
 *  * Copyright 2017, Peter Vincent
 *  * Licensed under the Apache License, Version 2.0, Promise.
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package promise.data.db;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import java.util.Arrays;
import java.util.concurrent.Callable;

import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import promise.Promise;
import promise.data.db.query.QueryBuilder;
import promise.data.log.LogUtil;
import promise.model.List;
import promise.model.ResponseCallBack;
import promise.model.S;
import promise.model.SList;
import promise.model.function.EachFunction;
import promise.model.function.MapFunction;
import promise.util.Conditions;

public abstract class ReactiveFastDB extends SQLiteOpenHelper implements ReactiveCrud<SQLiteDatabase> {
  private static final String DEFAULT_NAME = "fast";
  private String TAG = LogUtil.makeTag(ReactiveFastDB.class);
  private Context context;


  private ReactiveFastDB(
      String name,
      SQLiteDatabase.CursorFactory factory,
      int version,
      DatabaseErrorHandler errorHandler) {
    super(Promise.instance().context(), name, factory, version, errorHandler);
    LogUtil.d(TAG, "fast db init");
    this.context = Promise.instance().context();
    Promise.instance().listen(Promise.TAG, new ResponseCallBack<>()
    .response(new ResponseCallBack.Response<Object, Throwable>() {
      @Override
      public void onResponse(Object o) {
        if (o instanceof String) {
          if (o.equals(Promise.CLEANING_UP_RESOURCES)) {
            CompositeDisposable disposable = onTerminate();
            if (disposable != null) disposable.dispose();
          }
        }
      }
    }));
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public ReactiveFastDB(String name, int version, final Corrupt listener) {
    this(
        name,
        null,
        version,
        new DatabaseErrorHandler() {
          @Override
          public void onCorruption(SQLiteDatabase dbObj) {
            assert listener != null;
            listener.onCorrupt();
          }
        });
  }

  public ReactiveFastDB(int version) {
    this(DEFAULT_NAME, version, null);
  }

  /*private void initTables() {
    indexCreatedTableHashMap = new ArrayMap<>();
    List<Table<?, SQLiteDatabase>> tables = Conditions.checkNotNull(tables());
    for (int i = 0; i < tables.size(); i++)
        indexCreatedTableHashMap.put(new IndexCreated(i, false), tables.get(i));
  }*/

  @Override
  public final void onCreate(SQLiteDatabase db) {
    create(db);
  }

  @Override
  public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
    LogUtil.d(TAG, "onUpgrade", oldVersion, newVersion);
    if (shouldUpgrade(database, oldVersion, newVersion)) {
      LogUtil.d(TAG, "onUpgrade", "upgrading tables");
      upgrade(database, oldVersion, newVersion);
    }
  }

  public abstract boolean shouldUpgrade(SQLiteDatabase database, int oldVersion, int newVersion);

  public String name() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
        ? this.getDatabaseName()
        : DEFAULT_NAME;
  }

  public abstract List<ReactiveTable<?, SQLiteDatabase>> tables();

  private void create(SQLiteDatabase database) {
    boolean created = true;
    for (ReactiveTable<?, SQLiteDatabase> table : Conditions.checkNotNull(tables())) {
      try {
        created = created && create(table, database);
      } catch (DBError dbError) {
        LogUtil.e(TAG, dbError);
        return;
      }
    }
  }

  private void upgrade(SQLiteDatabase database, int v1, int v2) {
    for (ReactiveTable<?, SQLiteDatabase> table : Conditions.checkNotNull(tables())) {
      try {
        if ((v2 - v1) == 1) checkTableExist(table).onUpgrade(database, v1, v2);
        else {
          int i = v1;
          while (i < v2) {
            checkTableExist(table).onUpgrade(database, i, i + 1);
            i++;
          }
        }
      } catch (ModelError modelError) {
        LogUtil.e(TAG, modelError);
        return;
      }
    }
  }

  public boolean add(SQLiteDatabase database, List<ReactiveTable<?, SQLiteDatabase>> tables) {
    boolean created = true;
    int j = 0;
    IndexCreated indexCreated;
    for (ReactiveTable<?, SQLiteDatabase> table : tables) {
      try {
        created = created && create(table, database);
      } catch (DBError dbError) {
        LogUtil.e(TAG, dbError);
        return false;
      }
    }
    return created;
  }

  private boolean drop(SQLiteDatabase database) {
    boolean dropped = true;
    /*for (Map.Entry<IndexCreated, Table<?, SQLiteDatabase>> entry :
        indexCreatedTableHashMap.entrySet()) {
      try {
        dropped = dropped && drop(checkTableExist(entry.getValue()), database);
      } catch (DBError dbError) {
        dbError.printStackTrace();
        return false;
      }
    }*/
    return dropped;
  }

  private boolean create(ReactiveTable<?, SQLiteDatabase> table, SQLiteDatabase database) throws DBError {
    try {
      table.onCreate(database);
    } catch (ModelError e) {
      throw new DBError(e);
    }
    return true;
  }

  private Single<Boolean> drop(ReactiveTable<?, SQLiteDatabase> table, SQLiteDatabase database) {
    return checkTableExist(table).onDrop(database);
  }

  public Single<Cursor> query(final QueryBuilder builder) {
    return Single.fromCallable(new Callable<Cursor>() {
      @Override
      public Cursor call() throws Exception {
        String sql = builder.build();
        String[] params = builder.buildParameters();
        LogUtil.e(TAG, "query: " + sql, " params: " + Arrays.toString(params));
        return getReadableDatabase().rawQuery(sql, params);
      }
    }).subscribeOn(Schedulers.from(Promise.instance().executor()));
  }

  public Context getContext() {
    return context;
  }

  private <T extends S> ReactiveTable<T, SQLiteDatabase> checkTableExist(ReactiveTable<T, SQLiteDatabase> table) {
    return Conditions.checkNotNull(table);
    /*synchronized (this) {
        IndexCreated indexCreated = getIndexCreated(table);
        if (indexCreated.created) {
            return table;
        }
        SQLiteDatabase database = context.openOrCreateDatabase(name(),
                Context.MODE_PRIVATE, null);
        try {
            database.query(table.name(), null, null, null, null, null, null);
        } catch (SQLException e) {
            try {
                table.onCreate(database);
            } catch (ModelError modelError) {
                LogUtil.e(TAG, modelError);
                throw new RuntimeException(modelError);
            }
        }
    }*/
  }



  @Override
  public <T extends S> ReactiveTable.Extras<T> read(ReactiveTable<T, SQLiteDatabase> table) {
    return checkTableExist(table).read(getReadableDatabase());
  }

  @Override
  public <T extends S> Maybe<SList<T>> readAll(ReactiveTable<T, SQLiteDatabase> table) {
    return checkTableExist(table).onReadAll(getReadableDatabase(), true);
  }

  @Override
  public <T extends S> Maybe<SList<T>> readAll(ReactiveTable<T, SQLiteDatabase> table, Column column) {
    return checkTableExist(table).onReadAll(getReadableDatabase(), column);
  }

  @Override
  public <T extends S> Maybe<Boolean> update(T t, ReactiveTable<T, SQLiteDatabase> table, Column column) {
    return checkTableExist(table).onUpdate(t, getWritableDatabase(), column);
  }

  @Override
  public <T extends S> Maybe<Boolean> update(T t, ReactiveTable<T, SQLiteDatabase> table) {
    return checkTableExist(table).onUpdate(t, getWritableDatabase());
  }

  @Override
  public <T extends S> Maybe<SList<T>> readAll(ReactiveTable<T, SQLiteDatabase> table, Column[] columns) {
    return checkTableExist(table).onReadAll(getReadableDatabase(), columns);
  }

  @Override
  public <T extends S> ReactiveTable.Extras<T> read(ReactiveTable<T, SQLiteDatabase> table, Column... columns) {
    return checkTableExist(table).read(getReadableDatabase(), columns);
  }

  @Override
  public Maybe<Boolean> delete(ReactiveTable<?, SQLiteDatabase> table, Column column) {
    return checkTableExist(table).onDelete(getWritableDatabase(), column);
  }

  @Override
  public <T extends S> Maybe<Boolean> delete(ReactiveTable<T, SQLiteDatabase> table, T t) {
    return checkTableExist(table).onDelete(t, getWritableDatabase());
  }

  @Override
  public Maybe<Boolean> delete(ReactiveTable<?, SQLiteDatabase> table) {
    return checkTableExist(table).onDelete(getWritableDatabase());
  }

  @Override
  public <T> Maybe<Boolean> delete(ReactiveTable<?, SQLiteDatabase> table, Column<T> column, List<T> list) {
    return checkTableExist(table).onDelete(getWritableDatabase(), column, list);
  }

  @Override
  public <T extends S> Single<Long> save(T t, ReactiveTable<T, SQLiteDatabase> table) {
    return checkTableExist(table).onSave(t, getWritableDatabase());
  }

  @Override
  public <T extends S> Single<Boolean> save(SList<T> list, ReactiveTable<T, SQLiteDatabase> table) {
    return checkTableExist(table).onSave(list, getWritableDatabase(), true);
  }

  @Override
  public Maybe<Boolean> deleteAll() {
    return Maybe.zip(tables().map(new MapFunction<Maybe<Boolean>, ReactiveTable<?, SQLiteDatabase>>() {
      @Override
      public Maybe<Boolean> from(ReactiveTable<?, SQLiteDatabase> sqLiteDatabaseReactiveTable) {
        return delete(sqLiteDatabaseReactiveTable);
      }
    }), new Function<Object[], Boolean>() {
      @Override
      public Boolean apply(Object[] objects) {
        return List.fromArray(objects).allMatch(new EachFunction<Object>() {
          @Override
          public boolean filter(Object aBoolean) {
            return aBoolean instanceof Boolean && (Boolean) aBoolean;
          }
        });
      }
    }).subscribeOn(Schedulers.from(Promise.instance().executor()));
  }

  @Override
  public Maybe<Integer> getLastId(ReactiveTable<?, SQLiteDatabase> table) {
    return checkTableExist(table).onGetLastId(getReadableDatabase());
  }

  /*private IndexCreated getIndexCreated(Table<?, SQLiteDatabase> table) {
    for (Iterator<Map.Entry<IndexCreated, Table<?, SQLiteDatabase>>> iterator =
            indexCreatedTableHashMap.entrySet().iterator();
        iterator.hasNext(); ) {
      Map.Entry<IndexCreated, Table<?, SQLiteDatabase>> entry = iterator.next();
      Table<?, SQLiteDatabase> table1 = entry.getValue();
      if (table1.getName().equalsIgnoreCase(table.getName())) return entry.getKey();
    }
    return new IndexCreated(0, false);
  }*/

  public CompositeDisposable onTerminate() {
    return null;
  }

  private static class IndexCreated {
    int id;
    boolean created;

    IndexCreated(int id, boolean created) {
      this.id = id;
      this.created = created;
    }

    @Override
    public boolean equals(Object object) {
      if (this == object) return true;
      if (!(object instanceof IndexCreated)) return false;
      IndexCreated that = (IndexCreated) object;
      return id == that.id && created == that.created;
    }

    @Override
    public int hashCode() {
      int result = id;
      result = 31 * result + (created ? 1 : 0);
      return result;
    }
  }
}
