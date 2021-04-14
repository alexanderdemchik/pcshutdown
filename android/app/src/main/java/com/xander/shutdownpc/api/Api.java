package com.xander.shutdownpc.api;

import android.util.Log;

import com.google.gson.Gson;
import com.xander.shutdownpc.App;
import com.xander.shutdownpc.BuildConfig;
import com.xander.shutdownpc.model.DeviceInfo;
import com.xander.shutdownpc.model.enums.Command;
import com.xander.shutdownpc.model.exceptions.HttpException;
import com.xander.shutdownpc.utils.GsonProvider;
import com.xander.shutdownpc.utils.OkHttpProvider;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import io.socket.client.IO;
import io.socket.client.Socket;
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
    private static final String TAG = Api.class.getName();

    public static final String INFO_PATH = "/";
    public static final String SHUTDOWN_PATH = "/shutdown";
    public static final String SLEEP_PATH = "/sleep";
    public static final String RESTART_PATH = "/restart";
    public static final String API_PROTOCOL = BuildConfig.API_PROTOCOL;

    public static DeviceInfo getDeviceInfo(String ip, int port) throws IOException, HttpException {
        String address = API_PROTOCOL + "://" + ip + ":" + port + INFO_PATH;
        Log.d(Api.class.getName(), "getDeviceInfo: " + address);
        Request request = new Request.Builder().url(address).get().build();
        Response response = http.newBuilder().callTimeout(10, TimeUnit.SECONDS).build().newCall(request).execute();

        if (!response.isSuccessful()) throw new HttpException(response.code(), response.body());

        ResponseBody body = response.body();

        DeviceInfo info = gson.fromJson(body.string(), DeviceInfo.class);
        info.setIp(ip);
        info.setPort(port);
        return info;
    }

    public static void shutdown(String ip, int port) throws IOException, HttpException {
        sendCommand(ip, port, SHUTDOWN);
    }

    public static void restart(String ip, int port) throws IOException, HttpException {
        sendCommand(ip, port, RESTART);
    }

    public static void sleep(String ip, int port) throws IOException, HttpException {
        sendCommand(ip, port, SLEEP);
    }

    public static void sendCommand(String ip, int port, Command command) throws HttpException, IOException {
        String address;

        switch (command) {
            case SLEEP:
                address = API_PROTOCOL + "://" + ip + ":" + port + SLEEP_PATH;
                break;
            case RESTART:
                address = API_PROTOCOL + "://" + ip + ":" + port + RESTART_PATH;
                break;
            case SHUTDOWN:
                address = API_PROTOCOL + "://" + ip + ":" + port + SHUTDOWN_PATH;
                break;
            default: throw new IOException("Unsupported command");
        }

        Request request = new Request.Builder().url(address).post(RequestBody.create(new byte[0])).build();
        Response response = http.newBuilder().connectTimeout(4, TimeUnit.SECONDS).build().newCall(request).execute();

        if (!response.isSuccessful()) throw new HttpException(response.code(), response.body());
    }

    /**
     * ping with android ping utility (ECMP protocol)
     * @param ip ip address to ping
     * @return true if ip is reachable
     */
    public static boolean ping(String ip) {
//        Log.d(Api.class.getName(), "ping: ");
        Runtime runtime = Runtime.getRuntime();
        try
        {
            Process process = runtime.exec("/system/bin/ping -c 1 " + ip);

            int mExitValue = process.waitFor();

            if (process.getOutputStream() != null) process.getOutputStream().close();
            if (process.getInputStream() != null) process.getInputStream().close();
            if (process.getErrorStream() != null) process.getErrorStream().close();

            return mExitValue == 0;
        }
        catch (InterruptedException | IOException ignore)
        {
            return false;
        }
    }

    public static Socket createSocketConnection(String ip, int port) {
        URI uri = URI.create(API_PROTOCOL + "://" + ip + ":" + port);
        IO.Options options = IO.Options.builder().build();
        return IO.socket(uri, options);
    }
}
