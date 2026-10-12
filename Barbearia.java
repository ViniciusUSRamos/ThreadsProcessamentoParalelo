import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Barbearia {
    static final int CAPACIDADE = 20;
    static final int LUGARES_SOFA = 4;
    private final ReentrantLock lock = new ReentrantLock(true);
    private final Condition trabalhoDisponivel = lock.newCondition();
    private final Queue<Cliente> sofa = new ArrayDeque<>();
    private final Queue<Cliente> emPe = new ArrayDeque<>();
    private final Semaphore vagas = new Semaphore(CAPACIDADE, true);
    private final Semaphore pos = new Semaphore(1, true);
    private final Estatisticas estatisticas = new Estatisticas();
    private int dentro;
    private boolean encerrando;

    Condition novaCondicao() { return lock.newCondition(); }

    void aguardarPagamento(Cliente cliente, Condition mudanca) throws InterruptedException {
        lock.lockInterruptibly();
        try { while (!cliente.pagamentoFoiConcluido()) mudanca.await(); }
        finally { lock.unlock(); }
    }

    void confirmarPagamento(Cliente cliente) {
        lock.lock();
        try { cliente.pagamentoConcluido(); }
        finally { lock.unlock(); }
    }

    void entrar(Cliente cliente) throws InterruptedException {
        vagas.acquire();
        lock.lock();
        try {
            cliente.setChegada(System.nanoTime());
            dentro++;
            if (sofa.size() < LUGARES_SOFA) {
                cliente.sentouNoSofa(System.nanoTime());
                sofa.add(cliente);
                Log.evento(cliente.nome(), "ENTRADA_SOFA", "Lotação " + dentro + "/20");
            } else {
                cliente.comecouEsperaEmPe(System.nanoTime());
                emPe.add(cliente);
                Log.evento(cliente.nome(), "ESPERA_EM_PE", "Posição " + emPe.size() + "; lotação " + dentro + "/20");
            }
            trabalhoDisponivel.signal();
        } finally { lock.unlock(); }
    }

    Cliente aguardarCliente() {
        lock.lock();
        try {
            while (sofa.isEmpty() && !encerrando) trabalhoDisponivel.awaitUninterruptibly();
            if (sofa.isEmpty() && encerrando) return null;
            Cliente cliente = sofa.remove();
            cliente.chamado(System.nanoTime());
            promoverProximo();
            return cliente;
        } finally { lock.unlock(); }
    }

    private void promoverProximo() {
        Cliente proximo = emPe.poll();
        if (proximo != null) {
            proximo.promovidoDoChao(System.nanoTime());
            sofa.add(proximo);
            Log.evento(proximo.nome(), "PROMOCAO", "Entrou no sofá; restam " + emPe.size() + " em pé");
        }
    }

    long pagar(Cliente cliente, int barbeiro, long duracaoMs) throws InterruptedException {
        long solicitacao = System.nanoTime();
        Log.evento(cliente.nome(), "PAGAMENTO_SOLICITADO", "Aguardando POS");
        pos.acquire();
        long inicio = System.nanoTime();
        try {
            cliente.registrarEsperaPos(inicio - solicitacao);
            Log.evento("Barbeiro-" + barbeiro, "PAGAMENTO_INICIO", "Cliente " + cliente.nome());
            Thread.sleep(duracaoMs);
            Log.evento("Barbeiro-" + barbeiro, "PAGAMENTO_FIM", "Cliente " + cliente.nome());
            return System.nanoTime() - inicio;
        } finally { pos.release(); }
    }

    void registrarAtendimento(Cliente c, int barbeiro, long atividadeNs) {
        estatisticas.registrarAtividade(barbeiro, atividadeNs);
    }

    void sair(Cliente cliente) {
        lock.lock();
        try {
            dentro--;
            vagas.release();
            cliente.finalizar(System.nanoTime());
            estatisticas.registrarSaida(cliente);
            Log.evento(cliente.nome(), "SAIDA", "Lotação " + dentro + "/20");
        } finally { lock.unlock(); }
    }

    void encerrar() {
        lock.lock();
        try { encerrando = true; trabalhoDisponivel.signalAll(); }
        finally { lock.unlock(); }
    }

    void imprimirResumo(int solicitados) { estatisticas.imprimir(solicitados); }
}


