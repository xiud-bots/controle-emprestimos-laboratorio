#!/usr/bin/env bash
# Script para executar a Entrega 2: A Interface Grafica JavaFX
# Projeto Final de POO - Controle de Emprestimos do Laboratorio (IFPB Campus Monteiro)

set -e

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
cd "$DIR"

echo "==============================================================================="
echo "   Iniciando Entrega 2: Interface Grafica JavaFX com FXML e Estilos CSS       "
echo "==============================================================================="

# Garante o DISPLAY caso nao esteja exportado
export DISPLAY="${DISPLAY:-:0}"

mvn javafx:run
