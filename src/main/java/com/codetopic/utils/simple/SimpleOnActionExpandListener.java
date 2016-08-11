package com.codetopic.utils.simple;

import android.support.v4.view.MenuItemCompat;
import android.view.MenuItem;

public class SimpleOnActionExpandListener implements MenuItemCompat.OnActionExpandListener {

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        return true;
    }
}
