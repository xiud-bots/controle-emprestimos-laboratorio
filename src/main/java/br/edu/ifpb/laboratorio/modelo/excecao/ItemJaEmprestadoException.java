package br.edu.ifpb.laboratorio.modelo.excecao;

/**
 * Excecao de negocio lancada quando se tenta emprestar um item que ja esta emprestado.
 * Atende ao requisito 4.6 (Excecao personalizada obrigatoria).
 */
public class ItemJaEmprestadoException extends Exception {

    private final String patrimonio;

    public ItemJaEmprestadoException(String patrimonio) {
        super(String.format("Nao e possivel realizar o emprestimo: o item com patrimonio '%s' ja esta emprestado.", patrimonio));
        this.patrimonio = patrimonio;
    }

    public ItemJaEmprestadoException(String patrimonio, String mensagemAdicional) {
        super(String.format("Nao e possivel realizar o emprestimo do item '%s': %s", patrimonio, mensagemAdicional));
        this.patrimonio = patrimonio;
    }

    public String getPatrimonio() {
        return patrimonio;
    }
}
