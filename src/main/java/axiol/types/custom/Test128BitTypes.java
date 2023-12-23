package axiol.types.custom;

public class Test128BitTypes {
    public static void main(String[] args) {
        testUnsigned();
        testSigned();
    }

    public static void testUnsigned() {
        System.out.println("-".repeat(100));

        U128 first  = new U128("100000000000000000000000000000");
        U128 second = new U128("555555555555555555555555555555");
        U128 third  = new U128("222222222222222222222222222222");
        U128 fourth  = new U128("20");
        System.out.printf("%s + %s = %s%n", first, second, first.add(second));
        System.out.printf("%s / %s = %s%n", second, third, second.divide(third));
        System.out.printf("%s * %s = %s%n", second, fourth, first.multiply(fourth));
    }

    public static void testSigned() {
        System.out.println("-".repeat(100));
        I128 first = new I128(100);
        I128 second = new I128(200);

        System.out.println(first.subtract(second));
    }

}
