package br.ce.wcaquino.builders;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Usuario;

import java.util.Arrays;
import java.lang.Double;
import java.util.Date;

import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.utils.DataUtils;

import static br.ce.wcaquino.builders.FilmeBuilder.*;
import static br.ce.wcaquino.builders.UsuarioBuilder.*;
import static br.ce.wcaquino.utils.DataUtils.*;
import static java.util.Arrays.*;


public class LocacaoBuilder {

    private Locacao elemento;

    private LocacaoBuilder() {

    }

    public static LocacaoBuilder umLocacao() {
        LocacaoBuilder builder = new LocacaoBuilder();
        inicializarDadosPadroes(builder);
        return builder;
    }

    public static void inicializarDadosPadroes(LocacaoBuilder builder) {
        builder.elemento = new Locacao();
        Locacao elemento = builder.elemento;

        elemento.setUsuario(umUsuario().agora());
        elemento.setFilmes(Arrays.asList(umFilme().agora()));
        elemento.setDataLocacao(new Date());
        elemento.setDataRetorno(obterDataComDiferencaDias(1));
        elemento.setValor(4.0);
    }

    public LocacaoBuilder comUsuario(Usuario param) {
        elemento.setUsuario(param);
        return this;
    }

    public LocacaoBuilder comListaFilmes(Filme... params) {
        elemento.setFilmes(Arrays.asList(params));
        return this;
    }

    public LocacaoBuilder comDataLocacao(Date param) {
        elemento.setDataLocacao(param);
        return this;
    }

    public LocacaoBuilder comDataRetorno(Date param) {
        elemento.setDataRetorno(param);
        return this;
    }

    public LocacaoBuilder atrasado() {
        elemento.setDataRetorno(obterDataComDiferencaDias(-4));
        elemento.setDataRetorno(obterDataComDiferencaDias(-2));
        return this;
    }

    public LocacaoBuilder comValor(Double param) {
        elemento.setValor(param);
        return this;
    }

    public Locacao agora() {
        return elemento;
    }
}