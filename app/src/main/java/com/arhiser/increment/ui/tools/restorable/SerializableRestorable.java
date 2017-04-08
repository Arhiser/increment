package com.arhiser.increment.ui.tools.restorable;

import android.os.Bundle;

import java.io.Serializable;

/**
 * Created by arhis on 14.01.2017.
 */

public class SerializableRestorable<T extends Serializable> extends Restorable<Serializable> {

    public SerializableRestorable(RestorableManager restorableManager, String name, T object) {
        super(name, object);
        restorableManager.putRestorable(this);
    }

    public SerializableRestorable(String name, T object) {
        super(name, object);
    }

    @Override
    public void save(Bundle bundle) {
        bundle.putSerializable(getName(), getObject());
    }

    @Override
    public void restore(Bundle bundle) {
        setObject(bundle.getSerializable(getName()));
    }
}
