package org.worshipsongs.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.appsee.Appsee;

/**
 * Created by vignesh on 14/07/2017.
 */

public abstract class AbstractActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        Appsee.start("3b437ad98d184cfe82beb61a784c2b0c");
    }
}
