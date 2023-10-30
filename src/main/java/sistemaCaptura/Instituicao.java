package sistemaCaptura;

public class Instituicao {
    private Integer idInstituicao;
    private String nome;
    private String sigla;
    private String codigoHex;

    public Instituicao() {
    }

    public Instituicao(Integer idInstituicao, String nome, String sigla, String codigoHex) {
        this.idInstituicao = idInstituicao;
        this.nome = nome;
        this.sigla = sigla;
        this.codigoHex = codigoHex;
    }

    public Integer getIdInstituicao() {
        return idInstituicao;
    }

    public void setIdInstituicao(Integer idInstituicao) {
        this.idInstituicao = idInstituicao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getCodigoHex() {
        return codigoHex;
    }

    public void setCodigoHex(String codigoHex) {
        this.codigoHex = codigoHex;
    }

    @Override
    public String toString() {
        return "Instituicao{" +
                "idInstituicao=" + idInstituicao +
                ", nome='" + nome + '\'' +
                ", sigla='" + sigla + '\'' +
                ", codigoHex='" + codigoHex + '\'' +
                '}';
    }
}

