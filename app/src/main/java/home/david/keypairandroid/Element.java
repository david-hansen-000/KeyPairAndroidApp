package home.david.keypairandroid;

public class Element {
    private String main_key;
    private String key;
    private String value;
    private int id;

    public Element(String main_key, String key, String value) {
        this.main_key = main_key;
        this.key = key;
        this.value = value;
    }

    public Element() {
        main_key=null;
        key=null;
        value=null;
        id=0;
    }

    public void setMain_key(String main_key) {
        this.main_key = main_key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getMain_key() {
        return main_key;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String toString() {
        return "main_key="+main_key+" key="+key+" value="+value+" id="+id;
    }
}


