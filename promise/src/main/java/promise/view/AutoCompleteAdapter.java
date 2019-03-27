package promise.view;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import promise.model.List;
import promise.model.Searchable;

/**
 * Created on 7/5/18 by yoctopus.
 */
public class AutoCompleteAdapter<T extends Searchable> extends ArrayAdapter<T> {

    public AutoCompleteAdapter(@NonNull Context context, @NonNull List<T> objects) {
        super(context, objects.first().layout(), objects);
    }
}
