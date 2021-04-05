package com.xander.shutdownpc.ui.home;

import android.app.Application;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.xander.shutdownpc.App;
import com.xander.shutdownpc.api.Api;
import com.xander.shutdownpc.model.DeviceInfo;
import com.xander.shutdownpc.utils.GsonProvider;
import com.xander.shutdownpc.utils.OkHttpProvider;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.Getter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class HomeViewModel extends AndroidViewModel {
    private static final String TAG = HomeViewModel.class.getName();

    @Getter
    private final MutableLiveData<List<DeviceInfo>> devices = new MutableLiveData<>(new ArrayList<>());

    @Getter
    private final MutableLiveData<Boolean> searching = new MutableLiveData<>(false);

    private final CompositeDisposable disposables = new CompositeDisposable();

    public HomeViewModel(@NonNull Application application) {
        super(application);
    }

    public void findDevices() {
        WifiManager wifi = (WifiManager) getApplication().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        String ip = intToIp(wifi.getConnectionInfo().getIpAddress());

        final String supIp = ip.substring(0, ip.lastIndexOf('.') + 1);

        List<Observable<DeviceInfo>> requests = new ArrayList<>();

        for(int i = 1; i <= 254; i++) {
            int fi = i;
            requests.add(Observable.fromCallable(() -> {
                String serverIp = supIp + fi;

                try {
                    return Api.getDeviceInfo(serverIp);
                } catch (Exception e) {
                    return null;
                }
            }).subscribeOn(Schedulers.io()));
        }

        disposables.add(
                Observable.mergeDelayError(requests)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe((d) -> searching.setValue(true))
                        .doFinally(() -> {
                            searching.setValue(false);
                        })
                        .subscribe(this::addDevice, (err) -> {
                        })
        );

    }

    public void addDevice(DeviceInfo deviceInfo) {
        Log.d(TAG, "addDevice: " + deviceInfo.toString());
        devices.getValue().removeIf((d) -> d.getMac().equals(deviceInfo.getMac()));
        devices.getValue().add(deviceInfo);
        devices.setValue(devices.getValue());
    }

    public String intToIp(int i) {
        // Convert little-endian to big-endian if needed
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            i = Integer.reverseBytes(i);
        }

        return ((i >> 24 ) & 0xFF ) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ( i & 0xFF);
    }

    @Override
    protected void onCleared() {
        Log.d(TAG, "onCleared");
        disposables.clear();
        super.onCleared();
    }
}