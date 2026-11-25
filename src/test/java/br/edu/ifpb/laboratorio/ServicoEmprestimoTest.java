package br.edu.ifpb.laboratorio;

import br.edu.ifpb.laboratorio.modelo.entidade.Emprestimo;
import br.edu.ifpb.laboratorio.modelo.entidade.Item;
import br.edu.ifpb.laboratorio.modelo.entidade.Notebook;
import br.edu.ifpb.laboratorio.modelo.entidade.Pessoa;
import br.edu.ifpb.laboratorio.modelo.entidade.Projetor;
import br.edu.ifpb.laboratorio.modelo.excecao.ItemJaEmprestadoException;
import br.edu.ifpb.laboratorio.modelo.excecao.ItemNaoEncontradoException;
import br.edu.ifpb.laboratorio.modelo.excecao.PessoaNaoEncontradaException;
import br.edu.ifpb.laboratorio.persistencia.IRepositorioAcervo;
import br.edu.ifpb.laboratorio.persistencia.IRepositorioPessoas;
import br.edu.ifpb.laboratorio.servico.ServicoEmprestimo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do Servico de Emprestimo (Regras de Negocio, Colecoes e Excecoes)")
class ServicoEmprestimoTest {

    private ServicoEmprestimo servico;
    private List<Item> acervoMock;
    private List<Pessoa> pessoasMock;

    @BeforeEach
    void setUp() throws Exception {
        acervoMock = new ArrayList<>();
        acervoMock.add(new Notebook("PAT-10", "Notebook Dell i7"));
        acervoMock.add(new Projetor("PAT-20", "Projetor Epson"));

        pessoasMock = new ArrayList<>();
        pessoasMock.add(new Pessoa("1001", "Aluno Teste 1"));
        pessoasMock.add(new Pessoa("1002", "Aluno Teste 2"));

        IRepositorioAcervo repoAcervo = new IRepositorioAcervo() {
            @Override
            public List<Item> carregar() {
                return new ArrayList<>(acervoMock);
            }
            @Override
            public void salvar(List<Item> itens) {
                acervoMock = new ArrayList<>(itens);
            }
        };

        IRepositorioPessoas repoPessoas = () -> new ArrayList<>(pessoasMock);

        servico = new ServicoEmprestimo(repoAcervo, repoPessoas);
        servico.carregarDados();
    }

    @Test
    @DisplayName("Deve registrar emprestimo e atualizar situacao do item")
    void testRegistrarEmprestimoSucesso() throws Exception {
        Emprestimo emp = servico.registrarEmprestimo("PAT-10", "1001");
        assertNotNull(emp);
        assertTrue(emp.isAtivo());
        assertTrue(emp.getItem().isEmprestado());

        // Verificacao nos filtros
        assertEquals(1, servico.listarItensEmprestados().size());
        assertEquals(1, servico.listarItensDisponiveis().size());
        assertEquals(1, servico.listarEmprestimosAtivos().size());
    }

    @Test
    @DisplayName("Deve lancar ItemJaEmprestadoException ao tentar emprestar item ja emprestado")
    void testRecusaItemJaEmprestado() throws Exception {
        servico.registrarEmprestimo("PAT-10", "1001");

        // Segunda tentativa com mesmo patrimonio
        ItemJaEmprestadoException ex = assertThrows(ItemJaEmprestadoException.class, () ->
                servico.registrarEmprestimo("PAT-10", "1002")
        );
        assertEquals("PAT-10", ex.getPatrimonio());
    }

    @Test
    @DisplayName("Deve registrar devolucao e liberar o item para novo emprestimo")
    void testRegistrarDevolucao() throws Exception {
        servico.registrarEmprestimo("PAT-10", "1001");
        assertTrue(servico.buscarItemPorPatrimonio("PAT-10").isEmprestado());

        Emprestimo dev = servico.registrarDevolucao("PAT-10");
        assertFalse(dev.isAtivo());
        assertFalse(servico.buscarItemPorPatrimonio("PAT-10").isEmprestado());
        assertEquals(2, servico.listarItensDisponiveis().size());
        assertEquals(0, servico.listarEmprestimosAtivos().size());
    }

    @Test
    @DisplayName("Colecoes retornadas devem ser imutaveis (Encapsulamento defensivo)")
    void testEncapsulamentoColecoes() {
        List<Item> acervo = servico.getAcervo();
        assertThrows(UnsupportedOperationException.class, acervo::clear);

        List<Pessoa> pessoas = servico.getPessoas();
        assertThrows(UnsupportedOperationException.class, pessoas::clear);
    }

    @Test
    @DisplayName("Calculo acumulado de multas sobre emprestimos com atraso simulado")
    void testCalculoTotalMultas() throws Exception {
        LocalDate dataEmprestimo = LocalDate.of(2026, 11, 1);
        // Notebook prazo: 7 dias -> devolucao esperada: 08/11/2026
        servico.registrarEmprestimo("PAT-10", "1001", dataEmprestimo);

        // Data referencia: 11/11/2026 (3 dias de atraso no notebook: 3 * R$ 5,00 = R$ 15,00)
        LocalDate dataConsulta = LocalDate.of(2026, 11, 11);
        double totalMultas = servico.calcularTotalMultas(dataConsulta);
        assertEquals(15.0, totalMultas);
    }
}
