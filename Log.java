public final class Log {
    private static final long INICIO = System.nanoTime();
    private Log() { }

    public static synchronized void evento(String ator, String evento, String detalhe) {
        long ns = System.nanoTime() - INICIO;
        long ms = ns / 1_000_000;
        System.out.printf("[%02d:%02d.%03d] [%-14s] %-20s %s%n",
                ms / 60_000, (ms / 1_000) % 60, ms % 1_000, ator, evento, detalhe);
    }

    public static long tempoDecorridoMs() { return (System.nanoTime() - INICIO) / 1_000_000; }
}

