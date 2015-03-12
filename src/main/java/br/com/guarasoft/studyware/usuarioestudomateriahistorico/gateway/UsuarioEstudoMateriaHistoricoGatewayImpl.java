package br.com.guarasoft.studyware.usuarioestudomateriahistorico.gateway;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.joda.time.Duration;

import br.com.guarasoft.studyware.infra.dao.AbstractDao;
import br.com.guarasoft.studyware.usuarioestudo.bean.UsuarioEstudoBean;
import br.com.guarasoft.studyware.usuarioestudomateria.gateway.converter.UsuarioEstudoMateriaBuilder;
import br.com.guarasoft.studyware.usuarioestudomateria.gateway.entidade.UsuarioEstudoMateria;
import br.com.guarasoft.studyware.usuarioestudomateriahistorico.bean.ResumoMateriaEstudadaBean;
import br.com.guarasoft.studyware.usuarioestudomateriahistorico.bean.UsuarioEstudoMateriaHistoricoBean;
import br.com.guarasoft.studyware.usuarioestudomateriahistorico.gateway.converter.UsuarioEstudoMateriaHistoricoEntidadeConverter;
import br.com.guarasoft.studyware.usuarioestudomateriahistorico.gateway.entidade.ResumoMateriaEstudada;
import br.com.guarasoft.studyware.usuarioestudomateriahistorico.gateway.entidade.UsuarioEstudoMateriaHistorico;

@Stateless
public class UsuarioEstudoMateriaHistoricoGatewayImpl extends AbstractDao<UsuarioEstudoMateriaHistorico, Long> implements UsuarioEstudoMateriaHistoricoGateway {

	private final UsuarioEstudoMateriaBuilder usuarioEstudoMateriaBuilder = new UsuarioEstudoMateriaBuilder();
	private final UsuarioEstudoMateriaHistoricoEntidadeConverter usuarioEstudoMateriaHistoricoEntidadeConverter = new UsuarioEstudoMateriaHistoricoEntidadeConverter();

	public UsuarioEstudoMateriaHistoricoGatewayImpl() {
		super(UsuarioEstudoMateriaHistorico.class);
	}

	@Override
	public void persist(UsuarioEstudoMateriaHistoricoBean materiaEstudada) {
		UsuarioEstudoMateriaHistorico entidade = this.usuarioEstudoMateriaHistoricoEntidadeConverter.convert(materiaEstudada);

		this.persist(entidade);
	}

	@Override
	public List<UsuarioEstudoMateriaHistoricoBean> findAll(UsuarioEstudoBean estudo) {
		StringBuilder sql = new StringBuilder();
		sql.append(" FROM UsuarioEstudoMateriaHistorico uemh ");
		sql.append("WHERE uemh.usuarioEstudoMateria.usuarioEstudo.id = :usuarioEstudo ");
		sql.append("ORDER BY uemh.horaEstudo DESC");

		TypedQuery<UsuarioEstudoMateriaHistorico> query = this.entityManager.createQuery(sql.toString(), UsuarioEstudoMateriaHistorico.class);
		query.setParameter("usuarioEstudo", estudo.getId());
		List<UsuarioEstudoMateriaHistorico> entidades = query.getResultList();

		List<UsuarioEstudoMateriaHistoricoBean> beans = this.usuarioEstudoMateriaHistoricoEntidadeConverter.convert(estudo, entidades);

		return beans;
	}

	@Override
	public List<ResumoMateriaEstudadaBean> buscaResumosMaterias(UsuarioEstudoBean usuarioEstudoBean) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT new br.com.guarasoft.studyware.usuarioestudomateriahistorico.gateway.entidade.ResumoMateriaEstudada( uem, SUM( uemh.tempoEstudado ), '' ) ");
		sql.append("  FROM UsuarioEstudoMateria uem ");
		sql.append("  LEFT JOIN uem.historico uemh ");
		sql.append(" WHERE uem.usuarioEstudo.id = :usuarioEstudo ");
		sql.append(" GROUP BY uem ");
		sql.append(" ORDER BY uem.ordem");

		TypedQuery<ResumoMateriaEstudada> query = this.entityManager.createQuery(sql.toString(), ResumoMateriaEstudada.class);
		query.setParameter("usuarioEstudo", usuarioEstudoBean.getId());

		List<ResumoMateriaEstudada> entidades = query.getResultList();

		List<ResumoMateriaEstudadaBean> beans = new ArrayList<>();
		ResumoMateriaEstudadaBean bean = null;
		for (ResumoMateriaEstudada entidade : entidades) {
			bean = new ResumoMateriaEstudadaBean();

			bean.setUsuarioEstudoMateria(this.usuarioEstudoMateriaBuilder.convert(usuarioEstudoBean, entidade.getUsuarioEstudoMateria()));
			bean.setSomaTempo(new Duration(entidade.getSomaTempo()));
			bean.setObservacao(this.buscaUltimaObservacao(entidade.getUsuarioEstudoMateria()));

			beans.add(bean);
		}

		return beans;
	}

	private String buscaUltimaObservacao(UsuarioEstudoMateria usuarioEstudoMateria) {
		StringBuilder sql = new StringBuilder();
		// sql.append("SELECT uemh.observacao ");
		sql.append("  FROM UsuarioEstudoMateriaHistorico uemh ");
		sql.append(" WHERE uemh.id IN ( ");
		sql.append("SELECT MAX( h.id ) ");
		sql.append("  FROM UsuarioEstudoMateriaHistorico h ");
		sql.append(" WHERE h.usuarioEstudoMateria.id = :usuarioEstudoMateria ");
		sql.append(" GROUP BY h.usuarioEstudoMateria ) ");

		TypedQuery<UsuarioEstudoMateriaHistorico> query = this.entityManager.createQuery(sql.toString(), UsuarioEstudoMateriaHistorico.class);
		query.setParameter("usuarioEstudoMateria", usuarioEstudoMateria.getId());

		try {
			return query.getSingleResult().getObservacao();
		} catch (NoResultException e) {
			return "";
		}
	}
}
