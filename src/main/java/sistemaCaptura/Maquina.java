package sistemaCaptura;

public class Maquina {
    private Integer idMaquina;
    private String nome;
    private String SO; // Nome atualizado para corresponder ao banco de dados
    private Integer emUso;
    private Integer fkInstituicao;
    private String sistemaOperacional;
    private String detalhes;

    public Maquina() {
    }

    public Maquina(Integer idMaquina, String nome, String SO, Integer emUso, Integer fkInstituicao, String sistemaOperacional, String detalhes) {
        this.idMaquina = idMaquina;
        this.nome = nome;
        this.SO = SO;
        this.emUso = emUso;
        this.fkInstituicao = fkInstituicao;
        this.sistemaOperacional = sistemaOperacional;
        this.detalhes = detalhes;
    }

    public String getSistemaOperacional() {
        return sistemaOperacional;
    }

    public void setSistemaOperacional(String sistemaOperacional) {
        this.sistemaOperacional = sistemaOperacional;
    }

    public String getDetalhes() {
        return detalhes;
    }

    public void setDetalhes(String detalhes) {
        this.detalhes = detalhes;
    }

    public Integer getIdMaquina() {
        return idMaquina;
    }

    public void setIdMaquina(Integer idMaquina) {
        this.idMaquina = idMaquina;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSO() {
        return SO;
    }

    public void setSO(String SO) {
        this.SO = SO;
    }

    public Integer getEmUso() {
        return emUso;
    }

    public void setEmUso(Integer emUso) {
        this.emUso = emUso;
    }

    public Integer getFkInstituicao() {
        return fkInstituicao;
    }

    public void setFkInstituicao(Integer fkInstituicao) {
        this.fkInstituicao = fkInstituicao;
    }

    @Override
    public String toString() {
        return "Maquina{" +
                "idMaquina='" + idMaquina + '\'' +
                ", nome='" + nome + '\'' +
                ", SO='" + SO + '\'' +
                ", emUso=" + emUso +
                ", fkInstituicao=" + fkInstituicao +
                ", sistemaOperacional='" + sistemaOperacional + '\'' +
                ", detalhes='" + detalhes + '\'' +
                '}';
    }
}
