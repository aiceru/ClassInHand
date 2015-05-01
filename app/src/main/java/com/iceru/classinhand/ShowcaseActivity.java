package com.iceru.classinhand;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by iceru on 15. 4. 29..
 */
public class ShowcaseActivity extends AppCompatActivity {
    private ShowcaseView sv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showcase);

        sv = (ShowcaseView) findViewById(R.id.showcase_main);

        Intent i = getIntent();
        int[] location = i.getIntArrayExtra(ClassInHandApplication.SHOWCASE_TARGET);

        sv.setTargetPosition(location[0], location[1]);
    }
}
