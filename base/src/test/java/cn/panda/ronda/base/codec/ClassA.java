package cn.panda.ronda.base.codec;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.msgpack.annotation.Message;
import org.msgpack.annotation.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Message
public class ClassA {

    private String name;
    public String age;
    @Optional
    public Integer sex;
}
