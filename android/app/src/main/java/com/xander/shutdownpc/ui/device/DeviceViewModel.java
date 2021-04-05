package com.xander.shutdownpc.ui.device;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.xander.shutdownpc.api.Api;
import com.xander.shutdownpc.model.DeviceInfo;
import com.xander.shutdownpc.model.Result;
import com.xander.shutdownpc.model.enums.Command;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.Getter;
import lombok.Setter;

public class DeviceViewModel extends ViewModel {
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<Result<Command>> commandResult = new MutableLiveData<>();
    private @Getter @Setter DeviceInfo device = null;

    public void shutdown() {
        disposables.add(
                Completable.fromAction(() -> {
                    Api.shutdown(device.getIp());
                }).subscribeOn(Schedulers.io())
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
                    Api.restart(device.getIp());
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
                    Api.sleep(device.getIp());
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

    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }
}
