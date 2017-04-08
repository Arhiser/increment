package com.arhiser.increment.ui;


import com.arhiser.increment.R;

/**
 * Created by arhis on 12.01.2017.
 */

public class FragmentHostActivity extends ToolbarSecondaryActivity {

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_fragment_host;
    }

    protected int getFragmentContainerId() {
        return R.id.fragment_content;
    }

    protected void switchFragment(CustomerBaseFragment fragment) {
        setTitle(fragment.getFragmentTitleResource());
        getSupportFragmentManager().beginTransaction()
                .replace(getFragmentContainerId(), fragment)
                .commit();
    }

    protected void pushFragment(CustomerBaseFragment fragment) {
        setTitle(fragment.getFragmentTitleResource());
        getSupportFragmentManager().beginTransaction()
                .replace(getFragmentContainerId(), fragment)
                .addToBackStack(null)
                .commit();
    }
}
