package cn.panda.ronda.base.remoting.codec.msgpack.template;

import cn.panda.ronda.base.helper.MsgPackHelper;
import org.msgpack.packer.Packer;
import org.msgpack.template.AbstractTemplate;
import org.msgpack.unpacker.Unpacker;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MapObjectTemplate extends AbstractTemplate<Map<String, Object>> {

    @Override
    public void write(Packer pk, Map<String, Object> v, boolean required) throws IOException {
        if (v == null || v.isEmpty()) {
            pk.writeArrayBegin(1);
            pk.writeNil();
            pk.writeArrayEnd();
            return;
        }

        pk.writeArrayBegin(v.size());
        for (Map.Entry<String, Object> entry : v.entrySet()) {
            pk.writeArrayBegin(3);
            pk.write(entry.getKey());
            pk.write(entry.getValue().getClass().getCanonicalName());
            Class<?> aClass = entry.getValue().getClass();
            if (MsgPackHelper.getTemplateRegistry().lookup(aClass) == null) {
                MsgPackHelper.getTemplateRegistry().register(aClass);
            }
            pk.write(entry.getValue());

            pk.writeArrayEnd();
        }

        pk.writeArrayEnd();
    }

    @Override
    public Map<String, Object> read(Unpacker u, Map<String, Object> to, boolean required) throws IOException {
        int size = u.readArrayBegin();
        to = new HashMap<>(size);
        if (!u.trySkipNil()) {
            for (int i = 0; i < size; i++) {
                u.readArrayBegin();
                String key = u.read(String.class);
                String className = u.read(String.class);
                try {
                    Class<?> valueType = Class.forName(className);
                    Object value = u.read(valueType);
                    to.put(key, value);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                u.readArrayEnd();
            }
        }
        u.readArrayEnd();
        return to;
    }
}
