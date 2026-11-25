package br.edu.ifpb.laboratorio.modelo.multa;

/**
 * Interface que define o contrato para calculo de multa por dias de atraso.
 * Atende ao requisito 4.5 (Interface e Polimorfismo / Padrao Strategy).
 */
public interface RegraMulta {

    /**
     * Calcula o valor da multa com base na quantidade de dias de atraso.
     * @param diasAtraso quantidade de dias em atraso (>= 0).
     * @return valor total da multa em reais (R$).
     */
    double calcular(int diasAtraso);

    /**
     * Retorna a descricao legivel da politica de multa.
     * @return texto descritivo da regra.
     */
    String getDescricao();
}
