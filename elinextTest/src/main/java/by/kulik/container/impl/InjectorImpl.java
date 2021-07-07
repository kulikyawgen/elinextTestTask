package by.kulik.container.impl;

import by.kulik.annotation.Inject;
import by.kulik.container.Injector;
import by.kulik.container.Provider;
import by.kulik.exceptions.BindingNotFoundException;
import by.kulik.exceptions.ConstructorNotFoundException;
import by.kulik.exceptions.TooManyConstructorsException;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InjectorImpl implements Injector {
    private static final String BINDING_NOT_FOUND_MESSAGE_EXC = "Not found implementation for %s";
    private static final String TOO_MANY_CONSTR_MESSAGE_EXC = "The class %s have several constructor with annotated @Inject";
    private static final String CONSTR_NOT_FOUND_MESSAGE_EXC = "The class %s haven`t a default constructor";
    private final Map<Class, Class> beanBindings;
    private final Map<Class, Provider> context;

    public InjectorImpl() {
        beanBindings = new ConcurrentHashMap<>();
        context = new ConcurrentHashMap<>();
    }

    public <T> Provider<T> getProvider(Class<T> type) {

        if (!beanBindings.containsKey(type)) {
            return null;
        }

        if (context.containsKey(type)) {
            return (Provider<T>) context.get(type);
        }

        final Provider<T> provider = createProvider(type);

        context.put(type, provider);

        return provider;
    }

    private <T> Provider<T> createProvider(Class<T> type) {

        Constructor constructor = getDefaultOrWhithInjectAnnotationConstructor(beanBindings.get(type));
        Class[] parameterizedTypes = constructor.getParameterTypes();
        Provider<T> provider;

        if (parameterizedTypes.length != 0) {
            for (Class parameterizedType : parameterizedTypes) {
                if (!beanBindings.containsKey(parameterizedType)) {
                    throw new BindingNotFoundException(String.format(BINDING_NOT_FOUND_MESSAGE_EXC, type));
                }
            }

            final List<Object> instancesOfParameterizedTypes = new ArrayList<Object>();

            for (Class parameterizedType : parameterizedTypes) {
                instancesOfParameterizedTypes.add(getProvider(parameterizedType).getInstance());
            }

            provider = new ProviderImpl(constructor, instancesOfParameterizedTypes.toArray());
        } else {
            provider = new ProviderImpl(constructor, null);
        }

        return provider;
    }

    private <T> Constructor getDefaultOrWhithInjectAnnotationConstructor(Class<? extends T> impl)
            throws TooManyConstructorsException, ConstructorNotFoundException {

        final Constructor[] existingConstructors = impl.getConstructors();

        return Optional.ofNullable(getConstructorWithAnnotationInject(existingConstructors))
                .orElseGet(() -> getDefaultConstructor(existingConstructors));
    }

    private Constructor getConstructorWithAnnotationInject(Constructor[] existingConstructors) throws TooManyConstructorsException {

        Constructor oneInjectConstructor = null;

        int constructorsWithInjectAnnotation = 0;
        for (Constructor currentConstructor : existingConstructors) {
            if (currentConstructor.isAnnotationPresent(Inject.class)) {
                constructorsWithInjectAnnotation++;
                if (constructorsWithInjectAnnotation > 1) {
                    throw new TooManyConstructorsException(
                            String.format(TOO_MANY_CONSTR_MESSAGE_EXC,
                                    currentConstructor.getClass()));
                }
                oneInjectConstructor = currentConstructor;
            }
        }
        return oneInjectConstructor;
    }

    private Constructor getDefaultConstructor(Constructor[] existingConstructors) throws ConstructorNotFoundException {
        Optional<Constructor> defaultConstructor = Arrays.stream(existingConstructors)
                .filter(constr -> constr.getParameterTypes().length == 0).findFirst();
        return defaultConstructor.orElseThrow(() ->
                new ConstructorNotFoundException(String.format(CONSTR_NOT_FOUND_MESSAGE_EXC, existingConstructors[0].getDeclaringClass()))
        );
    }

    public <T> void bind(Class<T> intf, Class<? extends T> impl) {
        getDefaultOrWhithInjectAnnotationConstructor(impl);
        beanBindings.put(intf, impl);
    }

    public <T> void bindSingleton(Class<T> intf, Class<? extends T> impl) {
        getDefaultOrWhithInjectAnnotationConstructor(impl);
        beanBindings.put(intf, impl);
    }
}
