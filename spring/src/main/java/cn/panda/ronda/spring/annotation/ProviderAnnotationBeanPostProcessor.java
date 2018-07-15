package cn.panda.ronda.spring.annotation;

import cn.panda.ronda.spring.config.ProviderBean;
import cn.panda.ronda.spring.schema.RondaBeanDefinitionParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.beans.factory.support.*;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.springframework.context.annotation.AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR;

/**
 * @author yongkang.zhang
 * created at 15/07/2018
 */
public class ProviderAnnotationBeanPostProcessor implements BeanDefinitionRegistryPostProcessor, EnvironmentAware,
        ResourceLoaderAware, BeanClassLoaderAware {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Set<String> packagesToScan;

    private Environment environment;

    private ClassLoader classLoader;

    private ResourceLoader resourceLoader;

    public ProviderAnnotationBeanPostProcessor(String... packagesToScan) {
        this(Arrays.asList(packagesToScan));
    }

    public ProviderAnnotationBeanPostProcessor(Collection<String> packageToScan) {
        this(new LinkedHashSet<>(packageToScan));
    }

    public ProviderAnnotationBeanPostProcessor(Set<String> packagesToScan) {
        this.packagesToScan = packagesToScan;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        Set<String> resolvePackagesToScan = resolvePackagesToScan(packagesToScan);

        if (!CollectionUtils.isEmpty(resolvePackagesToScan)) {

        } else {
            logger.warn("packagesToScan is empty, ServiceBean registry will be ignored!");
        }

    }


    /**
     * Register Beans whose classes was annotated
     * @param packagesToScan
     * @param registry
     */
    private void registerProviderBeans(Set<String> packagesToScan, BeanDefinitionRegistry registry) {
        RondaBeanPathScanner rondaBeanPathScanner = new RondaBeanPathScanner(registry, environment, resourceLoader);

        BeanNameGenerator beanNameGenerator = resolveBeanNameGenerator(registry);

        rondaBeanPathScanner.setBeanNameGenerator(beanNameGenerator);

        rondaBeanPathScanner.addIncludeFilter(new AnnotationTypeFilter(Provider.class));

        for (String packageToScan : packagesToScan) {
            // Registers @Service Bean first
            rondaBeanPathScanner.scan(packageToScan);

            // Find all BeanDefinitionHolders of @Service whether @ComponentScan scans or not.
            Set<BeanDefinitionHolder> beanDefinitionHolders = findProviderBeanHolders(rondaBeanPathScanner, packageToScan, registry, beanNameGenerator);

            if (!CollectionUtils.isEmpty(beanDefinitionHolders)) {

                for (BeanDefinitionHolder beanDefinitionHolder : beanDefinitionHolders) {
                    registerProviderBean(beanDefinitionHolder, registry, rondaBeanPathScanner);
                }

                logger.info(beanDefinitionHolders.size() + "annotated Ronda's @Provider Components { " +
                            beanDefinitionHolders + " } were scanned under package[" + packagesToScan + "]");
            } else {
                logger.warn("No SpringBean annotating Ronda's @Provider was found under package[" + packageToScan + "]");
            }
        }
    }

    private BeanNameGenerator resolveBeanNameGenerator(BeanDefinitionRegistry registry) {
        BeanNameGenerator beanNameGenerator = null;

        if (registry instanceof SingletonBeanRegistry) {
            SingletonBeanRegistry singletonBeanRegistry = SingletonBeanRegistry.class.cast(registry);
            beanNameGenerator = (BeanNameGenerator) singletonBeanRegistry.getSingleton(CONFIGURATION_BEAN_NAME_GENERATOR);
        }

        if (beanNameGenerator == null) {
            logger.info("BeanNameGenerator bean can't be found in BeanFactory with name [" + CONFIGURATION_BEAN_NAME_GENERATOR + "]");
            logger.info("BeanNameGenerator will be a instance of " + AnnotationBeanNameGenerator.class.getName() +
            ", it maybe a potential problem on bean name generation.");

            beanNameGenerator = new AnnotationBeanNameGenerator();
        }

        return beanNameGenerator;
    }

    private Set<BeanDefinitionHolder> findProviderBeanHolders(
            ClassPathBeanDefinitionScanner scanner, String packageToScan, BeanDefinitionRegistry beanDefinitionRegistry,
            BeanNameGenerator beanNameGenerator) {
        Set<BeanDefinition> beanDefinitions = scanner.findCandidateComponents(packageToScan);

        LinkedHashSet<BeanDefinitionHolder> beanDefinitionHolders = new LinkedHashSet<>(beanDefinitions.size());

        for (BeanDefinition beanDefinition : beanDefinitions) {
            String beanName = beanNameGenerator.generateBeanName(beanDefinition, beanDefinitionRegistry);
            BeanDefinitionHolder beanDefinitionHolder = new BeanDefinitionHolder(beanDefinition, beanName);
            beanDefinitionHolders.add(beanDefinitionHolder);
        }

        return beanDefinitionHolders;
    }


    private Set<String> resolvePackagesToScan(Set<String> packagesToScan) {
        Set<String> resolvePackagesToScan = new LinkedHashSet<>(packagesToScan);
        for (String packageToScan : packagesToScan) {
            String resolvePackageToScan = environment.resolvePlaceholders(packageToScan.trim());
            resolvePackagesToScan.add(resolvePackageToScan);
        }

        return resolvePackagesToScan;
    }

    private void registerProviderBean(BeanDefinitionHolder beanDefinitionHolder, BeanDefinitionRegistry beanDefinitionRegistry,
                                      RondaBeanPathScanner scanner) {

        Class<?> beanClass = resolveClass(beanDefinitionHolder);

        Provider provider = AnnotationUtils.findAnnotation(beanClass, Provider.class);

        Class<?> interfaceClass = resolveInterfaceClass(beanClass, provider);

        String annotatedProviderBeanName = beanDefinitionHolder.getBeanName();

        AbstractBeanDefinition providerBeanDefinition = buildProviderBeanDefinition(provider, interfaceClass, annotatedProviderBeanName);

        // ServiceBean Bean name
        String beanName = generateServiceBeanName(interfaceClass, annotatedProviderBeanName);

        if (scanner.checkCandidate(beanName, providerBeanDefinition)) {
            beanDefinitionRegistry.registerBeanDefinition(beanName, providerBeanDefinition);

            logger.warn("The BeanDefinition[" + providerBeanDefinition +
                        "] of ProviderBean has been registered with name: " + beanName);
        } else {
            logger.warn("The Duplicated BeanDefinition[" + providerBeanDefinition +
                        "] of ProviderBean [ bean name : " + beanName +
                        "] was found, Did @RondaComponentScan scan to same package in many times?");
        }
    }

    private Class<?> resolveInterfaceClass(Class<?> annotatedProviderBeanClass, Provider provider) {
        Class<?> interfaceClass = provider.interfaceClass();

        Assert.notNull(interfaceClass, "@Provider interfaceClass() must be present!");
        Assert.isTrue(interfaceClass.isInterface(), "The type that was annotated @Provider is not an interface");

        return interfaceClass;
    }

    private String generateServiceBeanName(Class<?> interfaceClass, String annotatedProviderBeanName) {
        return "ProviderBean@" + interfaceClass.getName() + "#" + annotatedProviderBeanName;
    }


    private AbstractBeanDefinition buildProviderBeanDefinition(Provider provider, Class<?> interfaceClass,
                                                              String annotatedProviderBeanName) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(ProviderBean.class)
                .addConstructorArgValue(provider)
                .addPropertyReference("ref", annotatedProviderBeanName)
                .addPropertyReference("interface", interfaceClass.getName());


        // add id
        String id = provider.id();
        if (!StringUtils.isEmpty(id)) {
            builder.addPropertyValue("id", id);
        }

        // add protocols todo 具体实现
        String[] protocols = provider.protocols();

        return builder.getBeanDefinition();
    }


    private Class<?> resolveClass(BeanDefinitionHolder beanDefinitionHolder) {
        BeanDefinition beanDefinition = beanDefinitionHolder.getBeanDefinition();
        return resolveClass(beanDefinition);
    }

    private Class<?> resolveClass(BeanDefinition beanDefinition) {
        String beanClassName = beanDefinition.getBeanClassName();
        return ClassUtils.resolveClassName(beanClassName, classLoader);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
