package com.iceru.classinhand;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by iceru on 15. 4. 29..
 */
public class ShowcaseActivity extends AppCompatActivity {
    private ShowcaseView sv;
    //private LinearLayout mLinearLayout_showcase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showcase);

        //mLinearLayout_showcase = (LinearLayout)findViewById(R.id.linearlayout_showcase);
        //sv = new ShowcaseView(this);//
        sv = (ShowcaseView) findViewById(R.id.showcase_main);

        Intent i = getIntent();
        int[] location = i.getIntArrayExtra(ClassInHandApplication.SHOWCASE_TARGET_POSITION);
        int[] size = i.getIntArrayExtra(ClassInHandApplication.SHOWCASE_TARGET_SIZE);

        sv.setTargetPosition(location[0], location[1]);
        sv.setTargetSize(size[0], size[1]);
        //mLinearLayout_showcase.addView(sv);
    }
}
