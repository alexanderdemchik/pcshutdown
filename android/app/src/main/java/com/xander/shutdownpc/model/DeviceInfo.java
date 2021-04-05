package com.xander.shutdownpc.model;

import java.io.Serializable;
import lombok.Data;

@Data
public class DeviceInfo implements Serializable {
    private String hostname;
    private String arch;
    private String mac;
    private String ip;
    private String platform;
    private String version;
}
