package sistemaCaptura;

import com.github.britooo.looca.api.core.Looca;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import sistemaCaptura.conexao.Conexao;

import javax.swing.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

public class HistConsmRecurso {

    private Integer idHistorico;
    private LocalDateTime dataHora = LocalDateTime.now();
    private Integer fkMaquina;
    private Integer fkHardware;
    private Integer fkComponente;

    Conexao conexao = new Conexao();
    JdbcTemplate con = conexao.getConexaoDoBanco();
    Looca looca = new Looca();
    Timer timer = new Timer();

    public HistConsmRecurso() {
    }

    public void mostrarHistorico() {
        insertHistorico();
    }
    public void fecharSistema() {
        System.out.println("Sistema encerrado. Até mais!");
        System.exit(0);
    }
    public void insertHistorico() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Obter os valores de consumo
                Integer consumoCpu = (looca.getProcessador().getUso()).intValue();
                Long consumoDisco = (long) (looca.getGrupoDeDiscos().getTamanhoTotal() / 8e+9);
                Integer qtdJanelasAbertas = looca.getGrupoDeJanelas().getTotalJanelas();
                long consumoRam = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                dataHora = LocalDateTime.now();

                // Formatar a data/hora
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String dataHoraFormatada = dataHora.format(formatter);

                // Inserir dados no banco
                insertDadosNoBanco("CPU", consumoCpu);
                insertDadosNoBanco("RAM", consumoRam);
                insertDadosNoBanco("Disco", consumoDisco);
                insertDadosNoBanco("Janelas Abertas", qtdJanelasAbertas);

                // Exibir dados em forma de tabela
                mostrarDadosEmTabela(dataHoraFormatada, consumoCpu, consumoRam, consumoDisco, qtdJanelasAbertas);

//                 Verificar limite e enviar notificação por Slack
                verificarLimiteEEnviarNotificacao("CPU", consumoCpu, 20); // Substitua 80 pelo limite desejado
                verificarLimiteEEnviarNotificacao("RAM", consumoRam, 90); // Substitua 90 pelo limite desejado
                verificarLimiteEEnviarNotificacao("Disco", consumoDisco, 70);
                verificarLimiteEEnviarNotificacao("Quantidade janelas", qtdJanelasAbertas, 15); // Substitua 70 pelo limite desejado
            }
        }, 1000, 1000);
    }

    private void insertDadosNoBanco(String componente, Number consumo) {
        String unidade = componente.equals("RAM") ? " GB" : (componente.equals("Disco") ? " GB" : "");
        double consumoConvertido = componente.equals("RAM") ? consumo.doubleValue() / (1024 * 1024 * 1024) : consumo.doubleValue();

        String sql = "INSERT INTO historico (dataHora, consumo, qtdJanelasAbertas, fkComponente, fkHardware, fkMaquina) VALUES(?, ?, ?, ?, ?, ?)";
        con.update(sql, dataHora, consumoConvertido, looca.getGrupoDeJanelas().getTotalJanelas(), getComponenteId(componente), 2, 2);
        System.out.println("Inserido no Banco de Dados - " + componente + ": " + consumoConvertido + unidade);
    }

    private void mostrarDadosEmTabela(String dataHora, int consumoCpu, long consumoRam, long consumoDisco, int qtdJanelasAbertas) {
        System.out.println("+---------------------+--------------+-----------------+-------------+----------------------+");
        System.out.println("| Data/Hora           | Consumo CPU  | Consumo RAM    | Consumo Disco | Janelas Abertas |");
        System.out.println("+---------------------+--------------+-----------------+-------------+----------------------+");
        System.out.print("| " + dataHora + " | " + consumoCpu + "%        | ");
        System.out.print(consumoRam >= 0 ? consumoRam + " bytes  | " : "N/A             | ");
        System.out.print(consumoDisco >= 0 ? consumoDisco + " GB  | " : "N/A             | ");
        System.out.println(qtdJanelasAbertas + " janelas abertas |");
        System.out.println("+---------------------+--------------+-----------------+-------------+----------------------+");
    }

    private void verificarLimiteEEnviarNotificacao(String componente, Number consumo, int limite) {
        if (consumo.intValue() >= limite) {
            String mensagemAlerta = "O componente " + componente + " atingiu o limite de consumo: " + consumo + (componente.equals("RAM") ? " bytes" : " GB");

            if (componente.equals("CPU") || componente.equals("Janelas Abertas")) {
                // Notificar o usuário no Java
                System.out.println("alerta de limite no Jar");
            }

            // Enviar notificação por Slack
            enviarNotificacaoPorSlack(componente, consumo, limite);
        }
    }

    public String obterSistemaOperacionalDaMaquina(JdbcTemplate con, Maquina maquina) {
        String sql = "SELECT m.SO AS sistemaOperacional FROM maquina m WHERE m.idMaquina = ?";
        return con.queryForObject(sql, String.class, maquina.getIdMaquina());
    }

    private void enviarNotificacaoPorSlack(String componente, Number consumo, int limite) {
        try {
            // Substitua com o seu webhook URL do Slack
            String webhookUrl="https://hooks.slack.com/services/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2"; // Substitua pela sua URL

            // Crie a mensagem que você deseja enviar em formato JSON
            String mensagemSlack = "{\"text\": \"O componente " + componente + " atingiu o limite de consumo: " + consumo + (componente.equals("RAM") ? " bytes" : " GB") + "\"}";

            // Crie uma conexão HTTP
            URL url = new URL(webhookUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Escreva a mensagem no corpo da solicitação
            try (OutputStream os = connection.getOutputStream(); Writer writer = new OutputStreamWriter(os, "UTF-8")) {
                writer.write(mensagemSlack);
            }

            // Execute a solicitação
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                System.out.println("Notificação por Slack enviada com sucesso.");
            } else {
                System.out.println("Falha ao enviar a notificação por Slack. Código de resposta: " + responseCode);
            }

            // Feche a conexão
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Falha ao enviar a notificação por Slack: " + e.getMessage());
        }
    }

    private int getComponenteId(String componente) {
        // Mapear o nome do componente para o seu ID correspondente no banco de dados
        switch (componente) {
            case "CPU":
                return 1;
            case "RAM":
                return 2;
            case "Disco":
                return 3;
            case "Janelas Abertas":
                return 4;
            default:
                return 0; // Valor padrão, caso não corresponda a nenhum componente conhecido
        }
    }
}
