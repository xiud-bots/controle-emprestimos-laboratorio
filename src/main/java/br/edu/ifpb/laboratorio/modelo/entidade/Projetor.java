package br.edu.ifpb.laboratorio.modelo.entidade;

import br.edu.ifpb.laboratorio.modelo.multa.MultaProgressiva;
import br.edu.ifpb.laboratorio.modelo.multa.RegraMulta;

/**
 * Especializacao concreta de Item para Projetores.
 * Regras do enunciado:
 * - Prazo de emprestimo: 2 dias
 * - Multa por dia de atraso: R$ 10,00 no primeiro dia, subindo R$ 5,00 a cada dia seguinte (MultaProgressiva)
 * Atende aos requisitos 3.2, 4.4 e 4.5.
 */
public class Projetor extends Item {

    public static final int PRAZO_DIAS = 2;

    /**
     * Construtor curto sobrecarregado: aplica a regra padrao de multa progressiva.
     * Delega para o construtor completo via this(...).
     */
    public Projetor(String patrimonio, String descricao) {
        this(patrimonio, descricao, false, new MultaProgressiva());
    }

    /**
     * Construtor sobrecarregado com flag de emprestimo e regra padrao.
     */
    public Projetor(String patrimonio, String descricao, boolean emprestado) {
        this(patrimonio, descricao, emprestado, new MultaProgressiva());
    }

    /**
     * Construtor completo com injecao explicita da RegraMulta.
     * Chama o construtor da superclasse via super(...).
     */
    public Projetor(String patrimonio, String descricao, boolean emprestado, RegraMulta regraMulta) {
        super(patrimonio, descricao, emprestado, regraMulta);
    }

    @Override
    public int getPrazoEmprestimoDias() {
        return PRAZO_DIAS;
    }

    @Override
    public String getTipo() {
        return "Projetor";
    }
}
