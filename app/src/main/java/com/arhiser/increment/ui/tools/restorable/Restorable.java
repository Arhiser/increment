package com.arhiser.increment.ui.tools.restorable;

import android.os.Bundle;

/**
 * Created by arhis on 14.01.2017.
 */

public abstract class Restorable<T> {
    private String name;
    private T object;

    public Restorable(String name, T object) {
        this.name = name;
        this.object = object;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public void onSave() {

    }

    public void onRestore() {

    }

    abstract public void save(Bundle bundle);
    abstract public void restore(Bundle bundle);

}
