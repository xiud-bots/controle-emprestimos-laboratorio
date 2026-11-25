package br.edu.ifpb.laboratorio.modelo.entidade;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Entidade que representa o registro de emprestimo de um item para uma pessoa.
 * Atende aos requisitos:
 * - 4.1: Atributos privados, encapsulamento e construtores/metodos sobrecarregados.
 * - 4.2: Composicao (Emprestimo TEM UM item e TEM UMA pessoa, sem herdar deles).
 * - 4.5: Calculo polimorfico de multa delegando para o Item.
 */
public class Emprestimo {

    private static final DateTimeFormatter FORMATADOR_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Item item;
    private final Pessoa pessoa;
    private final LocalDate dataEmprestimo;
    private final LocalDate dataPrevistaDevolucao;
    private LocalDate dataDevolucao;
    private boolean ativo;

    /**
     * Construtor curto sobrecarregado (assume data de emprestimo como hoje).
     * Delega para o construtor completo via this(...).
     */
    public Emprestimo(Item item, Pessoa pessoa) {
        this(item, pessoa, LocalDate.now());
    }

    /**
     * Construtor completo sobrecarregado com definicao explicita da data de emprestimo.
     */
    public Emprestimo(Item item, Pessoa pessoa, LocalDate dataEmprestimo) {
        this.item = Objects.requireNonNull(item, "O item do emprestimo nao pode ser nulo.");
        this.pessoa = Objects.requireNonNull(pessoa, "A pessoa do emprestimo nao pode ser nula.");
        this.dataEmprestimo = Objects.requireNonNull(dataEmprestimo, "A data do emprestimo nao pode ser nula.");
        // Calcula a data prevista com base no prazo polimorfico do tipo do item
        this.dataPrevistaDevolucao = dataEmprestimo.plusDays(item.getPrazoEmprestimoDias());
        this.dataDevolucao = null;
        this.ativo = true;
    }

    /**
     * Metodo sobrecarregado 1: Calcula a multa considerando a data atual (ou a data da devolucao ja realizada).
     * @return valor da multa calculada.
     */
    public double calcularMulta() {
        LocalDate dataRef = (this.dataDevolucao != null) ? this.dataDevolucao : LocalDate.now();
        return calcularMulta(dataRef);
    }

    /**
     * Metodo sobrecarregado 2: Calcula a multa em relacao a uma data de referencia informada.
     * Atende ao requisito 4.1 (metodo sobrecarregado) e 4.5 (chamada polimorfica em item.calcularMulta).
     * @param dataReferencia data em que a devolucao foi ou sera realizada.
     * @return valor total da multa calculada polimorficamente.
     */
    public double calcularMulta(LocalDate dataReferencia) {
        int diasAtraso = getDiasAtraso(dataReferencia);
        if (diasAtraso <= 0) {
            return 0.0;
        }
        // Chamada polimorfica sem NENHUM 'if' verificando o tipo do item
        return this.item.calcularMulta(diasAtraso);
    }

    /**
     * Calcula o numero de dias de atraso em relacao a data informada.
     * @param dataReferencia data para verificacao de atraso.
     * @return quantidade de dias de atraso (0 se devolvido no prazo ou antes).
     */
    public int getDiasAtraso(LocalDate dataReferencia) {
        if (dataReferencia == null || !dataReferencia.isAfter(this.dataPrevistaDevolucao)) {
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(this.dataPrevistaDevolucao, dataReferencia);
    }

    /**
     * Metodo sobrecarregado 1 de devolucao: registra devolucao na data de hoje.
     */
    public void registrarDevolucao() {
        registrarDevolucao(LocalDate.now());
    }

    /**
     * Metodo sobrecarregado 2 de devolucao: registra devolucao com data informada.
     * Atualiza o estado do emprestimo e libera o item associado.
     * @param dataDevolucao data efetiva da entrega.
     */
    public void registrarDevolucao(LocalDate dataDevolucao) {
        if (dataDevolucao == null) {
            throw new IllegalArgumentException("A data de devolucao nao pode ser nula.");
        }
        if (dataDevolucao.isBefore(this.dataEmprestimo)) {
            throw new IllegalArgumentException("A data de devolucao nao pode ser anterior a data do emprestimo.");
        }
        this.dataDevolucao = dataDevolucao;
        this.ativo = false;
        this.item.devolver();
    }

    // Getters
    public Item getItem() {
        return item;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public LocalDate getDataEmprestimo() {
        return dataEmprestimo;
    }

    public LocalDate getDataPrevistaDevolucao() {
        return dataPrevistaDevolucao;
    }

    public LocalDate getDataDevolucao() {
        return dataDevolucao;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public String getDataEmprestimoFormatada() {
        return this.dataEmprestimo.format(FORMATADOR_DATA);
    }

    public String getDataPrevistaDevolucaoFormatada() {
        return this.dataPrevistaDevolucao.format(FORMATADOR_DATA);
    }

    public String getDataDevolucaoFormatada() {
        return (this.dataDevolucao != null) ? this.dataDevolucao.format(FORMATADOR_DATA) : "-";
    }

    @Override
    public String toString() {
        String status = this.ativo ? "EM ABERTO" : "DEVOLVIDO em " + getDataDevolucaoFormatada();
        double multa = calcularMulta();
        String infoMulta = (multa > 0.0) ? String.format(" | MULTA: R$ %.2f", multa) : "";

        return String.format("Emprestimo: [%s] %s -> %s | Emprestado em: %s | Previsao: %s | Situacao: %s%s",
                this.item.getPatrimonio(),
                this.item.getDescricao(),
                this.pessoa.getNome(),
                getDataEmprestimoFormatada(),
                getDataPrevistaDevolucaoFormatada(),
                status,
                infoMulta);
    }
}
