package br.com.guarasoft.studyware.usuarioestudo.gateway;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import br.com.guarasoft.studyware.infra.dao.AbstractDao;
import br.com.guarasoft.studyware.usuarioestudo.bean.UsuarioEstudoBean;
import br.com.guarasoft.studyware.usuarioestudo.gateway.converter.UsuarioEstudoBuilder;
import br.com.guarasoft.studyware.usuarioestudo.gateway.entidade.UsuarioEstudo;

@Stateless
public class UsuarioEstudoGatewayImpl extends AbstractDao<UsuarioEstudo, Long> implements UsuarioEstudoGateway {

	private final UsuarioEstudoBuilder builder = new UsuarioEstudoBuilder();

	public UsuarioEstudoGatewayImpl() {
		super(UsuarioEstudo.class);
	}

	@Override
	public void cadastrar(UsuarioEstudoBean usuarioEstudoBean) {
		UsuarioEstudo usuarioEstudo = this.builder.converteMaterias().converteDias().convert(usuarioEstudoBean);

		this.merge(usuarioEstudo);
	}

	@Override
	public List<UsuarioEstudoBean> recuperaTodosEstudos(String email) {
		StringBuilder sql = new StringBuilder();
		sql.append(" from UsuarioEstudo ue ");
		sql.append("where ue.email = :email ");
		sql.append("order by ue.fim, ue.nome ");

		TypedQuery<UsuarioEstudo> typedQuery = this.entityManager.createQuery(sql.toString(), UsuarioEstudo.class);

		typedQuery.setParameter("email", email);

		List<UsuarioEstudo> estudosUsuario = typedQuery.getResultList();

		List<UsuarioEstudoBean> estudos = this.builder.converteMaterias().converteDias().convert(estudosUsuario);

		return estudos;
	}

	@Override
	public List<UsuarioEstudoBean> recuperaEstudosValidos(String email) {
		StringBuilder sql = new StringBuilder();
		sql.append(" from UsuarioEstudo ue ");
		sql.append("where ue.email = :email ");
		sql.append("  and ue.fim >= current_date ");
		sql.append("   or ue.fim is null ");

		TypedQuery<UsuarioEstudo> typedQuery = this.entityManager.createQuery(sql.toString(), UsuarioEstudo.class);

		typedQuery.setParameter("email", email);

		List<UsuarioEstudo> estudosUsuario = typedQuery.getResultList();

		List<UsuarioEstudoBean> estudos = this.builder.converteMaterias().converteDias().convert(estudosUsuario);

		return estudos;
	}

	@Override
	public UsuarioEstudoBean buscaPorId(Long id) {
		UsuarioEstudo entidade = this.find(id);

		UsuarioEstudoBean bean = this.builder.convert(entidade);

		return bean;
	}

	@Override
	public void remover(UsuarioEstudoBean usuarioEstudo) {
		UsuarioEstudo entidade = this.builder.convert(usuarioEstudo);

		this.remove(entidade);
	}

}
