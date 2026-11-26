#!/usr/bin/env bash
# Script para compilar e executar a Entrega 1: O Modelo no Terminal
# Projeto Final de POO - Controle de Emprestimos do Laboratorio (IFPB Campus Monteiro)
# Execucao em Java 21 LTS Puro (Sem Maven)

set -e

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
cd "$DIR"

echo "==============================================================================="
echo "   Executando Entrega 1: Demonstracao do Modelo no Terminal (Secao 7)         "
echo "   Compilacao e Execucao Direta em Java 21 Puro (Sem Maven)                   "
echo "==============================================================================="

mkdir -p bin
javac -d bin -encoding UTF-8 -cp src/main/java $(find src/main/java -name "*.java" ! -path "*/gui/*")

java -cp bin br.edu.ifpb.laboratorio.terminal.PrincipalTerminal "$@"
