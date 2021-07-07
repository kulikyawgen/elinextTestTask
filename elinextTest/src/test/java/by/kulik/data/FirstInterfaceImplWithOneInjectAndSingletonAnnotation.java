package by.kulik.data;

import by.kulik.annotation.Inject;
import by.kulik.annotation.Singleton;

@Singleton
public class FirstInterfaceImplWithOneInjectAndSingletonAnnotation implements FirstInterface {

    @Inject
    public FirstInterfaceImplWithOneInjectAndSingletonAnnotation(SecondInterface secondInterface) {
    }
}
