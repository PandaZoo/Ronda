package cn.panda.ronda.register.convert;

import cn.panda.ronda.register.domain.TransportConfig;
import cn.panda.ronda.register.domain.TransportInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author yongkang.zhang
 * created at 14/08/2018
 */
@Slf4j
public class TransportConverter {

    private static String SEPARATOR = "-";

    public static String populatePath(TransportConfig transportConfig) {
        return transportConfig.getClassName()
                .concat(SEPARATOR)
                .concat(transportConfig.getProtocol());
    }

    public static String populateData(TransportInfo transportInfo) {
        return transportInfo.getHost()
                .concat(SEPARATOR)
                .concat(String.valueOf(transportInfo.getPort()));
    }

    public static TransportInfo resolveData(String data) {
        if (data == null || Objects.equals(data, "")) {
            return null;
        }

        String[] split = data.split(SEPARATOR);

        if (split.length < 2) {
            log.error("transportInfo returned is not valid");
            return null;
        }

        String host = split[0];
        Integer port = Integer.parseInt(split[1]);

        TransportInfo transportInfo = new TransportInfo();
        transportInfo.setHost(host);
        transportInfo.setPort(port);

        return transportInfo;
    }
}
