public class Barbeiro implements Runnable {
    private final int id;
    private final Barbearia loja;
    private final long corteMs;
    private final long pagamentoMs;

    public Barbeiro(int id, Barbearia loja, long corteMs, long pagamentoMs) {
        this.id = id;
        this.loja = loja;
        this.corteMs = corteMs;
        this.pagamentoMs = pagamentoMs;
    }

    @Override public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            Cliente cliente = loja.aguardarCliente(id);
            if (cliente == null) return;
            try {
                Log.evento("Barbeiro-" + id, "CORTE_INICIO", cliente.nome());
                long inicioCorte = System.nanoTime();
                try {
                    Thread.sleep(corteMs);
                } finally {
                    loja.registrarAtendimento(id, System.nanoTime() - inicioCorte);
                }
                Log.evento("Barbeiro-" + id, "CORTE_FIM", cliente.nome());
                loja.pagar(cliente, id, pagamentoMs);
                loja.confirmarPagamento(cliente);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Log.evento("Barbeiro-" + id, "INTERRUPCAO", "Atendimento interrompido para " + cliente.nome());
                loja.cancelarAtendimento(cliente);
                return;
            }
        }
    }
}


