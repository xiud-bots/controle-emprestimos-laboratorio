package br.edu.ifpb.laboratorio;

import br.edu.ifpb.laboratorio.modelo.multa.IsencaoMulta;
import br.edu.ifpb.laboratorio.modelo.multa.MultaFixa;
import br.edu.ifpb.laboratorio.modelo.multa.MultaProgressiva;
import br.edu.ifpb.laboratorio.modelo.multa.RegraMulta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitarios das Regras de Multa (Strategy e Polimorfismo)")
class MultaTest {

    @Test
    @DisplayName("Multa Fixa deve cobrar R$ 5,00 por dia de atraso")
    void testMultaFixa() {
        RegraMulta regra = new MultaFixa();

        assertEquals(0.0, regra.calcular(0));
        assertEquals(0.0, regra.calcular(-2));
        assertEquals(5.0, regra.calcular(1));
        assertEquals(10.0, regra.calcular(2));
        assertEquals(35.0, regra.calcular(7));
    }

    @Test
    @DisplayName("Multa Progressiva: R$ 10 no 1º dia, + R$ 5 a cada dia seguinte")
    void testMultaProgressiva() {
        RegraMulta regra = new MultaProgressiva();

        assertEquals(0.0, regra.calcular(0));
        assertEquals(0.0, regra.calcular(-1));
        // Dia 1: 10
        assertEquals(10.0, regra.calcular(1));
        // Dia 2: 10 + 15 = 25
        assertEquals(25.0, regra.calcular(2));
        // Dia 3: 10 + 15 + 20 = 45
        assertEquals(45.0, regra.calcular(3));
        // Dia 4: 10 + 15 + 20 + 25 = 70
        assertEquals(70.0, regra.calcular(4));
    }

    @Test
    @DisplayName("Isencao de Multa deve sempre retornar 0.0 independente dos dias de atraso")
    void testIsencaoMulta() {
        RegraMulta regra = new IsencaoMulta();

        assertEquals(0.0, regra.calcular(0));
        assertEquals(0.0, regra.calcular(1));
        assertEquals(0.0, regra.calcular(10));
        assertEquals(0.0, regra.calcular(100));
    }

    @Test
    @DisplayName("Valores negativos em construtores devem lancar IllegalArgumentException")
    void testValidacaoConstrutores() {
        assertThrows(IllegalArgumentException.class, () -> new MultaFixa(-1.0));
        assertThrows(IllegalArgumentException.class, () -> new MultaProgressiva(-10.0, 5.0));
        assertThrows(IllegalArgumentException.class, () -> new MultaProgressiva(10.0, -5.0));
    }
}
