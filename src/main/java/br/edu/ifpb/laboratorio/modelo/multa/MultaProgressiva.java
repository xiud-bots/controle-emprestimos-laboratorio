package br.edu.ifpb.laboratorio.modelo.multa;

/**
 * Regra de multa progressiva (utilizada para Projetores):
 * R$ 10,00 no primeiro dia, subindo R$ 5,00 a cada dia seguinte.
 * Exemplo:
 * Dia 1 de atraso: R$ 10,00
 * Dia 2 de atraso: R$ 10,00 + R$ 15,00 = R$ 25,00
 * Dia 3 de atraso: R$ 10,00 + R$ 15,00 + R$ 20,00 = R$ 45,00
 * Atende aos requisitos 3.2 e 4.5.
 */
public class MultaProgressiva implements RegraMulta {

    private final double valorPrimeiroDia;
    private final double incrementoDiario;

    /**
     * Construtor padrao com os valores definidos no enunciado (R$ 10,00 inicial + R$ 5,00/dia).
     */
    public MultaProgressiva() {
        this(10.0, 5.0);
    }

    /**
     * Construtor sobrecarregado permitindo configurar os valores da progressao.
     */
    public MultaProgressiva(double valorPrimeiroDia, double incrementoDiario) {
        if (valorPrimeiroDia < 0 || incrementoDiario < 0) {
            throw new IllegalArgumentException("Os valores da multa progressiva nao podem ser negativos.");
        }
        this.valorPrimeiroDia = valorPrimeiroDia;
        this.incrementoDiario = incrementoDiario;
    }

    @Override
    public double calcular(int diasAtraso) {
        if (diasAtraso <= 0) {
            return 0.0;
        }

        double total = 0.0;
        for (int dia = 1; dia <= diasAtraso; dia++) {
            total += this.valorPrimeiroDia + ((dia - 1) * this.incrementoDiario);
        }
        return total;
    }

    @Override
    public String getDescricao() {
        return String.format("Multa progressiva: R$ %.2f no 1º dia, subindo R$ %.2f a cada dia seguinte",
                this.valorPrimeiroDia, this.incrementoDiario);
    }

    public double getValorPrimeiroDia() {
        return valorPrimeiroDia;
    }

    public double getIncrementoDiario() {
        return incrementoDiario;
    }
}
