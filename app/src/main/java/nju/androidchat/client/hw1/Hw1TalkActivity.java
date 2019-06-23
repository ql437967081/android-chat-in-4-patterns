package nju.androidchat.client.hw1;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.java.Log;
import nju.androidchat.client.ClientMessage;
import nju.androidchat.client.R;
import nju.androidchat.client.Utils;
import nju.androidchat.client.component.ItemTextReceive;
import nju.androidchat.client.component.ItemTextSend;
import nju.androidchat.client.component.OnRecallMessageRequested;
import nju.androidchat.client.hw1.component.ItemImageReceive;
import nju.androidchat.client.hw1.component.ItemImageSend;

@Log
public class Hw1TalkActivity extends AppCompatActivity implements Hw1Contract.View, TextView.OnEditorActionListener, OnRecallMessageRequested {
    private Hw1Contract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Hw1TalkModel hw1TalkModel = new Hw1TalkModel();

        // Create the presenter
        this.presenter = new Hw1TalkPresenter(hw1TalkModel, this, new ArrayList<>());
        hw1TalkModel.setIHw1TalkPresenter(this.presenter);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.start();
    }

    @Override
    public void showMessageList(List<ClientMessage> messages) {
        runOnUiThread(() -> {
                    LinearLayout content = findViewById(R.id.chat_content);

                    // 删除所有已有的ItemTextAndImage
                    //content.removeAllViews();

                    for (int i = content.getChildCount(); i < messages.size(); i++) {
                        String text = String.format("%s", messages.get(i).getMessage());
                        System.out.println("add No." + i + "message: " + text);
                        content.addView(newMessageShow(messages.get(i), text));
                    }
/*
                    // 增加ItemTextAndImage
                    for (ClientMessage message : messages) {
                        String text = String.format("%s", message.getMessage());
                        content.addView(newMessageShow(message, text));
                    }*/

                    Utils.scrollListToBottom(this);
                }
        );
    }

    private LinearLayout newMessageShow(ClientMessage message, String text) {
        Pattern r = Pattern.compile("^!\\[.*?\\]\\((.*?)\\)$");
        Matcher m = r.matcher(text);
        boolean isImage = m.find();
        String uri = isImage ? m.group(1) : null;
        if (message.getSenderUsername().equals(this.presenter.getUsername())) {
            if (isImage)
                return new ItemImageSend(this, uri, message.getMessageId(), this);
            else
                return new ItemTextSend(this, text, message.getMessageId(), this);
        } else {
            if (isImage)
                return new ItemImageReceive(this, uri, message.getMessageId());
            else
                return new ItemTextReceive(this, text, message.getMessageId());
        }
    }

    @Override
    public void setPresenter(Hw1Contract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            return hideKeyboard();
        }
        return super.onTouchEvent(event);
    }

    private boolean hideKeyboard() {
        return Utils.hideKeyboard(this);
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (Utils.send(actionId, event)) {
            hideKeyboard();
            // 异步地让Controller处理事件
            sendText();
        }
        return false;
    }

    private void sendText() {
        EditText text = findViewById(R.id.et_content);
        AsyncTask.execute(() -> {
            this.presenter.sendMessage(text.getText().toString());
        });
    }

    public void onBtnSendClicked(View v) {
        hideKeyboard();
        sendText();
    }

    // 当用户长按消息，并选择撤回消息时做什么，MVP-0不实现
    @Override
    public void onRecallMessageRequested(UUID messageId) {

    }
}
