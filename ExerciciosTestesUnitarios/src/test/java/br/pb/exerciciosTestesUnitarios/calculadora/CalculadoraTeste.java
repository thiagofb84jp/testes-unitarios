package br.pb.exerciciosTestesUnitarios.calculadora;

import br.pb.exerciciosTestesUnitarios.calculadora.Calculadora;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class CalculadoraTeste {

    private Calculadora calculadora;

    @Before
    public void setup() {
        calculadora = new Calculadora();
    }

    @Test
    public void deveSomarDoisNumeros() {
        int a = 10;
        int b = 20;

        int resultado = calculadora.somar(10, 20);
        Assert.assertEquals(30, resultado);
    }

    @Test
    public void deveSubtrairDoisNumeros() {
        int a = 20;
        int b = 10;

        int resultado = calculadora.subtrair(a, b);
        Assert.assertEquals(10, resultado);
    }

    @Test
    public void deveSubtrairComUmValorNegativo() {
        int a = -20;
        int b = 10;

        int resultado = calculadora.subtrair(a, b);
        Assert.assertEquals(-30, resultado);
    }

    @Test
    public void deveSubtrairComDoisValoresNegativo() {
        int a = -20;
        int b = -10;

        int resultado = calculadora.subtrair(a, b);
        Assert.assertEquals(-10, resultado);
    }

    @Test
    public void deveMultiplicarDoisValores() {
        int a = 10;
        int b = 20;

        int resultado = calculadora.multiplicar(a, b);
        Assert.assertEquals(200, resultado);
    }

    @Test
    public void deveDividirDoisValores() {

    }

    @Test(expected = Exception.class)
    public void deveDividirPorZero() throws Exception {
        int a = 10;
        int b = 0;

        calculadora.dividir(a, b);
    }
}