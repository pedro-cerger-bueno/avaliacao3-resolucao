import java.util.Random;

class Registro {
    int codigo;

    Registro(int codigo) {
        this.codigo = codigo;
    }
}

class Tabela_Hash {
    private Registro[] tabela_hash;
    private String tipo_hash;
    private int tamanho;
    private int num_colisoes = 0;

    Tabela_Hash(int tamanho, String tipo_hash) {
        this.tamanho = tamanho;
        this.tipo_hash = tipo_hash;
        this.tabela_hash = new Registro[tamanho];
    }

    private int Hash(Registro r) {
        switch (tipo_hash) {
            case "divisao":
                return r.codigo % tamanho;
            case "multiplicacao":
                double A = 0.6180339887; 
                return (int) (tamanho * ((r.codigo * A) % 1));
            case "dobramento":
                int primeiros_digitos = r.codigo / 1000;  
                int ultimos_digitos = r.codigo % 1000;  
                return (primeiros_digitos + ultimos_digitos) % tamanho;
            default:
                throw new IllegalArgumentException("Função hash inválida");
        }
    }

    public void inserir(Registro r) {
        int index = Hash(r);
        if (tabela_hash[index] != null) {
            num_colisoes++;
        }
        tabela_hash[index] = r;
    }

    public boolean buscar(Registro r) {
        int index = Hash(r);
        return tabela_hash[index] != null && tabela_hash[index].codigo == r.codigo;
    }

    public int getColisoes() {
        return num_colisoes;
    }
}

public class Main {
    
    
    private static int[] gerar_dados(int tamanho, Random random) {
        int[] dados = new int[tamanho];
        for (int i = 0; i < tamanho; i++) {
            dados[i] = 100000000 + random.nextInt(900000000); 
        }
        return dados;
    }

    public static void main(String[] args) {
        int[] tamanhos_tabela = {1000, 10000, 100000};
        String[] tipos_hash = {"divisao", "multiplicacao", "dobramento"};
        int[] tamanho_conjuntos = {1000000, 5000000, 20000000};

        int[][] dados = new int[3][];
        Random seed_aleatoria = new Random(42); 
        for (int i = 0; i < tamanho_conjuntos.length; i++) {
            dados[i] = gerar_dados(tamanho_conjuntos[i], seed_aleatoria);
        }

        for (int i = 0; i < tamanhos_tabela.length; i++) {
            for (String funcao_hash : tipos_hash) {
                Tabela_Hash tabela_hash = new Tabela_Hash(tamanhos_tabela[i], funcao_hash);

                long tempo_inicial = System.nanoTime();
                for (int valor : dados[i]) {
                    tabela_hash.inserir(new Registro(valor));
                }
                long tempo_total = System.nanoTime() - tempo_inicial;

                System.out.println("Tabela tamanho " + tamanhos_tabela[i] + " com função hash " + funcao_hash);
                System.out.println("Tempo de inserção: " + (tempo_total / 1_000_000) + " ms");
                System.out.println("Colisões: " + tabela_hash.getColisoes());

                long tempo_total_busca = 0;
                for (int j = 0; j < 5; j++) {
                    int valor_busca = dados[i][seed_aleatoria.nextInt(dados[i].length)];
                    long tempo_inicial_busca = System.nanoTime();
                    tabela_hash.buscar(new Registro(valor_busca));
                    tempo_total_busca += System.nanoTime() - tempo_inicial_busca;
                }
                System.out.println("Tempo médio de busca: " + (tempo_total_busca / 5_000_000) + " ms");
                System.out.println("--------------------------------------");
            }
        }
    }
}

