package br.edu.ifpb.laboratorio;

import br.edu.ifpb.laboratorio.modelo.entidade.Item;
import br.edu.ifpb.laboratorio.modelo.entidade.KitComponentes;
import br.edu.ifpb.laboratorio.modelo.entidade.Notebook;
import br.edu.ifpb.laboratorio.modelo.entidade.Projetor;
import br.edu.ifpb.laboratorio.modelo.excecao.ItemJaEmprestadoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitarios do Modelo de Itens (Heranca, Abstracao e Encapsulamento)")
class ItemTest {

    @Test
    @DisplayName("Prazos de emprestimo devem respeitar as regras especificas de cada tipo")
    void testPrazosEmprestimo() {
        Item notebook = new Notebook("PAT-01", "Notebook Dell");
        Item projetor = new Projetor("PAT-02", "Projetor Epson");
        Item kit = new KitComponentes("PAT-03", "Kit Arduino");

        assertEquals(7, notebook.getPrazoEmprestimoDias());
        assertEquals(2, projetor.getPrazoEmprestimoDias());
        assertEquals(15, kit.getPrazoEmprestimoDias());
    }

    @Test
    @DisplayName("Polimorfismo no calculo da multa atraves da superclasse abstrata Item")
    void testCalculoMultaPolimorfico() {
        Item notebook = new Notebook("PAT-01", "Notebook Dell");
        Item projetor = new Projetor("PAT-02", "Projetor Epson");
        Item kit = new KitComponentes("PAT-03", "Kit Arduino");

        int atraso = 3;
        assertEquals(15.0, notebook.calcularMulta(atraso));
        assertEquals(45.0, projetor.calcularMulta(atraso));
        assertEquals(0.0, kit.calcularMulta(atraso));
    }

    @Test
    @DisplayName("ItemJaEmprestadoException deve ser lancada ao tentar emprestar item ja emprestado")
    void testExcecaoItemJaEmprestado() throws ItemJaEmprestadoException {
        Item notebook = new Notebook("PAT-01", "Notebook Dell");
        assertFalse(notebook.isEmprestado());

        // Primeiro emprestimo deve suceder
        notebook.emprestar();
        assertTrue(notebook.isEmprestado());

        // Segundo emprestimo consecutivo DEVE lancar a excecao
        ItemJaEmprestadoException ex = assertThrows(ItemJaEmprestadoException.class, notebook::emprestar);
        assertEquals("PAT-01", ex.getPatrimonio());

        // Apos devolver, pode emprestar novamente
        notebook.devolver();
        assertFalse(notebook.isEmprestado());
        assertDoesNotThrow(notebook::emprestar);
    }

    @Test
    @DisplayName("Validacao de setters e construtores com dados nulos ou vazios")
    void testValidacoesEncapsulamento() {
        assertThrows(IllegalArgumentException.class, () -> new Notebook("", "Descricao"));
        assertThrows(IllegalArgumentException.class, () -> new Notebook("PAT-01", ""));

        Item item = new Notebook("PAT-01", "Descricao Valida");
        assertThrows(IllegalArgumentException.class, () -> item.setDescricao(""));
        assertThrows(IllegalArgumentException.class, () -> item.setDescricao(null));
    }
}
