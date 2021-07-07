package by.kulik.data;

import by.kulik.annotation.Inject;

public class FirstInterfaceImplWithTwoInjectConstructor implements FirstInterface {
    @Inject
    public FirstInterfaceImplWithTwoInjectConstructor(SecondInterface secondInterface) {
    }

    @Inject
    public FirstInterfaceImplWithTwoInjectConstructor(FirstInterface firstInterface,SecondInterface secondInterface) {
    }
}
