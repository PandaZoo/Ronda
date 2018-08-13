package cn.panda.ronda.spring.annotation;

import cn.panda.ronda.spring.config.ConsumerBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.springframework.core.BridgeMethodResolver.findBridgedMethod;
import static org.springframework.core.BridgeMethodResolver.isVisibilityBridgeMethodPair;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

/**
 * @author yongkang.zhang
 * created at 15/07/2018
 */
public class ConsumerAnnotationBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter
        implements MergedBeanDefinitionPostProcessor, PriorityOrdered, ApplicationContextAware, BeanClassLoaderAware,
        DisposableBean {

    public static final String BEAN_NAME = "consumerAnnotationBeanPostProcessor";

    public final Logger logger = LoggerFactory.getLogger(getClass());

    private ApplicationContext applicationContext;

    private ClassLoader classLoader;

    private final ConcurrentMap<String, InjectionMetadata> injectionMetadataCache =
            new ConcurrentHashMap<>(256);

    private final ConcurrentMap<String, ConsumerBean<?>> consumerBeanCache =
            new ConcurrentHashMap<>();

    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {
        InjectionMetadata metadata = findConsumerMetadata(beanName, bean.getClass(), pvs);
        try {
            metadata.inject(bean, beanName, pvs);
        }
        catch (BeanCreationException ex) {
            throw ex;
        }
        catch (Throwable ex) {
            throw new BeanCreationException(beanName, "Injection of autowired dependencies failed", ex);
        }
        return pvs;
    }

    private InjectionMetadata findConsumerMetadata(String beanName, Class<?> clazz, PropertyValues pvs) {
        String cacheKey = StringUtils.hasLength(beanName) ? beanName : clazz.getName();
        InjectionMetadata metadata = this.injectionMetadataCache.get(cacheKey);
        if (InjectionMetadata.needsRefresh(metadata, clazz)) {
            synchronized (this.injectionMetadataCache) {
                metadata = this.injectionMetadataCache.get(cacheKey);
                if (InjectionMetadata.needsRefresh(metadata, clazz)) {
                    if (metadata != null) {
                        metadata.clear(pvs);
                    }
                    try {
                        metadata = buildConsumerMetadata(clazz);
                        this.injectionMetadataCache.put(cacheKey, metadata);
                    } catch (NoClassDefFoundError err) {
                        throw new IllegalStateException("Failed to introspect bean class [" + clazz.getName() +
                                                        "] for consumer metadata: could not find class that it depends on", err);
                    }

                }
            }
        }

        return metadata;
    }

    /**
     * @param beanClass
     * @return
     */
    private InjectionMetadata buildConsumerMetadata(final Class<?> beanClass) {

        final List<InjectionMetadata.InjectedElement> elements = new LinkedList<InjectionMetadata.InjectedElement>();

        elements.addAll(findFieldReferenceMetadata(beanClass));

        elements.addAll(findMethodReferenceMetadata(beanClass));

        return new InjectionMetadata(beanClass, elements);

    }


    private List<InjectionMetadata.InjectedElement> findFieldReferenceMetadata(final Class<?> beanClass) {

        final List<InjectionMetadata.InjectedElement> elements = new LinkedList<InjectionMetadata.InjectedElement>();
        ReflectionUtils.doWithFields(beanClass, new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {

                Consumer consumer = AnnotatedElementUtils.getMergedAnnotation(field, Consumer.class);

                if (consumer != null) {

                    if (Modifier.isStatic(field.getModifiers())) {
                        if (logger.isWarnEnabled()) {
                            logger.warn("@Reference annotation is not supported on static fields: " + field);
                        }
                        return;
                    }

                    elements.add(new ConsumerFieldElement(field, consumer));
                }

            }
        });

        return elements;

    }

    private List<InjectionMetadata.InjectedElement> findMethodReferenceMetadata(final Class<?> beanClass) {

        final List<InjectionMetadata.InjectedElement> elements = new LinkedList<InjectionMetadata.InjectedElement>();

        ReflectionUtils.doWithMethods(beanClass, new ReflectionUtils.MethodCallback() {
            @Override
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {

                Method bridgedMethod = findBridgedMethod(method);

                if (!isVisibilityBridgeMethodPair(method, bridgedMethod)) {
                    return;
                }

                Consumer consumer = findAnnotation(bridgedMethod, Consumer.class);

                if (consumer != null && method.equals(ClassUtils.getMostSpecificMethod(method, beanClass))) {
                    if (Modifier.isStatic(method.getModifiers())) {
                        if (logger.isWarnEnabled()) {
                            logger.warn("@Reference annotation is not supported on static methods: " + method);
                        }
                        return;
                    }
                    if (method.getParameterTypes().length == 0) {
                        if (logger.isWarnEnabled()) {
                            logger.warn("@Reference  annotation should only be used on methods with parameters: " +
                                    method);
                        }
                    }
                    PropertyDescriptor pd = BeanUtils.findPropertyForMethod(bridgedMethod, beanClass);
                    elements.add(new ConsumerMethodElement(method, pd, consumer));
                }
            }
        });

        return elements;

    }


    private class ConsumerFieldElement extends InjectionMetadata.InjectedElement {

        private final Field field;

        private final Consumer consumer;

        protected ConsumerFieldElement(Field field, Consumer consumer) {
            super(field, null);
            this.field = field;
            this.consumer = consumer;
        }

        @Override
        protected void inject(Object bean, String beanName, PropertyValues pvs) throws Throwable {

            Class<?> referenceClass = field.getType();

            Object referenceBean = buildReferenceBean(consumer, referenceClass);

            ReflectionUtils.makeAccessible(field);

            field.set(bean, referenceBean);

        }

    }


    private class ConsumerMethodElement extends InjectionMetadata.InjectedElement {

        private final Method method;

        private final Consumer consumer;

        protected ConsumerMethodElement(Method method, PropertyDescriptor pd, Consumer consumer) {
            super(method, pd);
            this.method = method;
            this.consumer = consumer;
        }

        @Override
        protected void inject(Object bean, String beanName, PropertyValues pvs) throws Throwable {

            Class<?> referenceClass = pd.getPropertyType();

            Object referenceBean = buildReferenceBean(consumer, referenceClass);

            ReflectionUtils.makeAccessible(method);

            method.invoke(bean, referenceBean);

        }

    }


    private Object buildReferenceBean(Consumer consumer, Class<?> referenceClass) throws Exception {

        String consumerBeanCacheKey = generateConsumerBeanCacheKey(consumer, referenceClass);

        ConsumerBean<?> consumerBean = consumerBeanCache.get(consumerBeanCacheKey);

        if (consumerBean == null) {
            ConsumerBeanBuilder beanBuilder = ConsumerBeanBuilder
                    .create(consumer, classLoader, applicationContext)
                    .interfaceClass(referenceClass);

            consumerBean = beanBuilder.build();

            consumerBeanCache.putIfAbsent(consumerBeanCacheKey, consumerBean);

        }


        return consumerBean.getObject();
    }

    /**
     * Generate a cache key of {@link ConsumerBean}
     *
     * @param beanClass {@link Class}
     *
     */
    private static String generateConsumerBeanCacheKey(Consumer consumer, Class<?> beanClass) {
        return resolveInterfaceName(consumer, beanClass);
    }

    private static String resolveInterfaceName(Consumer consumer, Class<?> beanClass)
            throws IllegalStateException {

        String interfaceName;
        if (!"".equals(consumer.interfaceName())) {
            interfaceName = consumer.interfaceName();
        } else if (!void.class.equals(consumer.interfaceClass())) {
            interfaceName = consumer.interfaceClass().getName();
        } else if (beanClass.isInterface()) {
            interfaceName = beanClass.getName();
        } else {
            throw new IllegalStateException(
                    "The @Reference undefined interfaceClass or interfaceName, and the property type "
                            + beanClass.getName() + " is not a interface.");
        }

        return interfaceName;

    }


    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void destroy() throws Exception {
        for (ConsumerBean consumerBean : consumerBeanCache.values()) {
            logger.info(consumerBean + " was destroying!");
            consumerBean.destroy();
        }

        injectionMetadataCache.clear();
        consumerBeanCache.clear();

        logger.info(getClass() + " was destroying!");
    }

    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        if (beanType != null) {
            InjectionMetadata metadata = findConsumerMetadata(beanName, beanType, null);
            metadata.checkConfigMembers(beanDefinition);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }
}
