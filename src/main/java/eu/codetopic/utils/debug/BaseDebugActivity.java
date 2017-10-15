package eu.codetopic.utils.debug;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import eu.codetopic.utils.debug.items.ConnectivityDebugItem;
import eu.codetopic.utils.debug.items.LoggingDebugItem;
import eu.codetopic.utils.debug.items.TimedComponentsManagerDebugItem;
import eu.codetopic.utils.ui.activity.modular.module.BackButtonModule;
import eu.codetopic.utils.ui.activity.modular.module.ToolbarModule;
import eu.codetopic.utils.ui.activity.modular.ModularActivity;
import eu.codetopic.utils.ui.container.items.custom.CustomItem;
import eu.codetopic.utils.ui.container.recycler.Recycler;

public abstract class BaseDebugActivity extends ModularActivity {

    private static final String LOG_TAG = "BaseDebugActivity";

    public BaseDebugActivity() {
        super(new ToolbarModule(), new BackButtonModule());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<CustomItem> items = new ArrayList<>();
        prepareDebugItems(items);
        Recycler.inflate().on(this).setAdapter(items);
    }

    protected void prepareDebugItems(List<CustomItem> items) {
        items.add(new ConnectivityDebugItem());
        items.add(new TimedComponentsManagerDebugItem());
        items.add(new LoggingDebugItem());
        // add here Utils debug items
    }

}
