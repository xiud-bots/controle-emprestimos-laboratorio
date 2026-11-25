package br.edu.ifpb.laboratorio.persistencia;

import br.edu.ifpb.laboratorio.modelo.entidade.Item;
import br.edu.ifpb.laboratorio.modelo.entidade.KitComponentes;
import br.edu.ifpb.laboratorio.modelo.entidade.Notebook;
import br.edu.ifpb.laboratorio.modelo.entidade.Projetor;
import br.edu.ifpb.laboratorio.modelo.excecao.FormatoArquivoInvalidoException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementacao de persistencia do acervo utilizando arquivo de texto CSV com BufferedReader e BufferedWriter.
 * Atende aos requisitos:
 * - 3.3: Leitura com BufferedReader e split(";"), gravacao com BufferedWriter.
 * - 4.6: Tratamento de arquivo inexistente e validacao de formato de linhas.
 */
public class RepositorioAcervoArquivo implements IRepositorioAcervo {

    private final File arquivo;

    public RepositorioAcervoArquivo(String caminhoArquivo) {
        if (caminhoArquivo == null || caminhoArquivo.trim().isEmpty()) {
            throw new IllegalArgumentException("O caminho do arquivo de acervo nao pode ser nulo ou vazio.");
        }
        this.arquivo = new File(caminhoArquivo);
    }

    public RepositorioAcervoArquivo(File arquivo) {
        if (arquivo == null) {
            throw new IllegalArgumentException("O arquivo de acervo nao pode ser nulo.");
        }
        this.arquivo = arquivo;
    }

    @Override
    public List<Item> carregar() throws IOException, FormatoArquivoInvalidoException {
        if (!arquivo.exists()) {
            throw new FileNotFoundException(String.format("Arquivo de acervo nao encontrado em: '%s'. " +
                    "Certifique-se de que a pasta 'dados' com o arquivo 'acervo.csv' esta presente.", arquivo.getPath()));
        }

        List<Item> itens = new ArrayList<>();
        int numeroLinha = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo, StandardCharsets.UTF_8))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                numeroLinha++;
                String linhaLimpa = linha.trim();

                // Ignora linhas em branco e comentarios com #
                if (linhaLimpa.isEmpty() || linhaLimpa.startsWith("#")) {
                    continue;
                }

                String[] partes = linhaLimpa.split(";");
                if (partes.length < 3) {
                    throw new FormatoArquivoInvalidoException(
                            arquivo.getName(),
                            numeroLinha,
                            linha,
                            "Esperava 3 campos separados por ponto e virgula: tipo;patrimonio;descricao");
                }

                String tipo = partes[0].trim();
                String patrimonio = partes[1].trim();
                String descricao = partes[2].trim();

                Item item = criarItemPorTipo(tipo, patrimonio, descricao, numeroLinha, linha);
                itens.add(item);
            }
        }

        return itens;
    }

    @Override
    public void salvar(List<Item> itens) throws IOException {
        if (itens == null) {
            throw new IllegalArgumentException("A lista de itens para salvar nao pode ser nula.");
        }

        File diretorioPai = arquivo.getParentFile();
        if (diretorioPai != null && !diretorioPai.exists()) {
            diretorioPai.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo, StandardCharsets.UTF_8))) {
            writer.write("# Acervo do Laboratorio - Dados Atualizados");
            writer.newLine();
            writer.write("# Formato: tipo;patrimonio;descricao");
            writer.newLine();

            for (Item item : itens) {
                String tipoCsv = formatarTipoCsv(item);
                writer.write(String.format("%s;%s;%s", tipoCsv, item.getPatrimonio(), item.getDescricao()));
                writer.newLine();
            }
        }
    }

    private Item criarItemPorTipo(String tipo, String patrimonio, String descricao, int numeroLinha, String linhaCompleta)
            throws FormatoArquivoInvalidoException {
        String normalizado = tipo.toUpperCase().replaceAll("[\\s_-]+", "");

        if (normalizado.contains("NOTEBOOK")) {
            return new Notebook(patrimonio, descricao);
        } else if (normalizado.contains("PROJETOR")) {
            return new Projetor(patrimonio, descricao);
        } else if (normalizado.contains("KIT")) {
            return new KitComponentes(patrimonio, descricao);
        } else {
            throw new FormatoArquivoInvalidoException(
                    arquivo.getName(),
                    numeroLinha,
                    linhaCompleta,
                    String.format("Tipo de item desconhecido: '%s'. Tipos aceitos: NOTEBOOK, PROJETOR, KIT_COMPONENTES", tipo));
        }
    }

    private String formatarTipoCsv(Item item) {
        if (item instanceof Notebook) {
            return "NOTEBOOK";
        } else if (item instanceof Projetor) {
            return "PROJETOR";
        } else if (item instanceof KitComponentes) {
            return "KIT_COMPONENTES";
        }
        return item.getTipo().toUpperCase();
    }

    public File getArquivo() {
        return arquivo;
    }
}
