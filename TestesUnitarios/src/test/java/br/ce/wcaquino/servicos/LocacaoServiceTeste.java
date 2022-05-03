package br.ce.wcaquino.servicos;

import br.ce.wcaquino.builders.FilmeBuilder;
import br.ce.wcaquino.builders.UsuarioBuilder;
import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.daos.LocacaoDAOFake;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.entidades.exceptions.FilmesSemEstoqueException;
import br.ce.wcaquino.entidades.exceptions.LocadoraException;
import br.ce.wcaquino.matchers.DiaSemanaMatcher;
import br.ce.wcaquino.matchers.MatchersProprios;
import br.ce.wcaquino.utils.DataUtils;
import buildermaster.BuilderMaster;
import org.junit.*;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.xml.crypto.Data;
import java.util.*;

import static br.ce.wcaquino.builders.FilmeBuilder.*;
import static br.ce.wcaquino.builders.UsuarioBuilder.*;
import static br.ce.wcaquino.matchers.MatchersProprios.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class LocacaoServiceTeste {

    private LocacaoService service;
    private SPCService spc;
    private LocacaoDAO dao;

    @Rule
    public ErrorCollector error = new ErrorCollector();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        service = new LocacaoService();
        dao = mock(LocacaoDAO.class);
        service.setLocacaoDAO(dao);

        spc = mock(SPCService.class);
        service.setSpcService(spc);
    }

    @Test
    public void deveAlugarFilmeComSucesso() throws Exception {
        Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().comValor(5.0).agora());

        Locacao locacao = service.alugarFilme(usuario, filmes);

        error.checkThat(locacao.getValor(), is(equalTo(5.0)));
        error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
        error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)), is(true));
//        error.checkThat(locacao.getDataRetorno(), ehHoje());
//        error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDias(1));
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

    @Ignore
    public void deveDevolverNaSegundaAoAlugarNoSabado() throws LocadoraException, FilmesSemEstoqueException {
        Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        Locacao retorno = service.alugarFilme(usuario, filmes);
        assertThat(retorno.getDataRetorno(), caiNumaSegunda());
    }

    @Test
    public void naoDeveAlugarFilmeParaNegativadoSPC() throws LocadoraException, FilmesSemEstoqueException {
        Usuario usuario = umUsuario().agora();
        Usuario usuario2 = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        when(spc.possuiNegativacao(usuario)).thenReturn(true);

        exception.expect(LocadoraException.class);
        exception.expectMessage("Usuário negativado");

        service.alugarFilme(usuario2, filmes);

        message();
    }

    public void message () {
        System.out.println("Here I have a message...");
    }
}
