package br.edu.ifpb.laboratorio.modelo.multa;

/**
 * Regra de multa fixa por dia de atraso (utilizada para Notebooks: R$ 5,00 por dia).
 * Atende aos requisitos 3.2 e 4.5.
 */
public class MultaFixa implements RegraMulta {

    private final double valorPorDia;

    /**
     * Construtor padrao com o valor especificado no enunciado (R$ 5,00).
     */
    public MultaFixa() {
        this(5.0);
    }

    /**
     * Construtor sobrecarregado que recebe o valor diario parametrizado.
     * @param valorPorDia valor cobrado por dia de atraso.
     */
    public MultaFixa(double valorPorDia) {
        if (valorPorDia < 0) {
            throw new IllegalArgumentException("O valor da multa diaria nao pode ser negativo.");
        }
        this.valorPorDia = valorPorDia;
    }

    @Override
    public double calcular(int diasAtraso) {
        if (diasAtraso <= 0) {
            return 0.0;
        }
        return diasAtraso * this.valorPorDia;
    }

    @Override
    public String getDescricao() {
        return String.format("Multa fixa de R$ %.2f por dia de atraso", this.valorPorDia);
    }

    public double getValorPorDia() {
        return valorPorDia;
    }
}
