# Barbearia de Hilzer — versão modular inicial

Código-fonte Java dividido em classes para o Trabalho Prático I de Processamento Paralelo.

## Arquivos

- `Main.java`: configura e inicia a simulação.
- `Barbearia.java`: controla capacidade, filas, sincronização e POS.
- `Cliente.java`: implementa cada cliente como `Runnable`.
- `Barbeiro.java`: implementa cada barbeiro como `Runnable`.
- `Estatisticas.java`: calcula e imprime métricas simples.
- `Log.java`: imprime eventos cronometrados.

## Compilação e execução

Requer JDK 17 ou superior. No PowerShell, dentro desta pasta:

```powershell
javac *.java
java Main 50 80 180 60
```

Argumentos: número de clientes, intervalo de chegada em ms, tempo-base do corte em ms e tempo de pagamento em ms. Para comparar os cenários, mantenha os argumentos iguais.

Cenário A no Windows, restrito ao núcleo lógico 0:

```powershell
Start-Process java -ArgumentList '-cp . Main 50 80 180 60' -Affinity 1 -Wait
```

Cenário B, livre para usar os núcleos disponíveis:

```powershell
java Main 50 80 180 60
```

## Sincronização implementada

A capacidade total é limitada por um semáforo justo de 20 vagas. Um `ReentrantLock` protege as filas e o contador de ocupação. As filas FIFO do sofá e da espera em pé são explícitas; retirar do sofá e promover quem está há mais tempo em pé ocorre sob o mesmo lock. Uma `Condition` bloqueia os barbeiros quando não há clientes. Outro semáforo justo garante uso exclusivo da POS.

Os tempos de corte e pagamento usam `Thread.sleep()`, portanto representam espera bloqueada e não carga real de CPU. As métricas são uma estimativa inicial e devem ser revisadas junto com os logs antes de serem usadas no relatório.
