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
        int compras = random.nextInt(3) + 1; // cada cliente compra de 1 a 3 carros

        for (int i = 0; i < compras; i++) {
            // escolhe uma loja aleatória
            Loja loja = lojas.get(random.nextInt(lojas.size()));

            // tenta comprar um veículo
            Veiculo veiculo = loja.venderParaCliente();
            if (veiculo != null) {
                veiculo.setIdCliente(id);
                garagem.add(veiculo);
                System.out.printf("Cliente %d comprou veiculo %d na loja %d%n", id, veiculo.getId(), loja.getId());
            } else {
                System.out.printf("Cliente %d tentou comprar na loja %d mas não havia veiculos%n", id, loja.getId());
            } try {
                Thread.sleep(1000); // espera antes de tentar novamente
                i--; // tenta novamente esta compra
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            // intervalo entre compras
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