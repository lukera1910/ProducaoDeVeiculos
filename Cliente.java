import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

class Cliente extends Thread {
    private final int id;
    private final List<Loja> lojas;
    private final List<Veiculo> garagem = Collections.synchronizedList(new ArrayList<>());
    
    public Cliente(int id, List<Loja> lojas) {
        this.id = id;
        this.lojas = lojas;
    }

    @Override
    public void run() {
        Random random = new Random();
        int compras = random.nextInt(3) + 1;

        for (int i = 0; i < compras; i++) {
            Loja loja = lojas.get(random.nextInt(lojas.size()));

            Veiculo veiculo = loja.venderParaCliente();
            if (veiculo != null) {
                veiculo.setIdCliente(id);
                garagem.add(veiculo);
                System.out.printf("Cliente %d comprou veiculo %d na loja %d%n", id, veiculo.getId(), loja.getId());
            } else {
                System.out.printf("Cliente %d tentou comprar na loja %d mas não havia veiculos%n", id, loja.getId());
            } try {
                Thread.sleep(1000);
                i--;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            try {
                Thread.sleep(random.nextInt(2000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public List<Veiculo> getGaragem() {
        return new ArrayList<>(garagem);
    }
}