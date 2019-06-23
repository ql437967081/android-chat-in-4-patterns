package nju.androidchat.client.hw1.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.StyleableRes;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import nju.androidchat.client.R;
import nju.androidchat.client.component.OnRecallMessageRequested;

public class ItemImageReceive extends LinearLayout {
    @StyleableRes
    int index0 = 0;

    private ImageView imageView;
    private Context context;
    private UUID messageId;
    private OnRecallMessageRequested onRecallMessageRequested;
    public ItemImageReceive(Context context, String text, UUID messageId) {
        super(context);
        this.context = context;
        inflate(context, R.layout.item_image_receive, this);
        this.imageView = findViewById(R.id.chat_item_content_image);
        this.messageId = messageId;
        setImage(text);
    }

    private void setImage(String text) {
        final Bitmap[] bm = {null};
        Thread thread = new Thread(() -> {
            try {
                URL url = new URL(text);
                InputStream is = url.openConnection().getInputStream();
                bm[0] = BitmapFactory.decodeStream(is);
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
            System.out.println("bitmap transferring completed");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        imageView.setImageBitmap(bm[0]);
    }
}
