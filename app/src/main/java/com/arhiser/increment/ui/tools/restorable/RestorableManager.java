package com.arhiser.increment.ui.tools.restorable;

import android.os.Bundle;

import com.arhiser.increment.tools.Utils;

import java.util.ArrayList;


/**
 * Created by arhis on 14.01.2017.
 */

public class RestorableManager {
    ArrayList<Restorable> restorables = new ArrayList<>();

    public void putRestorable(Restorable restorable) {
        restorables.add(restorable);
    }

    public void save(Bundle bundle) {
        if (bundle != null) {
            Utils.forEach(restorables, restorable -> {
                restorable.onSave();
                restorable.save(bundle);
            });
        }
    }

    public void restore(Bundle bundle) {
        if (bundle != null) {
            Utils.forEach(restorables, restorable -> {restorable.restore(bundle); restorable.onRestore();});
        }
    }
}
