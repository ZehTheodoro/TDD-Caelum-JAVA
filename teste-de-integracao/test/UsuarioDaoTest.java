import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNull;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.pm73.dao.CriadorDeSessao;
import br.com.caelum.pm73.dao.UsuarioDao;
import br.com.caelum.pm73.dominio.Usuario;

public class UsuarioDaoTest {
	
	private Session session;
	private UsuarioDao usuarioDao;
	
	
	
	@Before
	public void setUp() {
		session = new CriadorDeSessao().getSession();
		usuarioDao = new UsuarioDao(session);
		session.beginTransaction();
		
	}
	
	@After
	public void tearDown(){
		session.getTransaction().rollback();
		session.close();
	}

	@Test
	public void deveEncontrarPeloNomeEmail() {
		
		Usuario novoUsuario = new Usuario("Jao", "Jao@gmail.com");
		
		usuarioDao.salvar(novoUsuario);
		
		Usuario usuario = usuarioDao.porNomeEEmail(novoUsuario.getNome(), novoUsuario.getEmail());
		assertThat(usuario.getNome(), equalTo("Jao"));
		assertThat(usuario.getEmail(), equalTo("Jao@gmail.com"));
			
	}
	
	@Test
	public void deveRetornarNuloSeOUsuarioNaoExistir() {
		
		Usuario novoUsuario = new Usuario("Jao", "Jao@gmail.com");
	
		Usuario usuario = usuarioDao.porNomeEEmail(novoUsuario.getNome(), novoUsuario.getEmail());
		
		assertNull(usuario);
			
	}
	
	@Test
	public void garanteQueRemoveUsuario(){
		
		Usuario usuario = new Usuario("Renato", "renato@gmail.com");
		UsuarioDao usuarioDao = new UsuarioDao(session);
		
		usuarioDao.salvar(usuario);
		
		usuarioDao.deletar(usuario);
		
		session.flush();
		
		Usuario usuarioDeletado = usuarioDao.porNomeEEmail("Renato", "renato@gmail.com");
		
		assertNull(usuarioDeletado);
		
	}

}
