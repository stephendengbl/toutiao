package com.heima.password.test;

public class IntegerTest {
    public static void main(String[] args) {
        Integer i1 = new Integer(1);
        Integer i2 = new Integer(1);

        System.out.println("i1==i2:" + (i1 == i2));
        System.out.println("i1.equals(i2):" + i1.equals(i2));

        Integer i3 = 10;
        Integer i4 = 10;
        System.out.println("i3==i4:" + (i3 == i4));

        Integer i5 = 130;
        Integer i6 = 130;
        System.out.println("i5==i6:" + (i5 == i6));
    }
}
