package org.mosspaper.objects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.mosspaper.Env;
import org.mosspaper.ParseException;

import java.io.File;

public class Image implements MossObject {

    /**
     * Draw the image specified by the path. The cursor position will not be
     * changed. 
     */
    public Image(String imagePath) {
        this.imagePath = imagePath;
    }

    public void preDraw(Env env) { 
        if (bitmap != null) {
            return;
        }
        File image = new File(imagePath);
        if (image.isAbsolute()) {
            bitmap =
                BitmapFactory.decodeFile(imagePath);
        } else {
            File parentFile = env.getConfigFile().getParentFile();
            bitmap =
                BitmapFactory.decodeFile(new File(parentFile, imagePath).toString());
        }
    }

    public void draw(Env env) {
        if (bitmap != null) {
            env.getCanvas().drawBitmap(
                    bitmap, env.getX(), env.getY(), env.getPaint());
        }
    }

    public void postDraw(Env env) { }

    public DataProvider getDataProvider() {
        return null;
    }

    private Bitmap bitmap;
    private String imagePath;
}
