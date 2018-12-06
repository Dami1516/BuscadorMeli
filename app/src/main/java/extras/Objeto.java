package extras;

import org.json.JSONObject;

import java.io.Serializable;

public class Objeto implements Serializable {

    String title;
    String price;
    String thumbnail;
    String id;

    transient JSONObject json;

    public Objeto(String title,String price,String thumbnail,String id,JSONObject json) {
        this.title=title;
        this.price=price;
        this.thumbnail=thumbnail;
        this.id=id;
        this.json=json;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JSONObject getJson() {
        return json;
    }

    public void setJson(JSONObject json) {
        this.json = json;
    }
}
