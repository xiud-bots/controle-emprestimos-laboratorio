#!/usr/bin/env bash
# Script para executar a Entrega 1: O Modelo no Terminal
# Projeto Final de POO - Controle de Emprestimos do Laboratorio (IFPB Campus Monteiro)

set -e

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
cd "$DIR"

echo "==============================================================================="
echo "   Executando Entrega 1: Demonstracao do Modelo no Terminal (Secao 7)         "
echo "==============================================================================="

mvn exec:java -Dexec.mainClass="br.edu.ifpb.laboratorio.terminal.PrincipalTerminal" -Dexec.args="$*"
