package home.david.keypairandroid;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ElementAdapter<T> extends ArrayAdapter<T> {

    private Database database;

    public ElementAdapter(@NonNull Context context, Database database) {
        super(context, R.layout.element_holder);
        this.database=database;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = View.inflate(getContext(), R.layout.element_holder, null);
        }
        TextView key_tv=view.findViewById(R.id.key_tv);
        TextView value_tv=view.findViewById(R.id.value_tv);
        Element element= (Element) getItem(position);
        try {
            key_tv.setText(element.getKey());
            value_tv.setText(element.getValue());
        } catch (Exception e) {
            e.printStackTrace();
        }

        view.setOnClickListener(v -> {
            ClipboardManager clipboardManager= (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("value", value_tv.getText());
            if (clipboardManager != null) {
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(getContext(), "Copied to  Clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
