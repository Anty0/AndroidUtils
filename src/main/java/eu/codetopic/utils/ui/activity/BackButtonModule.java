package eu.codetopic.utils.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import eu.codetopic.utils.ui.activity.modular.SimpleActivityCallBackModule;

public class BackButtonModule extends SimpleActivityCallBackModule {

    private static final String LOG_TAG = "BackButtonModule";

    @Override
    protected void onPostSetSupportActionBar(@Nullable Toolbar toolbar) {
        super.onPostSetSupportActionBar(toolbar);
        initActionBar();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActionBar();
    }

    private void initActionBar() {
        ActionBar actionBar = getActivity().getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            getActivity().finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
