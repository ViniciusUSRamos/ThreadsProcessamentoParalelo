import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int quantidade = args.length > 0 ? Integer.parseInt(args[0]) : 50;
        long intervaloMs = args.length > 1 ? Long.parseLong(args[1]) : 0;
        long corteMs = args.length > 2 ? Long.parseLong(args[2]) : 180;
        long pagamentoMs = args.length > 3 ? Long.parseLong(args[3]) : 60;

        Barbearia loja = new Barbearia();
        List<Thread> barbeiros = new ArrayList<>();
        for (int i = 1; i <= Barbearia.quantidadeBarbeiros; i++) {
            Thread t = new Thread(new Barbeiro(i, loja, corteMs, pagamentoMs), "Barbeiro-" + i);
            barbeiros.add(t);
            t.start();
        }

        List<Thread> clientes = new ArrayList<>();
        for (int i = 1; i <= quantidade; i++) {
            Thread t = new Thread(new Cliente(i, loja), "Cliente-" + i);
            clientes.add(t);
            t.start();
            if (intervaloMs > 0) Thread.sleep(intervaloMs);
        }
        for (Thread t : clientes) t.join();
        loja.encerrar();
        for (Thread t : barbeiros) t.join();
        loja.imprimirResumo(quantidade);
    }
}

