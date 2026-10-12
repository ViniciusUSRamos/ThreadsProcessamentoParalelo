public class Estatisticas {
    private long esperaPe, esperaSofa, esperaPos, permanencia;
    private int atendidos;
    private final long[] ocupado = new long[3];

    public synchronized void registrarAtividade(int barbeiro, long atividadeNs) {
        ocupado[barbeiro - 1] += atividadeNs;
    }

    public synchronized void registrarSaida(Cliente cliente) {
        esperaPe += cliente.esperaEmPeMs();
        esperaSofa += cliente.esperaSofaMs();
        esperaPos += cliente.esperaPosMs();
        permanencia += cliente.permanenciaMs();
        atendidos++;
    }

    public synchronized void imprimir(int solicitados) {
        long tempoTotalMs = Log.tempoDecorridoMs();
        System.out.println("\n=== RESUMO ===");
        System.out.println("Atendidos: " + atendidos + " / " + solicitados);
        System.out.println("Tempo total (ms): " + tempoTotalMs);
        if (atendidos == 0) return;
        System.out.printf("Espera média em pé (ms): %.2f%n", (double) esperaPe / atendidos);
        System.out.printf("Espera média no sofá (ms): %.2f%n", (double) esperaSofa / atendidos);
        System.out.printf("Espera média na POS (ms): %.2f%n", (double) esperaPos / atendidos);
        System.out.printf("Permanência média no recinto (ms): %.2f%n", (double) permanencia / atendidos);
        for (int i = 0; i < 3; i++)
            System.out.printf("Utilização B%d (corte + pagamento): %.2f%%%n", i + 1,
                    100.0 * ocupado[i] / Math.max(1, tempoTotalMs * 1_000_000));
    }
}

