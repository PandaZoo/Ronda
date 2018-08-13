/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.panda.ronda.spring.annotation;

import cn.panda.ronda.base.remoting.codec.CodecTypeEnum;
import cn.panda.ronda.spring.config.ConsumerBean;
import org.assertj.core.util.Lists;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * {@link ConsumerBean} Builder
 */
class ConsumerBeanBuilder extends AbstractAnnotationConfigBeanBuilder<Consumer, ConsumerBean> {


    private ConsumerBeanBuilder(Consumer consumer, ClassLoader classLoader, ApplicationContext applicationContext) {
        super(consumer, classLoader, applicationContext);
    }

    private void configureInterface(Consumer consumer, ConsumerBean consumerBean) {

        Class<?> interfaceClass = consumer.interfaceClass();

        if (void.class.equals(interfaceClass)) {

            interfaceClass = null;

            String interfaceClassName = consumer.interfaceName();

            if (StringUtils.hasText(interfaceClassName)) {
                if (ClassUtils.isPresent(interfaceClassName, classLoader)) {
                    interfaceClass = ClassUtils.resolveClassName(interfaceClassName, classLoader);
                }
            }

        }

        if (interfaceClass == null) {
            interfaceClass = this.interfaceClass;
        }

        Assert.isTrue(interfaceClass.isInterface(),
                "The class of field or method that was annotated @Reference is not an interface!");

        consumerBean.setClazz(interfaceClass);

    }

    /**
     * TODO 具体实现
     * @param consumer
     * @param consumerBean
     */
    private void configureCodec(Consumer consumer, ConsumerBean consumerBean) {
        consumerBean.setProtocols(Lists.newArrayList(CodecTypeEnum.JSON));
    }


    private void configureConsumerConfig(Consumer consumer, ConsumerBean<?> consumerBean) {

        String consumerBeanName = consumer.id();

        /*ConsumerConfig consumerConfig = getOptionalBean(applicationContext, consumerBeanName);

        referenceBean.setConsumer(consumerConfig);*/

    }

    @Override
    protected ConsumerBean doBuild() {
        return new ConsumerBean<Object>(annotation);
    }

    @Override
    protected void preConfigureBean(Consumer annotation, ConsumerBean bean) {
        Assert.notNull(interfaceClass, "The interface class must set first!");
    }

    @Override
    protected void postConfigureBean(Consumer annotation, ConsumerBean bean) throws Exception {

        bean.setApplicationContext(applicationContext);

        configureInterface(annotation, bean);

        configureConsumerConfig(annotation, bean);

        configureCodec(annotation, bean);

        bean.afterPropertiesSet();

    }

    public static ConsumerBeanBuilder create(Consumer annotation, ClassLoader classLoader,
                                             ApplicationContext applicationContext) {
        return new ConsumerBeanBuilder(annotation, classLoader, applicationContext);
    }

}
