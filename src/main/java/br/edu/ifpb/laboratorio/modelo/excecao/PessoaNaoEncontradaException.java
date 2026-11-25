package br.edu.ifpb.laboratorio.modelo.excecao;

/**
 * Excecao lancada quando uma pessoa (estudante ou servidor) nao e localizada por sua matricula.
 */
public class PessoaNaoEncontradaException extends Exception {

    private final String matricula;

    public PessoaNaoEncontradaException(String matricula) {
        super(String.format("Pessoa com matricula '%s' nao foi encontrada no cadastro.", matricula));
        this.matricula = matricula;
    }

    public String getMatricula() {
        return matricula;
    }
}
