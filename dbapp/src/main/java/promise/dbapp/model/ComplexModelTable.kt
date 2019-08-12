package promise.dbapp.model

import android.content.ContentValues
import android.database.Cursor
import promise.model.List
import promise.promisedb.Column
import promise.promisedb.Model

class ComplexModelTable : Model<ComplexModel>() {
  /**
   * @return
   */
  override fun getName(): String = "name_of_complex_model_table"

  /**
   * gets all the columns for this model from the child class for creation purposes
   * see [.onCreate]
   *
   * @return list of columns
   */
  override fun getColumns(): List<Column<*>> {
   return List.fromArray(intVariableColumn, floatVariableColumn, doubleVariableColumn, stringVariableColumn)
  }

  override fun deserialize(e: Cursor): ComplexModel = ComplexModel().apply {
    intVariable = e.getInt(intVariableColumn.index)
    floatVariable = e.getFloat(floatVariableColumn.index)
    doubleVariable = e.getDouble(doubleVariableColumn.index)
    stringVariable = e.getString(stringVariableColumn.index)
  }

  override fun serialize(t: ComplexModel): ContentValues = ContentValues().apply {
    put(intVariableColumn.name, t.intVariable)
    put(floatVariableColumn.name, t.floatVariable)
    put(doubleVariableColumn.name, t.doubleVariable)
    put(stringVariableColumn.name, t.stringVariable)
  }

  companion object {
    fun id() = id

    val intVariableColumn: Column<Int> = Column("int", Column.Type.INTEGER.NOT_NULL(), 1)
    val floatVariableColumn: Column<Float> = Column("float", Column.Type.INTEGER.NOT_NULL(), 2)
    val doubleVariableColumn: Column<Double> = Column("double", Column.Type.INTEGER.NOT_NULL(), 3)
    val stringVariableColumn: Column<String> = Column("string", Column.Type.INTEGER.NOT_NULL(), 4)

  }
}