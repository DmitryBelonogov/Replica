package com.nougust3.diary.Utils;

import android.view.MenuItem;
import android.view.View;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class AnimateUtils {

    public static void safeAnimate(View view, int duration, Techniques type) {

        if (view != null) {
            YoYo.with(type).duration(duration).playOn(view);
        }

    }

}
