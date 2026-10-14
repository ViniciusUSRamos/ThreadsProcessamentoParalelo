public class Estatisticas {
    private long esperaPeNs, esperaSofaNs, esperaPosNs, permanenciaNs;
    private int atendidos;
    private final long[] ocupadoNs = new long[Barbearia.quantidadeBarbeiros];

    public synchronized void registrarAtividade(int barbeiro, long atividadeNs) {
        ocupadoNs[barbeiro - 1] += atividadeNs;
    }

    public synchronized void registrarSaida(Cliente cliente) {
        if (cliente.atendimentoFoiCancelado()) return;
        esperaPeNs += cliente.esperaEmPeNs();
        esperaSofaNs += cliente.esperaSofaNs();
        esperaPosNs += cliente.esperaPosNs();
        permanenciaNs += cliente.permanenciaNs();
        atendidos++;
    }

    public synchronized void imprimir(int solicitados) {
        long tempoTotalNs = Log.tempoDecorridoNs();
        double tempoTotalMs = tempoTotalNs / 1_000_000.0;
        System.out.println("\n=== RESUMO ===");
        System.out.println("Atendidos: " + atendidos + " / " + solicitados);
        System.out.printf("Tempo total (ms): %.2f%n", tempoTotalMs);
        if (atendidos == 0) return;
        System.out.printf("Espera media em pe de todos os clientes atendidos (ms): %.2f%n",
                esperaPeNs / (double) atendidos / 1_000_000);
        System.out.printf("Espera media no sofa (ms): %.2f%n", esperaSofaNs / (double) atendidos / 1_000_000);
        System.out.printf("Espera media na POS (ms): %.2f%n", esperaPosNs / (double) atendidos / 1_000_000);
        System.out.printf("Permanencia media no recinto (ms): %.2f%n", permanenciaNs / (double) atendidos / 1_000_000);
        for (int i = 0; i < Barbearia.quantidadeBarbeiros; i++)
            System.out.printf("Utilizacao B%d (corte + pagamento): %.2f%%%n", i + 1,
                    100.0 * ocupadoNs[i] / Math.max(1, tempoTotalNs));
    }
}

