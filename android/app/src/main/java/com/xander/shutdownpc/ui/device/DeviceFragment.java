package com.xander.shutdownpc.ui.device;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.xander.shutdownpc.R;
import com.xander.shutdownpc.model.DeviceInfo;
import com.xander.shutdownpc.ui.home.DevicesAdapter;
import com.xander.shutdownpc.ui.home.HomeViewModel;

import java.util.ArrayList;

public class DeviceFragment extends Fragment {
    private static final String TAG = DeviceFragment.class.getName();
    private DeviceViewModel deviceViewModel;
    private ImageView shutdownBtn;
    private ImageView sleepBtn;
    private ImageView restartBtn;

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
        deviceViewModel.setDevice(deviceInfo);

        shutdownBtn = view.findViewById(R.id.shutdown_btn);
        sleepBtn = view.findViewById(R.id.sleep_btn);
        restartBtn = view.findViewById(R.id.restart_btn);

        restartBtn.setOnClickListener((v) -> {
            deviceViewModel.restart();
        });

        shutdownBtn.setOnClickListener(v -> {
            deviceViewModel.shutdown();
        });

        sleepBtn.setOnClickListener((v) -> {
            deviceViewModel.sleep();
        });

        return view;
    }
}
