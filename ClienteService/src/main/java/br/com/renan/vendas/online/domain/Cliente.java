package br.com.renan.vendas.online.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "cliente", uniqueConstraints = {
        @UniqueConstraint(columnNames = "cpf"),
        @UniqueConstraint(columnNames = "email")
})
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 1, max = 100)
    @Column(nullable = false)
    private String nome;

    @NotBlank
    @Size(min = 11, max = 11)
    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @NotBlank
    @Size(min = 10, max = 15)
    @Column(nullable = false, length = 15)
    private String tel;

    @NotBlank
    @Size(min = 1, max = 100)
    @Column(nullable = false, unique = true, length = 100)
    @Pattern(regexp = ".+@.+\\..+", message = "Email inválido")
    private String email;

    @NotBlank
    @Size(min = 1, max = 100)
    @Column(name = "endereco", nullable = false, length = 100)
    private String endereco;

    @NotNull
    @Column(nullable = false)
    private Integer numero;

    @NotBlank
    @Size(min = 1, max = 50)
    @Column(nullable = false, length = 50)
    private String cidade;

    @NotBlank
    @Size(min = 2, max = 2)
    @Column(nullable = false, length = 2)
    private String estado;

    public Cliente() {}

    public Cliente(Long id, String nome, String cpf, String tel, String email,
                   String endereco, Integer numero, String cidade, String estado) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.tel = tel;
        this.email = email;
        this.endereco = endereco;
        this.numero = numero;
        this.cidade = cidade;
        this.estado = estado;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public String getTel() { return tel; }
    public String getEmail() { return email; }
    public String getEndereco() { return endereco; }
    public Integer getNumero() { return numero; }
    public String getCidade() { return cidade; }
    public String getEstado() { return estado; }

    public void setNome(String nome) { this.nome = nome; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public void setTel(String tel) { this.tel = tel; }
    public void setEmail(String email) { this.email = email; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public void setNumero(Integer numero) { this.numero = numero; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public void setEstado(String estado) { this.estado = estado; }
}