package org.lyreg.fido_uaf_android_demo.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.lyreg.fido_uaf_android_demo.BaseActivity;
import org.lyreg.fido_uaf_android_demo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/1/14.
 */
public class AuthenticatorListAdapter extends BaseAdapter {
    private List<String> aaidList;
    private LayoutInflater mLayoutInflater;
    private Activity mParent;

    public AuthenticatorListAdapter(Activity parent, List<String> list) {
        mParent = parent;
        mLayoutInflater = parent.getLayoutInflater();
        if(list != null) {
            aaidList = list;
        } else {
            aaidList = new ArrayList<String>();
        }
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return aaidList.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return aaidList.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder = null;
        if(convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.popup_item, null);
            Log.v("getView", "getView");
            mViewHolder = new ViewHolder();
            mViewHolder.aaid = (TextView) convertView.findViewById(R.id.aaid);

            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        String aaid = aaidList.get(position);
        mViewHolder.aaid.setText(aaid);

        return convertView;
    }

    private class ViewHolder {
        public TextView aaid;
    }

}
