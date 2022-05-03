package br.pb.exerciciosTestesUnitarios.calculadora;

public class Calculadora {

    private int a;
    private int b;
    private int total;

    public Calculadora(int a, int b) {
        this.a = a;
        this.b = b;
    }

    public Calculadora() {

    }

    public int somar(int a, int b) {
        total = a + b;
        return total;
    }

    public int subtrair(int a, int b) {
        total = a - b;
        return total;
    }

    public int multiplicar(int a, int b) {
        total = a * b;
        return total;
    }

    public int dividir(int a, int b) throws Exception {
        if (b == 0) {
            throw new Exception();
        }

        total = a / b;
        return total;
    }
}
