package promise.app.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import promise.app.R
import promise.app.ui.ui.todo.TodoFragment

class TodoActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.todo_activity)
    if (savedInstanceState == null) {
      supportFragmentManager.beginTransaction()
          .replace(R.id.container, TodoFragment.newInstance())
          .commitNow()
    }
  }
}
