package cn.panda.ronda.base.helper;

import cn.panda.ronda.base.remoting.codec.msgpack.template.MapObjectTemplate;
import cn.panda.ronda.base.remoting.codec.msgpack.template.RequestMessageTemplate;
import cn.panda.ronda.base.remoting.codec.msgpack.template.ResponseMessageTemplate;
import cn.panda.ronda.base.remoting.message.RequestMessage;
import cn.panda.ronda.base.remoting.message.ResponseMessage;
import org.msgpack.MessagePack;
import org.msgpack.template.AbstractTemplate;
import org.msgpack.template.TemplateRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * 使用单例的messagePack，然后由它创建多个packer或者unPacker
 */
public class MsgPackHelper {

    private static final MessagePack messagePack = new MessagePack();
    private static final TemplateRegistry templateRegistry = new TemplateRegistry(null);

    private static final Map<Class, AbstractTemplate> templateMap = new HashMap<>();

    static {
        templateMap.put(Map.class, new MapObjectTemplate());
        templateMap.put(RequestMessage.class, new RequestMessageTemplate());
        templateMap.put(ResponseMessage.class, new ResponseMessageTemplate());
    }

    public static MessagePack getMessagePack() {
        return messagePack;
    }

    public static TemplateRegistry getTemplateRegistry() {
        return templateRegistry;
    }

    public static Map<Class, AbstractTemplate> getTemplateMap() {
        return templateMap;
    }

}
