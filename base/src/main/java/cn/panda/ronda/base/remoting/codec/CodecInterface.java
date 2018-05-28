package cn.panda.ronda.base.remoting.codec;

import java.io.IOException;

public interface CodecInterface {

    CodecTypeEnum type();

    <T> byte[] encode(Object object) throws IOException;

    <T> T decode(byte[] bytes, Class<T> clazz) throws IOException;

}
