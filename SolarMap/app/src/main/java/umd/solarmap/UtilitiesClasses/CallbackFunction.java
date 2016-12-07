package umd.solarmap.UtilitiesClasses;

import javax.security.auth.callback.Callback;

/**
 * Created by user on 12/1/16.
 */

public abstract class CallbackFunction implements Callback {
    public abstract void onPostExecute(Object result);
}
