package cl.duoc.sanmarcos.usuarios.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "citas")
public class Cita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "dueno_id", nullable = false)
    private Usuario dueno;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "servicio_id", nullable = false)
    private Servicio servicio;

    @Column(nullable = false, length = 60)
    private String mascotaNombre;

    @Column(nullable = false, length = 40)
    private String mascotaEspecie;

    @Column(length = 60)
    private String mascotaRaza;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Column(length = 255)
    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCita estado = EstadoCita.PENDIENTE;

    @Column(length = 255)
    private String observacionClinica;

    public Long getId() { return id; }
    public Usuario getDueno() { return dueno; }
    public void setDueno(Usuario dueno) { this.dueno = dueno; }
    public Servicio getServicio() { return servicio; }
    public void setServicio(Servicio servicio) { this.servicio = servicio; }
    public String getMascotaNombre() { return mascotaNombre; }
    public void setMascotaNombre(String mascotaNombre) { this.mascotaNombre = mascotaNombre; }
    public String getMascotaEspecie() { return mascotaEspecie; }
    public void setMascotaEspecie(String mascotaEspecie) { this.mascotaEspecie = mascotaEspecie; }
    public String getMascotaRaza() { return mascotaRaza; }
    public void setMascotaRaza(String mascotaRaza) { this.mascotaRaza = mascotaRaza; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public EstadoCita getEstado() { return estado; }
    public void setEstado(EstadoCita estado) { this.estado = estado; }
    public String getObservacionClinica() { return observacionClinica; }
    public void setObservacionClinica(String observacionClinica) { this.observacionClinica = observacionClinica; }
}
