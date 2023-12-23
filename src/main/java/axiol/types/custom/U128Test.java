package axiol.types.custom;

public class U128Test {
    public static void main(String[] args) {
        U128 first  = new U128("100000000000000000000000000000");
        U128 second = new U128("555555555555555555555555555555");
        U128 third  = new U128("222222222222222222222222222222");
        U128 fourth  = new U128("20");
        System.out.printf("%s + %s = %s%n", first, second, first.add(second));
        System.out.printf("%s / %s = %s%n", second, third, second.divide(third));
        System.out.printf("%s * %s = %s%n", second, fourth, first.multiply(fourth));
    }

}
