package br.edu.ifpb.laboratorio.persistencia;

import br.edu.ifpb.laboratorio.modelo.entidade.Pessoa;
import br.edu.ifpb.laboratorio.modelo.excecao.FormatoArquivoInvalidoException;

import java.io.IOException;
import java.util.List;

/**
 * Interface que abstrai as operacoes de persistencia do cadastro de pessoas.
 * Atende ao requisito 4.5 e 4.8 (Inversao de Dependencia - DIP).
 */
public interface IRepositorioPessoas {

    /**
     * Le as pessoas cadastradas a partir do arquivo de dados.
     * @return lista de pessoas autorizadas para emprestimo.
     * @throws IOException se ocorrer falha de E/S ou arquivo nao encontrado.
     * @throws FormatoArquivoInvalidoException se alguma linha estiver fora do formato matricula;nome.
     */
    List<Pessoa> carregar() throws IOException, FormatoArquivoInvalidoException;
}
