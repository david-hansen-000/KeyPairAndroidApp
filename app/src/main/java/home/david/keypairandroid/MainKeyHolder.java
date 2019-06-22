package home.david.keypairandroid;

import android.content.Context;
import android.os.Environment;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class MainKeyHolder {
    private LinearLayout layout;
    public MainKeyHolder(Context context, String main_key, ArrayList<Element> elements) {
        layout = (LinearLayout) LinearLayout.inflate(context, R.layout.main_key_holder, null);
        TextView tv = layout.findViewById(R.id.main_key_text);
        tv.setText(main_key);
        LinearLayout key_value = null;
        for (Element e:elements) {
            key_value = (LinearLayout) LinearLayout.inflate(context, R.layout.element_holder, null);
            ((TextView)key_value.findViewById(R.id.key_tv)).setText(e.getKey());
            ((TextView)key_value.findViewById(R.id.value_tv)).setText(e.getValue());
            layout.addView(key_value);
        }
    }

    public LinearLayout getLayout() {
        return layout;
    }
}
