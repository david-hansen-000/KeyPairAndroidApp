package home.david.keypairandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Crypt crypt = new Crypt();
        try {
            crypt.setPassword("mytimeIs1462");
            database = new Database(this, crypt);
        } catch (Exception e) {
            e.printStackTrace();
        }

        findViewById(R.id.submit_btn).setOnClickListener(v -> {
            String search=((TextView)findViewById(R.id.search_et)).getText().toString();

        });
    }
}
