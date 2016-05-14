package com.degree.bachelor.jane_doe.virtualcardboard;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity
{
    @Override
    protected void onCreate(Bundle bundleState) {
        super.onCreate(bundleState);
        this.setContentView(new DrawView(this));
    }
}
