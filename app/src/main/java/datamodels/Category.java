package datamodels;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Shamyyoun on 2/22/2015.
 */
public class Category implements Serializable {
    private String id;
    private String name;
    private String imageUrl;
    private Bitmap image;

    public Category(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Category setName(String name) {
        this.name = name;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Category setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public Bitmap getImage() {
        return image;
    }

    public Category setImage(Bitmap image) {
        this.image = image;
        return this;
    }
}
