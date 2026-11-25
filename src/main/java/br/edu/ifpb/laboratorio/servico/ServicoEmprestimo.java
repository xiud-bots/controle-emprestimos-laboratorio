package br.edu.ifpb.laboratorio.servico;

import br.edu.ifpb.laboratorio.modelo.entidade.Emprestimo;
import br.edu.ifpb.laboratorio.modelo.entidade.Item;
import br.edu.ifpb.laboratorio.modelo.entidade.KitComponentes;
import br.edu.ifpb.laboratorio.modelo.entidade.Notebook;
import br.edu.ifpb.laboratorio.modelo.entidade.Pessoa;
import br.edu.ifpb.laboratorio.modelo.entidade.Projetor;
import br.edu.ifpb.laboratorio.modelo.excecao.FormatoArquivoInvalidoException;
import br.edu.ifpb.laboratorio.modelo.excecao.ItemJaEmprestadoException;
import br.edu.ifpb.laboratorio.modelo.excecao.ItemNaoEncontradoException;
import br.edu.ifpb.laboratorio.modelo.excecao.PessoaNaoEncontradaException;
import br.edu.ifpb.laboratorio.persistencia.IRepositorioAcervo;
import br.edu.ifpb.laboratorio.persistencia.IRepositorioPessoas;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Camada de Servico responsavel pela orquestracao das regras de negocio e gerenciamento das colecoes.
 * Atende aos requisitos:
 * - 4.1: Encapsulamento, metodos sobrecarregados.
 * - 4.2: Composicao e colecao como atributo interno.
 * - 4.3: Uso de ArrayList, busca por criterio, filtro de disponiveis e total acumulado de multas.
 *        A lista interna nunca e devolvida crua (Collections.unmodifiableList).
 * - 4.5: Injecao de Dependencia no construtor (recebe repositorios, nao da new).
 * - 4.6: Lancamento e propagacao de excecoes do dominio.
 * - 4.8: Alta coesao e baixo acoplamento.
 */
public class ServicoEmprestimo implements IServicoEmprestimo {

    private final IRepositorioAcervo repositorioAcervo;
    private final IRepositorioPessoas repositorioPessoas;

    // Colecoes em memoria gerenciadas via ArrayList (Requisito 4.3)
    private final List<Item> acervo = new ArrayList<>();
    private final List<Pessoa> pessoas = new ArrayList<>();
    private final List<Emprestimo> emprestimos = new ArrayList<>();

    /**
     * Construtor com Injecao de Dependencias (Requisito 4.5).
     * O servico nao instancia repositorios com new; recebe as interfaces pelo construtor.
     */
    public ServicoEmprestimo(IRepositorioAcervo repositorioAcervo, IRepositorioPessoas repositorioPessoas) {
        this.repositorioAcervo = Objects.requireNonNull(repositorioAcervo, "O repositorio de acervo nao pode ser nulo.");
        this.repositorioPessoas = Objects.requireNonNull(repositorioPessoas, "O repositorio de pessoas nao pode ser nulo.");
    }

    @Override
    public void carregarDados() throws IOException, FormatoArquivoInvalidoException {
        this.acervo.clear();
        this.pessoas.clear();
        this.emprestimos.clear();

        List<Item> itensCarregados = this.repositorioAcervo.carregar();
        this.acervo.addAll(itensCarregados);

        List<Pessoa> pessoasCarregadas = this.repositorioPessoas.carregar();
        this.pessoas.addAll(pessoasCarregadas);
    }

    @Override
    public Item cadastrarItem(Item item) throws IOException {
        if (item == null) {
            throw new IllegalArgumentException("O item a ser cadastrado nao pode ser nulo.");
        }

        // Valida duplicidade de patrimonio
        for (Item existente : this.acervo) {
            if (existente.getPatrimonio().equalsIgnoreCase(item.getPatrimonio())) {
                throw new IllegalArgumentException(
                        String.format("Ja existe um item cadastrado com o patrimonio '%s'.", item.getPatrimonio()));
            }
        }

        this.acervo.add(item);
        // Regrava o acervo de volta no arquivo CSV (Requisito 3.3)
        this.repositorioAcervo.salvar(this.acervo);
        return item;
    }

    /**
     * Metodo sobrecarregado de cadastro de item a partir de strings (Requisito 4.1).
     */
    @Override
    public Item cadastrarItem(String tipo, String patrimonio, String descricao)
            throws IOException, FormatoArquivoInvalidoException {
        if (tipo == null || tipo.trim().isEmpty()) {
            throw new IllegalArgumentException("O tipo de item nao pode ser nulo ou vazio.");
        }

        String tipoNormalizado = tipo.toUpperCase().replaceAll("[\\s_-]+", "");
        Item novoItem;

        if (tipoNormalizado.contains("NOTEBOOK")) {
            novoItem = new Notebook(patrimonio, descricao);
        } else if (tipoNormalizado.contains("PROJETOR")) {
            novoItem = new Projetor(patrimonio, descricao);
        } else if (tipoNormalizado.contains("KIT")) {
            novoItem = new KitComponentes(patrimonio, descricao);
        } else {
            throw new FormatoArquivoInvalidoException("Entrada", 0, tipo,
                    "Tipo de item invalido: " + tipo + ". Use NOTEBOOK, PROJETOR ou KIT_COMPONENTES.");
        }

        return cadastrarItem(novoItem);
    }

    /**
     * Metodo sobrecarregado 1: Registra emprestimo com a data atual.
     */
    @Override
    public Emprestimo registrarEmprestimo(String patrimonio, String matricula)
            throws ItemJaEmprestadoException, ItemNaoEncontradoException, PessoaNaoEncontradaException {
        return registrarEmprestimo(patrimonio, matricula, LocalDate.now());
    }

    /**
     * Metodo sobrecarregado 2: Registra emprestimo com data especifica.
     * Atende ao requisito 4.6 (ItemJaEmprestadoException).
     */
    @Override
    public Emprestimo registrarEmprestimo(String patrimonio, String matricula, LocalDate dataEmprestimo)
            throws ItemJaEmprestadoException, ItemNaoEncontradoException, PessoaNaoEncontradaException {
        Item item = buscarItemPorPatrimonio(patrimonio);
        Pessoa pessoa = buscarPessoaPorMatricula(matricula);

        // O metodo emprestar() da classe Item lanca ItemJaEmprestadoException se item.isEmprestado() == true
        item.emprestar();

        Emprestimo emprestimo = new Emprestimo(item, pessoa, dataEmprestimo);
        this.emprestimos.add(emprestimo);
        return emprestimo;
    }

    /**
     * Metodo sobrecarregado 1: Registra devolucao na data de hoje.
     */
    @Override
    public Emprestimo registrarDevolucao(String patrimonio) throws ItemNaoEncontradoException {
        return registrarDevolucao(patrimonio, LocalDate.now());
    }

    /**
     * Metodo sobrecarregado 2: Registra devolucao com data informada.
     */
    @Override
    public Emprestimo registrarDevolucao(String patrimonio, LocalDate dataDevolucao) throws ItemNaoEncontradoException {
        Item item = buscarItemPorPatrimonio(patrimonio);

        Emprestimo emprestimoAtivo = null;
        for (Emprestimo e : this.emprestimos) {
            if (e.isAtivo() && e.getItem().getPatrimonio().equalsIgnoreCase(patrimonio)) {
                emprestimoAtivo = e;
                break;
            }
        }

        if (emprestimoAtivo == null) {
            // Se o item estiver marcado como emprestado mas nao ha registro de emprestimo formal,
            // libera o item diretamente
            item.devolver();
            throw new IllegalArgumentException(
                    String.format("Nao ha registro de emprestimo em aberto para o item '%s'.", patrimonio));
        }

        emprestimoAtivo.registrarDevolucao(dataDevolucao);
        return emprestimoAtivo;
    }

    /**
     * Busca por criterio: localiza item por patrimonio (Requisito 4.3).
     */
    @Override
    public Item buscarItemPorPatrimonio(String patrimonio) throws ItemNaoEncontradoException {
        if (patrimonio == null || patrimonio.trim().isEmpty()) {
            throw new IllegalArgumentException("O patrimonio para busca nao pode ser vazio.");
        }
        for (Item item : this.acervo) {
            if (item.getPatrimonio().equalsIgnoreCase(patrimonio.trim())) {
                return item;
            }
        }
        throw new ItemNaoEncontradoException(patrimonio);
    }

    /**
     * Busca por criterio: localiza pessoa por matricula (Requisito 4.3).
     */
    @Override
    public Pessoa buscarPessoaPorMatricula(String matricula) throws PessoaNaoEncontradaException {
        if (matricula == null || matricula.trim().isEmpty()) {
            throw new IllegalArgumentException("A matricula para busca nao pode ser vazia.");
        }
        for (Pessoa pessoa : this.pessoas) {
            if (pessoa.getMatricula().equalsIgnoreCase(matricula.trim())) {
                return pessoa;
            }
        }
        throw new PessoaNaoEncontradaException(matricula);
    }

    /**
     * Encapsulamento: a lista interna nao e devolvida crua (Requisito 4.3).
     */
    @Override
    public List<Item> getAcervo() {
        return Collections.unmodifiableList(this.acervo);
    }

    @Override
    public List<Pessoa> getPessoas() {
        return Collections.unmodifiableList(this.pessoas);
    }

    @Override
    public List<Emprestimo> getEmprestimos() {
        return Collections.unmodifiableList(this.emprestimos);
    }

    /**
     * Filtro: retorna apenas os itens disponiveis no acervo (Requisito 4.3).
     */
    @Override
    public List<Item> listarItensDisponiveis() {
        List<Item> disponiveis = new ArrayList<>();
        for (Item item : this.acervo) {
            if (!item.isEmprestado()) {
                disponiveis.add(item);
            }
        }
        return Collections.unmodifiableList(disponiveis);
    }

    /**
     * Filtro: retorna apenas os itens atualmente emprestados (Requisito 4.3).
     */
    @Override
    public List<Item> listarItensEmprestados() {
        List<Item> emprestados = new ArrayList<>();
        for (Item item : this.acervo) {
            if (item.isEmprestado()) {
                emprestados.add(item);
            }
        }
        return Collections.unmodifiableList(emprestados);
    }

    /**
     * Filtro: retorna apenas os emprestimos ativos/em aberto (Requisito 4.3).
     */
    @Override
    public List<Emprestimo> listarEmprestimosAtivos() {
        List<Emprestimo> ativos = new ArrayList<>();
        for (Emprestimo e : this.emprestimos) {
            if (e.isAtivo()) {
                ativos.add(e);
            }
        }
        return Collections.unmodifiableList(ativos);
    }

    /**
     * Total acumulado: soma das multas pendentes na data atual (Requisito 4.3 e 4.5).
     */
    @Override
    public double calcularTotalMultas() {
        return calcularTotalMultas(LocalDate.now());
    }

    /**
     * Total acumulado: soma das multas em relacao a uma data de referencia (Requisito 4.3).
     * Itera sobre a lista de emprestimos e acumula a multa polimorfica.
     */
    @Override
    public double calcularTotalMultas(LocalDate dataReferencia) {
        double total = 0.0;
        for (Emprestimo e : this.emprestimos) {
            if (e.isAtivo()) {
                total += e.calcularMulta(dataReferencia);
            }
        }
        return total;
    }

    @Override
    public int getQuantidadeTotalItens() {
        return this.acervo.size();
    }

    @Override
    public int getQuantidadeItensDisponiveis() {
        return listarItensDisponiveis().size();
    }

    @Override
    public int getQuantidadeItensEmprestados() {
        return listarItensEmprestados().size();
    }
}
