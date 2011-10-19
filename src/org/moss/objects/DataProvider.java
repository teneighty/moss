package org.moss.objects;

import android.content.Context;

import org.moss.DataService.State;

public interface DataProvider {

    public void startup(Context context);

    public void update(State state);

    public void destroy(Context context);

    public boolean runWhenInvisible();

}
