package br.edu.ifpb.laboratorio.modelo.entidade;

import br.edu.ifpb.laboratorio.modelo.multa.MultaFixa;
import br.edu.ifpb.laboratorio.modelo.multa.RegraMulta;

/**
 * Especializacao concreta de Item para Notebooks.
 * Regras do enunciado:
 * - Prazo de emprestimo: 7 dias
 * - Multa por dia de atraso: R$ 5,00 por dia (MultaFixa)
 * Atende aos requisitos 3.2, 4.4 e 4.5.
 */
public class Notebook extends Item {

    public static final int PRAZO_DIAS = 7;

    /**
     * Construtor curto sobrecarregado: aplica a regra padrao de multa fixa (R$ 5,00/dia).
     * Delega para o construtor completo via this(...).
     */
    public Notebook(String patrimonio, String descricao) {
        this(patrimonio, descricao, false, new MultaFixa());
    }

    /**
     * Construtor sobrecarregado com flag de emprestimo e regra padrao.
     */
    public Notebook(String patrimonio, String descricao, boolean emprestado) {
        this(patrimonio, descricao, emprestado, new MultaFixa());
    }

    /**
     * Construtor completo com injecao explicita da RegraMulta.
     * Chama o construtor da superclasse via super(...).
     */
    public Notebook(String patrimonio, String descricao, boolean emprestado, RegraMulta regraMulta) {
        super(patrimonio, descricao, emprestado, regraMulta);
    }

    @Override
    public int getPrazoEmprestimoDias() {
        return PRAZO_DIAS;
    }

    @Override
    public String getTipo() {
        return "Notebook";
    }
}
