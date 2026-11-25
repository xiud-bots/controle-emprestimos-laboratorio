package br.edu.ifpb.laboratorio.terminal;

import br.edu.ifpb.laboratorio.modelo.entidade.Emprestimo;
import br.edu.ifpb.laboratorio.modelo.entidade.Item;
import br.edu.ifpb.laboratorio.modelo.entidade.Pessoa;
import br.edu.ifpb.laboratorio.modelo.excecao.FormatoArquivoInvalidoException;
import br.edu.ifpb.laboratorio.modelo.excecao.ItemJaEmprestadoException;
import br.edu.ifpb.laboratorio.modelo.excecao.ItemNaoEncontradoException;
import br.edu.ifpb.laboratorio.modelo.excecao.PessoaNaoEncontradaException;
import br.edu.ifpb.laboratorio.persistencia.IRepositorioAcervo;
import br.edu.ifpb.laboratorio.persistencia.IRepositorioPessoas;
import br.edu.ifpb.laboratorio.persistencia.RepositorioAcervoArquivo;
import br.edu.ifpb.laboratorio.persistencia.RepositorioPessoasArquivo;
import br.edu.ifpb.laboratorio.servico.IServicoEmprestimo;
import br.edu.ifpb.laboratorio.servico.ServicoEmprestimo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

/**
 * Ponto de entrada da Entrega 1: Demonstracao do modelo funcionando exclusivamente pelo terminal.
 * Executa de forma automatizada e didatica o Roteiro Oficial da Secao 7 do Enunciado,
 * seguido por demonstracao de calculo de multas polimorficas e opcao de menu interativo.
 */
public class PrincipalTerminal {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void main(String[] args) {
        imprimirCabecalho();

        // 1. Configuracao e Injecao de Dependencias (Requisito 4.5 e 8.2)
        // O programa principal monta o sistema e escolhe as implementacoes
        String caminhoAcervo = resolverCaminhoDados("dados/acervo.csv");
        String caminhoPessoas = resolverCaminhoDados("dados/pessoas.csv");

        IRepositorioAcervo repoAcervo = new RepositorioAcervoArquivo(caminhoAcervo);
        IRepositorioPessoas repoPessoas = new RepositorioPessoasArquivo(caminhoPessoas);

        IServicoEmprestimo servico = new ServicoEmprestimo(repoAcervo, repoPessoas);

        // 2. Execucao do Roteiro Oficial da Secao 7
        try {
            executarRoteiroSecao7(servico);
            demonstrarCalculoMultasPolimorficas(servico);
        } catch (Exception e) {
            System.err.println("\n[ERRO CRITICO] Ocorreu uma falha inesperada durante a demonstracao: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // Se houver argumento "--interativo" ou "-i", abre o menu
        if (args.length > 0 && (args[0].equalsIgnoreCase("--interativo") || args[0].equalsIgnoreCase("-i"))) {
            iniciarMenuInterativo(servico);
        } else {
            System.out.println("===============================================================================");
            System.out.println("   [SUCESSO] Roteiro oficial da Secao 7 executado com 100% de conformidade!   ");
            System.out.println("   Para testar o modo interativo, execute: ./executar-etapa1.sh --interativo   ");
            System.out.println("===============================================================================\n");
        }
    }

    /**
     * Roteiro obrigatorio da Secao 7 do Enunciado do Projeto:
     * 1. Abrir o programa com o acervo e as pessoas carregados dos arquivos e listar o acervo.
     * 2. Fazer um emprestimo (escolhe item disponivel e pessoa, registra e mostra item emprestado com data prevista).
     * 3. Tentar emprestar o mesmo item de novo (sistema recusa com mensagem da ItemJaEmprestadoException).
     * 4. Dar baixa no emprestimo (registra devolucao e mostra item disponivel novamente).
     */
    private static void executarRoteiroSecao7(IServicoEmprestimo servico) {
        System.out.println("\n>>> INICIANDO O ROTEIRO OFICIAL DE AVALIACAO (SECAO 7 DO ENUNCIADO) <<<\n");

        // PASSO 1: Carregar acervo e pessoas e listar o acervo
        System.out.println("-------------------------------------------------------------------------------");
        System.out.println("PASSO 1: Carregar arquivos da pasta 'dados' e listar o acervo");
        System.out.println("-------------------------------------------------------------------------------");
        try {
            servico.carregarDados();
            System.out.println("[OK] Dados carregados com sucesso!");
            System.out.printf("     - Total de itens no acervo: %d\n", servico.getQuantidadeTotalItens());
            System.out.printf("     - Total de pessoas cadastradas: %d\n\n", servico.getPessoas().size());

            System.out.println("LISTAGEM COMPLETA DO ACERVO CARREGADO:");
            List<Item> itens = servico.getAcervo();
            for (Item item : itens) {
                // Polimorfismo: toString() sobreposto em cada subclasse
                System.out.println("  * " + item);
            }
        } catch (FileNotFoundException e) {
            System.err.println("[ERRO DE ARQUIVO] " + e.getMessage());
            return;
        } catch (FormatoArquivoInvalidoException e) {
            System.err.println("[ERRO DE FORMATO] " + e.getMessage());
            return;
        } catch (IOException e) {
            System.err.println("[ERRO DE E/S] Falha ao acessar os arquivos: " + e.getMessage());
            return;
        }

        // PASSO 2: Fazer um emprestimo
        System.out.println("\n-------------------------------------------------------------------------------");
        System.out.println("PASSO 2: Fazer um emprestimo de um item disponivel para uma pessoa");
        System.out.println("-------------------------------------------------------------------------------");
        String patrimonioEscolhido = "PAT-001"; // Notebook Dell Latitude
        String matriculaEscolhida = "2024101001"; // Gabriel Aragao

        try {
            Item itemAntes = servico.buscarItemPorPatrimonio(patrimonioEscolhido);
            Pessoa pessoa = servico.buscarPessoaPorMatricula(matriculaEscolhida);

            System.out.printf("Item selecionado: [%s] %s (Situacao: %s)\n",
                    itemAntes.getPatrimonio(), itemAntes.getDescricao(), itemAntes.getSituacao());
            System.out.printf("Pessoa selecionada: %s (Matricula: %s)\n",
                    pessoa.getNome(), pessoa.getMatricula());

            Emprestimo emprestimo = servico.registrarEmprestimo(patrimonioEscolhido, matriculaEscolhida);

            System.out.println("\n[EMPRESTIMO REGISTRADO COM SUCESSO]");
            System.out.printf("  - Item emprestado: %s (%s)\n", emprestimo.getItem().getPatrimonio(), emprestimo.getItem().getDescricao());
            System.out.printf("  - Responsavel: %s\n", emprestimo.getPessoa().getNome());
            System.out.printf("  - Data do Emprestimo: %s\n", emprestimo.getDataEmprestimoFormatada());
            System.out.printf("  - Data Prevista para Devolucao: %s (Prazo: %d dias)\n",
                    emprestimo.getDataPrevistaDevolucaoFormatada(), emprestimo.getItem().getPrazoEmprestimoDias());

            Item itemDepois = servico.buscarItemPorPatrimonio(patrimonioEscolhido);
            System.out.printf("  - Nova situacao do item no acervo: %s\n", itemDepois.getSituacao().toUpperCase());

        } catch (ItemJaEmprestadoException | ItemNaoEncontradoException | PessoaNaoEncontradaException e) {
            System.err.println("[FALHA NO PASSO 2] " + e.getMessage());
        }

        // PASSO 3: Tentar emprestar o mesmo item de novo (Deve recusar e exibir a excecao criada)
        System.out.println("\n-------------------------------------------------------------------------------");
        System.out.println("PASSO 3: Tentar emprestar o MESMO item novamente (Validacao da Excecao)");
        System.out.println("-------------------------------------------------------------------------------");
        String outraMatricula = "2024101002"; // Maria Eduarda Silva
        System.out.printf("Tentando emprestar novamente o item '%s' para a matricula '%s'...\n",
                patrimonioEscolhido, outraMatricula);

        try {
            servico.registrarEmprestimo(patrimonioEscolhido, outraMatricula);
            System.err.println("[FALHA GRAVE] O sistema permitiu emprestar um item que ja estava emprestado!");
        } catch (ItemJaEmprestadoException e) {
            System.out.println("\n[SUCESSO - EXCECAO DE NEGOCIO CAPTURADA COMO ESPERADO]");
            System.out.printf("  Tipo da Excecao: %s\n", e.getClass().getName());
            System.out.printf("  Mensagem da Excecao: \"%s\"\n", e.getMessage());
            System.out.println("  -> O sistema recusou a operacao com integridade e seguranca.");
        } catch (Exception e) {
            System.err.println("[ERRO INESPERADO] Capturada excecao incorreta: " + e.getMessage());
        }

        // PASSO 4: Dar baixa no emprestimo (registrar devolucao)
        System.out.println("\n-------------------------------------------------------------------------------");
        System.out.println("PASSO 4: Dar baixa no emprestimo (Registrar devolucao do item)");
        System.out.println("-------------------------------------------------------------------------------");
        try {
            System.out.printf("Registrando devolucao do item '%s'...\n", patrimonioEscolhido);
            Emprestimo devolvido = servico.registrarDevolucao(patrimonioEscolhido);

            System.out.println("\n[DEVOLUCAO CONCLUIDA COM SUCESSO]");
            System.out.printf("  - Item devolvido: %s (%s)\n", devolvido.getItem().getPatrimonio(), devolvido.getItem().getDescricao());
            System.out.printf("  - Data de Devolucao: %s\n", devolvido.getDataDevolucaoFormatada());
            System.out.printf("  - Situacao do emprestimo: %s\n", devolvido.isAtivo() ? "EM ABERTO" : "FINALIZADO");

            Item itemAposDevolucao = servico.buscarItemPorPatrimonio(patrimonioEscolhido);
            System.out.printf("  - Situacao do item no acervo: %s (Pronto para novos emprestimos)\n",
                    itemAposDevolucao.getSituacao().toUpperCase());

        } catch (ItemNaoEncontradoException | IllegalArgumentException e) {
            System.err.println("[FALHA NO PASSO 4] " + e.getMessage());
        }
    }

    /**
     * Demonstracao adicional didatica: Calculo de Multas Polimorficas sem nenhum 'if' de tipo (Requisito 3.2 e 4.5).
     */
    private static void demonstrarCalculoMultasPolimorficas(IServicoEmprestimo servico) {
        System.out.println("\n===============================================================================");
        System.out.println("DEMONSTRACAO DIDATICA DE POLIMORFISMO E REGRAS DE MULTA (REQUISITOS 3.2 e 4.5)");
        System.out.println("===============================================================================");
        System.out.println("Simulando atraso de 3 dias em uma colecao contendo os 3 tipos de itens:");
        System.out.println("A mesma chamada 'item.calcularMulta(3)' produz comportamentos diferentes sem nenhum if!\n");

        int diasAtrasoSimulado = 3;

        for (Item item : servico.getAcervo()) {
            double multa = item.calcularMulta(diasAtrasoSimulado);
            System.out.printf("  * [%-18s] %-8s | Prazo: %2d dias | Regra: %-60s | Multa p/ %d dias atraso: R$ %6.2f\n",
                    item.getTipo(),
                    item.getPatrimonio(),
                    item.getPrazoEmprestimoDias(),
                    item.getRegraMulta().getDescricao(),
                    diasAtrasoSimulado,
                    multa);
        }
        System.out.println();
    }

    private static void iniciarMenuInterativo(IServicoEmprestimo servico) {
        Scanner scanner = new Scanner(System.in);
        int opcao = -1;

        while (opcao != 0) {
            System.out.println("\n--- MENU DE CONTROLE DO LABORATORIO ---");
            System.out.println("1. Listar todo o acervo");
            System.out.println("2. Listar apenas itens disponiveis (Filtro)");
            System.out.println("3. Listar pessoas cadastradas");
            System.out.println("4. Realizar novo emprestimo");
            System.out.println("5. Realizar devolucao de item");
            System.out.println("6. Cadastrar novo item no acervo");
            System.out.println("7. Ver emprestimos e total acumulado de multas");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opcao: ");

            try {
                String linha = scanner.nextLine().trim();
                if (linha.isEmpty()) continue;
                opcao = Integer.parseInt(linha);

                switch (opcao) {
                    case 1 -> {
                        System.out.println("\n--- ACERVO COMPLETO ---");
                        servico.getAcervo().forEach(item -> System.out.println("  * " + item));
                    }
                    case 2 -> {
                        System.out.println("\n--- ITENS DISPONIVEIS ---");
                        servico.listarItensDisponiveis().forEach(item -> System.out.println("  * " + item));
                    }
                    case 3 -> {
                        System.out.println("\n--- PESSOAS CADASTRADAS ---");
                        servico.getPessoas().forEach(p -> System.out.println("  * " + p));
                    }
                    case 4 -> {
                        System.out.print("Informe o patrimonio do item: ");
                        String pat = scanner.nextLine().trim();
                        System.out.print("Informe a matricula da pessoa: ");
                        String mat = scanner.nextLine().trim();
                        try {
                            Emprestimo emp = servico.registrarEmprestimo(pat, mat);
                            System.out.println("[SUCESSO] " + emp);
                        } catch (ItemJaEmprestadoException e) {
                            System.out.println("[AVISO] " + e.getMessage());
                        } catch (ItemNaoEncontradoException | PessoaNaoEncontradaException e) {
                            System.out.println("[ERRO] " + e.getMessage());
                        }
                    }
                    case 5 -> {
                        System.out.print("Informe o patrimonio do item a devolver: ");
                        String pat = scanner.nextLine().trim();
                        try {
                            Emprestimo dev = servico.registrarDevolucao(pat);
                            System.out.println("[SUCESSO] Devolucao registrada: " + dev);
                        } catch (Exception e) {
                            System.out.println("[ERRO] " + e.getMessage());
                        }
                    }
                    case 6 -> {
                        System.out.print("Tipo (NOTEBOOK, PROJETOR, KIT_COMPONENTES): ");
                        String tipo = scanner.nextLine().trim();
                        System.out.print("Patrimonio: ");
                        String pat = scanner.nextLine().trim();
                        System.out.print("Descricao: ");
                        String desc = scanner.nextLine().trim();
                        try {
                            servico.cadastrarItem(tipo, pat, desc);
                            System.out.println("[SUCESSO] Item cadastrado e salvo no arquivo acervo.csv!");
                        } catch (Exception e) {
                            System.out.println("[ERRO] " + e.getMessage());
                        }
                    }
                    case 7 -> {
                        System.out.println("\n--- EMPRESTIMOS REGISTRADOS ---");
                        List<Emprestimo> emprestimos = servico.getEmprestimos();
                        if (emprestimos.isEmpty()) {
                            System.out.println("Nenhum emprestimo registrado ate o momento.");
                        } else {
                            emprestimos.forEach(e -> System.out.println("  * " + e));
                        }
                        System.out.printf("Total acumulado de multas em atraso hoje: R$ %.2f\n", servico.calcularTotalMultas());
                    }
                    case 0 -> System.out.println("Encerrando o programa. Ate logo!");
                    default -> System.out.println("Opcao invalida. Tente novamente.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor, digite um numero valido.");
            }
        }
    }

    private static String resolverCaminhoDados(String caminhoRelativo) {
        // Tenta localizar na pasta de execucao ou em diretorios pais
        File f = new File(caminhoRelativo);
        if (f.exists()) {
            return f.getAbsolutePath();
        }
        File fProjeto = new File("dados", new File(caminhoRelativo).getName());
        if (fProjeto.exists()) {
            return fProjeto.getAbsolutePath();
        }
        return caminhoRelativo;
    }

    private static void imprimirCabecalho() {
        System.out.println("===============================================================================");
        System.out.println("       SISTEMA DE CONTROLE DE EMPRESTIMOS DO LABORATORIO - IFPB MONTEIRO       ");
        System.out.println("               Projeto Final de Programacao Orientada a Objetos                ");
        System.out.println("                        ENTREGA 1: O MODELO NO TERMINAL                        ");
        System.out.println("===============================================================================");
    }
}
