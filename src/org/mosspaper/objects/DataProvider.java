package org.mosspaper.objects;

import android.content.Context;

import org.mosspaper.DataService.State;

public interface DataProvider {

    public void startup(Context context);

    public void update(State state);

    public void destroy(Context context);

    public boolean runWhenInvisible();

}
