import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Classe Cartao
class Cartao {
    private long id;
    private String titulo;
    private String descricao;
    private OffsetDateTime criadoEm;

    public Cartao(long id, String titulo, String descricao) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.criadoEm = OffsetDateTime.now();
    }

    public long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }

    @Override
    public String toString() {
        return "ID: " + id + " | Título: " + titulo + " | Descrição: " + descricao;
    }
}

// Classe ColunaBoard
class ColunaBoard {
    private long id;
    private String nome;
    private String tipo;
    private int ordem;
    private List<Cartao> cartoes;

    public ColunaBoard(long id, String nome, String tipo, int ordem) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.ordem = ordem;
        this.cartoes = new ArrayList<>();
    }

    public void adicionarCartao(Cartao cartao) {
        cartoes.add(cartao);
    }

    public boolean removerCartao(long idCartao) {
        return cartoes.removeIf(c -> c.getId() == idCartao);
    }

    public List<Cartao> getCartoes() {
        return cartoes;
    }

    public String getNome() {
        return nome;
    }

    public int getOrdem() {
        return ordem;
    }

    public Cartao buscarCartao(long idCartao) {
        for (Cartao cartao : cartoes) {
            if (cartao.getId() == idCartao) {
                return cartao;
            }
        }
        return null;
    }
}

// Classe Bloqueio
class Bloqueio {
    private long id;
    private String motivoBloqueio;
    private OffsetDateTime bloqueadoEm;
    private String motivoDesbloqueio;
    private OffsetDateTime desbloqueadoEm;

    public Bloqueio(long id, String motivoBloqueio) {
        this.id = id;
        this.motivoBloqueio = motivoBloqueio;
        this.bloqueadoEm = OffsetDateTime.now();
    }

    public void desbloquear(String motivoDesbloqueio) {
        this.motivoDesbloqueio = motivoDesbloqueio;
        this.desbloqueadoEm = OffsetDateTime.now();
    }

    public String getMotivoBloqueio() {
        return motivoBloqueio;
    }

    public boolean estaBloqueado() {
        return desbloqueadoEm == null;
    }
}

// Classe Board
class Board {
    private long id;
    private String nome;
    private List<ColunaBoard> colunas;
    private List<Bloqueio> bloqueios;
    private long proximoIdCartao;

    public Board(long id, String nome) {
        this.id = id;
        this.nome = nome;
        this.colunas = new ArrayList<>();
        this.bloqueios = new ArrayList<>();
        this.proximoIdCartao = 1;

        // Criar colunas padrão
        colunas.add(new ColunaBoard(1, "A Fazer", "inicial", 1));
        colunas.add(new ColunaBoard(2, "Em Progresso", "intermediario", 2));
        colunas.add(new ColunaBoard(3, "Concluído", "final", 3));
    }

    public String getNome() {
        return nome;
    }

    public void criarCartao(String titulo, String descricao) {
        Cartao novoCartao = new Cartao(proximoIdCartao++, titulo, descricao);
        colunas.get(0).adicionarCartao(novoCartao); // Adiciona na primeira coluna
        System.out.println("Cartão criado com sucesso! ID: " + novoCartao.getId());
    }

    public void moverCartao(long idCartao, int colunaDestino) {
        if (colunaDestino < 0 || colunaDestino >= colunas.size()) {
            System.out.println("Coluna inválida!");
            return;
        }

        Cartao cartao = null;
        ColunaBoard colunaOrigem = null;

        // Buscar cartão em todas as colunas
        for (ColunaBoard coluna : colunas) {
            cartao = coluna.buscarCartao(idCartao);
            if (cartao != null) {
                colunaOrigem = coluna;
                break;
            }
        }

        if (cartao == null) {
            System.out.println("Cartão não encontrado!");
            return;
        }

        // Verificar se o cartão está bloqueado
        if (estaBloqueado(idCartao)) {
            System.out.println("Cartão está bloqueado e não pode ser movido!");
            return;
        }

        colunaOrigem.removerCartao(idCartao);
        colunas.get(colunaDestino).adicionarCartao(cartao);
        System.out.println("Cartão movido para: " + colunas.get(colunaDestino).getNome());
    }

    public void bloquearCartao(long idCartao, String motivo) {
        Bloqueio bloqueio = new Bloqueio(idCartao, motivo);
        bloqueios.add(bloqueio);
        System.out.println("Cartão bloqueado: " + motivo);
    }

    public void desbloquearCartao(long idCartao, String motivo) {
        for (Bloqueio bloqueio : bloqueios) {
            if (bloqueio.estaBloqueado()) {
                bloqueio.desbloquear(motivo);
                System.out.println("Cartão desbloqueado!");
                return;
            }
        }
        System.out.println("Cartão não está bloqueado!");
    }

    private boolean estaBloqueado(long idCartao) {
        for (Bloqueio bloqueio : bloqueios) {
            if (bloqueio.estaBloqueado()) {
                return true;
            }
        }
        return false;
    }

    public void cancelarCartao(long idCartao) {
        for (ColunaBoard coluna : colunas) {
            if (coluna.removerCartao(idCartao)) {
                System.out.println("Cartão cancelado com sucesso!");
                return;
            }
        }
        System.out.println("Cartão não encontrado!");
    }

    public void exibirBoard() {
        System.out.println("\n========== BOARD: " + nome + " ==========");
        for (ColunaBoard coluna : colunas) {
            System.out.println("\n--- " + coluna.getNome() + " ---");
            if (coluna.getCartoes().isEmpty()) {
                System.out.println("  (vazio)");
            } else {
                for (Cartao cartao : coluna.getCartoes()) {
                    System.out.println("  " + cartao);
                }
            }
        }
        System.out.println("\n=====================================\n");
    }
}

// Classe Principal
class SistemaBoard {
    private static List<Board> boards = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    private static long proximoIdBoard = 1;

    public static void main(String[] args) {
        boolean executando = true;

        while (executando) {
            exibirMenu();
            int opcao = scanner.nextInt();
            scanner.nextLine(); // Limpar buffer

            switch (opcao) {
                case 1:
                    criarBoard();
                    break;
                case 2:
                    selecionarBoard();
                    break;
                case 3:
                    excluirBoard();
                    break;
                case 4:
                    executando = false;
                    System.out.println("Encerrando sistema...");
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    private static void exibirMenu() {
        System.out.println("\n===== MENU PRINCIPAL =====");
        System.out.println("1 - Criar novo Board");
        System.out.println("2 - Selecionar Board");
        System.out.println("3 - Excluir Board");
        System.out.println("4 - Sair");
        System.out.print("Escolha uma opção: ");
    }

    private static void criarBoard() {
        System.out.print("Digite o nome do board: ");
        String nome = scanner.nextLine();
        Board novoBoard = new Board(proximoIdBoard++, nome);
        boards.add(novoBoard);
        System.out.println("Board criado com sucesso!");
    }

    private static void selecionarBoard() {
        if (boards.isEmpty()) {
            System.out.println("Nenhum board disponível!");
            return;
        }

        System.out.println("\n===== BOARDS DISPONÍVEIS =====");
        for (int i = 0; i < boards.size(); i++) {
            System.out.println((i + 1) + " - " + boards.get(i).getNome());
        }
        System.out.print("Selecione um board: ");
        int opcao = scanner.nextInt();
        scanner.nextLine();

        if (opcao > 0 && opcao <= boards.size()) {
            menuBoard(boards.get(opcao - 1));
        } else {
            System.out.println("Board inválido!");
        }
    }

    private static void menuBoard(Board board) {
        boolean voltar = false;

        while (!voltar) {
            board.exibirBoard();
            System.out.println("===== MENU DO BOARD =====");
            System.out.println("1 - Criar Card");
            System.out.println("2 - Mover Card");
            System.out.println("3 - Cancelar Card");
            System.out.println("4 - Bloquear Card");
            System.out.println("5 - Desbloquear Card");
            System.out.println("6 - Voltar");
            System.out.print("Escolha uma opção: ");

            int opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    System.out.print("Título do card: ");
                    String titulo = scanner.nextLine();
                    System.out.print("Descrição do card: ");
                    String descricao = scanner.nextLine();
                    board.criarCartao(titulo, descricao);
                    break;
                case 2:
                    System.out.print("ID do card: ");
                    long idMover = scanner.nextLong();
                    System.out.println("0 - A Fazer | 1 - Em Progresso | 2 - Concluído");
                    System.out.print("Coluna destino: ");
                    int coluna = scanner.nextInt();
                    board.moverCartao(idMover, coluna);
                    break;
                case 3:
                    System.out.print("ID do card: ");
                    long idCancelar = scanner.nextLong();
                    board.cancelarCartao(idCancelar);
                    break;
                case 4:
                    System.out.print("ID do card: ");
                    long idBloquear = scanner.nextLong();
                    scanner.nextLine();
                    System.out.print("Motivo do bloqueio: ");
                    String motivoBloquear = scanner.nextLine();
                    board.bloquearCartao(idBloquear, motivoBloquear);
                    break;
                case 5:
                    System.out.print("ID do card: ");
                    long idDesbloquear = scanner.nextLong();
                    scanner.nextLine();
                    System.out.print("Motivo do desbloqueio: ");
                    String motivoDesbloquear = scanner.nextLine();
                    board.desbloquearCartao(idDesbloquear, motivoDesbloquear);
                    break;
                case 6:
                    voltar = true;
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    private static void excluirBoard() {
        if (boards.isEmpty()) {
            System.out.println("Nenhum board disponível!");
            return;
        }

        System.out.println("\n===== BOARDS DISPONÍVEIS =====");
        for (int i = 0; i < boards.size(); i++) {
            System.out.println((i + 1) + " - " + boards.get(i).getNome());
        }
        System.out.print("Selecione um board para excluir: ");
        int opcao = scanner.nextInt();
        scanner.nextLine();

        if (opcao > 0 && opcao <= boards.size()) {
            boards.remove(opcao - 1);
            System.out.println("Board excluído com sucesso!");
        } else {
            System.out.println("Board inválido!");
        }
    }
}