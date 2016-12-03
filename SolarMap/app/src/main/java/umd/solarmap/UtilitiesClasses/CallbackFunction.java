package umd.solarmap.UtilitiesClasses;

import javax.security.auth.callback.Callback;

/**
 * Created by user on 12/1/16.
 */

public abstract class CallbackFunction implements Callback {

    private Object result = null;

    public void setResult(Object aResult) {
        result = aResult;
    }

    public Object getResult() {
        return result;
    }

    public abstract void onPostExecute();
}
