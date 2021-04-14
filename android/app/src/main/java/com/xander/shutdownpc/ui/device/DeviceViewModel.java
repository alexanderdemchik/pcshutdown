package com.xander.shutdownpc.ui.device;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.xander.shutdownpc.api.Api;
import com.xander.shutdownpc.model.DeviceInfo;
import com.xander.shutdownpc.model.Result;
import com.xander.shutdownpc.model.enums.Command;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.socket.client.Socket;
import lombok.Getter;
import lombok.Setter;

public class DeviceViewModel extends ViewModel {
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final String TAG = DeviceViewModel.class.getName();

    private final @Getter MutableLiveData<Result<Command>> commandResult = new MutableLiveData<>();
    private @Getter @Setter MutableLiveData<DeviceInfo> device = new MutableLiveData<>(null);
    private Socket socket;

    public void createSocketConnection() {
        if (device.getValue() != null) {
            socket = Api.createSocketConnection(device.getValue().getIp(), device.getValue().getPort());

            socket.on(Socket.EVENT_CONNECT, (data) -> {
                Log.d(TAG, "createSocketConnection connected");
                device.getValue().setConnectionStatus(DeviceInfo.ConnectionStatus.CONNECTED);
                device.postValue(device.getValue());
            });

            socket.on(Socket.EVENT_CONNECT_ERROR, (data) -> {
                Log.d(TAG, "createSocketConnection error");
                device.getValue().setConnectionStatus(DeviceInfo.ConnectionStatus.DISCONNECTED);
                device.postValue(device.getValue());
            });

            socket.on(Socket.EVENT_DISCONNECT, (data) -> {
                Log.d(TAG, "createSocketConnection disconnected");
                device.getValue().setConnectionStatus(DeviceInfo.ConnectionStatus.DISCONNECTED);
                device.postValue(device.getValue());
            });

            socket.connect();
        }
    }

    public void shutdown() {
        disposables.add(
                Completable.fromAction(() -> {
                    Api.shutdown(device.getValue().getIp(), device.getValue().getPort());
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .doOnComplete(() -> {
                            commandResult.setValue(new Result<>(Command.SHUTDOWN, Result.Status.SUCCESS));
                        })
                        .doOnError(err -> {
                            commandResult.setValue(new Result<>(Command.SHUTDOWN, Result.Status.ERROR));
                        })
                        .subscribe()
        );
    }

    public void restart() {
        disposables.add(
                Completable.fromAction(() -> {
                    Api.restart(device.getValue().getIp(), device.getValue().getPort());
                }).subscribeOn(Schedulers.io())
                        .doOnComplete(() -> {
                            commandResult.setValue(new Result<>(Command.RESTART, Result.Status.SUCCESS));
                        })
                        .doOnError(err -> {
                            commandResult.setValue(new Result<>(Command.RESTART, Result.Status.ERROR));
                        })
                        .subscribe()
        );
    }

    public void sleep() {
        disposables.add(
                Completable.fromAction(() -> {
                    Api.sleep(device.getValue().getIp(), device.getValue().getPort());
                }).subscribeOn(Schedulers.io())
                        .doOnComplete(() -> {
                            commandResult.setValue(new Result<>(Command.SLEEP, Result.Status.SUCCESS));
                        })
                        .doOnError(err -> {
                            commandResult.setValue(new Result<>(Command.SLEEP, Result.Status.ERROR));
                        })
                        .subscribe()
        );
    }

//    public void checkConnectionStatus() {
//        disposables.add(
//                Completable.fromAction(() -> {
//                    Api.getDeviceInfo(device.getValue().getIp(), device.getValue().getPort());
//                }).subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .doOnComplete(() -> {
//                            device.getValue().setConnectionStatus(DeviceInfo.ConnectionStatus.CONNECTED);
//                            device.setValue(device.getValue());
//                        })
//                        .doOnError(err -> {
//                            device.getValue().setConnectionStatus(DeviceInfo.ConnectionStatus.DISCONNECTED);
//                            device.setValue(device.getValue());
//                        })
//                        .subscribe()
//        );
//    }
//
//    public void runPeriodicalConnectionCheck() {
//        disposables.add(Observable.interval(2, TimeUnit.SECONDS).subscribe((l) -> {
//            checkConnectionStatus();
//        }));
//    }

    @Override
    protected void onCleared() {
        disposables.clear();
        socket.close();
        commandResult.setValue(null);
        super.onCleared();
    }
}
