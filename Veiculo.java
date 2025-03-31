import java.util.concurrent.atomic.AtomicInteger;

class Veiculo {
    private static final AtomicInteger nextId = new AtomicInteger(1);

    private int id;
    private String cor;
    private String tipo;
    private int idEstacao;
    private int idFuncionario;
    private int posicaoEsteira;
    private int idLoja;
    private int posicaoEsteiraLoja;
    private int idCliente;

    public Veiculo() {
        this.id = nextId.getAndIncrement();
    }

    public static int getNextId() {
        return nextId.get();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getIdEstacao() {
        return idEstacao;
    }

    public void setIdEstacao(int idEstacao) {
        this.idEstacao = idEstacao;
    }

    public int getIdFuncionario() {
        return idFuncionario;
    }

    public void setIdFuncionario(int idFuncionario) {
        this.idFuncionario = idFuncionario;
    }

    public int getPosicaoEsteira() {
        return posicaoEsteira;
    }

    public void setPosicaoEsteira(int posicaoEsteira) {
        this.posicaoEsteira = posicaoEsteira;
    }

    public int getIdLoja() {
        return idLoja;
    }

    public void setIdLoja(int idLoja) {
        this.idLoja = idLoja;
    }

    public int getPosicaoEsteiraLoja() {
        return posicaoEsteiraLoja;
    }

    public void setPosicaoEsteiraLoja(int posicaoEsteiraLoja) {
        this.posicaoEsteiraLoja = posicaoEsteiraLoja;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }
}