package com.xander.shutdownpc.ui.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.color.MaterialColors;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.xander.shutdownpc.R;
import com.xander.shutdownpc.ui.device.DeviceFragment;
import com.xander.shutdownpc.ui.device.DeviceFragmentArgs;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private final static String TAG = HomeFragment.class.getName();

    private HomeViewModel mViewModel;
    private RecyclerView devicesList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearProgressIndicator indicator;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        devicesList = view.findViewById(R.id.devices_list);
        indicator = view.findViewById(R.id.device_search_indicator);
        swipeRefreshLayout = view.findViewById(R.id.device_list_swiperefresh);


        devicesList.setAdapter(new DevicesAdapter(new ArrayList<>(), device -> {
            HomeFragmentDirections.ActionMainFragmentToDeviceFragment action = HomeFragmentDirections.actionMainFragmentToDeviceFragment();
            action.setDevice(device);
            Navigation.findNavController(view).navigate(action);
        }));

        swipeRefreshLayout.setColorSchemeColors(MaterialColors.getColor(view, R.attr.colorPrimary));
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(MaterialColors.getColor(view, R.attr.colorSurface));
        swipeRefreshLayout.setOnRefreshListener(() -> {
            mViewModel.findDevices();
        });

        mViewModel.getDevices().observe(getViewLifecycleOwner(), (deviceInfoList) -> {
            Log.d(TAG, "onCreateView: " + deviceInfoList);
            ((DevicesAdapter) devicesList.getAdapter()).setLocalDataSet(deviceInfoList);
        });

        mViewModel.getSearching().observe(getViewLifecycleOwner(), (searching) -> {
            indicator.setVisibility(searching ? View.VISIBLE : View.INVISIBLE);
            if (!searching) swipeRefreshLayout.setRefreshing(false);
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel.findDevices();
    }
}