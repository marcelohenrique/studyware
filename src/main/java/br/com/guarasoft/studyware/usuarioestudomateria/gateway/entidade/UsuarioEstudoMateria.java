package br.com.guarasoft.studyware.usuarioestudomateria.gateway.entidade;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import lombok.Data;
import br.com.guarasoft.studyware.infra.dao.Entidade;

@Entity
@Data
public class UsuarioEstudoMateria implements Entidade {

	private static final long serialVersionUID = 6291112509470918058L;

	@EmbeddedId
	private UsuarioEstudoMateriaPK usuarioEstudoMateriaPK;

	private Long tempoAlocado;

	private Long ordem;

}
