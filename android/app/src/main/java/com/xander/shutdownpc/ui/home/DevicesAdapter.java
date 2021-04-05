package com.xander.shutdownpc.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.xander.shutdownpc.R;
import com.xander.shutdownpc.model.DeviceInfo;

import java.util.List;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.ViewHolder> {
    private List<DeviceInfo> localDataSet;
    private OnDeviceClickListener onClickListener;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView deviceName;
        private TextView macAddress;
        private TextView deviceInfo;
        private ImageView image;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            deviceName = view.findViewById(R.id.device_name);
            macAddress = view.findViewById(R.id.device_mac);
            deviceInfo = view.findViewById(R.id.device_additional_info);
            image = view.findViewById(R.id.device_image);
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet [] containing the data to populate views to be used
     * by RecyclerView.
     * @param onClickListener
     */
    public DevicesAdapter(List<DeviceInfo> dataSet, OnDeviceClickListener onClickListener) {
        localDataSet = dataSet;
        this.onClickListener = onClickListener;
    }

    public void setLocalDataSet(List<DeviceInfo> localDataSet) {
        this.localDataSet = localDataSet;
        notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.device_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.itemView.setOnClickListener(v -> {
            onClickListener.onClick(localDataSet.get(position));
        });
        viewHolder.image.setImageResource(R.drawable.windows);
        DeviceInfo device = localDataSet.get(position);
        viewHolder.deviceName.setText(device.getHostname());
        viewHolder.deviceInfo.setText(formatDeviceInfo(device.getVersion(), device.getArch()));

        viewHolder.macAddress.setText(device.getMac());
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    private String formatDeviceInfo(String version, String arch) {
        return version + " " + arch;
    }
}

interface OnDeviceClickListener {
    void onClick(DeviceInfo deviceInfo);
}