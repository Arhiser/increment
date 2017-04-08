package com.arhiser.increment.ui.tools.restorable;

import android.os.Bundle;

/**
 * Created by arhis on 14.01.2017.
 */

public class IntegerRestorable extends Restorable<Integer> {
    public IntegerRestorable(String name, Integer object) {
        super(name, object);
    }

    public IntegerRestorable(RestorableManager restorableManager, String name, Integer object) {
        super(name, object);
        restorableManager.putRestorable(this);
    }

    @Override
    public void save(Bundle bundle) {
        bundle.putInt(getName(), getObject());
    }

    @Override
    public void restore(Bundle bundle) {
        if (bundle.containsKey(getName())) {
            setObject(bundle.getInt(getName(), 0));
        }
    }
}
