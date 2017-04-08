package com.arhiser.increment.ui.tools.restorable;

import android.os.Bundle;
import android.os.Parcelable;

/**
 * Created by arhis on 14.01.2017.
 */

public class ParcelableRestorable<T extends Parcelable> extends Restorable<T> {

    public ParcelableRestorable(RestorableManager restorableManager, String name, T object) {
        super(name, object);
        restorableManager.putRestorable(this);
    }

    public ParcelableRestorable(String name, T object) {
        super(name, object);
    }

    @Override
    public void save(Bundle bundle) {
        bundle.putParcelable(getName(), getObject());
    }

    @Override
    public void restore(Bundle bundle) {
        setObject(bundle.getParcelable(getName()));
    }
}
