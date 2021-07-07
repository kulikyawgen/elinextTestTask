package by.kulik.container.impl;

import by.kulik.annotation.Singleton;
import by.kulik.container.Provider;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ProviderImpl<T> implements Provider<T> {
    private final Constructor<T> constructor;
    private final Object[] newInstanceParameters;
    private T instance = null;

    public ProviderImpl(Constructor<T> constructor, Object[] newInstanceParameters) {
        this.constructor = constructor;
        this.newInstanceParameters = newInstanceParameters;
    }

    public T getInstance() {
        if (instance == null) {
            instance = createNewInstance();
            return instance;
        } else if (instance.getClass().isAnnotationPresent(Singleton.class)) {
            return instance;
        } else {
            return createNewInstance();
        }
    }

    private T createNewInstance(){

        T newInstance = null;
        try {
            newInstance =constructor.newInstance(newInstanceParameters);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return newInstance;
    }
}
