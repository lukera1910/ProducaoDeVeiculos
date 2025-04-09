import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Loja {
    private final int id;
    private final Fabrica fabrica;
    private final EsteiraVeiculos esteiraVeiculos;
    private final List<String> logsCompra = Collections.synchronizedList(new ArrayList<>());
    private final List<String> logsVenda = Collections.synchronizedList(new ArrayList<>());

    public Loja(int id, Fabrica fabrica) {
        this.id = id;
        this.fabrica = fabrica;
        this.esteiraVeiculos = new EsteiraVeiculos(20); // capacidade menor que a fábrica
    }

    public void iniciar() {
        // thread para comprar veículos da fabrica
        new Thread(() -> {
            while (true) {
                try {
                    Veiculo veiculo = fabrica.venderParaLoja(id);
                    if (veiculo != null) {
                        veiculo.setPosicaoEsteiraLoja(esteiraVeiculos.getQuantidadeVeiculos());
                        esteiraVeiculos.adicionarVeiculo(veiculo);
                        registrarCompra(veiculo);
                    } else {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
    }

    public Veiculo venderParaCliente(int idCliente) throws InterruptedException {
        Veiculo veiculo = esteiraVeiculos.removerVeiculo();
        if (veiculo != null) {
            veiculo.setIdCliente(idCliente);
            registrarVenda(veiculo);
        }
        return veiculo;
    }

    private void registrarCompra(Veiculo veiculo) {
        String log = String.format("LOJA %d - COMPRA - ID: %d, Cor: %s, Tipo: %s, Estacao: %d, Funcionario: %d, PosicaoEsteira: %d",
                id, veiculo.getId(), veiculo.getCor(), veiculo.getTipo(), 
                veiculo.getIdEstacao(), veiculo.getIdFuncionario(), veiculo.getPosicaoEsteiraLoja());
        logsCompra.add(log);
        System.out.println(log);   
    }

    private void registrarVenda(Veiculo veiculo) {
        String log = String.format("LOJA %d - VENDA - ID: %d, Cor: %s, Tipo: %s, Cliente: %d",
                id, veiculo.getId(), veiculo.getCor(), veiculo.getTipo(), veiculo.getIdCliente());
        logsVenda.add(log);
        System.out.println(log);
    }

    public int getId() {
        return id;
    }

    public List<String> getLogsCompra() {
        return new ArrayList<>(logsCompra);
    }

    public List<String> getLogsVenda() {
        return new ArrayList<>(logsVenda);
    }
}