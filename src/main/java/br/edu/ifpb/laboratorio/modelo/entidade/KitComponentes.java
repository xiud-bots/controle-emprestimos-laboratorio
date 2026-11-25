package br.edu.ifpb.laboratorio.modelo.entidade;

import br.edu.ifpb.laboratorio.modelo.multa.IsencaoMulta;
import br.edu.ifpb.laboratorio.modelo.multa.RegraMulta;

/**
 * Especializacao concreta de Item para Kits de Componentes (Arduino, ESP32, etc).
 * Regras do enunciado:
 * - Prazo de emprestimo: 15 dias
 * - Multa por dia de atraso: Isento de multa (IsencaoMulta / R$ 0,00)
 * Atende aos requisitos 3.2, 4.4 e 4.5.
 */
public class KitComponentes extends Item {

    public static final int PRAZO_DIAS = 15;

    /**
     * Construtor curto sobrecarregado: aplica a regra padrao de isencao de multa.
     * Delega para o construtor completo via this(...).
     */
    public KitComponentes(String patrimonio, String descricao) {
        this(patrimonio, descricao, false, new IsencaoMulta());
    }

    /**
     * Construtor sobrecarregado com flag de emprestimo e regra padrao.
     */
    public KitComponentes(String patrimonio, String descricao, boolean emprestado) {
        this(patrimonio, descricao, emprestado, new IsencaoMulta());
    }

    /**
     * Construtor completo com injecao explicita da RegraMulta.
     * Chama o construtor da superclasse via super(...).
     */
    public KitComponentes(String patrimonio, String descricao, boolean emprestado, RegraMulta regraMulta) {
        super(patrimonio, descricao, emprestado, regraMulta);
    }

    @Override
    public int getPrazoEmprestimoDias() {
        return PRAZO_DIAS;
    }

    @Override
    public String getTipo() {
        return "Kit de Componentes";
    }
}
