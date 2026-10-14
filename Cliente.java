import java.util.concurrent.locks.Condition;

public class Cliente implements Runnable {
    private final int id;
    private final Barbearia loja;
    private final Condition mudanca;
    private boolean pagamentoConcluido;
    private boolean atendimentoCancelado;
    private long chegada;
    private long sentou;
    private long chamada;
    private long esperaEmPeNs;
    private long esperaPosNs;
    private long saida;

    public Cliente(int id, Barbearia loja) {
        this.id = id;
        this.loja = loja;
        this.mudanca = loja.novaCondicao();
    }

    public String nome() { return String.format("Cliente-%02d", id); }
    void setChegada(long t) { chegada = t; }
    void sentouNoSofa(long t) { sentou = t; }
    void comecouEsperaEmPe(long t) { esperaEmPeNs = -t; }
    void promovidoDoChao(long t) { esperaEmPeNs += t; sentou = t; }
    void chamado(long t) { chamada = t; }
    void registrarEsperaPos(long ns) { esperaPosNs = ns; }
    void pagamentoConcluido() { pagamentoConcluido = true; mudanca.signal(); }
    void cancelarAtendimento() { atendimentoCancelado = true; pagamentoConcluido = true; mudanca.signal(); }
    boolean pagamentoFoiConcluido() { return pagamentoConcluido; }
    boolean atendimentoFoiCancelado() { return atendimentoCancelado; }
    void finalizar(long t) { saida = t; }
    long esperaEmPeNs() { return esperaEmPeNs; }
    long esperaSofaNs() { return Math.max(0, chamada - sentou); }
    long esperaPosNs() { return esperaPosNs; }
    long permanenciaNs() { return Math.max(0, saida - chegada); }

    @Override public void run() {
        boolean entrou = false;
        try {
            loja.entrar(this);
            entrou = true;
            loja.aguardarPagamento(this, mudanca);
            loja.sair(this);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Log.evento(nome(), "INTERRUPCAO", entrou ? "Interrompido dentro da barbearia" : "Interrompido antes da entrada");
        }
    }
}


