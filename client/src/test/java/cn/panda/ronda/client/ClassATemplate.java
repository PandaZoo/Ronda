package cn.panda.ronda.client;

import org.msgpack.packer.Packer;
import org.msgpack.template.AbstractTemplate;
import org.msgpack.unpacker.Unpacker;

import java.io.IOException;

public class ClassATemplate extends AbstractTemplate<ClassA> {

    @Override
    public void write(Packer pk, ClassA v, boolean required) throws IOException {
        if (v == null) {
            pk.writeNil();
            return;
        }

        // 只有一个Class
        pk.writeArrayBegin(2);
        Class testClass = v.getTestClass();
        String canonicalName = testClass.getCanonicalName();
        pk.write(canonicalName);
        Object obj = v.getObj();
        // 开始write obj
        pk.writeArrayBegin(2);
        pk.write(obj.getClass().getCanonicalName());
        pk.write(obj);
        pk.writeArrayEnd();

        pk.writeArrayEnd();
    }

    @Override
    public ClassA read(Unpacker u, ClassA to, boolean required) throws IOException {
        if (to == null) {
            return null;
        }

        u.readArrayBegin();
        String canonicalName = u.read(String.class);
        try {
            Class<?> aClass = Class.forName(canonicalName);
            to.setTestClass(aClass);
            u.readArrayBegin();
            String objType = u.read(String.class);
            Class<?> objClass = Class.forName(objType);
            Object obj = u.read(objClass);
            to.setObj(obj);
            u.readArrayEnd();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(canonicalName + " not found");
        }
        u.readArrayEnd();

        return to;
    }
}
