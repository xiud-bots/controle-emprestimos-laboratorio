# Controle de Empréstimos do Laboratório

Projeto Final da Disciplina de **Programação Orientada a Objetos**  
**CST em Análise e Desenvolvimento de Sistemas — IFPB *Campus* Monteiro**  
**Professor:** Prof. Dr. Paulo Pereira  
**Autor:** Gabriel Aragão (`@domaragao`) / Squad XIUD  

---

## 1. Visão Geral do Sistema

O sistema substitui o controle manual (em caderno) de empréstimos de equipamentos do laboratório do IFPB Monteiro.  
O laboratório atende estudantes e servidores, controlando o acervo de três categorias principais de equipamentos, cada uma com regras específicas de prazo e de multa por atraso:

| Tipo de Item | Prazo de Empréstimo | Regra de Multa por Dia de Atraso |
| :--- | :--- | :--- |
| **Notebook** | 7 dias | R$ 5,00 por dia fixo |
| **Projetor** | 2 dias | R$ 10,00 no 1º dia, subindo R$ 5,00 a cada dia seguinte (Progressiva) |
| **Kit de Componentes** | 15 dias | Isento de multa (R$ 0,00) |

---

## 2. Estrutura das Duas Entregas e Ramificações Git

O projeto foi modelado e organizado com precisão para as duas noites de apresentação:

1. **Entrega 1 — O Modelo no Terminal (Nota: 60 pontos):**
   - Roda exclusivamente pelo terminal via `br.edu.ifpb.laboratorio.terminal.PrincipalTerminal`.
   - Executa rigorosamente o roteiro oficial da Seção 7 do enunciado.
   - Demonstra todo o domínio de POO (encapsulamento, herança, polimorfismo, interfaces, coleções, exceções e arquivos CSV).
   - Tag Git: `v1.0-etapa1-modelo` | Branch: `etapa-1-terminal`.

2. **Entrega 2 — O Sistema com Interface JavaFX (Nota: 40 pontos + 5 pontos extras FXML):**
   - Interface desktop rica desenvolvida em **JavaFX** com **FXML desacoplado** e estilização CSS.
   - Janela principal com lista do acervo, empréstimos abertos, rodapé de totais e botões de ação.
   - Janelas modais (`Modality.APPLICATION_MODAL`) para novo empréstimo e cadastro de item.
   - Uso de `Alert` (informação, confirmação e erro capturando exceção).
   - Tag Git: `v2.0-etapa2-javafx` | Branch: `main` (ou `etapa-2-javafx`).

---

## 3. Matriz de Rastreabilidade dos Requisitos de POO (Seção 4)

| Item | Requisito do Enunciado | Implementação no Código |
| :--- | :--- | :--- |
| **4.1** | Atributos privados e métodos públicos | Todas as entidades possuem atributos `private`, com acesso estrito via getters e setters. |
| **4.1** | Validação dentro do setter | `Item.setDescricao()`, `Pessoa.setNome()` e construtores lançam `IllegalArgumentException` se o valor for inválido. |
| **4.1** | Construtores sobrecarregados com `this()` | `Item(patrimonio, descricao)` delega para `this(patrimonio, descricao, false, regraMulta)`. `Notebook`, `Projetor` e `Emprestimo` usam a mesma técnica. |
| **4.1** | Métodos sobrecarregados | `Emprestimo.calcularMulta()`, `Emprestimo.calcularMulta(LocalDate)`, `ServicoEmprestimo.cadastrarItem()`, `registrarEmprestimo()`, `registrarDevolucao()`. |
| **4.2** | Composição | `Emprestimo` tem um `Item` e tem uma `Pessoa`. Não herda de nenhum deles. |
| **4.2** | Objeto com lista de outros objetos | `ServicoEmprestimo` gerencia atributos com `List<Item>`, `List<Pessoa>` e `List<Emprestimo>`. |
| **4.3** | Coleções (`ArrayList`) | `ArrayList` utilizado para acervo, pessoas e empréstimos. |
| **4.3** | Busca, filtro e total acumulado | `buscarItemPorPatrimonio()`, `listarItensDisponiveis()`, `listarItensEmprestados()`, `calcularTotalMultas()`. |
| **4.3** | Encapsulamento de coleções | As listas internas nunca são retornadas cruas: protegidas com `Collections.unmodifiableList(...)`. |
| **4.4** | Classe abstrata e subclasses | `Item` é classe abstrata; `Notebook`, `Projetor` e `KitComponentes` são as subclasses concretas. |
| **4.4** | Método abstrato com `@Override` | `getPrazoEmprestimoDias()` e `getTipo()` são abstratos em `Item` e sobrescritos nas subclasses. |
| **4.4** | Chamada ao construtor com `super` | Todas as subclasses chamam `super(patrimonio, descricao, emprestado, regraMulta)`. |
| **4.4** | Sobrescrita de `toString()` | Implementado em `Item`, `Notebook`, `Projetor`, `KitComponentes`, `Pessoa` e `Emprestimo`. |
| **4.5** | Interface para regra de multa | `RegraMulta` com implementações `MultaFixa`, `MultaProgressiva` e `IsencaoMulta` (Pattern Strategy). |
| **4.5** | Injeção de dependências no construtor | `ServicoEmprestimo` recebe `IRepositorioAcervo` e `IRepositorioPessoas` pelo construtor; não dá `new` nelas. |
| **4.5** | Polimorfismo sem `if` de tipo | `item.calcularMulta(diasAtraso)` e `item.getPrazoEmprestimoDias()` respondem polimorficamente sobre qualquer coleção de `Item`. |
| **4.6** | Exceção personalizada de negócio | `ItemJaEmprestadoException` lançada ao tentar emprestar item já ocupado. |
| **4.6** | Tratamento e propagação de exceções | `throw` no domínio/serviço e `try/catch` na interface de exibição (terminal ou caixas de diálogo `Alert`). |
| **4.6** | Robustez na leitura de arquivos | `FormatoArquivoInvalidoException` e mensagens claras para arquivo ausente ou linhas corrompidas. Sem `catch` genérico vazio. |
| **4.8** | Boas práticas | Nomenclatura em português, alta coesão, baixo acoplamento e obediência à Lei de Deméter. |

---

## 4. Banco de Dados do Sistema (Arquivos de Texto)

- `dados/acervo.csv`: formato `tipo;patrimonio;descricao`
- `dados/pessoas.csv`: formato `matricula;nome`
- Leitura com `BufferedReader` e `split(";")`; gravação com `BufferedWriter`.
- Linhas vazias e iniciadas por `#` são ignoradas como comentários.

---

## 5. Como Executar

### 5.1 Pré-requisitos
- **Java:** OpenJDK 21 LTS
- **Maven:** Apache Maven 3.8+ (exigido para a Etapa 2 / JavaFX)

### 5.2 Executando a Entrega 1 (Modelo no Terminal)
A Entrega 1 opera em Java 21 puro, compilando e executando diretamente sem dependência de Maven:
```bash
./executar-etapa1.sh
```

Para abrir o menu interativo no terminal:
```bash
./executar-etapa1.sh --interativo
```

### 5.3 Executando a Entrega 2 (Interface Gráfica JavaFX)
```bash
./executar-etapa2.sh
```
Ou via Maven:
```bash
mvn javafx:run
```

---

## 6. Gabarito de Defesa Didática (Perguntas da Seção 8.2)

1. **Por que `Item` é classe abstrata em vez de ser uma interface?**  
   *Resposta:* Porque `Item` define a identidade ontológica dos equipamentos ("É UM Item"). Ele compartilha estado comum (campos privados `patrimonio`, `descricao`, `emprestado` e `regraMulta`), fornece implementação de código reutilizável (`emprestar()`, `devolver()`, `toString()`) e apenas declara os comportamentos variáveis como abstratos (`getPrazoEmprestimoDias()`). Uma interface não pode guardar estado mutável de instâncias.

2. **Por que `RegraMulta` é uma interface em vez de classe abstrata?**  
   *Resposta:* Porque representa um contrato puro de comportamento ("CALCULA MULTA") desacoplado da hierarquia de itens. Isso implementa o padrão de projeto **Strategy**, permitindo que diferentes estratégias de cálculo sejam intercambiáveis e que novas regras de multa possam surgir no futuro sem alterar a hierarquia de `Item`.

3. **Por que o construtor do `Item` recebe a regra de multa em vez de dar `new` dentro dele?**  
   *Resposta:* Para aplicar o princípio de **Inversão de Controle e Injeção de Dependências**. Receber a dependência por construtor desacopla o item da regra concreta, facilitando testes unitários com regras mockadas e permitindo estender políticas de multa sem modificar as classes existentes (Princípio Aberto/Fechado - OCP).

4. **Como o sistema sabe qual prazo e qual multa aplicar sem nenhum `if` de tipo?**  
   *Resposta:* Através de **Polimorfismo dinâmico**. O compilador Java despacha a invocação do método para a implementação da classe real em tempo de execução (*late binding*). Quando invocamos `item.calcularMulta(dias)`, a chamada é delegada para o objeto de `RegraMulta` associado àquela instância concreta (`MultaFixa`, `MultaProgressiva` ou `IsencaoMulta`).

5. **O que acontece se a classe chamadora tentar modificar a lista do `getAcervo()`?**  
   *Resposta:* O método retorna uma visualização imutável criada com `Collections.unmodifiableList()`. Qualquer tentativa de adicionar ou remover elementos diretamente (como `getAcervo().clear()`) lançará `UnsupportedOperationException`, preservando o encapsulamento interno do serviço.
