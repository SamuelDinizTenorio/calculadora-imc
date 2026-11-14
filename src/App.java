import java.util.Scanner;

public class App {

    public static void main(String[] args) throws Exception {
        var scanner = new Scanner(System.in);

        System.out.print("Digite seu peso: ");
        double weight = Double.parseDouble(scanner.nextLine());

        System.out.print("Digite sua altura: ");
        double height = Double.parseDouble(scanner.nextLine());

        double imc = weight / (height * height);
        System.out.printf("Seu IMC: %.2f%n", imc);
        if (imc < 18.6) {
            System.out.println("Abaixo do peso");
        } else if (imc < 25) {
            System.out.println("Peso ideal");
        } else if (imc < 30) {
            System.out.println("Levemente acima do peso");
        } else if (imc < 35) {
            System.out.println("Obesidade Grau I");
        } else if (imc < 40) {
            System.out.println("Obesidade Grau II (Severa)");
        } else {
            System.out.println("Obesidade III (Mórbida)");
        }
        scanner.close();
    }
}
