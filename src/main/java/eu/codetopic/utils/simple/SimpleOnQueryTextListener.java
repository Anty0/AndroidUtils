package eu.codetopic.utils.simple;

import android.support.v7.widget.SearchView;

public class SimpleOnQueryTextListener implements SearchView.OnQueryTextListener {

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
