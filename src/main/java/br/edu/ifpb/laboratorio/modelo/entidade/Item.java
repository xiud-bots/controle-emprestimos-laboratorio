package br.edu.ifpb.laboratorio.modelo.entidade;

import br.edu.ifpb.laboratorio.modelo.excecao.ItemJaEmprestadoException;
import br.edu.ifpb.laboratorio.modelo.multa.RegraMulta;

import java.util.Objects;

/**
 * Superclasse abstrata que reune os atributos e comportamentos comuns a todos os itens do acervo.
 * Atende aos requisitos:
 * - 4.1: Atributos privados, getters/setters validados, construtores sobrecarregados via this().
 * - 4.4: Classe abstrata, metodos abstratos e sobreposicao de toString().
 * - 4.5: Composicao com RegraMulta recebida por injecao de dependencia no construtor.
 */
public abstract class Item {

    private final String patrimonio;
    private String descricao;
    private boolean emprestado;
    private RegraMulta regraMulta;

    /**
     * Construtor curto sobrecarregado (item inicialmente disponivel / nao emprestado).
     * Delega para o construtor completo via this(...).
     */
    public Item(String patrimonio, String descricao, RegraMulta regraMulta) {
        this(patrimonio, descricao, false, regraMulta);
    }

    /**
     * Construtor completo sobrecarregado.
     * Valida os parametros de entrada com IllegalArgumentException.
     */
    public Item(String patrimonio, String descricao, boolean emprestado, RegraMulta regraMulta) {
        if (patrimonio == null || patrimonio.trim().isEmpty()) {
            throw new IllegalArgumentException("O patrimonio do item nao pode ser nulo ou vazio.");
        }
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("A descricao do item nao pode ser nula ou vazia.");
        }
        this.patrimonio = patrimonio.trim();
        this.descricao = descricao.trim();
        this.emprestado = emprestado;
        this.regraMulta = Objects.requireNonNull(regraMulta, "A regra de multa nao pode ser nula.");
    }

    /**
     * Metodo abstrato que define o prazo maximo em dias de emprestimo regular para cada tipo de item.
     * Atende ao requisito 4.4 (metodo abstrato na superclasse).
     * @return prazo em dias (ex: Notebook = 7, Projetor = 2, Kit = 15).
     */
    public abstract int getPrazoEmprestimoDias();

    /**
     * Metodo abstrato que retorna o nome do tipo do item para apresentacao e persistencia.
     * @return nome legivel do tipo.
     */
    public abstract String getTipo();

    /**
     * Metodo polimorfico que delega o calculo da multa para a regra injetada (Strategy).
     * Permite calcular a multa sem nenhum 'if' verificando o tipo do item.
     * Atende ao requisito 4.5.
     * @param diasAtraso quantidade de dias em atraso.
     * @return valor da multa em reais.
     */
    public double calcularMulta(int diasAtraso) {
        if (diasAtraso < 0) {
            throw new IllegalArgumentException("Dias de atraso nao podem ser negativos.");
        }
        return this.regraMulta.calcular(diasAtraso);
    }

    /**
     * Registra a saida do item por emprestimo.
     * Lanca excecao de negocio caso o item ja esteja emprestado.
     * Atende ao requisito 4.6 (throw na classe que detecta o problema).
     */
    public void emprestar() throws ItemJaEmprestadoException {
        if (this.emprestado) {
            throw new ItemJaEmprestadoException(this.patrimonio,
                    String.format("o item '%s - %s' ja se encontra emprestado.", this.patrimonio, this.descricao));
        }
        this.emprestado = true;
    }

    /**
     * Registra o retorno do item ao acervo, tornando-o disponivel novamente.
     */
    public void devolver() {
        this.emprestado = false;
    }

    // Getters e Setters com encapsulamento rigoroso
    public String getPatrimonio() {
        return patrimonio;
    }

    public String getDescricao() {
        return descricao;
    }

    /**
     * Setter de descricao com validacao interna (Requisito 4.1).
     */
    public void setDescricao(String descricao) {
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("A descricao nao pode ser nula ou vazia.");
        }
        this.descricao = descricao.trim();
    }

    public boolean isEmprestado() {
        return emprestado;
    }

    public void setEmprestado(boolean emprestado) {
        this.emprestado = emprestado;
    }

    public RegraMulta getRegraMulta() {
        return regraMulta;
    }

    /**
     * Permite alterar a politica de multa por injecao (Strategy).
     */
    public void setRegraMulta(RegraMulta regraMulta) {
        this.regraMulta = Objects.requireNonNull(regraMulta, "A regra de multa nao pode ser nula.");
    }

    public String getSituacao() {
        return this.emprestado ? "Emprestado" : "Disponivel";
    }

    /**
     * Sobreposicao de toString() para apresentacao textual direta (Requisito 4.4).
     */
    @Override
    public String toString() {
        return String.format("[%s] Patrimonio: %-8s | Descricao: %-35s | Situacao: %-10s | Prazo: %2d dias | Regra: %s",
                getTipo().toUpperCase(),
                this.patrimonio,
                this.descricao,
                getSituacao(),
                getPrazoEmprestimoDias(),
                this.regraMulta.getDescricao());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item item)) return false;
        return Objects.equals(patrimonio, item.patrimonio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(patrimonio);
    }
}
