package com.xander.shutdownpc.api;

import com.google.gson.Gson;
import com.xander.shutdownpc.App;
import com.xander.shutdownpc.model.DeviceInfo;
import com.xander.shutdownpc.model.enums.Command;
import com.xander.shutdownpc.model.exceptions.HttpException;
import com.xander.shutdownpc.utils.GsonProvider;
import com.xander.shutdownpc.utils.OkHttpProvider;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;

import static com.xander.shutdownpc.model.enums.Command.RESTART;
import static com.xander.shutdownpc.model.enums.Command.SHUTDOWN;
import static com.xander.shutdownpc.model.enums.Command.SLEEP;

public class Api {
    private static OkHttpClient http = OkHttpProvider.getInstance();
    private static Gson gson = GsonProvider.getInstance();

    public static final String INFO_PATH = "/";
    public static final String SHUTDOWN_PATH = "/shutdown";
    public static final String SLEEP_PATH = "/sleep";
    public static final String RESTART_PATH = "/restart";


    public static DeviceInfo getDeviceInfo(String ip) throws IOException, HttpException {
        String address = App.API_PROTOCOL + "://" + ip + ":" + App.API_PORT + INFO_PATH;

        Request request = new Request.Builder().url(address).get().build();
        Response response = http.newBuilder().callTimeout(10, TimeUnit.SECONDS).build().newCall(request).execute();

        if (!response.isSuccessful()) throw new HttpException(response.code(), response.body());

        ResponseBody body = response.body();

        if (body != null) {
            DeviceInfo info = gson.fromJson(body.string(), DeviceInfo.class);
            info.setIp(ip);
            return info;
        }

        return null;
    }

    public static void shutdown(String ip) throws IOException, HttpException {
        sendCommand(ip, SHUTDOWN);
    }

    public static void restart(String ip) throws IOException, HttpException {
        sendCommand(ip, RESTART);
    }

    public static void sleep(String ip) throws IOException, HttpException {
        sendCommand(ip, SLEEP);
    }

    public static void sendCommand(String ip, Command command) throws HttpException, IOException {
        String address;

        switch (command) {
            case SLEEP:
                address = App.API_PROTOCOL + "://" + ip + ":" + App.API_PORT + SLEEP_PATH;
                break;
            case RESTART:
                address = App.API_PROTOCOL + "://" + ip + ":" + App.API_PORT + RESTART_PATH;
                break;
            case SHUTDOWN:
                address = App.API_PROTOCOL + "://" + ip + ":" + App.API_PORT + SHUTDOWN_PATH;
                break;
            default: throw new IOException("Unsupported command");
        }

        Request request = new Request.Builder().url(address).post(RequestBody.create(new byte[0])).build();
        Response response = http.newCall(request).execute();

        if (!response.isSuccessful()) throw new HttpException(response.code(), response.body());
    }
}
