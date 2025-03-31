import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

class EstacaoProducao {
    private final int id;
    private final Fabrica fabrica;
    private final Funcionario[] funcionarios;
    private final Semaphore[] ferramentas;

    public EstacaoProducao(int id, Fabrica fabrica) {
        this.id = id;
        this.fabrica = fabrica;
        this.funcionarios = new Funcionario[5];
        this.ferramentas = new Semaphore[5];

        for (int i = 0; i < 5; i++) {
            funcionarios[i] = new Funcionario(i + 1, this);
            ferramentas[i] = new Semaphore(1);
        }
    }

    public void iniciar() {
        for (Funcionario funcionario : funcionarios) {
            new Thread(funcionario).start();
        }
    }

    public boolean produzirVeiculo(Funcionario funcionario) {
        int idFuncionario = funcionario.getId();
        int ferramentaEsquerda = idFuncionario - 1;
        int ferramentaDireita = idFuncionario % 5;

        try {
            if (ferramentas[ferramentaEsquerda].tryAcquire()) {
                if(ferramentas[ferramentaDireita].tryAcquire()) {
                    if(fabrica.pegarPecas(10)) {
                        Thread.sleep(ThreadLocalRandom.current().nextInt(500, 1500));
                        Veiculo veiculo = new Veiculo();
                        veiculo.setId(Veiculo.getNextId());
                        veiculo.setCor(id % 3 == 0 ? "VERMELHO" : id % 3 == 1 ? "VERDE" : "AZUL");
                        veiculo.setTipo(id % 2 == 0 ? "SUV" : "SEDAN");
                        veiculo.setIdEstacao(id);
                        veiculo.setIdFuncionario(idFuncionario);

                        fabrica.adicionarVeiculoEsteira(veiculo);

                        ferramentas[ferramentaDireita].release();
                        ferramentas[ferramentaEsquerda].release();
                        return true;
                    } else {
                        ferramentas[ferramentaDireita].release();
                        ferramentas[ferramentaEsquerda].release();
                        return false;
                    }
                } else {
                    ferramentas[ferramentaEsquerda].release();
                    return false;
                }
            }
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public int getId() {
        return id;
    }
}

class Funcionario implements Runnable {
    private final int id;
    private final EstacaoProducao estacao;

    public Funcionario(int id, EstacaoProducao estacao) {
        this.id = id;
        this.estacao = estacao;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            boolean produziu = estacao.produzirVeiculo(this);
            if (!produziu) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    public int getId() {
        return id;
    }
}