package br.ce.wcaquino.servicos;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.entidades.exceptions.FilmesSemEstoqueException;
import br.ce.wcaquino.entidades.exceptions.LocadoraException;
import br.ce.wcaquino.runners.ParallelRunner;
import br.ce.wcaquino.utils.DataUtils;
import org.junit.*;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.mockito.internal.configuration.PowerMockitoSpyAnnotationEngine;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builders.FilmeBuilder.umFilmeSemEstoque;
import static br.ce.wcaquino.builders.LocacaoBuilder.umLocacao;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.matchers.MatchersProprios.*;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

@RunWith(ParallelRunner.class)
public class LocacaoServiceTeste {

    @InjectMocks
    @Spy
    private LocacaoService service;
    @Mock
    private SPCService spc;
    @Mock
    private LocacaoDAO dao;
    @Mock
    private EmailService email;

    @Rule
    public ErrorCollector error = new ErrorCollector();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        System.out.println("Iniciando 2");
    }

    @After
    public void tearDown() {
        System.out.println("Finalizando...");
    }

    @Test
    public void deveAlugarFilmeComSucesso() throws Exception {
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().comValor(5.0).agora());

        Mockito.doReturn(DataUtils.obterData(28, 4, 2017)).when(service).obterData();
        Locacao locacao = service.alugarFilme(usuario, filmes);

        error.checkThat(locacao.getValor(), is(equalTo(5.0)));
        error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), DataUtils.obterData(28, 4, 2017)), is(true));
        error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterData(29, 4, 2017)), is(true));
    }

    @Test(expected = FilmesSemEstoqueException.class)
    public void naoDeveAlugarFilmeSemEstoque_TesteUm() throws Exception {
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilmeSemEstoque().agora());

        service.alugarFilme(usuario, filmes);
    }

    @Test
    public void naoDeveAlugarFilmeSemEstoque_TesteDois() {
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilmeSemEstoque().agora());

        try {
            service.alugarFilme(usuario, filmes);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Test
    public void naoDeveAlugarFilmeSemEstoque_TesteTres() {
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilmeSemEstoque().agora());

        try {
            service.alugarFilme(usuario, filmes);
            fail("Deveria ter lançado uma exceção");
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Test
    public void naoDeveAlugarFilmeSemEstoque_TesteQuatro() throws LocadoraException, FilmesSemEstoqueException {
        List<Filme> filmes = Arrays.asList(umFilmeSemEstoque().agora());
        Usuario usuario = umUsuario().agora();

        exception.expect(FilmesSemEstoqueException.class);

        service.alugarFilme(usuario, filmes);
    }

    @Test
    public void naoDeveAlugarFilmeSemUsuario() throws FilmesSemEstoqueException {
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        try {
            service.alugarFilme(null, filmes);
            fail();
        } catch (LocadoraException e) {
            assertThat(e.getMessage(), is("Usuário vazio"));
        }
    }

    @Test
    public void naoDeveAlugarFilmeSemFilme() throws LocadoraException, FilmesSemEstoqueException {
        Usuario usuario = umUsuario().agora();

        exception.expect(LocadoraException.class);
        exception.expectMessage("Filme vazio");

        service.alugarFilme(usuario, null);
    }

    @Test
    public void devePagar75PctNoFilme3() throws LocadoraException, FilmesSemEstoqueException {
        //Cenário
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0), new Filme("Filme 2", 2, 4.0),
                new Filme("Filme 3", 2, 4.0));
        //Ação
        Locacao resultado = service.alugarFilme(usuario, filmes);
        //Verificação
        assertThat(resultado.getValor(), is(11.0));
    }

    @Test
    public void devePagar50PctNoFilme4() throws LocadoraException, FilmesSemEstoqueException {
        //Cenário
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0), new Filme("Filme 2", 2, 4.0),
                new Filme("Filme 3", 2, 4.0), new Filme("Filme 4", 2, 4.0));
        //Ação
        Locacao resultado = service.alugarFilme(usuario, filmes);
        //Verificação
        assertThat(resultado.getValor(), is(13.0));
    }

    @Test
    public void devePagar25PctNoFilme5() throws LocadoraException, FilmesSemEstoqueException {
        //Cenário
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0), new Filme("Filme 2", 2, 4.0),
                new Filme("Filme 3", 2, 4.0), new Filme("Filme 4", 2, 4.0),
                new Filme("Filme 5", 2, 4.0));
        //Ação
        Locacao resultado = service.alugarFilme(usuario, filmes);
        //Verificação
        assertThat(resultado.getValor(), is(14.0));
    }

    @Test
    public void devePagar0PctNoFilme6() throws LocadoraException, FilmesSemEstoqueException {
        //Cenário
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0), new Filme("Filme 2", 2, 4.0),
                new Filme("Filme 3", 2, 4.0), new Filme("Filme 4", 2, 4.0),
                new Filme("Filme 5", 2, 4.0), new Filme("Filme 6", 2, 4.0));
        //Ação
        Locacao resultado = service.alugarFilme(usuario, filmes);
        //Verificação
        assertThat(resultado.getValor(), is(14.0));
    }

    @Test
    public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception {
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        Mockito.doReturn(DataUtils.obterData(29, 4, 2017)).when(service).obterData();

        Locacao retorno = service.alugarFilme(usuario, filmes);
        assertThat(retorno.getDataRetorno(), caiNumaSegunda());
    }

    @Test
    public void naoDeveAlugarFilmeParaNegativadoSPC() throws Exception {
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        when(spc.possuiNegativacao(Mockito.any(Usuario.class))).thenReturn(true);

        try {
            service.alugarFilme(usuario, filmes);
            Assert.fail();
        } catch (LocadoraException e) {
            Assert.assertThat(e.getMessage(), is("Usuário Negativado"));
        }

        verify(spc).possuiNegativacao(usuario);
    }

    @Test
    public void deveEnviarEmailParaLocacoesAtrasadasOne() {
        Usuario usuario = umUsuario().agora();
        List<Locacao> locacoes = Arrays.asList(
                umLocacao()
                        .comUsuario(usuario)
                        .comDataRetorno(obterDataComDiferencaDias(-2))
                        .agora());
        when(dao.obterLocacoesPendentes()).thenReturn(locacoes);

        service.notificarAtrasos();

        verify(email).notificarAtraso(usuario);
    }

    @Test
    public void deveEnviarEmailParaLocacoesAtrasadasTwo() {
        Usuario usuario = umUsuario().agora();
        Usuario usuario2 = umUsuario().comNome("Usuário em dia").agora();
        Usuario usuario3 = umUsuario().comNome("Outro usuário").agora();
        List<Locacao> locacoes = Arrays.asList(
                umLocacao()
                        .atrasado().comUsuario(usuario).agora(),
                umLocacao().comUsuario(usuario2).agora(),
                umLocacao().atrasado().comUsuario(usuario3).agora(),
                umLocacao().atrasado().comUsuario(usuario3).agora());
        when(dao.obterLocacoesPendentes()).thenReturn(locacoes);

        service.notificarAtrasos();

        verify(email, times(3)).notificarAtraso(Mockito.any(Usuario.class));
        verify(email).notificarAtraso(usuario);
        verify(email, atLeastOnce()).notificarAtraso(usuario3);
        verify(email, never()).notificarAtraso(usuario2);
        verifyNoMoreInteractions(email);
    }

    @Test
    public void deveTratarErrosNoSPC() throws Exception {
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        when(spc.possuiNegativacao(usuario)).thenThrow(new Exception("Falha catastrófica!"));

        exception.expect(LocadoraException.class);
        exception.expectMessage("Problemas com SPC, tente novamente");

        service.alugarFilme(usuario, filmes);
    }

    @Test
    public void deveProrrogarUmaLocacao() {
        Locacao locacao = umLocacao().agora();

        service.prorrogarLocacao(locacao, 3);

        ArgumentCaptor<Locacao> argumentCaptor = ArgumentCaptor.forClass(Locacao.class);
        Mockito.verify(dao).salvar(argumentCaptor.capture());
        Locacao locacaoRetornada = argumentCaptor.getValue();

        error.checkThat(locacaoRetornada.getValor(), is(12.0));
        error.checkThat(locacaoRetornada.getDataLocacao(), ehHoje());
        error.checkThat(locacaoRetornada.getDataRetorno(), ehHojeComDiferencaDias(3));
    }

    @Test
    public void deveCalcularValorLocacao() throws Exception {
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        Class<LocacaoService> clazz = LocacaoService.class;
        Method metodo = clazz.getDeclaredMethod("calcularValorLocacao", List.class);
        metodo.setAccessible(true);
        Double valor = (Double) metodo.invoke(service, filmes);

        Assert.assertThat(valor, is(4.0));
    }
}
