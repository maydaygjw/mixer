package org.gejunwen.mixer.generics;

import java.util.ArrayList;
import java.util.List;

public class Generics1 {

    private void inspect(List<Object> list) {
        for(Object o:list) {
            System.out.println(o);
        }
    }

    private void inspect2(List<?> list) {
        for(Object o:list) {
            System.out.println(o);
        }
    }

    private <T> T out(T... args) {
        for(T t: args) {
            System.out.println(t);
        }

        return args[0];
    }

    private void wildcard(List<? extends Number> list) {
        for(Number num: list) {
            System.out.println(num.intValue());
        }
    }

    public static void main(String[] args) {

        List<String> l1 = new ArrayList<>();
        List<Object> l2 = new ArrayList<>();
        List<Integer> l4 = new ArrayList<>();

        Generics1 g = new Generics1();

        //此行会报编译错误
//        new Generics1().inspect(l1);
        g.inspect(l2);

        //该行不会报错
        g.inspect2(l1);
        g.inspect2(l2);

        //可变参数，第二个参数起可以突破泛型限制
        String outResult = (String) g.out("findingsea", 123, 11.11, false);

        l4.add(1);
        l4.add(2);
        g.wildcard(l4);


    }
}
