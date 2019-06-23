package nju.androidchat.client.hw1.component;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.StyleableRes;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import lombok.Setter;
import nju.androidchat.client.R;
import nju.androidchat.client.component.OnRecallMessageRequested;

public class ItemImageSend extends LinearLayout implements View.OnLongClickListener {
    @StyleableRes
    int index0 = 0;

    private ImageView imageView;
    private Context context;
    private UUID messageId;
    @Setter
    private OnRecallMessageRequested onRecallMessageRequested;

    public ItemImageSend(Context context, String text, UUID messageId, OnRecallMessageRequested onRecallMessageRequested) {
        super(context);
        this.context = context;
        inflate(context, R.layout.item_image_send, this);
        this.imageView = findViewById(R.id.chat_item_content_image);
        this.messageId = messageId;
        this.onRecallMessageRequested = onRecallMessageRequested;

        this.setOnLongClickListener(this);
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

    @Override
    public boolean onLongClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("确定要撤回这条消息吗？")
                .setPositiveButton("是", (dialog, which) -> {
                    if (onRecallMessageRequested != null) {
                        onRecallMessageRequested.onRecallMessageRequested(this.messageId);
                    }
                })
                .setNegativeButton("否", ((dialog, which) -> {
                }))
                .create()
                .show();

        return true;


    }
}
