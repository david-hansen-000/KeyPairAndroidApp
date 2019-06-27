package home.david.keypairandroid;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Database database;
    private MainKeyAdapter<String> main_keys;
    public static TextView debug_text;
    private AutoCompleteTextView key_fill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        debug_text=findViewById(R.id.debug_result);
        findViewById(R.id.clear_clipboard_btn).setOnClickListener(v -> clearClipboard());
        Crypt crypt = new Crypt();
        try {
            crypt.setPassword("mytimeIs1462");
            database = new Database(this, crypt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayAdapter<String> autofill=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, database.getMainKeys());
        key_fill = findViewById(R.id.search_et);
        key_fill.setAdapter(autofill);
        key_fill.setOnLongClickListener(v -> {
            key_fill.showDropDown();
            return false;
        });
        key_fill.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item= autofill.getItem(position);
                key_fill.setText(item);
                main_keys.add(item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ListView main_keys_list=findViewById(R.id.main_key_list);
        main_keys= new MainKeyAdapter<>(this, database);
        main_keys_list.setAdapter(main_keys);

        findViewById(R.id.submit_btn).setOnClickListener(v -> {
            String search=key_fill.getText().toString();
            main_keys.add(search);
        });
    }

    private void clearClipboard() {
        ClipboardManager clipboardManager= (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip=ClipData.newPlainText("blank","");
        if (clipboardManager != null) {
            clipboardManager.setPrimaryClip(clip);
            Toast.makeText(this, "Clipboard Cleared", Toast.LENGTH_SHORT).show();
        }

    }

}
