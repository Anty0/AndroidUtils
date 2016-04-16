package eu.codetopic.utils.list.recyclerView;

/**
 * Created by anty on 10.4.16.
 *
 * @author anty
 */
public final class Recycler {

    private static final String LOG_TAG = "Recycler";

    private Recycler() {
    }

    public static RecyclerInflater inflate() {
        return new RecyclerInflater();
    }

}
