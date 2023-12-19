package sistemaCaptura;

import sistemaCaptura.conexao.Conexao;
import sistemaCaptura.log.metodos.Logs;
import sistemaCaptura.user.Usuario;
import sistemaCaptura.user.Adiministrador;
import sistemaCaptura.user.Professor;
import sistemaCaptura.user.AdmNowl;
import sistemaCaptura.user.Aluno;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.ArrayList;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class AppHistorico {

    static Logs logs = new Logs();
    static LocalDate dataAtual;
    static Conexao conexao = new Conexao();

    public static void main(String[] args) {
        JdbcTemplate con = conexao.getConexaoDoBancoMySQL();
        JdbcTemplate con2 = conexao.getConexaoDoBancoSQLServer();
        HistConsmRecurso histConsmRecurso = new HistConsmRecurso();


        Scanner in = new Scanner(System.in);
        Integer escolha;
        String motivoComeco = ":--SUCCESS: O Sistema iniciou normalmente";
        logs.adicionarMotivo(motivoComeco);
        do {
            System.out.println("╔══════════════════════════════════════════════════════════════╗");
            System.out.println("║                  Bem-vindo ao MAGISTER de Will Dantas        ║");
            System.out.println("╠══════════════════════════════════════════════════════════════╣");
            System.out.println("║               Explore todas as funcionalidades               ║");
            System.out.println("║               do incrível Sistema Will Dantas.               ║");
            System.out.println("║                                                              ║");
            System.out.println("║                                                              ║");
            System.out.println("║                Tenha uma experiência incrível!               ║");
            System.out.println("║                                                              ║");
            System.out.println("╚══════════════════════════════════════════════════════════════╝");
            System.out.println("╔══════════════════════╗");
            System.out.println("║ Escolha uma opção:   ║");
            System.out.println("╠══════════════════════╣");
            System.out.println("║ 1-Fazer login        ║");
            System.out.println("║ 2-Sair               ║");
            System.out.println("║ 3-Visualizar e editar║");
            System.out.println("║ 4-Enviar sugestão    ║");
            System.out.println("╚══════════════════════╝");

            System.out.println("-".repeat(15));

            System.out.println("Digite um número para iniciar o sistema:");
            escolha = in.nextInt();

            switch (escolha) {
                case 1:
                    fazerLogin(con, con2, histConsmRecurso, in);
                    break;
                case 2:
                    exibirMensagemDespedida();
                    histConsmRecurso.fecharSistema();
                    break;
                case 3:
                    visualizarEditarPerfil(con, con2);
                    break;
                case 4:
                    enviarSugestao(con, in);
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        } while (escolha != 2);
    }

    private static void cadastrarUsuario(JdbcTemplate con, JdbcTemplate con2) {
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

        // Outros campos de cadastro, se necessário

        // Inserir o novo usuário no banco de dados com as colunas fkInstituicao e fkTipoUsuario

        con.update("INSERT INTO usuario (nome, email, senha, fkInstituicao, fkTipoUsuario) VALUES (?, ?, ?, ?, ?)", nome, email, senha, fkInstituicao, fkTipoUsuario);

        con2.update("INSERT INTO usuario (nome, email, senha, fkInstituicao, fkTipoUsuario) VALUES (?, ?, ?, ?, ?)", nome, email, senha, fkInstituicao, fkTipoUsuario);


        System.out.println("Usuário cadastrado com sucesso!");
    }

    private static void fazerLogin(JdbcTemplate con,JdbcTemplate con2, HistConsmRecurso histConsmRecurso, Scanner in) {
        Scanner leitor = new Scanner(System.in);
        Integer numeroMaquina = null;
        Usuario usuario;

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
        List<Usuario> usuarios;
        if (conexao.getDev()) {
            usuarios = con.query("SELECT * FROM usuario WHERE email = ? AND senha = ?",
                    new BeanPropertyRowMapper<>(Usuario.class), email, senha);
        } else {
            usuarios = con2.query("SELECT * FROM usuario WHERE email = ? AND senha = ?",
                    new BeanPropertyRowMapper<>(Usuario.class), email, senha);
        }
        if (usuarios.size() > 0) {
            String motivo = ":--SUCCESS: O Sistema localizou " + usuarios.size() + " registro(s)  relacionado ao email ('" + email + "') e a senha ('" + senha + "')!";
            logs.adicionarMotivo(motivo);

            if (usuarios.get(0).getFkTipoUsuario().equals(1)) {
                usuario = new AdmNowl(usuarios.get(0));
                System.out.println("Bem vindo ADM Nowl-" + usuario.getNome());
            } else if (usuarios.get(0).getFkTipoUsuario().equals(2)) {
                usuario = new Adiministrador(usuarios.get(0));
                System.out.println("Bem vindo ADM- " + usuario.getNome());
            } else if (usuarios.get(0).getFkTipoUsuario().equals(3)) {
                usuario = new Professor(usuarios.get(0));
                System.out.println("Bem vindo Professor- " + usuario.getNome());
            } else {
                usuario = new Aluno(usuarios.get(0));
                System.out.println("Bem vindo Usuario" + usuario.getNome());
            }
            Integer opcaoUsuario;

            do {
                System.out.println("╔════════════════════════════════════╗");
                System.out.println("║        || Opções do Sistema ||     ║");
                System.out.println("╠════════════════════════════════════╣");
                System.out.println("║          1 - Ativar Máquina        ║");

                if (usuario instanceof Professor) {
                    System.out.println("║ 2 - Opções de Professor        ║");
                }
                if (usuario instanceof Adiministrador) {
                    System.out.println("║ 2 - Opções de Professor        ║");
                }
                if (usuario instanceof AdmNowl) {
                    System.out.println("║          2 - Opções de ADM Nowl    ║");
                }
                if (usuario instanceof Aluno) {
                    System.out.println("║ 2 - Opções de Aluno            ║");
                }
                System.out.println("║          3 - Fechar Sistema        ║");
                System.out.println("╚════════════════════════════════════╝");

                opcaoUsuario = in.nextInt();

                switch (opcaoUsuario) {

                    case 1:
                        List<Maquina> maquinas;
                        if (conexao.getDev()) {
                            maquinas = con.query("SELECT * FROM maquina WHERE emUso = 0 AND fkInstituicao = ?",
                                    new BeanPropertyRowMapper<>(Maquina.class), usuario.getFkInstituicao());
                        } else {
                            maquinas = con2.query("SELECT * FROM maquina WHERE emUso = 0 AND fkInstituicao = ?",
                                    new BeanPropertyRowMapper<>(Maquina.class), usuario.getFkInstituicao());
                        }
                        if (maquinas.size() > 0) {
                            String motivoMaquina = ":--SUCCESS: O Sistema localizou " + maquinas.size() + " maquina(s) registrada(s)  relacionadas a intituição!";
                            logs.adicionarMotivo(motivoMaquina);

                            System.out.println("-".repeat(15));
                            System.out.println(   "╔══════════════════════╗");
                            System.out.println(   "║ Escolha uma máquina: ║");


                            for (Maquina maquina : maquinas) {
                                System.out.println("╠══════════════════════╣");
                                System.out.println("║ id: " + maquina.getIdMaquina());
                                System.out.println("║ nome: " + maquina.getNome());
                                System.out.println("║ Sistema Operacional: " + maquina.getSO());
                                if (maquina.getDetalhes() != null) {
                                    System.out.println("║ Detalhes: " + maquina.getDetalhes());
                                }
                            }

                            System.out.println("╠══════════════════════╣");
                            System.out.println("║ Digite o número do PC║");
                            System.out.println("╚══════════════════════╝");
                            Integer numMaquina = in.nextInt();
                            ativarMaquina(con,con2,numMaquina, histConsmRecurso);
                            numeroMaquina = numMaquina;
                            List<Permissao> permissaos;
                            if (conexao.getDev()) {
                                permissaos = con.query("SELECT * FROM permissao WHERE emUso = 1 AND fkUsuario =?",
                                        new BeanPropertyRowMapper<>(Permissao.class),usuario.getIdUsuario());
                            } else {
                                permissaos = con2.query("SELECT * FROM permissao WHERE emUso = 1 AND fkUsuario =?",
                                        new BeanPropertyRowMapper<>(Permissao.class),usuario.getIdUsuario());
                            }
                            System.out.println("-".repeat(15));
                            for (Permissao permissao : permissaos) {
                                System.out.println("╠════════════════════════════════════╣");
                                System.out.println("║ Código aula: " + permissao.getNome());
                                System.out.println("╚════════════════════════════════════╝");

                            }
                            System.out.println("╠═══════════════════════╣");
                            System.out.println("║Digite o código da aula║");
                            System.out.println("╚═══════════════════════╝");
                            String codigoAula = leitor.nextLine();
                            histConsmRecurso.mostrarHistorico(numeroMaquina, codigoAula);
                            break;
                        } else {
                            String motivoMaquina = ":--ERROR: O Sistema não localizou registro de maquinas na sua intituição";
                            logs.adicionarMotivo(motivoMaquina);
                        }
                    case 2:
                        if (usuario instanceof Professor) {
                            ((Professor) usuario).opcaoProfessor();
                        }
                        if (usuario instanceof Adiministrador) {
                            cadastrarMaquina(con,con2);
                        }
                        if (usuario instanceof AdmNowl) {
                            ((AdmNowl) usuario).opcaoAdmNowl();
                        }
                        if (usuario instanceof Aluno) {
                            ((Aluno) usuario).opcaoAluno();
                        }
                        break;
                    case 3:
                        desativarMaquina(con, con2,numeroMaquina);
                        exibirMensagemDespedida();
                        histConsmRecurso.fecharSistema();// Isso encerrará o programa
                        return;
                    default:
                        System.out.println("Opção inválida");
                }
            } while (opcaoUsuario != 3);
        } else {
            System.out.println("Dados de login inválidos");

            String motivo = ":--ERROR: O Sistema não localizou nenhum dado relacionado ao email ('" + email + "') e a senha ('" + senha + "')!";
            logs.adicionarMotivo(motivo);
        }
    }




    private static void ativarMaquina(JdbcTemplate con, JdbcTemplate con2, Integer maquinaId, HistConsmRecurso histConsmRecurso) {
        Maquina maquina = con.queryForObject("SELECT * FROM maquina WHERE idMaquina = ? AND emUso = 0",
                new BeanPropertyRowMapper<>(Maquina.class), maquinaId);

        if (maquina != null) {


            con.update("UPDATE maquina SET emUso = 1 WHERE idMaquina = ?", maquinaId);
            System.out.println("Máquina ativada com sucesso: " + maquina.getNome());

            con2.update("UPDATE maquina SET emUso = 1 WHERE idMaquina = ?", maquinaId);
            System.out.println("Máquina ativada com sucesso: " + maquina.getNome());

        } else {
            System.out.println("Máquina não disponível ou inválida.");
        }
    }

    private static void desativarMaquina(JdbcTemplate con, JdbcTemplate conSer, Integer maquinaId) {

        con.update("UPDATE maquina SET emUso = 0 WHERE idMaquina = ?", maquinaId);

        conSer.update("UPDATE maquina SET emUso = 0 WHERE idMaquina = ?", maquinaId);


    }

    private static void visualizarEditarPerfil(JdbcTemplate con, JdbcTemplate conSer) {
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

        List<Usuario> usuariosMySQL = con.query("SELECT * FROM usuario WHERE email = ? AND senha = ?",
                new BeanPropertyRowMapper<>(Usuario.class), email, senha);

        List<Usuario> usuariosSQLServer = conSer.query("SELECT * FROM usuario WHERE email = ? AND senha = ?",
                new BeanPropertyRowMapper<>(Usuario.class), email, senha);

        if (usuariosMySQL.size() > 0 || usuariosSQLServer.size() > 0) {
            Usuario usuario;
            if (usuariosMySQL.size() > 0) {
                usuario = usuariosMySQL.get(0);
            } else {
                usuario = usuariosSQLServer.get(0);
            }

            System.out.println("Perfil do usuário:");
            System.out.println("Nome: " + usuario.getNome());
            System.out.println("Email: " + usuario.getEmail());
            System.out.println("Senha: " + usuario.getSenha());
            System.out.println("Instituição: " + usuario.getFkInstituicao());

            System.out.println("Deseja editar o perfil? (1 - Sim, 2 - Não)");
            int editarPerfil = leitor.nextInt();

            if (editarPerfil == 1) {
                editarPerfilUsuario(con, conSer, usuario);
            }
        } else {
            System.out.println("Dados de login inválidos");
        }
    }

    private static void editarPerfilUsuario(JdbcTemplate con, JdbcTemplate conSer, Usuario usuario) {
        Scanner leitor = new Scanner(System.in);

        System.out.println("Digite o novo nome do usuário:");
        String novoNome = leitor.nextLine();
        if (!novoNome.isEmpty()) {
            usuario.setNome(novoNome);
        }

        System.out.println("Digite o novo email do usuário:");
        String novoEmail = leitor.nextLine();
        if (!novoEmail.isEmpty() && novoEmail.contains("@")) {
            usuario.setEmail(novoEmail);
        }

        System.out.println("Digite a nova senha do usuário (mínimo de 8 caracteres):");
        String novaSenha = leitor.nextLine();
        if (!novaSenha.isEmpty() && novaSenha.length() >= 8) {
            usuario.setSenha(novaSenha);
        }

        // Atualiza as informações no banco de dados MySQL
        con.update("UPDATE usuario SET nome = ?, email = ?, senha = ? WHERE idUsuario = ?",
                usuario.getNome(), usuario.getEmail(), usuario.getSenha(), usuario.getIdUsuario());

        // Atualiza as informações no banco de dados SQL
        conSer.update("UPDATE usuario SET nome = ?, email = ?, senha = ? WHERE idUsuario = ?",
                usuario.getNome(), usuario.getEmail(), usuario.getSenha(), usuario.getIdUsuario());

        System.out.println("Perfil editado com sucesso!");
    }


    private static void enviarSugestao(JdbcTemplate con, Scanner in) {
        Scanner leitor = new Scanner(System.in);

        System.out.println("Digite a sua sugestão:");
        String sugestao = leitor.nextLine();

        // Exibe a sugestão no console
        System.out.println("Sugestão enviada: " + sugestao);
        System.out.println("Sugestão enviada com sucesso!");
    }

    private static void exibirMensagemDespedida() {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                 Até logo e volte sempre!                     ║");
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        System.out.println("║              Agradecemos por utilizar o Sistema Will Dantas  ║");
        System.out.println("║          Continue explorando as incríveis funcionalidades    ║");
        System.out.println("║            que o Sistema Will Dantas tem a oferecer.         ║");
        System.out.println("║                                                              ║");
        System.out.println("║                     Tenha um dia maravilhoso!                ║");
        System.out.println("║                                                              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
    }

    private static void cadastrarMaquina(JdbcTemplate con, JdbcTemplate conSer) {
        Scanner leitor = new Scanner(System.in);

        System.out.println("Digite o nome da máquina:");
        String nomeMaquina = leitor.nextLine();

        System.out.println("Digite o sistema operacional da máquina:");
        String sistemaOperacional = leitor.nextLine();

        System.out.println("A máquina está em uso? (Digite 1 para sim, 0 para não):");
        int emUso = leitor.nextInt();

        System.out.println("Digite o ID da instituição (fkInstituicao):");
        int fkInstituicao = leitor.nextInt();

        // Cadastrar a máquina no banco de dados MySQL
        con.update("INSERT INTO maquina (nome, SO, emUso, fkInstituicao) VALUES (?, ?, ?, ?)", nomeMaquina, sistemaOperacional, emUso, fkInstituicao);

        // Cadastrar a máquina no banco de dados SQL Server
        conSer.update("INSERT INTO maquina (nome, SO, emUso, fkInstituicao) VALUES (?, ?, ?, ?)", nomeMaquina, sistemaOperacional, emUso, fkInstituicao);

        Integer idMaquinaMySQL;
        Integer idMaquinaSQLServer;

        // Recuperar o ID da máquina recém-cadastrada do MySQL
        idMaquinaMySQL = con.queryForObject("SELECT MAX(idMaquina) FROM maquina", Integer.class);

        // Recuperar o ID da máquina recém-cadastrada do SQL Server
        idMaquinaSQLServer = conSer.queryForObject("SELECT MAX(idMaquina) FROM maquina", Integer.class);

        // Cadastrar hardware e componente para a máquina nas duas fontes de dados
        cadastrarHardwareEComponente(con, conSer, idMaquinaMySQL, 2);
        cadastrarHardwareEComponente(con, conSer, idMaquinaMySQL, 3);
        cadastrarHardwareEComponente(con, conSer, idMaquinaMySQL, 1);

        System.out.println("Máquina cadastrada com sucesso!");
    }


    private static Integer cadastrarTipoHardware(JdbcTemplate con, JdbcTemplate conSer) {
        Scanner leitor = new Scanner(System.in);

        System.out.println("Digite o nome do tipo de hardware (ex: CPU, GPU, Memória):");
        String tipoHardware = leitor.nextLine();

        // Cadastrar o tipo de hardware no banco de dados MySQL
        con.update("INSERT INTO tipoHardware (tipo) VALUES (?)", tipoHardware);

        // Cadastrar o tipo de hardware no banco de dados SQL Server
        conSer.update("INSERT INTO tipoHardware (tipo) VALUES (?)", tipoHardware);

        // Recuperar o ID do tipo de hardware recém-cadastrado do MySQL
        Integer idTipoHardwareMySQL = con.queryForObject("SELECT MAX(idTipoHardware) FROM tipoHardware", Integer.class);

        // Recuperar o ID do tipo de hardware recém-cadastrado do SQL Server
        Integer idTipoHardwareSQLServer = conSer.queryForObject("SELECT MAX(idTipoHardware) FROM tipoHardware", Integer.class);

        // Retornar o ID do tipo de hardware
        return idTipoHardwareMySQL; // Você pode escolher retornar o ID do MySQL ou SQL Server, dependendo da sua lógica.

    }

    private static void cadastrarHardwareEComponente(JdbcTemplate con, JdbcTemplate conSer, Integer idMaquina, Integer tipo) {
        Scanner leitor = new Scanner(System.in);

        // Cadastrar hardware
        System.out.println("Digite o fabricante do hardware:");
        String fabricante = leitor.nextLine();

        System.out.println("Digite o modelo do hardware:");
        String modelo = leitor.nextLine();

        System.out.println("Digite a capacidade do hardware:");
        double capacidade = leitor.nextDouble();
        leitor.nextLine(); // Consumir a quebra de linha

        System.out.println("Digite a especificidade do hardware:");
        String especificidade = leitor.nextLine();

        // Cadastrar o tipo de hardware
        Integer idTipoHardware = cadastrarTipoHardware(con, conSer);

        // Cadastrar o hardware no banco de dados MySQL
        con.update("INSERT INTO hardware (fabricante, modelo, capacidade, especificidade, fkTipoHardware) VALUES (?, ?, ?, ?, ?)", fabricante, modelo, capacidade, especificidade, idTipoHardware);

        // Cadastrar o hardware no banco de dados SQL Server
        conSer.update("INSERT INTO hardware (fabricante, modelo, capacidade, especificidade, fkTipoHardware) VALUES (?, ?, ?, ?, ?)", fabricante, modelo, capacidade, especificidade, idTipoHardware);

        // Recuperar o ID do hardware recém-cadastrado do MySQL
        Integer idHardwareMySQL = con.queryForObject("SELECT MAX(idHardware) FROM hardware", Integer.class);

        // Recuperar o ID do hardware recém-cadastrado do SQL Server
        Integer idHardwareSQLServer = conSer.queryForObject("SELECT MAX(idHardware) FROM hardware", Integer.class);

        // Cadastrar componente
        System.out.println("Digite a porcentagem máxima para o componente (deixe em branco para usar o valor padrão):");
        String inputMax = leitor.nextLine();
        Integer max;
        boolean verificaStringNula = inputMax.isEmpty();
        if (verificaStringNula) {
            max = 60;
        } else {
            max = Integer.parseInt(inputMax);
        }

        // Cadastrar o componente no banco de dados MySQL
        con.update("INSERT INTO componente (max, fkMaquina, fkHardware) VALUES (?, ?, ?)", max, idMaquina, idHardwareMySQL);

        // Cadastrar o componente no banco de dados SQL Server
        conSer.update("INSERT INTO componente (max, fkMaquina, fkHardware) VALUES (?, ?, ?)", max, idMaquina, idHardwareSQLServer);

        System.out.println("Hardware e componente cadastrados com sucesso!");
    }
}