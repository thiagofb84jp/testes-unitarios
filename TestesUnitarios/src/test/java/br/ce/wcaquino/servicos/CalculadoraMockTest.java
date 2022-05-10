package br.ce.wcaquino.servicos;

import br.ce.wcaquino.entidades.Locacao;
import com.sun.security.jgss.GSSUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

public class CalculadoraMockTest {

    @Mock
    private Calculadora calcMock;

    @Spy
    private Calculadora calcSpy;

    @Mock
    private EmailService emailService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void devoMostrarDiferencaEntreMockSpy() {
        Mockito.when(calcMock.somar(1, 2)).thenReturn(5);
//        Mockito.when(calcSpy.somar(1, 2)).thenReturn(5);
        Mockito.doReturn(5).when(calcSpy).somar(1, 2);
        Mockito.doNothing().when(calcSpy).imprime();

        System.out.println("Mock: " + calcMock.somar(1, 2));
        System.out.println("Spy: " + calcSpy.somar(1, 2));

        System.out.println("Mock");
        calcMock.imprime();
        System.out.println("Spy");
        calcSpy.imprime();
    }

    @Test
    public void teste() {
        Calculadora calculadora = Mockito.mock(Calculadora.class);

        ArgumentCaptor<Integer> argumentCaptor = ArgumentCaptor.forClass(Integer.class);
        Mockito.when(calculadora.somar(argumentCaptor.capture(), argumentCaptor.capture())).thenReturn(5);

        Assert.assertEquals(5, calculadora.somar(1, 10000));
//        System.out.println(argumentCaptor.getAllValues());
    }
}
