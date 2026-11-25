package br.edu.ifpb.laboratorio.persistencia;

import br.edu.ifpb.laboratorio.modelo.entidade.Item;
import br.edu.ifpb.laboratorio.modelo.excecao.FormatoArquivoInvalidoException;

import java.io.IOException;
import java.util.List;

/**
 * Interface que abstrai as operacoes de persistencia do acervo do laboratorio.
 * Atende ao requisito 4.5 e 4.8 (Inversao de Dependencia - DIP e Baixo Acoplamento).
 */
public interface IRepositorioAcervo {

    /**
     * Le os itens do arquivo de dados e os carrega na memoria.
     * @return lista de itens do acervo.
     * @throws IOException se ocorrer falha de E/S ou arquivo nao encontrado.
     * @throws FormatoArquivoInvalidoException se alguma linha estiver fora do formato tipo;patrimonio;descricao.
     */
    List<Item> carregar() throws IOException, FormatoArquivoInvalidoException;

    /**
     * Grava a lista atual de itens do acervo de volta no arquivo de dados.
     * @param itens colecao de itens a serem persistidos.
     * @throws IOException se ocorrer falha na gravacao.
     */
    void salvar(List<Item> itens) throws IOException;
}
