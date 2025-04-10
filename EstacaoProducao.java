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
            // tenta pegar ambas ferramentas
            if (ferramentas[ferramentaEsquerda].tryAcquire()) {
                if(ferramentas[ferramentaDireita].tryAcquire()) {
                    // verifica se há peças disponíveis
                    if(fabrica.pegarPecas(10)) {
                        // simula tempo de produção
                        Thread.sleep(ThreadLocalRandom.current().nextInt(500, 1500));
                        // cria o veículo
                        Veiculo veiculo = new Veiculo();
                        veiculo.setId(Veiculo.getNextId());
                        veiculo.setCor(id % 3 == 0 ? "VERMELHO" : id % 3 == 1 ? "VERDE" : "AZUL");
                        veiculo.setTipo(id % 2 == 0 ? "SUV" : "SEDAN");
                        veiculo.setIdEstacao(id);
                        veiculo.setIdFuncionario(idFuncionario);

                        // adiciona a esteira da fábrica
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

    public Fabrica getFabrica() {
        return fabrica;
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
            if (!estacao.getFabrica().isProducaoAtiva()) {
                System.out.println("Estação " + estacao.getId() + " encerrando trabalho.");
                break;
            }

            estacao.produzirVeiculo(this);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public int getId() {
        return id;
    }
    
    public Fabrica getFabrica() {
        return estacao.getFabrica();
    }
}