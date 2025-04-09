import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class EsteiraVeiculos {
    private final Veiculo[] buffer;
    private final int capacidade;
    private int head = 0; // Índice de remoção
    private int tail = 0; // Índice de inserção
    private final Lock lock = new ReentrantLock();
    private final Condition naoCheio = lock.newCondition();
    private final Condition naoVazio = lock.newCondition();

    public EsteiraVeiculos(int capacidade) {
        this.capacidade = capacidade;
        this.buffer = new Veiculo[capacidade];
    }

    public void adicionarVeiculo(Veiculo veiculo) throws InterruptedException {
        lock.lock();
        try {
            while ((tail + 1) % capacidade == head) { // Se estiver cheio
                naoCheio.await(); // Espera até ter espaço
            }
            buffer[tail] = veiculo;
            tail = (tail + 1) % capacidade; // Avança o índice circularmente
            naoVazio.signal(); // Avisa que há um veículo novo
        } finally {
            lock.unlock();
        }
    }

    public Veiculo removerVeiculo() throws InterruptedException {
        lock.lock();
        try {
            while (head == tail) { // Se estiver vazio
                naoVazio.await(); // Espera até ter um veículo
            }
            Veiculo veiculo = buffer[head];
            head = (head + 1) % capacidade; // Avança o índice circularmente
            naoCheio.signal(); // Avisa que há espaço livre
            return veiculo;
        } finally {
            lock.unlock();
        }
    }

    public int getQuantidadeVeiculos() {
        lock.lock();
        try {
            if (tail >= head) {
                return tail - head;
            } else {
                return capacidade - head + tail;
            }
        } finally {
            lock.unlock();
        }
    }    
}