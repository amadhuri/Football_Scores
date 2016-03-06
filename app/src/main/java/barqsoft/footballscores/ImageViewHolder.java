package barqsoft.footballscores;

import android.graphics.drawable.Drawable;
import android.media.Image;
import android.widget.ImageView;

/**
 * Created by Madhuri on 1/31/2016.
 */
public class ImageViewHolder {
    ImageView crestImage;
    String imagePath;
    Drawable imageDrawable;

    public ImageViewHolder(ImageView imageView, String imageUrl) {
        this.crestImage = imageView;
        this.imagePath = imageUrl;
        this.imageDrawable = null;
    }

    public String getImagePath() {
        return imagePath; //"https://upload.wikimedia.org/wikipedia/en/8/86/Sevilla_cf_200px.png";
    }

    public ImageView getImageView() {
        return crestImage;
    }

    public Drawable getImageDrawable() {
        return imageDrawable;
    }

    public void setImageDrawable(Drawable imageDrawable) {
        this.imageDrawable = imageDrawable;
    }

}
