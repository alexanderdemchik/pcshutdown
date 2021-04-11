package com.xander.shutdownpc.ui.device;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.xander.shutdownpc.R;
import com.xander.shutdownpc.model.DeviceInfo;

public class DeviceFragment extends Fragment {
    private static final String TAG = DeviceFragment.class.getName();
    private DeviceViewModel deviceViewModel;
    private ImageView shutdownBtn;
    private ImageView sleepBtn;
    private ImageView restartBtn;
    private View connectionStatusIndicator;
    private TextView connectionStatusText;
    private TextView deviceTitle;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceViewModel = new ViewModelProvider(this).get(DeviceViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.device_fragment, container, false);
        DeviceInfo deviceInfo = DeviceFragmentArgs.fromBundle(getArguments()).getDevice();
        deviceViewModel.getDevice().setValue(deviceInfo);

        shutdownBtn = view.findViewById(R.id.shutdown_btn);
        sleepBtn = view.findViewById(R.id.sleep_btn);
        restartBtn = view.findViewById(R.id.restart_btn);
        connectionStatusIndicator = view.findViewById(R.id.connection_status_indicator);
        connectionStatusText = view.findViewById(R.id.connection_status_text);
        deviceTitle = view.findViewById(R.id.device_title);

        restartBtn.setOnClickListener((v) -> {
            deviceViewModel.restart();
        });

        shutdownBtn.setOnClickListener(v -> {
            deviceViewModel.shutdown();
        });

        sleepBtn.setOnClickListener((v) -> {
            deviceViewModel.sleep();
        });

        deviceViewModel.getDevice().observe(getViewLifecycleOwner(), (device) -> {
            Log.d(TAG, "onCreateView: device changed" + device);
            deviceTitle.setText(device.getHostname() + " - " + device.getVersion());
            switch (device.getConnectionStatus()) {
                case CONNECTED:
                    connectionStatusIndicator.setBackgroundResource(R.drawable.connected_indicator);
                    connectionStatusText.setText(R.string.connected);
                    break;
                case DISCONNECTED:
                    connectionStatusIndicator.setBackgroundResource(R.drawable.disconnected_indicator);
                    connectionStatusText.setText(R.string.disconnected);
                    break;
            }
        });

        deviceViewModel.createSocketConnection();

        return view;
    }

    @Override
    public void onDestroyView() {
        deviceViewModel.onCleared();
        super.onDestroyView();
    }
}
