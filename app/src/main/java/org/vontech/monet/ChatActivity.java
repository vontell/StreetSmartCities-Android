package org.vontech.monet;

import android.os.Bundle;
import android.app.Activity;
import android.webkit.WebView;

public class ChatActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        WebView chat = findViewById(R.id.chat_view);
        chat.getSettings().setJavaScriptEnabled(true);
        chat.loadData("<iframe height=\"98%\" width=\"97%\" src='https://webchat.botframework.com/embed/StreetSmartCity?s=O04UM0ebZOM.cwA.cbM.5rDMg8fQCAcEag4ar0oB71ja_SXPgoln8tMCgH8DGXU'></iframe>",
                "text/html", null);

    }

}
