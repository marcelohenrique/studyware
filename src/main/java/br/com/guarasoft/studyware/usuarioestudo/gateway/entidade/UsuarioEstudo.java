package br.com.guarasoft.studyware.usuarioestudo.gateway.entidade;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;
import br.com.guarasoft.studyware.estudodiario.gateway.entidade.UsuarioEstudoDiario;

@Entity
@Data
public class UsuarioEstudo {

	@Id
	private Long id;
	private String email;
	private String nome;
	@Temporal(TemporalType.DATE)
	private Date fim;

	@OneToMany(mappedBy = "pk.usuarioEstudo")
	private List<UsuarioEstudoDiario> usuarioEstudoDiarios;

}
