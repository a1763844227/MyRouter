package com.jh.other;

import android.content.Context;
import android.util.Log;

import com.jh.annotation.RouteProvider;
import com.jh.router.IRouterProvider;
import com.jh.router.OtherService;

/**
 * create by jh on 2021/3/29.
 */
@RouteProvider(path = "/other/provider")
public class ProviderOther implements OtherService {

    @Override
    public void init(Context context) {

    }

    @Override
    public void text(String text) {

        Log.i("tag","sdgfsfs"+text);

    }
}
