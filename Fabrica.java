import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

class Fabrica {
    private final int ESTOQUE_MAXIMO_PECAS = 500;
    private final Semaphore semaforoEsteiraPecas = new Semaphore(5, true);
    private final AtomicInteger pecasDisponiveis = new AtomicInteger(ESTOQUE_MAXIMO_PECAS);
    private final List<EstacaoProducao> estacoes = new ArrayList<>();
    private final EsteiraVeiculos esteiraVeiculos = new EsteiraVeiculos(40);
    private final List<String> logsProducao = Collections.synchronizedList(new ArrayList<>());
    private final List<String> logsVenda = Collections.synchronizedList(new ArrayList<>());
    private boolean producaoAtiva = true;
    private boolean producaoEncerrada = false;

    public Fabrica() {
        // cria 4 estações de produção
        for (int i = 1; i <= 4; i++) {
            estacoes.add(new EstacaoProducao(i, this));
        }
    }

    public void iniciarProducao() {
        estacoes.forEach(EstacaoProducao::iniciar);

        new Thread(() -> {
            int reposicoes = 0;
            while (producaoAtiva) {
                if (pecasDisponiveis.get() < 100) {
                    if (reposicoes < 3){
                        int repor = ESTOQUE_MAXIMO_PECAS - pecasDisponiveis.get();
                        pecasDisponiveis.addAndGet(repor);
                        reposicoes++;
                        System.out.println("Respostas " + repor + " pecas. Total: " + pecasDisponiveis.get());
                    } else {
                        encerrarProducao();
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
    }

    public boolean pegarPecas(int quantidade) {
        try {
            semaforoEsteiraPecas.acquire();
            if (pecasDisponiveis.get() >= quantidade) {
                pecasDisponiveis.addAndGet(-quantidade);
                semaforoEsteiraPecas.release();
                return true;
            }
            semaforoEsteiraPecas.release();
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public void adicionarVeiculoEsteira(Veiculo veiculo) throws InterruptedException {
        esteiraVeiculos.adicionarVeiculo(veiculo);
        veiculo.setPosicaoEsteira(esteiraVeiculos.getQuantidadeVeiculos());
        registrarProducao(veiculo);
    }

    public Veiculo venderParaLoja(int idLoja) throws InterruptedException {
        Veiculo veiculo = esteiraVeiculos.removerVeiculo();
        if (veiculo != null) {
            veiculo.setIdLoja(idLoja);
            registrarVenda(veiculo);
        }
        return veiculo;
    }

    private void registrarProducao(Veiculo veiculo) {
        String log = String.format("PRODUCAO - ID: %d, COR: %s, Tipo: %s, Estacao: %d, Funcionario: %d, PosicaoEsteira: %d", 
        veiculo.getId(), veiculo.getCor(), veiculo.getTipo(), veiculo.getIdEstacao(), veiculo.getIdFuncionario(), veiculo.getPosicaoEsteira());
        logsProducao.add(log);
        System.out.println(log);
    }

    private void registrarVenda(Veiculo veiculo) {
        String log = String.format("VENDA - ID: %d, Cor: %s, Tipo: %s, Estacao: %d, Funcionario: %d, Loja: %d, PosicaoEsteiraLoja: %d",
        veiculo.getId(), veiculo.getCor(), veiculo.getTipo(), veiculo.getIdEstacao(), veiculo.getIdFuncionario(), veiculo.getIdLoja(), veiculo.getPosicaoEsteira());
        logsVenda.add(log);
        System.out.println(log);
    }

    public boolean isProducaoAtiva() {
        return producaoAtiva;
    }

    public synchronized void encerrarProducao() {
        if (!producaoEncerrada) {
            producaoAtiva = false;
            producaoEncerrada = true;
            System.out.println(">>> ENCERRANDO PRODUÇÃO: Estoque de peças esgotado.");
        }
    }

    public int getQuantidadeVeiculosNaEsteira() {
        return esteiraVeiculos.getQuantidadeVeiculos();
    }    

    public List<String> getLogsProducao() {
        return new ArrayList<>(logsProducao);
    }

    public List<String> getLogsVenda() {
        return new ArrayList<>(logsVenda);
    }
}