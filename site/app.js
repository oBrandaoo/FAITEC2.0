const profileData = {
  citizen: { name: 'Olá, Rafael', role: 'morador', kicker: 'visão do morador', title: 'O que está acontecendo por aí?', action: 'Novo chamado' },
  manager: { name: 'Olá, gestão', role: 'administrador', kicker: 'visão operacional', title: 'Prioridades da cidade hoje', action: 'Gerenciar chamados' },
  tracking: { name: 'Acompanhamento', role: 'chamado público', kicker: 'status em tempo real', title: 'Do relato à solução', action: 'Ver mapa público' }
};

const $ = (selector, parent = document) => parent.querySelector(selector);
const $$ = (selector, parent = document) => [...parent.querySelectorAll(selector)];

// Mobile navigation
const menuToggle = $('.menu-toggle');
const mainNav = $('.main-nav');
menuToggle?.addEventListener('click', () => {
  const isOpen = mainNav.classList.toggle('is-open');
  menuToggle.setAttribute('aria-expanded', String(isOpen));
});
$$('.nav-link').forEach((link) => link.addEventListener('click', () => {
  mainNav.classList.remove('is-open');
  menuToggle?.setAttribute('aria-expanded', 'false');
}));

// Reveal elements as they enter the viewport.
const revealObserver = new IntersectionObserver((entries, observer) => {
  entries.forEach((entry) => {
    if (!entry.isIntersecting) return;
    entry.target.classList.add('is-visible');
    observer.unobserve(entry.target);
  });
}, { threshold: 0.12 });
$$('.reveal').forEach((element) => revealObserver.observe(element));

// Count the hero dashboard metrics once.
const countObserver = new IntersectionObserver((entries, observer) => {
  entries.forEach((entry) => {
    if (!entry.isIntersecting) return;
    const element = entry.target;
    const target = Number(element.dataset.count);
    const suffix = element.dataset.suffix || '';
    const duration = 900;
    const startedAt = performance.now();
    const tick = (now) => {
      const progress = Math.min((now - startedAt) / duration, 1);
      const eased = 1 - Math.pow(1 - progress, 3);
      element.textContent = `${Math.round(target * eased)}${suffix}`;
      if (progress < 1) requestAnimationFrame(tick);
    };
    requestAnimationFrame(tick);
    observer.unobserve(element);
  });
}, { threshold: 0.6 });
$$('[data-count]').forEach((metric) => countObserver.observe(metric));

// Interactive profile preview.
$$('.profile-tab').forEach((tab) => tab.addEventListener('click', () => {
  const profile = tab.dataset.profile;
  const data = profileData[profile];
  $$('.profile-tab').forEach((button) => {
    const isActive = button === tab;
    button.classList.toggle('is-active', isActive);
    button.setAttribute('aria-selected', String(isActive));
  });
  $$('.panel-content').forEach((panel) => panel.classList.add('is-hidden'));
  $(`#profile-${profile}`)?.classList.remove('is-hidden');
  $('#side-name').textContent = data.name;
  $('#side-role').textContent = data.role;
  $('#panel-kicker').textContent = data.kicker;
  $('#panel-title').textContent = data.title;
  $('#side-action').textContent = data.action;
}));

// FAQ accordion.
$$('.faq-item').forEach((item) => item.addEventListener('click', () => {
  const wasOpen = item.classList.contains('is-open');
  $$('.faq-item').forEach((faq) => faq.classList.remove('is-open'));
  if (!wasOpen) item.classList.add('is-open');
}));

// Demonstration image uploader.
const uploadInput = $('#demo-upload');
const uploadedGallery = $('#uploaded-gallery');
uploadInput?.addEventListener('change', (event) => {
  [...event.target.files].forEach((file) => {
    if (!file.type.startsWith('image/')) return;
    const reader = new FileReader();
    reader.addEventListener('load', () => {
      const item = document.createElement('div');
      item.className = 'uploaded-item reveal is-visible';
      item.innerHTML = `<img src="${reader.result}" alt="Demonstração adicionada: ${file.name}" /><span>${file.name}</span>`;
      uploadedGallery.appendChild(item);
    });
    reader.readAsDataURL(file);
  });
  event.target.value = '';
});

// Modal flow.
const modal = $('#demo-modal');
const openModal = () => {
  modal.hidden = false;
  document.body.classList.add('modal-open');
  $('[data-close-modal]', modal)?.focus();
};
const closeModal = () => {
  modal.hidden = true;
  document.body.classList.remove('modal-open');
};
$$('[data-open-modal]').forEach((button) => button.addEventListener('click', openModal));
$$('[data-close-modal]').forEach((button) => button.addEventListener('click', closeModal));
modal?.addEventListener('click', (event) => { if (event.target === modal) closeModal(); });
document.addEventListener('keydown', (event) => { if (event.key === 'Escape' && !modal.hidden) closeModal(); });

// Highlight the navigation item for the section currently in view.
const navSections = $$('main section[id]');
const navObserver = new IntersectionObserver((entries) => {
  entries.forEach((entry) => {
    if (!entry.isIntersecting) return;
    const link = $(`.nav-link[href="#${entry.target.id}"]`);
    if (!link) return;
    $$('.nav-link').forEach((navLink) => navLink.classList.remove('is-active'));
    link.classList.add('is-active');
  });
}, { rootMargin: '-35% 0px -58% 0px' });
navSections.forEach((section) => navObserver.observe(section));
