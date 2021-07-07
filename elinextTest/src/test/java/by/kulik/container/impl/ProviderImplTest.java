package by.kulik.container.impl;

import by.kulik.container.Injector;
import by.kulik.container.Provider;
import by.kulik.data.FirstInterface;
import by.kulik.data.FirstInterfaceImplWithDefaultConstructor;
import by.kulik.data.FirstInterfaceImplWithOneInjectAndSingletonAnnotation;
import by.kulik.data.FirstInterfaceImplWithOneParameterizedConstructor;
import by.kulik.data.FirstInterfaceImplWithTwoInjectConstructor;
import by.kulik.data.SecondInterface;
import by.kulik.data.SecondInterfaceImpl;
import by.kulik.exceptions.ConstructorNotFoundException;
import by.kulik.exceptions.TooManyConstructorsException;
import org.junit.Test;

import static org.junit.Assert.*;

public class ProviderImplTest {


    @Test(expected = ConstructorNotFoundException.class)
    public void testGetDefaultConstructorExpectException() {
        Injector injector = new InjectorImpl();
        injector.bind(FirstInterface.class, FirstInterfaceImplWithOneParameterizedConstructor.class);
    }

    @Test(expected = TooManyConstructorsException.class)
    public void testGetOneInjectConstructorExpectException() {
        Injector injector = new InjectorImpl();
        injector.bind(FirstInterface.class, FirstInterfaceImplWithTwoInjectConstructor.class);
    }

    @Test
    public void testGetProviderExpectNotSingletonScope(){
        Injector injector = new InjectorImpl();
        injector.bindSingleton(FirstInterface.class, FirstInterfaceImplWithDefaultConstructor.class);
        injector.bindSingleton(FirstInterface.class, FirstInterfaceImplWithDefaultConstructor.class);

        Provider<FirstInterface> provider = injector.getProvider(FirstInterface.class);
        final FirstInterface instance1 = provider.getInstance();
        final FirstInterface instance2 = provider.getInstance();

        assertNotNull(instance1);
        assertNotEquals(instance1, instance2);
    }
 @Test
    public void testGetProviderExpectSingletonScope(){
        Injector injector = new InjectorImpl();
        injector.bindSingleton(FirstInterface.class, FirstInterfaceImplWithOneInjectAndSingletonAnnotation.class);
        injector.bindSingleton(SecondInterface.class, SecondInterfaceImpl.class);

        Provider<FirstInterface> provider = injector.getProvider(FirstInterface.class);
        final FirstInterface instance1 = provider.getInstance();
        final FirstInterface instance2 = provider.getInstance();

        assertNotNull(instance1);
        assertEquals(instance1, instance2);
    }

}