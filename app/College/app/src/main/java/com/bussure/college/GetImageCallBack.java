package com.bussure.college;

import android.graphics.Bitmap;

public interface GetImageCallBack
{
    void done(Bitmap bitmap);
    void error(String message);
}
