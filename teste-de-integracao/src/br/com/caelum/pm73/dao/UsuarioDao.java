package br.com.caelum.pm73.dao;

import org.hibernate.Session;

import br.com.caelum.pm73.dominio.Usuario;

public class UsuarioDao {

	private final Session session;

	public UsuarioDao(Session session) {
		this.session = session;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((session == null) ? 0 : session.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UsuarioDao other = (UsuarioDao) obj;
		if (session == null) {
			if (other.session != null)
				return false;
		} else if (!session.equals(other.session))
			return false;
		return true;
	}

	public Usuario porId(int id) {
		return (Usuario) session.load(Usuario.class, id);
	}
	
	public Usuario porNomeEEmail(String nome, String email) {
		return (Usuario) session.createQuery("from Usuario u where u.nome = :nome and u.email = :email")
				.setParameter("nome", nome)
				.setParameter("email", email)
				.uniqueResult();
	}
	
	public void salvar(Usuario usuario) {
		session.save(usuario);
	}
	
	public void atualizar(Usuario usuario) {
		session.merge(usuario);
	}
	
	public void deletar(Usuario usuario) {
		session.delete(usuario);
	}
}
