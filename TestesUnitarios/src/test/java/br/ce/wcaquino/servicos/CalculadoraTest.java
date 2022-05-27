package br.ce.wcaquino.servicos;

import br.ce.wcaquino.entidades.exceptions.NaoPodeDividirPorZeroException;
import br.ce.wcaquino.runners.ParallelRunner;
import br.ce.wcaquino.servicos.Calculadora.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(ParallelRunner.class)
public class CalculadoraTest {

    private Calculadora calc;

    @Before
    public void setup() {
        calc = new Calculadora();
        System.out.println("Iniciando...");
    }

    @After
    public void tearDown() {
        System.out.println("Finalizando...");
    }

    @Test
    public void deveSomarDoisValores() {
        //Cenário
        int a = 5;
        int b = 3;

        //Ação
        int resultado = calc.somar(a, b);

        //Verificação
        Assert.assertEquals(8, resultado);
    }

    @Test
    public void deveSubtrairDoisValores() {
        int a = 8;
        int b = 5;

        int resultado = calc.subtrair(a, b);

        Assert.assertEquals(3, resultado);
    }

    @Test
    public void deveMultiplicarDoisValores() {
        int a = 10;
        int b = 5;

        int resultado = calc.multiplicar(a, b);

        Assert.assertEquals(50, resultado);
    }

    @Test
    public void deveDividirDoisNumeros() throws NaoPodeDividirPorZeroException {
        int a = 6;
        int b = 3;

        int resultado = calc.dividir(a, b);

        Assert.assertEquals(2, resultado);
    }

    @Test(expected = NaoPodeDividirPorZeroException.class)
    public void deveLancarExcecaoAoDividirPorZero() throws NaoPodeDividirPorZeroException {
        int a = 10;
        int b = 0;

        calc.dividir(a, b);
    }

    @Test
    public void deveDividir() {
        String a = "6";
        String b = "3";

        int resultado = calc.divide(a, b);

        Assert.assertEquals(2, resultado);
    }
}
