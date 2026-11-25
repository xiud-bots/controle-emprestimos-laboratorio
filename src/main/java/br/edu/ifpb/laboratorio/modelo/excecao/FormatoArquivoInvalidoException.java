package br.edu.ifpb.laboratorio.modelo.excecao;

/**
 * Excecao lancada quando uma linha de arquivo CSV estiver fora do formato esperado.
 * Atende ao requisito 4.6 (Tratar linha fora do formato sem deixar o programa cair).
 */
public class FormatoArquivoInvalidoException extends Exception {

    private final String nomeArquivo;
    private final int numeroLinha;
    private final String conteudoLinha;

    public FormatoArquivoInvalidoException(String nomeArquivo, int numeroLinha, String conteudoLinha, String motivo) {
        super(String.format("Erro de formato no arquivo '%s' (linha %d: '%s'): %s",
                nomeArquivo, numeroLinha, conteudoLinha, motivo));
        this.nomeArquivo = nomeArquivo;
        this.numeroLinha = numeroLinha;
        this.conteudoLinha = conteudoLinha;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public int getNumeroLinha() {
        return numeroLinha;
    }

    public String getConteudoLinha() {
        return conteudoLinha;
    }
}
