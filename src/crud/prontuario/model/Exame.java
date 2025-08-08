package crud.prontuario.model;

import java.time.LocalDate;
import java.util.Objects;

public class Exame {

    private Long id;
    private String descricao;
    private LocalDate dataExame;
    private Long pacienteId;
    
    public Exame() {
        super();
    }

    public Exame(Long id, String descricao) {
        super();
        this.id = id;
        this.descricao = descricao;
        this.dataExame = LocalDate.now();
    }
    
    public Exame(Long id, String descricao, Long pacienteId) {
        super();
        this.id = id;
        this.descricao = descricao;
        this.dataExame = LocalDate.now();
        this.pacienteId = pacienteId;
    }
    
    public Exame(Long id, String descricao, LocalDate dataExame, Long pacienteId) {
        super();
        this.id = id;
        this.descricao = descricao;
        this.dataExame = dataExame;
        this.pacienteId = pacienteId;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getDataExame() {
        return dataExame;
    }

    public void setDataExame(LocalDate dataAtual) {
        this.dataExame = dataAtual;
    }
    
    public Long getPacienteId() {
        return pacienteId;
    }
    
    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
    }

    @Override
    public String toString() {
        return "Exame [id=" + id + ", descricao=" + descricao + ", dataExame=" + dataExame + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Exame other = (Exame) obj;
        return Objects.equals(id, other.id);
    }
}