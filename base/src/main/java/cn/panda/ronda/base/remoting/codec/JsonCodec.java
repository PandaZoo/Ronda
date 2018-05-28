package cn.panda.ronda.base.remoting.codec;

import com.alibaba.fastjson.JSON;

public class JsonCodec implements CodecInterface {

    @Override
    public CodecTypeEnum type() {
        return CodecTypeEnum.JSON;
    }

    @Override
    public byte[] encode(Object object) {
        return JSON.toJSONString(object).getBytes();
    }

    @Override
    public <T> T decode(byte[] bytes, Class<T> clazz) {
        return JSON.parseObject(new String(bytes), clazz);
    }
}
