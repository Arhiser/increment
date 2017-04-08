package com.arhiser.increment.ui.tools.restorable;

import android.os.Bundle;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by arhis on 24.01.2017.
 */

public class ParcelableListRestorable<T extends Parcelable> extends Restorable<ArrayList<T>> {
    public ParcelableListRestorable(String name, ArrayList<T> object) {
        super(name, object);
    }

    @Override
    public void save(Bundle bundle) {
        bundle.putParcelableArrayList(getName(), getObject());
    }

    @Override
    public void restore(Bundle bundle) {
        setObject(bundle.getParcelableArrayList(getName()));
    }
}
