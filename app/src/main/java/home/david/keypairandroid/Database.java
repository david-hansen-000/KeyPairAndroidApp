package home.david.keypairandroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;

public class Database {

    private SQLiteDatabase secrets_db;
    private SQLiteDatabase data_db;
    private Crypt crypt;

    public Database(Context context, Crypt crypt) {
        File dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File secrets = new File(dir, "secrets.db");
        File data = new File(dir, "data.db");
        secrets_db  = SQLiteDatabase.openOrCreateDatabase(secrets, null);
        data_db = SQLiteDatabase.openOrCreateDatabase(data, null);
        this.crypt=crypt;
    }

    public void add(Element element) {
        ContentValues values = convertElementToContent(element);
        secrets_db.insert("key_pairs", null, values);
    }

    public ArrayList<Element> getElements(String main_key) {
        ArrayList<Element> elements = new ArrayList<>();
        Element element = null;
        String key = null;
        String value = null;
        Cursor c = secrets_db.query("key_pairs", new String[]{"rowid", "key", "value"},"where main_key=?", new String[]{main_key},null, null, null);
        if (c.moveToFirst()) {
            do {
                element = new Element();
                key = c.getString(c.getColumnIndex("key"));
                value = c.getString(c.getColumnIndex("value"));
                element.setId(c.getInt(c.getColumnIndex("rowid")));
                if (key != null) {
                    try {
                        element.setKey(crypt.getDecryptedFromHexString(key));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    element.setKey("none");
                }
                if (value != null) {
                    try {
                        element.setValue(crypt.getDecryptedFromHexString(value));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    element.setValue("none");
                }
                elements.add(element);
            } while(c.moveToNext());
        }
        return elements;
    }

    private ContentValues convertElementToContent(Element element) {
        ContentValues values = new ContentValues();
        try {
            values.put("main_key", Crypt.getHash(element.getMain_key()));
            values.put("key", crypt.getEncryptedHexString(element.getKey()));
            values.put("value", crypt.getEncryptedHexString(element.getValue()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }



}
