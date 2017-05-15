package sg.lifecare.framework.data;

import android.content.Context;

import javax.inject.Inject;
import javax.inject.Singleton;

import sg.lifecare.framework.di.ApplicationContext;

@Singleton
public class AppDataManager implements DataManager {

    private final Context mContext;

    @Inject
    public AppDataManager(@ApplicationContext Context context) {
        mContext = context;
    }
}
