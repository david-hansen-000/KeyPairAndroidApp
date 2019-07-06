package home.david.keypairandroid;

import android.content.Context;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainKeyAdapter<T> extends ArrayAdapter<T> {
    private Database database;
    public MainKeyAdapter(@NonNull Context context, Database database) {
        super(context, R.layout.main_key_holder);
        this.database=database;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view=null;
        if (convertView==null) {
            view = View.inflate(getContext(), R.layout.main_key_holder, null);
        } else {
            view=convertView;
        }
        TextView main_key_tv=view.findViewById(R.id.main_key_tv);
        String main_key= (String) getItem(position);
        main_key_tv.setText(main_key);
        main_key_tv.setOnLongClickListener(v -> {
            //TODO: create a dialog for adding/editing/deleting
            return true;
        });
        ListView listView=view.findViewById(R.id.element_list);
        DatabaseTask task=new DatabaseTask(listView);
        task.execute(main_key);
        view.findViewById(R.id.key_list_up).setOnClickListener(v -> listView.scrollListBy(-20));
        view.findViewById(R.id.key_list_down).setOnClickListener(v -> listView.scrollListBy(20));
        return view;
    }

    class DatabaseTask extends AsyncTask<String,String,Void> {
        private ArrayList<Element> elements;
        private ListView listView;

        public DatabaseTask(ListView listView) {
            this.listView=listView;
        }

        @Override
        protected Void doInBackground(String... strings) {
            String main_key=strings[0];
            String hash_mainkey=Crypt.getHash(main_key);
            publishProgress(main_key, hash_mainkey);
            elements = database.getElements(hash_mainkey);
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            MainActivity.debug_text.setText(values[0]+"~~>"+values[1]+"\n");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            for (Element e:elements) {
                MainActivity.debug_text.append("element:"+e.toString()+"\n");
            }
            ElementAdapter<Element> adapter=new ElementAdapter<>(getContext(), database);
            adapter.addAll(elements);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }
}
