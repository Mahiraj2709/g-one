package customer.glympse.glympse;

import android.app.Application;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by admin on 1/10/2017.
 */

public class GlympseApplication extends Application {
    public static boolean isVisible = false;

    public static Bus bus = null;

    public static Bus getBus() {
        if (bus == null) {
            bus = new Bus(ThreadEnforcer.ANY);
        }
        return bus;
    }
}
