const screenDetails = {
  mapa: {
    src: 'img/mapa.png',
    alt: 'Tela do mapa do Cidade em Dia com marcadores, filtros e controles de visualização.',
    caption: 'Mapa público · filtros por escopo e prioridade'
  },
  acompanhamento: {
    src: 'img/acompanhamento.png',
    alt: 'Tela de acompanhamento de um problema urbano com progresso, detalhes e linha do tempo.',
    caption: 'Acompanhamento · status atual e histórico do atendimento'
  },
  analisesIA: {
    src: 'img/analisesIA.png',
    alt: 'Tela de análises e inteligência com taxa de resolução, fila ativa e recomendações.',
    caption: 'Análises e IA · indicadores e fila crítica'
  },
  inicial: {
    src: 'img/inicial.png',
    alt: 'Painel inicial administrativo com indicadores e reclamações urgentes.',
    caption: 'Painel inicial · visão administrativa'
  },
  login: {
    src: 'img/login.png',
    alt: 'Tela de login com apresentação do projeto e perfis de demonstração.',
    caption: 'Login · acesso por perfil de demonstração'
  }
};

const selectScreen = (key) => {
  const screen = screenDetails[key];
  if (!screen) return;
  const image = document.querySelector('#active-screen');
  image.src = screen.src;
  image.alt = screen.alt;
  document.querySelector('#screen-caption').textContent = screen.caption;
  document.querySelector('#screen-open').href = screen.src;
  document.querySelectorAll('.screen-tab').forEach((tab) => {
    const active = tab.dataset.screen === key;
    tab.classList.toggle('is-active', active);
    tab.setAttribute('aria-selected', String(active));
  });
};

document.querySelectorAll('.screen-tab').forEach((tab) => {
  tab.addEventListener('click', () => selectScreen(tab.dataset.screen));
});

const menuToggle = document.querySelector('.tech-menu-toggle');
const techNav = document.querySelector('.tech-nav');
menuToggle?.addEventListener('click', () => {
  const isOpen = techNav.classList.toggle('is-open');
  menuToggle.setAttribute('aria-expanded', String(isOpen));
});
techNav?.querySelectorAll('a').forEach((link) => link.addEventListener('click', () => {
  techNav.classList.remove('is-open');
  menuToggle?.setAttribute('aria-expanded', 'false');
}));

const revealObserver = new IntersectionObserver((entries, observer) => {
  entries.forEach((entry) => {
    if (!entry.isIntersecting) return;
    entry.target.classList.add('is-visible');
    observer.unobserve(entry.target);
  });
}, { threshold: 0.1 });
document.querySelectorAll('.reveal').forEach((element) => revealObserver.observe(element));

const presentationButton = document.querySelector('#presentation-toggle');
const syncPresentationButton = () => {
  const active = document.body.classList.contains('presentation-mode');
  presentationButton.innerHTML = active
    ? '<span>⛶</span> Sair do modo apresentação'
    : '<span>⛶</span> Modo apresentação';
  presentationButton.setAttribute('aria-pressed', String(active));
};
presentationButton?.addEventListener('click', async () => {
  const enabling = !document.body.classList.contains('presentation-mode');
  document.body.classList.toggle('presentation-mode', enabling);
  syncPresentationButton();
  if (enabling && document.documentElement.requestFullscreen) {
    try {
      await document.documentElement.requestFullscreen();
    } catch {
      // O modo visual continua disponível quando fullscreen não é permitido.
    }
  } else if (!enabling && document.fullscreenElement) {
    await document.exitFullscreen().catch(() => {});
  }
});
document.addEventListener('fullscreenchange', () => {
  if (!document.fullscreenElement && document.body.classList.contains('presentation-mode')) {
    document.body.classList.remove('presentation-mode');
    syncPresentationButton();
  }
});

const sectionLinks = [...document.querySelectorAll('.tech-nav a[href^="#"]')];
const sectionObserver = new IntersectionObserver((entries) => {
  entries.forEach((entry) => {
    if (!entry.isIntersecting) return;
    sectionLinks.forEach((link) => link.classList.toggle('is-active', link.hash === `#${entry.target.id}`));
  });
}, { rootMargin: '-28% 0px -62% 0px' });
document.querySelectorAll('main section[id]').forEach((section) => sectionObserver.observe(section));
