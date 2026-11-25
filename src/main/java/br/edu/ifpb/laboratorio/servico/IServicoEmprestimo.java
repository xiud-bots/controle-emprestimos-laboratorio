package br.edu.ifpb.laboratorio.servico;

import br.edu.ifpb.laboratorio.modelo.entidade.Emprestimo;
import br.edu.ifpb.laboratorio.modelo.entidade.Item;
import br.edu.ifpb.laboratorio.modelo.entidade.Pessoa;
import br.edu.ifpb.laboratorio.modelo.excecao.FormatoArquivoInvalidoException;
import br.edu.ifpb.laboratorio.modelo.excecao.ItemJaEmprestadoException;
import br.edu.ifpb.laboratorio.modelo.excecao.ItemNaoEncontradoException;
import br.edu.ifpb.laboratorio.modelo.excecao.PessoaNaoEncontradaException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Interface de Servico que define os contratos de operacao do sistema.
 * Atende aos requisitos:
 * - 4.5: Inversao de Dependencia e desacoplamento.
 * - 4.8: Baixo acoplamento (a interface grafica chama o servico atraves desta interface).
 */
public interface IServicoEmprestimo {

    void carregarDados() throws IOException, FormatoArquivoInvalidoException;

    Item cadastrarItem(Item item) throws IOException;

    Item cadastrarItem(String tipo, String patrimonio, String descricao) throws IOException, FormatoArquivoInvalidoException;

    Emprestimo registrarEmprestimo(String patrimonio, String matricula)
            throws ItemJaEmprestadoException, ItemNaoEncontradoException, PessoaNaoEncontradaException;

    Emprestimo registrarEmprestimo(String patrimonio, String matricula, LocalDate dataEmprestimo)
            throws ItemJaEmprestadoException, ItemNaoEncontradoException, PessoaNaoEncontradaException;

    Emprestimo registrarDevolucao(String patrimonio) throws ItemNaoEncontradoException;

    Emprestimo registrarDevolucao(String patrimonio, LocalDate dataDevolucao) throws ItemNaoEncontradoException;

    Item buscarItemPorPatrimonio(String patrimonio) throws ItemNaoEncontradoException;

    Pessoa buscarPessoaPorMatricula(String matricula) throws PessoaNaoEncontradaException;

    List<Item> getAcervo();

    List<Pessoa> getPessoas();

    List<Emprestimo> getEmprestimos();

    List<Item> listarItensDisponiveis();

    List<Item> listarItensEmprestados();

    List<Emprestimo> listarEmprestimosAtivos();

    double calcularTotalMultas();

    double calcularTotalMultas(LocalDate dataReferencia);

    int getQuantidadeTotalItens();

    int getQuantidadeItensDisponiveis();

    int getQuantidadeItensEmprestados();
}
