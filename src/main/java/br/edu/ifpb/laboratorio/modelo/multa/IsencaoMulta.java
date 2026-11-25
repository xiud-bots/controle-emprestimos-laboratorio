package br.edu.ifpb.laboratorio.modelo.multa;

/**
 * Regra de isencao total de multa (utilizada para Kits de Componentes).
 * Atende aos requisitos 3.2 e 4.5.
 */
public class IsencaoMulta implements RegraMulta {

    @Override
    public double calcular(int diasAtraso) {
        return 0.0;
    }

    @Override
    public String getDescricao() {
        return "Isento de multa por atraso";
    }
}
