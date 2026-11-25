package br.edu.ifpb.laboratorio.modelo.excecao;

/**
 * Excecao lancada quando um item do acervo nao e localizado pelo seu patrimonio.
 */
public class ItemNaoEncontradoException extends Exception {

    private final String patrimonio;

    public ItemNaoEncontradoException(String patrimonio) {
        super(String.format("Item com patrimonio '%s' nao foi encontrado no acervo.", patrimonio));
        this.patrimonio = patrimonio;
    }

    public String getPatrimonio() {
        return patrimonio;
    }
}
