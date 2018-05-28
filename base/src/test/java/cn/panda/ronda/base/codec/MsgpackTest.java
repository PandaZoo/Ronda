package cn.panda.ronda.base.codec;

import org.junit.Test;
import org.msgpack.MessagePack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class MsgpackTest {

    @org.junit.Test
    public void serializeWithAnnotation() throws IOException {
        ClassA classA = new ClassA("xnsj", "33", 2);
        // ClassB classB = new ClassB("xxx", "yyyy", 40);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MessagePack messagePack = new MessagePack();
        byte[] aBytes = messagePack.write(classA);
        System.out.println("classA的结果是: " + Arrays.toString(aBytes));
        // byte[] bBytes = messagePack.write(classB);
        // System.out.println("ClassB的结果是: " + Arrays.toString(bBytes));
        ClassA dA = messagePack.read(aBytes, ClassA.class);
        System.out.println("反序列化的结果是:" + dA);
    }

    @org.junit.Test
    public void serializeWithoutAnnotation() throws IOException {
        ClassB classB = new ClassB("xxx", "yyyy", 40);
        MessagePack messagePack = new MessagePack();
        messagePack.register(ClassB.class);
        byte[] bBytes = messagePack.write(classB);
        System.out.println("ClassB的结果是: " + Arrays.toString(bBytes));
        ClassB dB = messagePack.read(bBytes, ClassB.class);
        System.out.println("反序列化的结果是:" + dB);
    }

    @Test
    public void serialMultiple() {
        MessagePack msgPack = new MessagePack();
    }
}
