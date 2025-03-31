import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class EsteiraVeiculos {
    private final int capacidade;
    final Queue<Veiculo> veiculos;
    private final Semaphore semaforo;
    private final Lock lock;

    public EsteiraVeiculos(int capacidade) {
        this.capacidade = capacidade;
        this.veiculos = new LinkedList<>();
        this.semaforo = new Semaphore(capacidade);
        this.lock = new ReentrantLock();
    }

    public void adicionarVeiculo(Veiculo veiculo) {
        try {
            semaforo.acquire();
            lock.lock();
            try {
                veiculo.setPosicaoEsteira(veiculos.size() + 1);
                veiculos.add(veiculo);
            } finally {
                lock.unlock();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public Veiculo removerVeiculo() {
        lock.lock();
        try {
            if (!veiculos.isEmpty()) {
                Veiculo veiculo = veiculos.poll();
                semaforo.release();
                return veiculo;
            }
            return null;
        } finally {
            lock.unlock();
        }
    }

    public boolean isEmpty() {
        return veiculos.isEmpty();
    }
}