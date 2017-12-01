import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.Calendar;
import java.util.List;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.pm73.dao.CriadorDeSessao;
import br.com.caelum.pm73.dao.LeilaoDao;
import br.com.caelum.pm73.dao.UsuarioDao;
import br.com.caelum.pm73.dominio.Lance;
import br.com.caelum.pm73.dominio.Leilao;
import br.com.caelum.pm73.dominio.Usuario;
import builder.LeilaoBuilder;

public class LeilaoDaoTest {
	
	
	private Session session;
	private LeilaoDao leilaoDao;
	private UsuarioDao usuarioDao;

	@Before
	public void setUp() {
		
		session = new CriadorDeSessao().getSession();
		leilaoDao = new LeilaoDao(session);
		usuarioDao = new UsuarioDao(session);
		session.beginTransaction();
		
	}

	@After
	public void tearDown() {
		session.getTransaction().rollback();
		session.close();
	}

	@Test
	public void deveContarLeiloesNaoEncerrados() {
		Usuario mauricio = new Usuario("Mauricio", "mauricio@uol.com.br");
		
		Leilao ativo = new Leilao("Geladeira", 1500.0, mauricio, false);
		
		Leilao encerrado = new Leilao("Xbox", 700.0, mauricio, false);
		encerrado.encerra();
		
		usuarioDao.salvar(mauricio);
		leilaoDao.salvar(ativo);
		leilaoDao.salvar(encerrado);
		
		long total = leilaoDao.total();
	
		assertThat(total, equalTo(1L));
	}
	
	@Test
	public void deveContarLeiloesEncerrados() {
		Usuario mauricio = new Usuario("Mauricio", "mauricio@uol.com.br");
		
		Leilao ativo = new Leilao("Geladeira", 1500.0, mauricio, false);
		ativo.encerra();
		Leilao encerrado = new Leilao("Xbox", 700.0, mauricio, false);
		encerrado.encerra();
		
		usuarioDao.salvar(mauricio);
		leilaoDao.salvar(ativo);
		leilaoDao.salvar(encerrado);
		
		long total = leilaoDao.total();
		
		assertThat(total, equalTo(0L));
	}
	
	@Test
	public void deveContarLeiloesNovos() {
		Usuario mauricio = new Usuario("Mauricio", "mauricio@uol.com.br");
		
		Leilao ativo = new Leilao("Geladeira", 1500.0, mauricio, false);
		
		Leilao encerrado = new Leilao("Xbox", 700.0, mauricio, true);
		
		
		usuarioDao.salvar(mauricio);
		leilaoDao.salvar(ativo);
		leilaoDao.salvar(encerrado);
		
		int novos = leilaoDao.novos().size();
		
		assertThat(novos, equalTo(1));
		assertThat(ativo.getNome(), equalTo("Geladeira"));
	}
	
	@Test
	public void deveContarLeiloesUsados() {
		Usuario mauricio = new Usuario("Mauricio", "mauricio@uol.com.br");
		
		Leilao ativo = new Leilao("Geladeira", 1500.0, mauricio, true);
		
		Leilao encerrado = new Leilao("Xbox", 700.0, mauricio, false);
		
		
		usuarioDao.salvar(mauricio);
		leilaoDao.salvar(ativo);
		leilaoDao.salvar(encerrado);
		
		int usados = leilaoDao.novos().size();
		
		
		assertThat(usados, equalTo(1));
	}
	
	
	@Test
	public void deveTrazerLeiloesNaoEncerradosNoPeriodo(){
		
		Calendar comecoDoIntervalo = Calendar.getInstance();
		comecoDoIntervalo.add(Calendar.DAY_OF_MONTH, -10);
		Calendar fimDoIntervalo = Calendar.getInstance();
		
		
		Usuario mauricio = new Usuario("Mauricio", "mauricio@uol.com.br");
		
		Leilao leilao = new LeilaoBuilder()
										.comDono(mauricio)
										.comNome("Xbox")
										.comValor(700.0)
										.usado()
										.diasAtras(10)
										.constroi();
		
		
		Leilao leilao2 = new LeilaoBuilder()
										.comDono(mauricio)
										.comNome("TV")
										.comValor(1700.0)
										.usado()
										.diasAtras(8)
										.constroi();
		
		
		usuarioDao.salvar(mauricio);
		leilaoDao.salvar(leilao);
		leilaoDao.salvar(leilao2);
		
		List<Leilao> leiloes = leilaoDao.porPeriodo(comecoDoIntervalo, fimDoIntervalo);
		
		assertThat(leiloes.size(), equalTo(2));
		assertThat(leiloes, hasItem(leilao));
		
		
	}
	
	@Test
	public void naoDeveTrazerLeiloesEncerradosNoPeriodo(){
		
		Calendar comecoDoIntervalo = Calendar.getInstance();
		comecoDoIntervalo.add(Calendar.DAY_OF_MONTH, -10);
		Calendar fimDoIntervalo = Calendar.getInstance();
		
		
		Usuario mauricio = new Usuario("Mauricio", "mauricio@uol.com.br");
		
		Leilao leilao = new LeilaoBuilder()
										.comDono(mauricio)
										.comNome("Xbox")
										.comValor(700.0)
										.usado()
										.diasAtras(10)
										.encerrado()
										.constroi();
		
		
		Leilao leilao2 = new LeilaoBuilder()
										.comDono(mauricio)
										.comNome("TV")
										.comValor(1700.0)
										.usado()
										.diasAtras(8)
										.encerrado()
										.constroi();
		
		
		usuarioDao.salvar(mauricio);
		leilaoDao.salvar(leilao);
		leilaoDao.salvar(leilao2);
		
		List<Leilao> leiloes = leilaoDao.porPeriodo(comecoDoIntervalo, fimDoIntervalo);
		
		assertThat(leiloes.size(), equalTo(0));
		
	}
	
	@Test
	public void naoDeveTrazerLeiloesEncerradosForaDoPeriodo(){
		
		Calendar comecoDoIntervalo = Calendar.getInstance();
		comecoDoIntervalo.add(Calendar.DAY_OF_MONTH, -10);
		Calendar fimDoIntervalo = Calendar.getInstance();
		
		
		Usuario mauricio = new Usuario("Mauricio", "mauricio@uol.com.br");
		
		Leilao leilao = new LeilaoBuilder()
										.comDono(mauricio)
										.comNome("Xbox")
										.comValor(700.0)
										.usado()
										.diasAtras(16)
										.encerrado()
										.constroi();
		
		
		Leilao leilao2 = new LeilaoBuilder()
										.comDono(mauricio)
										.comNome("TV")
										.comValor(1700.0)
										.usado()
										.diasAtras(88)
										.encerrado()
										.constroi();
		
		
		usuarioDao.salvar(mauricio);
		leilaoDao.salvar(leilao);
		leilaoDao.salvar(leilao2);
		
		List<Leilao> leiloes = leilaoDao.porPeriodo(comecoDoIntervalo, fimDoIntervalo);
		
		assertThat(leiloes.size(), equalTo(0));
		
	}
	
	
	@Test
	public void deveTrazerLeiloesAtivoNoPeriodoQuandoHouverAtivoEEncerrados(){
		
		Calendar comecoDoIntervalo = Calendar.getInstance();
		comecoDoIntervalo.add(Calendar.DAY_OF_MONTH, -10);
		Calendar fimDoIntervalo = Calendar.getInstance();
		
		
		Usuario mauricio = new Usuario("Mauricio", "mauricio@uol.com.br");
		
		Leilao leilao = new LeilaoBuilder()
										.comDono(mauricio)
										.comNome("Xbox")
										.comValor(700.0)
										.usado()
										.diasAtras(1)
										.encerrado()
										.constroi();
		
		
		Leilao leilao2 = new LeilaoBuilder()
										.comDono(mauricio)
										.comNome("TV")
										.comValor(1700.0)
										.usado()
										.diasAtras(5)
										.constroi();
		
		
		usuarioDao.salvar(mauricio);
		leilaoDao.salvar(leilao);
		leilaoDao.salvar(leilao2);
		
		List<Leilao> leiloes = leilaoDao.porPeriodo(comecoDoIntervalo, fimDoIntervalo);
		
		assertThat(leiloes.size(), equalTo(1));
		assertThat(leiloes, hasItem(leilao2));
		
	}

	@Test
	public void leilaoNaoEncerradoComValorDentroDoIntervalo(){
		
		Usuario mauricio = new Usuario("Mauricio", "mauricio@uol.com.br");
		Usuario jose = new Usuario("Jose", "mauricio@uol.com.br");
		
		Leilao leilao = new LeilaoBuilder()
										.comDono(mauricio)
										.comNome("Xbox")
										.comValor(700.0)
										.usado()
										.diasAtras(10)
										.constroi();
		
		
		leilao.adicionaLance(new Lance(Calendar.getInstance(), mauricio, 300.0, leilao));
		leilao.adicionaLance(new Lance(Calendar.getInstance(), jose, 400.0, leilao));
		leilao.adicionaLance(new Lance(Calendar.getInstance(), mauricio, 500.0, leilao));
		leilao.adicionaLance(new Lance(Calendar.getInstance(), jose, 600.0, leilao));
		
		
		usuarioDao.salvar(mauricio);
		usuarioDao.salvar(jose);
		leilaoDao.salvar(leilao);
		
		List<Leilao> leiloes = leilaoDao.disputadosEntre(120.0, 3330.0);
		
		assertThat(leiloes.size(), equalTo(1));
		assertThat(leiloes, hasItem(leilao));
		
	}
	@Test
	public void deveRetornarOsValoresInicaisMediosLeilao(){
		
		Usuario mauricio = new Usuario("Mauricio", "mauricio@uol.com.br");
		Usuario jose = new Usuario("Jose", "mauricio@uol.com.br");
		
		Leilao leilao = new LeilaoBuilder()
										.comDono(mauricio)
										.comNome("Xbox")
										.comValor(700.0)
										.usado()
										.diasAtras(10)
										.constroi();
		
		
		leilao.adicionaLance(new Lance(Calendar.getInstance(), mauricio, 100.0, leilao));
		leilao.adicionaLance(new Lance(Calendar.getInstance(), mauricio, 100.0, leilao));

		
		
		usuarioDao.salvar(mauricio);
		usuarioDao.salvar(jose);
		leilaoDao.salvar(leilao);
		
		double valorMedio = leilaoDao.getValorInicialMedioDoUsuario(mauricio);
		
		assertThat(valorMedio, equalTo(700.0));
		
	}
}
