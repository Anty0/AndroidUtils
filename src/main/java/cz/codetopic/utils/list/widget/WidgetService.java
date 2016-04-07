package cz.codetopic.utils.list.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.widget.RemoteViewsService;

import cz.codetopic.utils.Log;

@TargetApi(11)
public class WidgetService extends RemoteViewsService {

    private static final String LOG_TAG = "WidgetService";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d(LOG_TAG, "onGetViewFactory");

        return (new WidgetMultilineAdapter(getApplicationContext(), intent));
    }

}
