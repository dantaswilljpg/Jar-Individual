package sistemaCaptura;

import sistemaCaptura.conexao.Conexao;
import sistemaCaptura.HistConsmRecurso;
import sistemaCaptura.Maquina;
import sistemaCaptura.Usuario;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Scanner;

public class AppHistorico {
    public static void main(String[] args) {
        Conexao conexao = new Conexao();
        JdbcTemplate con = conexao.getConexaoDoBanco();
        HistConsmRecurso histConsmRecurso = new HistConsmRecurso();

        Scanner in = new Scanner(System.in);
        Integer escolha;

        do {
            System.out.println("-".repeat(15));
            System.out.println("Bem vindo ao sistema Nowl");
            System.out.println("Escolha uma das opções abaixo");
            System.out.println("1 - Cadastrar Usuário");
            System.out.println("2 - Fazer login");
            System.out.println("3 - Cadastrar Máquina");
            System.out.println("4 - Editar Usuário");
            System.out.println("5 - Deletar Usuário");
            System.out.println("6 - Editar Máquina");
            System.out.println("7 - Deletar Máquina");
            System.out.println("8 - Ver todas informações de Usuário");
            System.out.println("9 - Ver todas informações de Máquina");
            System.out.println("10 - Cadastrar Instituição");
            System.out.println("11 - Editar Instituição");
            System.out.println("12 - Deletar Instituição");
            System.out.println("13 - Ver todas informações de Instituição");
            System.out.println("14 - Sair");
            System.out.println("-".repeat(15));


            escolha = in.nextInt();

            switch (escolha) {
                case 1:
                    cadastrarUsuario(con);
                    break;
                case 2:
                    fazerLogin(con, histConsmRecurso, in);
                    break;
                case 3:
                    cadastrarMaquina(con);
                    break;
                case 4:
                    atualizarUsuario(con);
                    break;
                case 5:
                    deletarUsuario(con);
                    break;
                case 6:
                    editarMaquina(con);
                    break;
                case 7:
                    deletarMaquina(con);
                    break;
                case 8:
                    verTodasInformacoesUsuario(con);
                    break;
                case 9:
                    verTodasInformacoesMaquina(con);
                    break;
                case 10:
                    cadastrarInstituicao(con);
                    break;
                case 11:
                    editarInstituicao(con);
                    break;
                case 12:
                    deletarInstituicao(con);
                    break;
                case 13:
                    verTodasInformacoesInstituicao(con);
                    break;
                case 14:
                    exibirMensagemDespedida();
                    histConsmRecurso.fecharSistema();
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        } while (escolha != 14);
    }

    private static void cadastrarUsuario(JdbcTemplate con) {
        Scanner leitor = new Scanner(System.in);

        System.out.println("Digite o nome do usuário (mínimo de 3 caracteres):");
        String nome = leitor.nextLine();
        while (nome.length() < 3) {
            System.out.println("O nome deve ter no mínimo 3 caracteres. Tente novamente:");
            nome = leitor.nextLine();
        }

        System.out.println("Digite o email do usuário (deve conter '@'):");
        String email = leitor.nextLine();
        while (!email.contains("@")) {
            System.out.println("O email deve conter o caractere '@'. Tente novamente:");
            email = leitor.nextLine();
        }

        System.out.println("Digite a senha do usuário (mínimo de 8 caracteres):");
        String senha = leitor.nextLine();
        while (senha.length() < 8) {
            System.out.println("A senha deve ter no mínimo 8 caracteres. Tente novamente:");
            senha = leitor.nextLine();
        }

        System.out.println("Digite o ID da instituição (fkInstituicao):");
        int fkInstituicao = leitor.nextInt();

        System.out.println("Digite o ID do tipo de usuário (fkTipoUsuario) (deve ser 1, 2 ou 3):");
        int fkTipoUsuario = leitor.nextInt();
        while (fkTipoUsuario < 1 || fkTipoUsuario > 3) {
            System.out.println("O ID do tipo de usuário deve ser 1, 2 ou 3. Tente novamente:");
            fkTipoUsuario = leitor.nextInt();
        }

        con.update("INSERT INTO usuario (nome, email, senha, fkInstituicao, fkTipoUsuario) VALUES (?, ?, ?, ?, ?)", nome, email, senha, fkInstituicao, fkTipoUsuario);

        System.out.println("Usuário cadastrado com sucesso!");
    }

    private static void cadastrarMaquina(JdbcTemplate con) {
        Scanner leitor = new Scanner(System.in);

        System.out.println("Digite o nome da máquina:");
        String nomeMaquina = leitor.nextLine();

        System.out.println("Digite o sistema operacional da máquina:");
        String sistemaOperacional = leitor.nextLine();

        System.out.println("A máquina está em uso? (Digite 1 para sim, 0 para não):");
        int emUso = leitor.nextInt();

        System.out.println("Digite a chave estrangeira da instituição (fkInstituicao):");
        int fkInstituicao = leitor.nextInt();

        con.update("INSERT INTO maquina (nome, SO , emUso, fkInstituicao) VALUES (?, ?, ?, ?)", nomeMaquina, sistemaOperacional, emUso, fkInstituicao);

        System.out.println("Máquina cadastrada com sucesso!");
    }

    private static void fazerLogin(JdbcTemplate con, HistConsmRecurso histConsmRecurso, Scanner in) {
        Scanner leitor = new Scanner(System.in);

        System.out.println("Digite o seu email:");
        String email = leitor.nextLine();
        while (!email.contains("@")) {
            System.out.println("O email deve conter o caractere '@'. Tente novamente:");
            email = leitor.nextLine();
        }

        System.out.println("Digite a sua senha (mínimo de 8 caracteres):");
        String senha = leitor.nextLine();
        while (senha.length() < 8) {
            System.out.println("A senha deve ter no mínimo 8 caracteres. Tente novamente:");
            senha = leitor.nextLine();
        }

        List<Usuario> usuarios = con.query("SELECT * FROM usuario WHERE email = ? AND senha = ?",
                new BeanPropertyRowMapper<>(Usuario.class), email, senha);

        if (usuarios.size() > 0) {
            Usuario usuario = usuarios.get(0);
            System.out.println("Bem vindo " + usuario.getNome());

            Integer opcaoUsuario;

            do {
                System.out.println("-".repeat(15));
                System.out.println("Escolha uma das opções abaixo");
                System.out.println("1 - Ativar máquina");
                System.out.println("2 - Fechar sistema");
                System.out.println("-".repeat(15));

                opcaoUsuario = in.nextInt();

                switch (opcaoUsuario) {
                    case 1:
                        List<Maquina> maquinas = con.query("SELECT * FROM maquina WHERE emUso = 0",
                                new BeanPropertyRowMapper<>(Maquina.class));

                        System.out.println("-".repeat(15));
                        System.out.println("Escolha uma máquina disponível");

                        for (Maquina maquina : maquinas) {
                            System.out.println("id: " + maquina.getIdMaquina());
                            System.out.println("nome: " + maquina.getNome());
                            System.out.println("Sistema Operacional: " + histConsmRecurso.obterSistemaOperacionalDaMaquina(con, maquina));
                            if (maquina.getDetalhes() != null) {
                                System.out.println("Detalhes: " + maquina.getDetalhes());
                            }
                        }

                        System.out.println("-".repeat(15));
                        System.out.println("Digite o número da máquina");
                        Integer numMaquina = in.nextInt();
                        ativarMaquina(con, numMaquina, histConsmRecurso);
                        histConsmRecurso.mostrarHistorico();
                        break;

                    case 2:
                        exibirMensagemDespedida();
                        histConsmRecurso.fecharSistema();
                        return; // Isso encerrará o programa
                    default:
                        System.out.println("Opção inválida");
                }
            } while (opcaoUsuario != 2);
        } else {
            System.out.println("Dados de login inválidos");
        }
    }

    private static void ativarMaquina(JdbcTemplate con, Integer maquinaId, HistConsmRecurso histConsmRecurso) {
        Maquina maquina = con.queryForObject("SELECT * FROM maquina WHERE idMaquina = ? AND emUso = 0",
                new BeanPropertyRowMapper<>(Maquina.class), maquinaId);

        if (maquina != null) {

            String sistemaOperacional = histConsmRecurso.obterSistemaOperacionalDaMaquina(con, maquina);
            maquina.setSistemaOperacional(sistemaOperacional);

            con.update("UPDATE maquina SET emUso = 1 WHERE idMaquina = ?", maquinaId);
            System.out.println("Máquina ativada com sucesso: " + maquina.getNome());

        } else {
            System.out.println("Máquina não disponível ou inválida.");
        }
    }


    private static void atualizarUsuario(JdbcTemplate con) {
        Scanner leitor = new Scanner(System.in);

        System.out.println("Digite o ID do usuário que deseja atualizar:");
        int userId = leitor.nextInt();

        System.out.println("Digite o novo nome do usuário (mínimo de 3 caracteres):");
        String nome = leitor.next();
        while (nome.length() < 3) {
            System.out.println("O nome deve ter no mínimo 3 caracteres. Tente novamente:");
            nome = leitor.next();
        }

        System.out.println("Digite o novo email do usuário (deve conter '@'):");
        String email = leitor.next();
        while (!email.contains("@")) {
            System.out.println("O email deve conter o caractere '@'. Tente novamente:");
            email = leitor.next();
        }

        System.out.println("Digite a nova senha do usuário (mínimo de 8 caracteres):");
        String senha = leitor.next();
        while (senha.length() < 8) {
            System.out.println("A senha deve ter no mínimo 8 caracteres. Tente novamente:");
            senha = leitor.next();
        }

        System.out.println("Digite o novo TipoUsuario (deve ser 1, 2 ou 3):");
        int fkTipoUsuario = leitor.nextInt();
        while (fkTipoUsuario < 1 || fkTipoUsuario > 3) {
            System.out.println("O TipoUsuario deve ser 1, 2 ou 3. Tente novamente:");
            fkTipoUsuario = leitor.nextInt();
        }

        con.update("UPDATE usuario SET nome = ?, email = ?, senha = ?, fkTipoUsuario = ? WHERE idUsuario = ?", nome, email, senha, fkTipoUsuario, userId);
        System.out.println("Usuário atualizado com sucesso!");
    }

    private static void deletarUsuario(JdbcTemplate con) {
        Scanner leitor = new Scanner(System.in);

        System.out.println("Digite o ID do usuário que deseja deletar:");
        int userId = leitor.nextInt();

        con.update("DELETE FROM usuario WHERE idUsuario = ?", userId);
        System.out.println("Usuário deletado com sucesso!");
    }

    private static void editarMaquina(JdbcTemplate con) {
        Scanner leitor = new Scanner(System.in);

        System.out.println("Digite o ID da máquina que deseja editar:");
        int maquinaId = leitor.nextInt();

        System.out.println("Digite o novo nome da máquina:");
        String nomeMaquina = leitor.next();

        System.out.println("Digite o novo sistema operacional da máquina:");
        String sistemaOperacional = leitor.next();

        con.update("UPDATE maquina SET nome = ?, SO = ? WHERE idMaquina = ?", nomeMaquina, sistemaOperacional, maquinaId);
        System.out.println("Máquina editada com sucesso!");
    }

    private static void deletarMaquina(JdbcTemplate con) {
        Scanner leitor = new Scanner(System.in);

        System.out.println("Digite o ID da máquina que deseja deletar:");
        int maquinaId = leitor.nextInt();

        con.update("DELETE FROM maquina WHERE idMaquina = ?", maquinaId);
        System.out.println("Máquina deletada com sucesso!");
    }

    private static void verTodasInformacoesUsuario(JdbcTemplate con) {
        System.out.println("Lista de informações de Usuário:");
        List<Usuario> usuarios = con.query("SELECT * FROM usuario",
                new BeanPropertyRowMapper<>(Usuario.class));

        for (Usuario usuario : usuarios) {
            System.out.println("ID: " + usuario.getIdUsuario());
            System.out.println("Nome: " + usuario.getNome());
            System.out.println("Email: " + usuario.getEmail());
            System.out.println("Senha: " + usuario.getSenha());
            System.out.println("Instituição (fkInstituicao): " + usuario.getFkInstituicao());
            System.out.println("Tipo de Usuário (fkTipoUsuario): " + usuario.getFkTipoUsuario());
            System.out.println("-".repeat(15));
        }
    }

    private static void verTodasInformacoesMaquina(JdbcTemplate con) {
        System.out.println("Lista de informações de Máquina:");
        List<Maquina> maquinas = con.query("SELECT * FROM maquina",
                new BeanPropertyRowMapper<>(Maquina.class));

        for (Maquina maquina : maquinas) {
            System.out.println("ID: " + maquina.getIdMaquina());
            System.out.println("Nome: " + maquina.getNome());
            System.out.println("Sistema Operacional: " + maquina.getSistemaOperacional());
            System.out.println("Em Uso: " + maquina.getEmUso());
            System.out.println("Instituição (fkInstituicao): " + maquina.getFkInstituicao());
            System.out.println("-".repeat(15));
        }
    }

    private static void cadastrarInstituicao(JdbcTemplate con) {
        Scanner leitor = new Scanner(System.in);

        System.out.println("Digite o nome da instituição:");
        String nome = leitor.nextLine();

        System.out.println("Digite a sigla da instituição:");
        String sigla = leitor.nextLine();

        System.out.println("Digite o código hexadecimal (6 caracteres) da instituição:");
        String codigoHex = leitor.nextLine();
        while (codigoHex.length() != 6) {
            System.out.println("O código hexadecimal deve ter 6 caracteres. Tente novamente:");
            codigoHex = leitor.nextLine();
        }

        con.update("INSERT INTO instituicao (nome, sigla, codigoHex) VALUES (?, ?, ?)", nome, sigla, codigoHex);

        System.out.println("Instituição cadastrada com sucesso!");
    }

    private static void deletarInstituicao(JdbcTemplate con) {
        Scanner leitor = new Scanner(System.in);

        System.out.println("Digite o ID da instituição que deseja deletar:");
        int instituicaoId = leitor.nextInt();

        con.update("DELETE FROM instituicao WHERE idInstituicao = ?", instituicaoId);
        System.out.println("Instituição deletada com sucesso!");
    }

    private static void editarInstituicao(JdbcTemplate con) {
        Scanner leitor = new Scanner(System.in);

        System.out.println("Digite o ID da instituição que deseja editar:");
        int instituicaoId = leitor.nextInt();

        System.out.println("Digite o novo nome da instituição:");
        String nome = leitor.next();

        System.out.println("Digite a nova sigla da instituição:");
        String sigla = leitor.next();

        System.out.println("Digite o novo código hexadecimal (6 caracteres) da instituição:");
        String codigoHex = leitor.next();
        while (codigoHex.length() != 6) {
            System.out.println("O código hexadecimal deve ter 6 caracteres. Tente novamente:");
            codigoHex = leitor.next();
        }

        con.update("UPDATE instituicao SET nome = ?, sigla = ?, codigoHex = ? WHERE idInstituicao = ?", nome, sigla, codigoHex, instituicaoId);
        System.out.println("Instituição editada com sucesso!");
    }

    private static void verTodasInformacoesInstituicao(JdbcTemplate con) {
        System.out.println("Lista de informações de Instituições:");
        List<Instituicao> instituicoes = con.query("SELECT * FROM instituicao",
                new BeanPropertyRowMapper<>(Instituicao.class));

        for (Instituicao instituicao : instituicoes) {
            System.out.println("ID: " + instituicao.getIdInstituicao());
            System.out.println("Nome: " + instituicao.getNome());
            System.out.println("Sigla: " + instituicao.getSigla());
            System.out.println("Código Hexadecimal: " + instituicao.getCodigoHex());
            System.out.println("-".repeat(15));
        }
    }

    private static void exibirMensagemDespedida() {
        System.out.println("+---------------------------------------------------------------+");
        System.out.println("|                      \u001B[38;2;109;73;157mMAGISTER\u001B[0m                         |");
        System.out.println("|   \u001B[38;2;109;73;157mObrigado por usar o nosso sistema\u001B[0m               |"); // Cor personalizada
        System.out.println("|   \u001B[38;2;109;73;157mTenha um ótimo dia e uma vida incrível\u001B[0m      |"); // Cor personalizada
        System.out.println("|                                                                   |");
        System.out.println("|                                           |");
        System.out.println("+---------------------------------------------------------------+");
    }
}
