package com.arhiser.increment.ui.tools.restorable;

import android.os.Bundle;

/**
 * Created by arhis on 14.01.2017.
 */

public class StringRestorable extends Restorable<String> {

    public StringRestorable(String name, String object) {
        super(name, object);
    }

    public StringRestorable(RestorableManager restorableManager, String name, String object) {
        super(name, object);
        restorableManager.putRestorable(this);
    }

    @Override
    public void save(Bundle bundle) {
        bundle.putString(getName(), getObject());
    }

    @Override
    public void restore(Bundle bundle) {
        if (bundle.containsKey(getName())) {
            setObject(bundle.getString(getName()));
        }
    }
}
