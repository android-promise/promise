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

package promise.promisedb;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import java.util.Arrays;

import promise.Promise;
import promise.data.log.LogUtil;
import promise.model.List;
import promise.model.S;
import promise.model.SList;
import promise.promisedb.query.QueryBuilder;
import promise.util.Conditions;

/**
 *
 */
public abstract class FastDB extends SQLiteOpenHelper implements Crud<SQLiteDatabase> {
  /**
   *
   */
  private static final String DEFAULT_NAME = "fast";
  /*private static Map<IndexCreated, Table<?, SQLiteDatabase>> indexCreatedTableHashMap;*/
  private String TAG = LogUtil.makeTag(FastDB.class);
  /**
   *
   */
  private Context context;

  /**
   *
   * @param name
   * @param factory
   * @param version
   * @param errorHandler
   */
  private FastDB(
      String name,
      SQLiteDatabase.CursorFactory factory,
      int version,
      DatabaseErrorHandler errorHandler) {
    super(Promise.instance().context(), name, factory, version, errorHandler);
    LogUtil.d(TAG, "fast db init");
    this.context = Promise.instance().context();
    /*initTables();*/
  }

  /**
   *
   * @param name
   * @param version
   * @param cursorListener
   * @param listener
   */
  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public FastDB(String name, int version, final FastDbCursorFactory.Listener cursorListener, final Corrupt listener) {
    this(
        name,
        cursorListener != null ? new FastDbCursorFactory(cursorListener) : null,
        version,
        dbObj -> {
          assert listener != null;
          listener.onCorrupt();
        });
  }

  /**
   *
   * @param version
   */
  public FastDB(int version) {
    this(DEFAULT_NAME, version, null,null);
  }

  /*private void initTables() {
    indexCreatedTableHashMap = new ArrayMap<>();
    List<Table<?, SQLiteDatabase>> tables = Conditions.checkNotNull(tables());
    for (int i = 0; i < tables.size(); i++)
        indexCreatedTableHashMap.put(new IndexCreated(i, false), tables.get(i));
  }*/

  /**
   *
   * @param db
   */
  @Override
  public final void onCreate(SQLiteDatabase db) {
    create(db);
  }

  /**
   *
   * @param database
   * @param oldVersion
   * @param newVersion
   */
  @Override
  public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
    LogUtil.d(TAG, "onUpgrade", oldVersion, newVersion);
    if (shouldUpgrade(database, oldVersion, newVersion)) {
      LogUtil.d(TAG, "onUpgrade", "upgrading tables");
      upgrade(database, oldVersion, newVersion);
    }
  }

  /**
   *
   * @param database
   * @param oldVersion
   * @param newVersion
   * @return
   */
  public abstract boolean shouldUpgrade(SQLiteDatabase database, int oldVersion, int newVersion);

  /**
   *
   * @return
   */
  public String name() {
    return this.getDatabaseName();
  }

  /**
   *
   * @return
   */
  public abstract List<Table<?, SQLiteDatabase>> tables();

  /**
   *
   * @param database
   */
  private void create(SQLiteDatabase database) {
   boolean created = true;

   for (Table<?, SQLiteDatabase> table: Conditions.checkNotNull(tables())) {
    try {
     created = created && create(table, database);
    }
    catch (DBError dbError) {
     LogUtil.e(TAG, dbError);
     return;
    }
   }
  }

  /**
   *
   * @param database
   * @param v1
   * @param v2
   */
  private void upgrade(SQLiteDatabase database, int v1, int v2) {
   for (Table<?, SQLiteDatabase> table: Conditions.checkNotNull(tables())) {
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

  /**
   *
   * @param database
   * @param tables
   * @return
   */
  public boolean add(SQLiteDatabase database, Table<?, SQLiteDatabase>... tables) {
    boolean created = true;
    for (Table<?, SQLiteDatabase> table : tables) {
      try {
        created = created && create(table, database);
      } catch (DBError dbError) {
        LogUtil.e(TAG, dbError);
        return false;
      }
    }
    return created;
  }

  /**
   *
   * @param database
   * @return
   */
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

  /**
   *
   * @param table
   * @param database
   * @return
   * @throws DBError
   */
  private boolean create(Table<?, SQLiteDatabase> table, SQLiteDatabase database) throws DBError {
    try {
      table.onCreate(database);
    } catch (ModelError e) {
      throw new DBError(e);
    }
    return true;
  }

  /**
   *
   * @param table
   * @param database
   * @return
   * @throws DBError
   */
  private boolean drop(Table<?, SQLiteDatabase> table, SQLiteDatabase database) throws DBError {
    try {
      checkTableExist(table).onDrop(database);
    } catch (ModelError e) {
      throw new DBError(e);
    }
    return true;
  }

  /**
   *
   * @param builder
   * @return
   */
  public Cursor query(QueryBuilder builder) {
    String sql = builder.build();
    String[] params = builder.buildParameters();
    LogUtil.d(TAG, "query: "+ sql, " params: "+ Arrays.toString(params));
    return getReadableDatabase().rawQuery(sql, params);
  }

  /**
   *
   * @param table
   * @param <T>
   * @return
   */
  @Override
  public <T extends S> Table.Extras<T> read(Table<T, SQLiteDatabase> table) {
    return checkTableExist(table).read(getReadableDatabase());
  }

  /**
   *
   * @param table
   * @param <T>
   * @return
   */
  @Override
  public <T extends S> SList<T> readAll(Table<T, SQLiteDatabase> table) {
    return checkTableExist(table).onReadAll(getReadableDatabase(), true);
  }

  /**
   *
   * @param table
   * @param column
   * @param <T>
   * @return
   */
  @Override
  public <T extends S> SList<T> readAll(Table<T, SQLiteDatabase> table, Column column) {
    return checkTableExist(table).onReadAll(getReadableDatabase(), column);
  }

  /**
   *
   * @param t
   * @param table
   * @param <T>
   * @return
   */
  @Override
  public <T extends S> boolean update(T t, Table<T, SQLiteDatabase> table) {
    return checkTableExist(table).onUpdate(t, getWritableDatabase());
  }

  /**
   *
   * @param t
   * @param table
   * @param column
   * @param <T>
   * @return
   */
  @Override
  public <T extends S> boolean update(T t, Table<T, SQLiteDatabase> table, Column column) {
    try {
      return checkTableExist(table).onUpdate(t, getWritableDatabase(), column);
    } catch (ModelError modelError) {
      LogUtil.e(TAG, "update error", modelError);
      return false;
    }
  }

  /**
   *
   * @param table
   * @param columns
   * @param <T>
   * @return
   */
  @Override
  public <T extends S> SList<T> readAll(Table<T, SQLiteDatabase> table, Column[] columns) {
    return checkTableExist(table).onReadAll(getReadableDatabase(), columns);
  }

  /**
   *
   * @param table
   * @param t
   * @param <T>
   * @return
   */
  @Override
  public <T extends S> boolean delete(Table<T, SQLiteDatabase> table, T t) {
    return checkTableExist(table).onDelete(t, getWritableDatabase());
  }

  /**
   *
   * @param table
   * @param column
   * @return
   */
  @Override
  public boolean delete(Table<?, SQLiteDatabase> table, Column column) {
    return checkTableExist(table).onDelete(getWritableDatabase(), column);
  }

  /**
   *
   * @param table
   * @return
   */
  @Override
  public boolean delete(Table<?, SQLiteDatabase> table) {
    return checkTableExist(table).onDelete(getWritableDatabase());
  }

  /**
   *
   * @param tables
   * @return
   */
  public boolean delete(Table<?, SQLiteDatabase>... tables) {
    boolean delete = true;
    for (Table<?, SQLiteDatabase> table : tables)
      delete = delete && delete(table);
    return delete;
  }

  /**
   *
   * @param table
   * @param column
   * @param list
   * @param <T>
   * @return
   */
  @Override
  public <T> boolean delete(Table<?, SQLiteDatabase> table, Column<T> column, List<T> list) {
    return checkTableExist(table).onDelete(getWritableDatabase(), column, list);
  }

  /**
   *
   * @param t
   * @param table
   * @param <T>
   * @return
   */
  @Override
  public <T extends S> long save(T t, Table<T, SQLiteDatabase> table) {
    return checkTableExist(table).onSave(t, getWritableDatabase());
  }

  /**
   *
   * @param list
   * @param table
   * @param <T>
   * @return
   */
  @Override
  public <T extends S> boolean save(SList<T> list, Table<T, SQLiteDatabase> table) {
    return checkTableExist(table).onSave(list, getWritableDatabase());
  }

  /**
   *
   * @return
   */
  @Override
  public boolean deleteAll() {
    synchronized (FastDB.class) {
      boolean deleted = true;
      for (Table<?, SQLiteDatabase> table: Conditions.checkNotNull(tables()))
       deleted = deleted && delete(checkTableExist(table));
      return deleted;
    }
  }

  /**
   *
   * @param table
   * @return
   */
  @Override
  public int getLastId(Table<?, SQLiteDatabase> table) {
    return checkTableExist(table).onGetLastId(getReadableDatabase());
  }

  /**
   *
   * @return
   */
  public Context getContext() {
    return context;
  }

  /**
   *
   * @param table
   * @param <T>
   * @return
   */
  private <T extends S> Table<T, SQLiteDatabase> checkTableExist(Table<T, SQLiteDatabase> table) {
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
