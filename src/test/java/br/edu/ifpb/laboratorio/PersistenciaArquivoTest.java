package br.edu.ifpb.laboratorio;

import br.edu.ifpb.laboratorio.modelo.entidade.Item;
import br.edu.ifpb.laboratorio.modelo.entidade.Notebook;
import br.edu.ifpb.laboratorio.modelo.entidade.Pessoa;
import br.edu.ifpb.laboratorio.modelo.entidade.Projetor;
import br.edu.ifpb.laboratorio.modelo.excecao.FormatoArquivoInvalidoException;
import br.edu.ifpb.laboratorio.persistencia.RepositorioAcervoArquivo;
import br.edu.ifpb.laboratorio.persistencia.RepositorioPessoasArquivo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Persistencia com Arquivo CSV")
class PersistenciaArquivoTest {

    @Test
    @DisplayName("Deve ler e gravar acervo no formato tipo;patrimonio;descricao")
    void testSalvarECarregarAcervo(@TempDir Path tempDir) throws Exception {
        File arquivo = tempDir.resolve("acervo.csv").toFile();
        RepositorioAcervoArquivo repo = new RepositorioAcervoArquivo(arquivo);

        List<Item> itensOriginal = List.of(
                new Notebook("PAT-100", "Dell XPS 13"),
                new Projetor("PAT-200", "Epson PowerLite")
        );

        repo.salvar(itensOriginal);
        assertTrue(arquivo.exists());

        List<Item> itensLidos = repo.carregar();
        assertEquals(2, itensLidos.size());
        assertEquals("PAT-100", itensLidos.get(0).getPatrimonio());
        assertEquals("Notebook", itensLidos.get(0).getTipo());
        assertEquals("PAT-200", itensLidos.get(1).getPatrimonio());
        assertEquals("Projetor", itensLidos.get(1).getTipo());
    }

    @Test
    @DisplayName("Deve ignorar linhas de comentarios (#) e linhas em branco")
    void testLinhasComentarios(@TempDir Path tempDir) throws Exception {
        File arquivo = tempDir.resolve("pessoas.csv").toFile();
        try (FileWriter w = new FileWriter(arquivo)) {
            w.write("# Cabecalho de teste\n");
            w.write("\n");
            w.write("2024101;Maria Silva\n");
            w.write("# Outro comentario\n");
            w.write("2024102;Joao Santos\n");
        }

        RepositorioPessoasArquivo repo = new RepositorioPessoasArquivo(arquivo);
        List<Pessoa> pessoas = repo.carregar();
        assertEquals(2, pessoas.size());
        assertEquals("2024101", pessoas.get(0).getMatricula());
        assertEquals("Maria Silva", pessoas.get(0).getNome());
    }

    @Test
    @DisplayName("Deve lancar FormatoArquivoInvalidoException se linha estiver com campos insuficientes")
    void testFormatoInvalido(@TempDir Path tempDir) throws Exception {
        File arquivo = tempDir.resolve("acervo_invalido.csv").toFile();
        try (FileWriter w = new FileWriter(arquivo)) {
            w.write("NOTEBOOK;PAT-100\n"); // falta a descricao!
        }

        RepositorioAcervoArquivo repo = new RepositorioAcervoArquivo(arquivo);
        assertThrows(FormatoArquivoInvalidoException.class, repo::carregar);
    }
}
